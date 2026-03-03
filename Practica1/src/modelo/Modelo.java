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
        double d = 0;
        switch (tipo) {
            case "O(n)" -> {
                for (int i = 0; i < n; i++) {
                    d += i;
                }
            }

            case "O(n log n)" -> {
                for (int i = 0; i < n; i++) {
                    for (int j = 1; j < n; j *= 2) {
                        d += i + j;
                    }
                }
            }

            case "O(n^2)" -> {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        d += i + j;
                    }
                }
            }

            case "O(n^3)" -> {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            d += i + j + k;
                        }
                    }
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
