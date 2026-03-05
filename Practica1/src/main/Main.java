

package main;

import modelo.Modelo;
import vista.Vista;
import controlador.Controlador;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Practica 1
 */
public class Main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        String tituloVentana = "Practica 1 - Complexitat Algorítmica";
        
        int N_MAX = 2000; // Valor máximo de 'n'
        
        Modelo modelo = new Modelo();
        
        Vista vista = new Vista(tituloVentana);
        vista.getPanelGrafica().setMaxN(N_MAX);
        
        new Controlador(modelo, vista, N_MAX);
        
        vista.setVisible(true);
    }
}
