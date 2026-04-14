package controlador;

import vista.*;
import modelo.*;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 4 abr 2026
 * @name Controlador
 */
public class Controlador {

    private Vista vista;
    private GeneradorPunts generador;
    private List<Punt> punts;
    private Algoritmes algoritmes;

    public Controlador(Vista vista) {
        this.vista = vista;
        this.generador = new GeneradorPunts();
        this.algoritmes = new Algoritmes();

        vista.setControladorGenerar(e -> generar());
        vista.setControladorCalcular(e -> calcular());
        // Se añade el listener para el nuevo botón de parar
        vista.setControladorParar(e -> parar());
    }

    private void generar() {
        int n = vista.getNumPunts();
        String tipus = vista.getDistribucio();

        punts = generador.generar(n, tipus);

        vista.mostrarPunts(punts);

        vista.setEstat("Punts generats amb distribució " + vista.getDistribucio() + ". Selecciona l'algorisme de càlcul.");
        System.out.println("Generados " + n + " puntos (" + tipus + ")");
    }

    private void parar() {
        // Llama al método del modelo para activar el flag de cancelación
        algoritmes.cancelar();
        vista.setEstat("Càlcul cancel·lat per l'usuari.");
    }

    private void calcular() {

        if (punts == null || punts.isEmpty()) {
            System.out.println("Primero genera puntos");
            return;
        }

        String alg = vista.getAlgoritme();
        System.out.println("Algoritmo seleccionado: " + alg);
        
        // Bloquea botones de generación/cálculo y habilita el botón de parar
        vista.setModoCalculando(true);

        // Ejecuta el cálculo en un hilo independiente para evitar que la interfaz se congele
        new Thread(() -> {
            long start = System.nanoTime();
            Resultat res;

            // Asegurar que el flag de cancelación esté desactivado antes de empezar
            algoritmes.preparar();

            if (alg.equals("n2")) {
                res = algoritmes.mesProperaBrut(punts);
            } else if (alg.equals("llunyana")) {
                res = algoritmes.mesLlunyana(punts);
            } else if (alg.equals("nlogn")) {
                if (punts.size() > 5000) {
                    System.out.println("Usando bucket optimization");
                    res = algoritmes.bucketClosest(punts);
                } else {
                    res = algoritmes.mesProperaDivideParallel(punts);
                }
            } else {
                res = algoritmes.mesLlunyana(punts);
            }

            long end = System.nanoTime();
            double tempsMs = (end - start) / 1e6;

            // Vuelve al hilo de despacho de eventos (EDT) para actualizar la interfaz
            SwingUtilities.invokeLater(() -> {
                if (res != null) { // Si el resultado no es null, el cálculo terminó correctamente
                    boolean esMesPropera = alg.equals("n2") || alg.equals("nlogn");
                    vista.mostrarResultat(res, tempsMs, esMesPropera);

                    System.out.println("Distancia: " + res.distancia);
                    System.out.println("Tiempo: " + tempsMs + " ms");
                }
                
                // Restaura los controles de la vista (habilita Generar/Calcular y deshabilita Parar)
                vista.setModoCalculando(false);
            });
        }).start();
    }
}