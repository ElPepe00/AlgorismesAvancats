package vista;

import javax.swing.*;
import java.awt.*;
import controlador.Controlador;
import modelo.Datos;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 20 feb 2026
 * @name PanelGrafico
 */
/**
 * Componente JPanel para dibujar las graficas
 */
public class PanelGrafico extends JPanel {

    private Controlador controlador;

    /**
     * Método Contructor
     *
     * @param c notificacion pasada por parámetro
     */
    public PanelGrafico(Controlador c) {
        this.controlador = c;
    }

    /**
     * Método que pinta las 4 gráficas
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Obtenir dades des del controlador (Practica1 haurà de tenir el mètode getDades)
        Datos datos = controlador.getDatos();
        if (datos == null || datos.getTam() == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight() - 20;
        int m = 40; // margen

        // Fondo y Ejes
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        g2.drawLine(m, h - m, w - m, h - m);    // X
        g2.drawLine(m, m, m, h - m);            // Y

        int maxN = datos.getN(datos.getTam() - 1);
        long maxT = 1;
        for (int i = 0; i < datos.getTam(); i++) {
            maxT = Math.max(maxT, Math.max(datos.getT_N(i), Math.max(datos.getT_NlogN(i),
                    Math.max(datos.getT_N2(i), datos.getT_N3(i)))));
        }

        // Dibuixar les 4 sèries
        dibuixarSerie(g2, "N", Color.BLUE, w, h, m, maxN, maxT);
        dibuixarSerie(g2, "NlogN", Color.GREEN, w, h, m, maxN, maxT);
        dibuixarSerie(g2, "N2", Color.ORANGE, w, h, m, maxN, maxT);
        dibuixarSerie(g2, "N3", Color.RED, w, h, m, maxN, maxT);
    }

    private void dibuixarSerie(Graphics2D g, String tipus, Color c, int w, int h, int m, int maxN, long maxT) {
        Datos d = controlador.getDatos();
        g.setColor(c);
        int pax = m, pay = h - m; // Comença a (0,0)

        for (int i = 0; i < d.getTam(); i++) {
            long t = switch (tipus) {
                case "N" ->
                    d.getT_N(i);
                case "NlogN" ->
                    d.getT_NlogN(i);
                case "N2" ->
                    d.getT_N2(i);
                case "N3" ->
                    d.getT_N3(i);
                default ->
                    -1L;
            };

            if (t == -1) {
                break; // Encara no s'ha calculat
            }
            int px = m + (d.getN(i) * (w - 2 * m) / maxN);
            int py = (h - m) - (int) (t * (h - 2 * m) / maxT);

            g.drawLine(pax, pay, px, py);
            g.fillOval(px - 3, py - 3, 6, 6);
            pax = px;
            pay = py;
        }
    }
}
