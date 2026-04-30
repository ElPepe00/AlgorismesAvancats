package modelo;

import java.io.File;
import java.util.Map;

import modelo.algoritmos.AlgorismeHuffman;
import modelo.estructuras.BinaryHeapQueue;
import modelo.estructuras.CuaPrioritat;
import modelo.estructuras.FibonacciHeapQueue;
import modelo.estructuras.Node;
import modelo.io.GestorIO;

/**
 * @author Josep Oliver i Hugo Valls
 * @name Modelo
 */
public class Modelo {

    private File fitxerActual;
    private long[] frequencies;
    private AlgorismeHuffman algorismeHuffman;
    private boolean usarFibonacci = false;
    
    // Bandera de cancel·lació (Thread-safe)
    private volatile boolean cancelat = false;

    /** Inicialitza l'array de freqüències. */
    public Modelo() {
        this.frequencies = new long[256];
    }

    /** Guarda la referència al fitxer que estem tractant. */
    public void setFitxerActual(File fitxer) {
        this.fitxerActual = fitxer;
    }

    /** Retorna el fitxer seleccionat actualment. */
    public File getFitxerActual() {
        return fitxerActual;
    }

    // --- GESTIÓ DE LA CANCEL·LACIÓ ---
    /** Activa la bandera per aturar qualsevol operació en curs. */
    public void cancelarOperacio() {
        this.cancelat = true;
    }

    /** Reinicia la bandera de cancel·lació per a una nova operació. */
    public void reiniciarCancelacio() {
        this.cancelat = false;
    }

    /** Indica si l'usuari ha demanat aturar el procés. */
    public boolean isCancelat() {
        return cancelat;
    }
    // ---------------------------------

    /** Analitza el fitxer, calcula freqüències i construeix l'arbre de Huffman. */
    public void analitzarFitxer() throws Exception {
        this.frequencies = GestorIO.calcularFrequencies(fitxerActual);

        if (cancelat) return;

        CuaPrioritat cua;
        if (usarFibonacci) {
            cua = new FibonacciHeapQueue();
        } else {
            cua = new BinaryHeapQueue();
        }

        this.algorismeHuffman = new AlgorismeHuffman(frequencies, cua);
        this.algorismeHuffman.construirArbre();
        this.algorismeHuffman.generarCodis();
    }

    /** Comprimeix el fitxer actual i el guarda al destí especificat. */
    public long comprimir(File fitxerDesti, IProgresListener listener) throws Exception {
        Map<Integer, String> codis = algorismeHuffman.getCodisHuffman();
        return GestorIO.generarArxiuComprimit(fitxerActual, fitxerDesti, frequencies, codis, listener, this::isCancelat);
    }

    /** Descomprimeix un fitxer .huff i recupera l'original. */
    public void descomprimir(File origen, File desti, IProgresListener listener) throws Exception {
        GestorIO.descomprimir(origen, desti, listener, this::isCancelat);
    }

    /** Calcula quants bits ocupa de mitjana cada símbol en el fitxer comprimit. */
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

    /** Retorna el node arrel de l'arbre de Huffman generat. */
    public Node getArrelArbre() {
        if (algorismeHuffman != null) {
            return algorismeHuffman.getArrel();
        }
        return null;
    }

    /** Retorna el diccionari de codis Huffman (Símbol -> Cadena de bits). */
    public Map<Integer, String> getCodisHuffman() {
        if (algorismeHuffman != null) {
            return algorismeHuffman.getCodisHuffman();
        }
        return null;
    }

    /** Retorna l'array de freqüències de cada byte. */
    public long[] getFrequencies() {
        return frequencies;
    }

    /** Defineix si s'ha d'utilitzar Fibonacci Heap per a la cua de prioritats. */
    public void setUsarFibonacci(boolean usarFibonacci) {
        this.usarFibonacci = usarFibonacci;
    }

    
}