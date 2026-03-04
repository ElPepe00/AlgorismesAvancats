package controlador;

import modelo.Modelo;
import vista.Vista;
import javax.swing.Timer;
import modelo.Punto;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Controlador
 */
public class Controlador {

    private Modelo modelo;
    private Vista vista;
    private Timer timer;
    private int N_MAX;
    private int puntoActual = 1;

    private static final int NUM_PASOS = 30;

    /**
     * Constructor del Controlador
     *
     * @param modelo instancia de los datos
     * @param vista instancia de la interficie
     * @param N_MAX valor de n
     */
    public Controlador(Modelo modelo, Vista vista, int N_MAX) {

        this.modelo = modelo;
        this.vista = vista;
        this.N_MAX = N_MAX;

        this.timer = new Timer(30, e -> ejecutarPaso());
        asignarEvento();
    }

    /**
     * Método que assigna la accion a los botones
     */
    private void asignarEvento() {

        vista.getBtnIniciar().addActionListener(e -> {
            iniciarSimulacion();
        });

        vista.getBtnParar().addActionListener(e -> {
            pararSimulacion();

        });

        vista.getitemIniciar().addActionListener(e -> {
            iniciarSimulacion();
        });

        vista.getitemParar().addActionListener(e -> {
            pararSimulacion();

        });

        vista.getItemSortir().addActionListener(e -> {
            vista.dispose();
            System.exit(0);
        });
    }

    /**
     * Método que inicia la simulación, partiendo del (0,0)
     */
    private void iniciarSimulacion() {
        puntoActual = 1;
        modelo.limpiar();

        modelo.getDN().add(new Punto(0, 0));
        modelo.getDNlogN().add(new Punto(0, 0));
        modelo.getDN2().add(new Punto(0, 0));
        modelo.getDN3().add(new Punto(0, 0));

        vista.getBtnIniciar().setEnabled(false);
        vista.getBtnParar().setEnabled(true);

        timer.start();
    }

    /**
     * Método que para la simulación
     */
    private void pararSimulacion() {
        timer.stop();
        vista.getBtnIniciar().setEnabled(true);
        vista.getBtnParar().setEnabled(false);
    }

    /**
     * Método que en cada paso añade los puntos pertinentes a cada lista en
     * función la complejidad algoritmica
     */
    private void ejecutarPaso() {

        if (puntoActual > NUM_PASOS) {
            pararSimulacion();
            return;
        }

        int nCalculo = puntoActual * (N_MAX / NUM_PASOS);
        String sel = vista.getSeleccion();

        switch (sel) {

            case "Tots":
                long tN = modelo.simular(nCalculo, "O(n)");
                long tNlogN = modelo.simular(nCalculo, "O(n log n)");
                long tN2 = modelo.simular(nCalculo, "O(n^2)");
                long tN3 = modelo.simular(nCalculo, "O(n^3)");

                modelo.getDN().add(new Punto(nCalculo, tN));
                modelo.getDNlogN().add(new Punto(nCalculo, tNlogN));
                modelo.getDN2().add(new Punto(nCalculo, tN2));
                modelo.getDN3().add(new Punto(nCalculo, tN3));
                
                vista.actualizarTiempos(tN, tNlogN, tN2, tN3);
                break;

            case "O(n)":
                long tsN = modelo.simular(nCalculo, "O(n)");
                modelo.getDN().add(new Punto(nCalculo, tsN));
                vista.actualizarTiempos(tsN, -1, -1, -1);
                break;

            case "O(n log n)":
                long tsNlogN = modelo.simular(nCalculo, "O(n log n)");
                modelo.getDNlogN().add(new Punto(nCalculo, tsNlogN));
                vista.actualizarTiempos(-1, tsNlogN, -1, -1);
                break;

            case "O(n^2)":
                long tsN2 = modelo.simular(nCalculo, "O(n^2)");
                modelo.getDN2().add(new Punto(nCalculo, tsN2));
                vista.actualizarTiempos(-1, -1, tsN2, -1);
                break;

            case "O(n^3)":
                long tsN3 = modelo.simular(nCalculo, "O(n^3)");
                modelo.getDN3().add(new Punto(nCalculo, tsN3));
                vista.actualizarTiempos(-1, -1, -1, tsN3);
                break;

            default:
                break;
        }

        //Actualizamos datos y repintamos
        vista.getPanelGrafica().setDatos(modelo.getDN(), modelo.getDNlogN(), modelo.getDN2(), modelo.getDN3());
        puntoActual++;
    }
}
