package modelo;

import java.io.*;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * @author Josep Oliver i Hugo Valls
 * @name GestorIO
 */
public class GestorIO {

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

    /**
     * @param comprovarCancelacio Expressió funcional que retorna true si l'usuari ha premut Aturar
     */
    public static long generarArxiuComprimit(File original, File desti, long[] frequencies, Map<Integer, String> codis, IProgresListener listener, BooleanSupplier comprovarCancelacio) throws Exception {
        
        long totalBytesOriginals = original.length();
        
        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(desti)))) {
            
            // 1. Capçalera: Diccionari de freqüències i el pes total de l'original
            for (int i = 0; i < frequencies.length; i++) {
                dos.writeLong(frequencies[i]);
            }
            dos.writeLong(totalBytesOriginals);
            
            // 2. Cos: Escriptura a nivell de bits
            EscriptorBits empaquetador = new EscriptorBits(dos);
            
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(original))) {
                byte[] buffer = new byte[8192];
                int bytesLlegits;
                
                long bytesProcessats = 0;
                long tempsInici = System.currentTimeMillis();
                int ultimPercentatge = -1;
                
                while ((bytesLlegits = bis.read(buffer)) != -1) {
                    
                    // --- COMPROVACIÓ DE CANCEL·LACIÓ ---
                    if (comprovarCancelacio != null && comprovarCancelacio.getAsBoolean()) {
                        bis.close();
                        dos.close();
                        desti.delete(); // Eliminem l'arxiu corrupte que s'estava generant
                        throw new Exception("Procés aturat per l'usuari.");
                    }
                    
                    // Empaquetem els bytes d'aquest bloc
                    for (int i = 0; i < bytesLlegits; i++) {
                        int byteSencer = buffer[i] & 0xFF;
                        String codiBit = codis.get(byteSencer);
                        if (codiBit != null) {
                            empaquetador.escriureBits(codiBit);
                        }
                    }
                    
                    // --- CÀLCUL DE LA BARRA DE PROGRÉS ---
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
                
                // Forcem a escriure els bits sobrants (Padding)
                empaquetador.buidar();
            }
        }
        
        return desti.length();
    }

    public static void descomprimir(File origen, File desti,
            IProgresListener listener,
            BooleanSupplier comprovarCancelacio) throws Exception {

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(origen)));
            BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(desti))) {

            // =========================
            // 1. LEER CABECERA
            // =========================
            long[] frequencies = new long[256];

            for (int i = 0; i < 256; i++) {
                frequencies[i] = dis.readLong();
            }

            long midaOriginal = dis.readLong();

            // =========================
            // 2. RECONSTRUIR ÁRBOL
            // =========================
            

            CuaPrioritat cua = new BinaryHeapQueue();
            AlgorismeHuffman huffman = new AlgorismeHuffman(frequencies, cua);
            huffman.construirArbre();
            Node arrel = huffman.getArrel();

            // =========================
            // 3. DECODIFICAR BITS
            // =========================
            LectorBits lector = new LectorBits(dis);

            Node actual = arrel;
            long escrits = 0;

            int ultimPercentatge = -1;

            while (escrits < midaOriginal) {

                // --- CANCELACIÓN ---
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

                    // --- PROGRESO ---
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