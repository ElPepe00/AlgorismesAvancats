

package controlador;

import modelo.Modelo;
import vista.Vista;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Controlador
 */
public class Controlador {
    
    private Modelo model;
    private Vista vista;

    /**
     * Constructor del Controlador
     * @param model instancia de los datos
     * @param vista instancia de la interficie
     */
    public Controlador(Modelo model, Vista vista) {
        this.model = model;
        this.vista = vista;
        asignarEventos();
    }

    /**
     * Método que define el comportamiento de los componentes a través de Listeners.
     * Implementa el Patrón por Eventos.
     */
    private void asignarEventos() {
        
        // Evento para cerrar la ventana y cortar ejecución
        vista.getItemSortir().addActionListener(e -> {
            vista.dispose();
            System.exit(0);
        });

        //TODO
    }
}
