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

    public Modelo() {
        // WARM-UP (Calentamiento de la JVM)
        // Ejecutamos simulaciones en segundo plano al arrancar la app.
        // Esto fuerza a Java a compilar y optimizar los bucles (JIT) 
        // ANTES de que el usuario pulse el botón, evitando saltos bruscos iniciales.
        simular(500, "O(n)");
        simular(500, "O(n log n)");
        simular(100, "O(n^2)");
    }
    
    /**
     * Método que simula la carga de trabajo según el tipo de complejidad.
     */
    public long simular(int n, String tipo) {
        long inicio = System.nanoTime();
        double d = 0;
        int repeticiones = 1;

        switch (tipo) {
            case "O(n)" -> {
                repeticiones = 10000;
                for (int r = 0; r < repeticiones; r++) {
                    for (int i = 0; i < n; i++) {
                        d += i;
                    }
                }
            }

            case "O(n log n)" -> {
                repeticiones = 1000;
                for (int r = 0; r < repeticiones; r++) {
                    for (int i = 0; i < n; i++) {
                        for (int j = 1; j < n; j *= 2) {
                            d += i + j;
                        }
                    }
                }
            }

            case "O(n^2)" -> {
                repeticiones = 10;
                for (int r = 0; r < repeticiones; r++) {
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            d += i + j;
                        }
                    }
                }
            }

            case "O(n^3)" -> {
                repeticiones = 1;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            d += i + j + k;
                        }
                    }
                }
            }
        }

        return (System.nanoTime() - inicio)/repeticiones;
    }

    /**
     * Método que realiza una simulacion teorica de la curva que tiene que
     * realizar la gráfica
     *
     * @param n
     * @param tipo
     * @return
     */
    public long simulacionTeorica(int n, String tipo) {
        if (n <= 0) {
            return 0;
        }
        return switch (tipo) {
            case "O(n)" ->
                (long) n;
            case "O(n log n)" ->
                (long) (n * Math.log(n));
            case "O(n^2)" ->
                (long) n * n;
            case "O(n^3)" ->
                (long) n * n * n;
            default ->
                0L;
        };
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
