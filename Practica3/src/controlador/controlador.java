package controlador;

import vista.*;
import modelo.*;


import java.util.List;

public class controlador {

    private Vista vista;
    private GeneradorPunts generador;
    private List<Punt> punts;

    public controlador(Vista vista) {
        this.vista = vista;
        this.generador = new GeneradorPunts();

        vista.setControladorGenerar(e -> generar());
        vista.setControladorCalcular(e -> calcular());
    }

    private void generar() {
    int n = vista.getNumPunts();
    String tipus = vista.getDistribucio();

    punts = generador.generar(n, tipus);

    vista.mostrarPunts(punts); 

    System.out.println("Generados " + n + " puntos (" + tipus + ")");
}

    private void calcular() {
        System.out.println("calculando puntos...");
    }

    
}