package modelo;

import java.io.File;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 16 abr 2026
 * @name Modelo
 */
public class Modelo {

    private File fitxerActual;

    public Modelo() {
        // Inicialització d'estructures de dades en el futur
    }

    public void setFitxerActual(File fitxerActual) {
        this.fitxerActual = fitxerActual;
    }

    public File getFitxerActual() {
        return fitxerActual;
    }
}
