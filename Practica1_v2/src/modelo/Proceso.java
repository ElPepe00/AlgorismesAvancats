package modelo;

import controlador.Controlador;
import main.Notificar;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Controlador
 */
public class Proceso extends Thread implements Notificar {

    private boolean cancel;
    private final Controlador controlador;
    private final String tipo;
    private final int nMax;
    private long bo = 0;

    /**
     * Método Constructor
     * @param c
     * @param tipo
     * @param nMax 
     */
    public Proceso(Controlador c, String tipo, int nMax) {
        this.controlador = c;
        this.tipo = tipo;
        this.nMax = nMax;
    }

    @Override
    public void run() {
        cancel = false;
        Datos datos = controlador.getDatos();
        int punts = 10;
        int salt = nMax / punts;

        for (int k = 1; (k <= punts) && (!cancel); k++) {
            int n = k * salt;
            long tiempo = System.nanoTime();
            
            simular(n); // Execució de la càrrega de treball

            if (!cancel) {
                tiempo = System.nanoTime() - tiempo;
                // Guardar segons tipus
                switch (tipo) {
                    case "O(n)" -> datos.addT_N(tiempo);
                    case "O(n log n)" -> datos.addT_NlogN(tiempo);
                    case "O(n^2)" -> datos.addT_N2(tiempo);
                    case "O(n^3)" -> datos.addT_N3(tiempo);
                }
                
                controlador.notificar("pintar");
            }
        }
    }

    private void simular(int n) {
        // Implementació de bucles asimptòtics segons el tipus
        switch (tipo) {
            case "O(n)" -> {
                for (int i = 0; i < n && !cancel; i++) bo++;
            }
            case "O(n log n)" -> {
                for (int i = 0; i < n && !cancel; i++) {
                    for (int j = 1; j < Math.log10(n) * 10 && !cancel; j++) bo++;
                }
            }
            case "O(n^2)" -> {
                for (int i = 0; i < n && !cancel; i++) {
                    for (int j = 0; j < n && !cancel; j++) bo++;
                }
            }
            case "O(n^3)" -> {
                // Ajustem n per O(n^3) per evitar esperes excessives
                int limit = (int) Math.pow(n, 1.0/1.5); 
                for (int i = 0; i < limit && !cancel; i++)
                    for (int j = 0; j < limit && !cancel; j++)
                        for (int l = 0; l < limit && !cancel; l++) bo++;
            }
        }
    }

    @Override
    public void notificar(String s) {
        if (s.equals("parar")) cancel = true;
    }
}

