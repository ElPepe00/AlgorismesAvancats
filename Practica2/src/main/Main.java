

package main;

//import modelo.Modelo;
import vista.Vista;
//import controlador.Controlador;
import javax.swing.SwingUtilities;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 5 mar 2026
 * @name Practica 2
 */
public class Main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        String tituloVentana = "Practica 2 - Tauler d'Escacs";
        
        //Modelo modelo = new Modelo();
        
        // Garantim que la vista es crea de forma segura
        SwingUtilities.invokeLater(() -> {
            Vista vista = new Vista(tituloVentana);
            vista.setVisible(true);
        });
        
        
        //new Controlador(modelo, vista);
        
        
    }
}
