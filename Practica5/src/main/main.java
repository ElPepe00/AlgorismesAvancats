

package main;

import vista.Vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


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
            vista.setVisible(true);
        });
    }

}

    