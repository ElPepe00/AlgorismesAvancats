

package main;

import modelo.Modelo;
import vista.Vista;
import controlador.Controlador;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Plantilla
 */
public class Main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        String tituloVentana = "Practica X - Titulo";
        
        Modelo modelo = new Modelo();
        Vista vista = new Vista(tituloVentana);
        
        new Controlador(modelo, vista);
        
        vista.setVisible(true);
    }
}
