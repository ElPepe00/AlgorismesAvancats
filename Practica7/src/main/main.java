

package main;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Practica7
 */
public class main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        // Crear y mostrar la vista
        javax.swing.SwingUtilities.invokeLater(() -> {
            new vista.Vista().setVisible(true);
        });
    }
}
