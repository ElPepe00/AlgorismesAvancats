

package main;

import vista.Vista;
import controlador.Controlador;
import javax.swing.SwingUtilities;


/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 3 may 2026
 * @name Practica5
 */
public class main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista();
            new Controlador(vista);
            vista.setVisible(true);
        });
    }

}

    