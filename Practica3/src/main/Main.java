package main;

import vista.Vista;
import controlador.Controlador;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 24 mar 2026
 * @name Practica 3
 */
public class Main {

    /**
     * Static Main
     */
    public static void main(String[] args) {
        
        Vista v = new Vista();
        new Controlador(v);
        v.setVisible(true);
    }
}
