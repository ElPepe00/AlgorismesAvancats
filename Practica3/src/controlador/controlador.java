package controlador;

import vista.*;
import modelo.*;


import java.util.List;

public class controlador {

    private Vista vista;
    private GeneradorPunts generador;
    private List<Punt> punts;
    private Algoritmes algoritmes;

    public controlador(Vista vista) {
        this.vista = vista;
        this.generador = new GeneradorPunts();
        this.algoritmes = new Algoritmes();

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

        if (punts == null || punts.isEmpty()) {
            System.out.println("Primero genera puntos");
            return;
        }

        String alg = vista.getAlgoritme();
        System.out.println("Algoritmo seleccionado: " + alg);
        Resultat res;

        long start = System.nanoTime();

        if (alg.equals("n2")) {
            res = algoritmes.mesProperaBrut(punts);
        } else if (alg.equals("llunyana")) {
            res = algoritmes.mesLlunyana(punts);
        } else {
            res = algoritmes.mesProperaDivideParallel(punts);
        }

        long end = System.nanoTime();
        double tempsMs = (end - start) / 1e6;

        vista.mostrarResultat(res, tempsMs); 

        System.out.println("Distancia: " + res.distancia);
        System.out.println("Tiempo: " + tempsMs + " ms");
    }

    
}