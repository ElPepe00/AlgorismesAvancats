package modelo;

import java.io.*;
import java.util.Map;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 20 abr 2026
 * @name GestorIO
 */
public class GestorIO {

    /**
     * Llegeix l'arxiu i retorna les freqüències i el total de bytes. Retorna un
     * objecte o array [0] = frequencies (long[]), [1] = totalBytes (long).
     */
    public static ResultatLector calcularFrequencies(File fitxer) throws IOException {
        long[] frequencies = new long[256];
        long totalBytes = 0;

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fitxer))) {
            byte[] buffer = new byte[8192];
            int bytesLlegits;

            while ((bytesLlegits = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesLlegits; i++) {
                    int indexHex = buffer[i] & 0xFF;
                    frequencies[indexHex]++;
                    totalBytes++;
                }
            }
        }
        return new ResultatLector(frequencies, totalBytes);
    }

    /**
     * Comprimeix l'arxiu original i el guarda al destí.
     * Retorna el pes final de l'arxiu comprimit per a les estadístiques.
     */
    public static long generarArxiuComprimit(File original, File desti, long[] frequencies, Map<Integer, String> codis) throws IOException {
        
        // FASE 1: Escriure la Capçalera (Metadades) amb DataOutputStream
        // Ens permet escriure tipus primitius (long) directament al disc
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(desti)))) {
            // Guardem l'array de 256 freqüències perquè el descompressor pugui reconstruir l'arbre
            for (int i = 0; i < 256; i++) {
                dos.writeLong(frequencies[i]);
            }
        } // Es tanca automàticament aquí per assegurar-nos que s'ha guardat
        
        // FASE 2: Escriure els bits reals de la informació original
        // Obrim el fitxer original per llegir-lo un altre cop (amb buffer per no gastar RAM)
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(original));
             EscriptorBits eb = new EscriptorBits(desti)) { // EscriptorBits ja fa "append" (afegeix al final)
             
             byte[] buffer = new byte[8192];
             int bytesLlegits;

             while ((bytesLlegits = bis.read(buffer)) != -1) {
                 for (int i = 0; i < bytesLlegits; i++) {
                     int byteActual = buffer[i] & 0xFF; // Netegem el signe
                     String codiHuffman = codis.get(byteActual); // Busquem al diccionari
                     
                     // Escrivim la seqüència de bits real al disc dur
                     eb.escriureCodiString(codiHuffman);
                 }
             }
        } // Aquí s'activa el mètode close() del nostre EscriptorBits, empaquetant els últims bits

        // Retornem el pes de l'arxiu nou creat
        return desti.length();
    }
}

// Classe de suport per retornar ambdues dades fàcilment (o pots fer-ho com prefereixis)
class ResultatLector {

    long[] frequencies;
    long totalBytes;

    public ResultatLector(long[] f, long tb) {
        this.frequencies = f;
        this.totalBytes = tb;
    }
}
