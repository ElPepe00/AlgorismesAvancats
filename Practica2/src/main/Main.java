

package main;

import vista.Vista;
import javax.swing.SwingUtilities;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 5 mar 2026
 * @name Practica 2
 */
public class main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        // Garantim que la vista es crea de forma segura
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista();
            vista.setVisible(true);
        });
    }
}
