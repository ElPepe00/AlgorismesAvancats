package modelo;

import java.util.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Modelo
 */
public class Datos {

    private ArrayList<Long> tiempoN = new ArrayList<>();
    private ArrayList<Long> tiempoNlogN = new ArrayList<>();
    private ArrayList<Long> tiempoN2 = new ArrayList<>();
    private ArrayList<Long> tiempoN3 = new ArrayList<>();
    private ArrayList<Integer> elementosN = new ArrayList<>();

    /**
     * Método que limpia las listas
     */
    public synchronized void limpiar() {
        tiempoN.clear();
        tiempoNlogN.clear();
        tiempoN2.clear();
        tiempoN3.clear();
        elementosN.clear();
    }

    // Getters para la gráfica
    public synchronized int getTam() {
        return elementosN.size();
    }

    public synchronized int getN(int i) {
        return elementosN.get(i);
    }

    public synchronized long getT_N(int i) {
        return (i < tiempoN.size()) ? tiempoN.get(i) : 0;
    }

    public synchronized long getT_NlogN(int i) {
        return (i < tiempoNlogN.size()) ? tiempoNlogN.get(i) : 0;
    }

    public synchronized long getT_N2(int i) {
        return (i < tiempoN2.size()) ? tiempoN2.get(i) : 0;
    }

    public synchronized long getT_N3(int i) {
        return (i < tiempoN3.size()) ? tiempoN3.get(i) : 0;
    }

    // Setters para los hilos y añadir datos
    public synchronized void addElemento(int n) {
        elementosN.add(n);
    }

    public synchronized void addN(int n) {
        elementosN.add(n);
    }

    public synchronized void addT_N(long t) {
        tiempoN.add(t);
    }

    public synchronized void addT_NlogN(long t) {
        tiempoNlogN.add(t);
    }

    public synchronized void addT_N2(long t) {
        tiempoN2.add(t);
    }

    public synchronized void addT_N3(long t) {
        tiempoN3.add(t);
    }
}
