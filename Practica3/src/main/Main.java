package main;

import vista.Vista;
import controlador.controlador;

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
        new controlador(v);
        v.setVisible(true);
    }
}
