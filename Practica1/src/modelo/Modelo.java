package modelo;

import java.util.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Modelo
 */
public class Modelo {

    private ArrayList<Punto> datosN = new ArrayList<>();
    private ArrayList<Punto> datosNlogN = new ArrayList<>();
    private ArrayList<Punto> datosN2 = new ArrayList<>();
    private ArrayList<Punto> datosN3 = new ArrayList<>();

    /**
     * Método que simula la carga de trabajo según el tipo de complejidad.
     */
    public long simular(int n, String tipo) {
        long inicio = System.nanoTime();
        double dummy = 0;
        switch (tipo) {
            case "O(n)" -> {
                for (int i = 0; i < n; i++) {
                    dummy += Math.sin(i);
                }
            }

            case "O(n log n)" -> {
                for (int i = 0; i < n * (Math.log(n + 1) / Math.log(2)); i++) {
                    dummy += Math.sin(i);
                }
            }

            case "O(n^2)" -> {
                for (int i = 0; i < n * n; i++) {
                    dummy += Math.sin(i);
                }
            }

            case "O(n^3)" -> {
                for (int i = 0; i < n * n * n / 100; i++) {
                    dummy += Math.sin(i);
                }
            }
        }
        return System.nanoTime() - inicio;
    }

    /**
     * Método que limpia las listas
     */
    public void limpiar() {
        datosN.clear();
        datosNlogN.clear();
        datosN2.clear();
        datosN3.clear();
    }

    // Getters para las listas de puntos
    public ArrayList<Punto> getDN() {
        return datosN;
    }

    public ArrayList<Punto> getDNlogN() {
        return datosNlogN;
    }

    public ArrayList<Punto> getDN2() {
        return datosN2;
    }

    public ArrayList<Punto> getDN3() {
        return datosN3;
    }
}
