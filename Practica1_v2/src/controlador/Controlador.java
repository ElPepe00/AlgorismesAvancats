package controlador;

import java.util.ArrayList;
import main.Notificar;
import modelo.Datos;
import modelo.Proceso;
import vista.Vista;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 19 feb 2026
 * @name Controlador
 */
public class Controlador implements Notificar {

    private Vista vista;
    private Datos datos;
    private ArrayList<Notificar> procesos = new ArrayList<>();
    private final int N_MAX = 10000;

    public Controlador () {
        this.datos = new Datos();
        this.procesos = new ArrayList<>();
    }
    
    public void inicio() {
        String tituloVentana = "Practica 1 - Complexitat Algorítmica";
        vista = new Vista(tituloVentana, this);
        vista.setVisible(true);
    }

    private void prepararDatos() {
        datos.limpiar();

        for (int i = 1000; i <= 10000; i += 1000) {
            datos.addElemento(i);
        }
    }

    @Override
    public synchronized void notificar(String s) {
        switch (s) {
            case "iniciar" -> {
                boolean vius = procesos.stream().anyMatch(p -> ((Thread) p).isAlive());
                if (!vius) {
                    prepararDatos();
                    procesos.add(new Proceso(this, "O(n)", N_MAX));
                    procesos.add(new Proceso(this, "O(n log n)", N_MAX));
                    procesos.add(new Proceso(this, "O(n^2)", N_MAX));
                    procesos.add(new Proceso(this, "O(n^3)", N_MAX));
                    for (Notificar p : procesos) {
                        ((Thread) p).start();
                    }
                }
            }
            case "parar" -> {
                for (Notificar p : procesos) {
                    p.notificar("parar");
                }
            }
            case "pintar" ->
                vista.pintar();
        }
    }

    public Datos getDatos() {
        return datos;
    }
}
