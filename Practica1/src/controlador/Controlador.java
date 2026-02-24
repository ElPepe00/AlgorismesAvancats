

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
    private int nActual = 1;
    private final int N_MAX = 1000;

    /**
     * Constructor del Controlador
     * @param modelo instancia de los datos
     * @param vista instancia de la interficie
     */
    public Controlador(Modelo modelo, Vista vista) {
        
        this.modelo = modelo;
        this.vista = vista;
        
        // Timer que se ejecuta cada 50ms para la animación
        timer = new Timer(50, e -> ejecutarPaso());
        asignarEvento();
    }

    /**
     * Método que assigna la accion a los botones
     */
    private void asignarEvento() {
        
        vista.getBtnIniciar().addActionListener(e -> {
            nActual = 1;
            modelo.limpiar();
            vista.getBtnIniciar().setEnabled(false);
            timer.start();
        });

        vista.getItemSortir().addActionListener(e -> {
            vista.dispose();
            System.exit(0);
        });
    }

    /**
     * Método que se ejecuta repetidamente mediante el Timer.
     * Calcula el nuevo punto y actualiza la gráfica en tiempo real.
     */
    private void ejecutarPaso() {
        
        if (nActual > N_MAX) { // Límite de la gráfica
            timer.stop();
            vista.getBtnIniciar().setEnabled(true);
            return;
        }

        String seleccion = vista.getSeleccio();

        // Ejecutamos solo lo seleccionado
        if (seleccion.equals("O(n)") || seleccion.equals("Tots")) {
            long t = modelo.simular(nActual, "O(n)");
            modelo.getDN().add(new Punto(nActual, t));
        }
        
        if (seleccion.equals("O(n log n)") || seleccion.equals("Tots")) {
            long t = modelo.simular(nActual, "O(n log n)");
            modelo.getDNlogN().add(new Punto(nActual, t));
        }
        
        if (seleccion.equals("O(n^2)") || seleccion.equals("Tots")) {
            long t = modelo.simular(nActual, "O(n^2)");
            modelo.getDN2().add(new Punto(nActual, t));
        }
        
        if (seleccion.equals("O(n^3)") || seleccion.equals("Tots")) {
            long t = modelo.simular(nActual, "O(n^3)");
            modelo.getDN3().add(new Punto(nActual, t));
        }

        // Actualizamos la grafica de la vista con una nueva lista de puntos
        vista.getPanelGrafica().setDatos(modelo.getDN(), modelo.getDNlogN(), modelo.getDN2(), modelo.getDN3());

        // Incrementamos n para el siguiente paso (30 para una animacion minimamente fluida)
        nActual += 30;
    }
}
