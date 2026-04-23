package modelo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 20 abr 2026
 * @name Modelo
 */
public class Modelo {

    private File fitxerActual;
    private long[] frequencies;
    private long totalBytesLlegits;
    private AlgorismeHuffman generadorHuffman;

    public void setFitxerActual(File fitxerActual) {
        this.fitxerActual = fitxerActual;
    }

    public File getFitxerActual() {
        return fitxerActual;
    }
    
    // AFEGEIX A MODELO.JAVA (Si no ho tenies)
    public Node getArrelArbre() {
        if (generadorHuffman != null) {
            return generadorHuffman.getArrel();
        }
        return null;
    }

    /**
     * Orquestra la fase 1: Llegir i preparar diccionaris
     */
    public void analitzarFitxer() throws IOException {
        // 1. Deleguem la I/O
        ResultatLector res = ProcessadorFitxers.calcularFrequencies(fitxerActual);
        this.frequencies = res.frequencies;
        this.totalBytesLlegits = res.totalBytes;

        // 2. Deleguem l'algorisme pur
        this.generadorHuffman = new AlgorismeHuffman(frequencies);
    }

    public double calcularLongitudMitjana() {
        if (totalBytesLlegits == 0 || generadorHuffman == null) {
            return 0.0;
        }
        double suma = 0;
        for (Map.Entry<Integer, String> entrada : generadorHuffman.getCodisHuffman().entrySet()) {
            suma += (frequencies[entrada.getKey()] * entrada.getValue().length());
        }
        return suma / totalBytesLlegits;
    }

    // Passarel·les cap a les dades
    public Map<Integer, String> getCodisHuffman() {
        return generadorHuffman != null ? generadorHuffman.getCodisHuffman() : null;
    }
    
    public long comprimir(File fitxerDesti) throws IOException {
        if (generadorHuffman == null || frequencies == null) {
            throw new IllegalStateException("Cal analitzar el fitxer abans de comprimir-lo.");
        }
        return ProcessadorFitxers.generarArxiuComprimit(fitxerActual, fitxerDesti, frequencies, generadorHuffman.getCodisHuffman());
    }

    public long[] getFrequencies() {
        return frequencies;
    }
}
