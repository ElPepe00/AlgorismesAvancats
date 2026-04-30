package main;

import controlador.Controlador;
import modelo.Modelo;
import vista.Vista;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 16 abr 2026
 * @name Practica 4
 */
public class Main {
    /** Inicia l'aplicació creant la vista, el model i el controlador. */
    public static void main(String[] args) {
        Vista vista = new Vista();
        Modelo modelo = new Modelo();
        new Controlador(vista, modelo);
        vista.setVisible(true);
    }
}
