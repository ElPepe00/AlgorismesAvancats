

package main;

import controlador.Controlador;
import modelo.Modelo;
import vista.Vista;

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
            
            // 1. Crear el núcleo lógico (Modelo)
            Modelo modelo = new Modelo();
            
            // 2. Crear la interfaz gráfica (Vista)
            Vista vista = new Vista();
            
            // 3. Crear el coordinador táctico pasándole ambas instancias (Controlador)
            new Controlador(vista, modelo);
            
            // 4. Iniciar visualización
            vista.setVisible(true);
        });
    }
}
