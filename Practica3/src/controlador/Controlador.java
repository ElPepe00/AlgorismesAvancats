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

    public Controlador(Vista vista) {
        this.vista = vista;
        this.generador = new GeneradorPunts();
        this.algoritmes = new Algoritmes();

        vista.setControladorGenerar(e -> generar());
        vista.setControladorCalcular(e -> calcular());
        vista.setControladorParar(e -> parar());
    }

    private void generar() {
        int n = vista.getNumPunts();
        String tipus = vista.getDistribucio();

        punts = generador.generar(n, tipus);

        vista.mostrarPunts(punts);

        vista.getPanelPunts().setNumPunts(punts.size());
        vista.getPanelPunts().setCellSize(-1);

        vista.setEstat("Punts generats amb distribució " + tipus + ". Selecciona l'algorisme de càlcul.");
        System.out.println("Generados " + n + " puntos (" + tipus + ")");
    }

    private void parar() {
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

        vista.setModoCalculando(true);

        Timer timer = new Timer(500, null); //refresh cada 500 ms

        new Thread(() -> {
            long start = System.nanoTime();
            Resultat res;

            algoritmes.preparar();

            // Timer en tiempo real
            timer.addActionListener(e -> {
                double tempsMsLive = (System.nanoTime() - start) / 1e6;
                vista.setEstat(String.format("Calculant... %.2f ms", tempsMsLive));
            });
            timer.start();

            // -------- EJECUCIÓN DEL ALGORITMO --------
            if (alg.equals("n2")) {
                res = algoritmes.mesProperaBrut(punts);
                vista.getPanelPunts().setCellSize(-1);

            } else if (alg.equals("llunyana")) {
                res = algoritmes.mesLlunyana(punts);
                vista.getPanelPunts().setCellSize(-1);

            } else if (alg.equals("nlogn")) {

                if (punts.size() > 5000) {
                    System.out.println("Usando bucket optimization");
                    res = algoritmes.bucketClosest(punts);

                    double cellSize = algoritmes.getLastCellSize();
                    vista.getPanelPunts().setCellSize(cellSize);

                } else {
                    res = algoritmes.mesProperaDivideParallel(punts);
                    vista.getPanelPunts().setCellSize(-1);
                }

            } else {
                res = algoritmes.mesLlunyana(punts);
                vista.getPanelPunts().setCellSize(-1);
            }
            // ----------------------------------------

            long end = System.nanoTime();
            double tempsMs = (end - start) / 1e6;

            SwingUtilities.invokeLater(() -> {
                timer.stop(); //detener timer

                if (res != null) {
                    boolean esMesPropera = alg.equals("n2") || alg.equals("nlogn");
                    vista.mostrarResultat(res, tempsMs, esMesPropera);

                    System.out.println("Distancia: " + res.distancia);
                    System.out.println("Tiempo: " + tempsMs + " ms");
                }

                vista.setModoCalculando(false);
            });

        }).start();
    }
}