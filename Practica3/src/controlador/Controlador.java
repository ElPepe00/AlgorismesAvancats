package controlador;

import vista.*;
import modelo.*;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

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

    // Timer para actualizar el tiempo en pantalla
    private Timer timer;

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

        // Pasar número de puntos al panel para el escalado de la cuadrícula
        vista.getPanelPunts().setNumPunts(punts.size());

        // Al generar puntos no se deben mostrar celdas
        vista.getPanelPunts().setCellSize(-1);

        vista.setEstat("Punts generats amb distribució " + vista.getDistribucio() + ". Selecciona l'algorisme de càlcul.");
        System.out.println("Generados " + n + " puntos (" + tipus + ")");
    }

    private void parar() {
        // Llama al método del modelo para activar el flag de cancelación
        algoritmes.cancelar();

        // Detener el timer si está activo
        if (timer != null) timer.stop();

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

        // Tiempo inicial
        long start = System.nanoTime();

        // Timer que actualiza cada 100ms
        timer = new Timer(100, e -> {
            double temps = (System.nanoTime() - start) / 1e6;
            vista.mostrarTempsActual(temps);
        });

        timer.start();

        // Ejecuta el cálculo en un hilo independiente para evitar que la interfaz se congele
        new Thread(() -> {

            Resultat res;

            // Asegurar que el flag de cancelación esté desactivado antes de empezar
            algoritmes.preparar();

            if (alg.equals("n2")) {
                res = algoritmes.mesProperaBrut(punts);

                // No se muestran celdas en este algoritmo
                vista.getPanelPunts().setCellSize(-1);

            } else if (alg.equals("llunyana")) {
                res = algoritmes.mesLlunyana(punts);

                // No se muestran celdas en este algoritmo
                vista.getPanelPunts().setCellSize(-1);

            } else if (alg.equals("nlogn")) {

                if (punts.size() > 5000) {
                    System.out.println("Usando bucket optimization");
                    res = algoritmes.bucketClosest(punts);

                    // Solo en bucket se dibuja la cuadrícula
                    double cellSize = algoritmes.getLastCellSize();
                    vista.getPanelPunts().setCellSize(cellSize);

                } else {
                    res = algoritmes.mesProperaDivideParallel(punts);

                    // En divide y vencerás no se muestran celdas
                    vista.getPanelPunts().setCellSize(-1);
                }

            } else {
                res = algoritmes.mesLlunyana(punts);

                // Seguridad: no mostrar celdas
                vista.getPanelPunts().setCellSize(-1);
            }

            long end = System.nanoTime();
            double tempsMs = (end - start) / 1e6;

            // Vuelve al hilo de despacho de eventos (EDT) para actualizar la interfaz
            SwingUtilities.invokeLater(() -> {

                // Detener el timer al finalizar
                if (timer != null) timer.stop();

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