

package practica1;

import modelo.Modelo;
import vista.Vista;
import controlador.Controlador;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Practica 1
 */
public class Practica1 {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        String tituloVentana = "Practica 1 - Complexitat Algorítmica";
        
        Modelo modelo = new Modelo();
        Vista vista = new Vista(tituloVentana);
        
        new Controlador(modelo, vista);
        
        vista.setVisible(true);
    }
}
