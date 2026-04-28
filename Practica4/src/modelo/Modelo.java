package modelo;

import controlador.IProgresListener;
import java.io.File;
import java.util.Map;

/**
 * @author Josep Oliver i Hugo Valls
 * @name Modelo
 */
public class Modelo {

    private File fitxerActual;
    private long[] frequencies;
    private AlgorismeHuffman algorismeHuffman; // Aquesta és la teva classe que crea l'arbre
    
    // Bandera de cancel·lació (Thread-safe)
    private volatile boolean cancelat = false;

    public Modelo() {
        this.frequencies = new long[256];
    }

    public void setFitxerActual(File fitxer) {
        this.fitxerActual = fitxer;
    }

    public File getFitxerActual() {
        return fitxerActual;
    }

    // --- GESTIÓ DE LA CANCEL·LACIÓ ---
    public void cancelarOperacio() {
        this.cancelat = true;
    }

    public void reiniciarCancelacio() {
        this.cancelat = false;
    }

    public boolean isCancelat() {
        return cancelat;
    }
    // ---------------------------------

    public void analitzarFitxer() throws Exception {
        // 1. Llegim el fitxer per extreure les freqüències
        this.frequencies = GestorIO.calcularFrequencies(fitxerActual);
        
        if (cancelat) return;

        // 2. Generem l'arbre i els codis amb la classe encarregada
        this.algorismeHuffman = new AlgorismeHuffman(frequencies);
        this.algorismeHuffman.construirArbre();
        this.algorismeHuffman.generarCodis();
    }

    public long comprimir(File fitxerDesti, IProgresListener listener) throws Exception {
        Map<Integer, String> codis = algorismeHuffman.getCodisHuffman();
        
        // Passem el model en format BooleanSupplier (expressió Lambda) per mantenir el desacoblament
        return GestorIO.generarArxiuComprimit(fitxerActual, fitxerDesti, frequencies, codis, listener, this::isCancelat);
    }

    public double calcularLongitudMitjana() {
        if (algorismeHuffman == null) return 0.0;
        
        Map<Integer, String> codis = algorismeHuffman.getCodisHuffman();
        long totalSimbols = 0;
        long totalBits = 0;

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                totalSimbols += frequencies[i];
                String codi = codis.get(i);
                if (codi != null) {
                    totalBits += (frequencies[i] * codi.length());
                }
            }
        }
        return totalSimbols == 0 ? 0 : (double) totalBits / totalSimbols;
    }

    public Node getArrelArbre() {
        if (algorismeHuffman != null) {
            return algorismeHuffman.getArrel();
        }
        return null;
    }

    public Map<Integer, String> getCodisHuffman() {
        if (algorismeHuffman != null) {
            return algorismeHuffman.getCodisHuffman();
        }
        return null;
    }

    public long[] getFrequencies() {
        return frequencies;
    }
}