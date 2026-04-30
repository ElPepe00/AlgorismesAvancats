package modelo.io;

import java.io.*;
import java.util.Map;
import java.util.function.BooleanSupplier;

import modelo.IProgresListener;
import modelo.algoritmos.AlgorismeHuffman;
import modelo.estructuras.BinaryHeapQueue;
import modelo.estructuras.CuaPrioritat;
import modelo.estructuras.Node;

/**
 * @author Josep Oliver i Hugo Valls
 * @name GestorIO
 */
public class GestorIO {

    /** Llegeix el fitxer byte a byte per comptar quantes vegades apareix cada un. */
    public static long[] calcularFrequencies(File arxiu) throws IOException {
        long[] freq = new long[256];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(arxiu))) {
            byte[] buffer = new byte[8192];
            int bytesLlegits;
            while ((bytesLlegits = bis.read(buffer)) != -1) {
                for (int i = 0; i < bytesLlegits; i++) {
                    int byteValor = buffer[i] & 0xFF;
                    freq[byteValor]++;
                }
            }
        }
        return freq;
    }

    /** Crea el fitxer .huff escrivint primer el diccionari i després les dades comprimides. */
    public static long generarArxiuComprimit(File original, File desti, long[] frequencies, Map<Integer, String> codis, IProgresListener listener, BooleanSupplier comprovarCancelacio) throws Exception {
        
        long totalBytesOriginals = original.length();
        
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(desti)))) {
            for (int i = 0; i < frequencies.length; i++) {
                dos.writeLong(frequencies[i]);
            }
            dos.writeLong(totalBytesOriginals);
            
            EscriptorBits empaquetador = new EscriptorBits(dos);
            
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(original))) {
                byte[] buffer = new byte[8192];
                int bytesLlegits;
                long bytesProcessats = 0;
                long tempsInici = System.currentTimeMillis();
                int ultimPercentatge = -1;
                
                while ((bytesLlegits = bis.read(buffer)) != -1) {
                    if (comprovarCancelacio != null && comprovarCancelacio.getAsBoolean()) {
                        bis.close();
                        dos.close();
                        desti.delete();
                        throw new Exception("Procés aturat per l'usuari.");
                    }
                    
                    for (int i = 0; i < bytesLlegits; i++) {
                        int byteSencer = buffer[i] & 0xFF;
                        String codiBit = codis.get(byteSencer);
                        if (codiBit != null) {
                            empaquetador.escriureBits(codiBit);
                        }
                    }
                    
                    bytesProcessats += bytesLlegits;
                    if (totalBytesOriginals > 0 && listener != null) {
                        int percentatgeActual = (int) ((bytesProcessats * 100) / totalBytesOriginals);
                        if (percentatgeActual != ultimPercentatge) {
                            ultimPercentatge = percentatgeActual;
                            long tempsAra = System.currentTimeMillis();
                            long tempsTranscorregut = tempsAra - tempsInici;
                            if (percentatgeActual > 0) {
                                long tempsTotalEstimat = (tempsTranscorregut * 100) / percentatgeActual;
                                long msRestants = tempsTotalEstimat - tempsTranscorregut;
                                long segonsRestants = msRestants / 1000;
                                String textTemps = String.format("%02d:%02d", segonsRestants / 60, segonsRestants % 60);
                                listener.actualitzar(percentatgeActual, textTemps);
                            }
                        }
                    }
                }
                empaquetador.buidar();
            }
        }
        return desti.length();
    }

    /** Reverteix la compressió llegint la capçalera i després recorrent l'arbre bit a bit. */
    public static void descomprimir(File origen, File desti, IProgresListener listener, BooleanSupplier comprovarCancelacio) throws Exception {

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(origen)));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(desti))) {

            long[] frequencies = new long[256];
            for (int i = 0; i < 256; i++) {
                frequencies[i] = dis.readLong();
            }
            long midaOriginal = dis.readLong();

            CuaPrioritat cua = new BinaryHeapQueue();
            AlgorismeHuffman huffman = new AlgorismeHuffman(frequencies, cua);
            huffman.construirArbre();
            Node arrel = huffman.getArrel();

            LectorBits lector = new LectorBits(dis);
            Node actual = arrel;
            long escrits = 0;
            int ultimPercentatge = -1;

            while (escrits < midaOriginal) {
                if (comprovarCancelacio != null && comprovarCancelacio.getAsBoolean()) {
                    bos.close();
                    dis.close();
                    desti.delete();
                    throw new Exception("Procés aturat per l'usuari.");
                }

                int bit = lector.llegirBit();
                if (bit == -1) break;

                if (bit == 0) {
                    actual = actual.getFillEsquerre();
                } else {
                    actual = actual.getFillDret();
                }

                if (actual.esFulla()) {
                    bos.write(actual.getSimbol());
                    actual = arrel;
                    escrits++;

                    if (listener != null && midaOriginal > 0) {
                        int percentatge = (int) ((escrits * 100) / midaOriginal);
                        if (percentatge != ultimPercentatge) {
                            ultimPercentatge = percentatge;
                            listener.actualitzar(percentatge, "--:--");
                        }
                    }
                }
            }
        }
    }
}