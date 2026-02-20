

package vista;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 20 feb 2026
 * @name PanelGrafico
 */
import javax.swing.*;
import java.awt.*;
import java.util.*;

import modelo.Punto;

/**
 * Componente JPanel para dibujar las graficas
 */
public class PanelGrafico extends JPanel {

    private ArrayList<Punto> dN, dNlogN, dN2, dN3;
    private double maxT = 1.0;

    /**
     * Método que 
     * @param n
     * @param nl
     * @param n2
     * @param n3 
     */
    public void setDatos(ArrayList<Punto> n, ArrayList<Punto> nl, ArrayList<Punto> n2, ArrayList<Punto> n3) {
        this.dN = n;
        this.dNlogN = nl;
        this.dN2 = n2;
        this.dN3 = n3;

        // Buscamos el tiempo máximo actual para escalar el eje Y dinámicamente
        maxT = 1.0;
        actualizarMaxT(dN);
        actualizarMaxT(dNlogN);
        actualizarMaxT(dN2);
        actualizarMaxT(dN3);

        repaint();
    }

    private void actualizarMaxT(ArrayList<Punto> lista) {
        if (lista == null) {
            return;
        }
        for (Punto p : lista) {
            if (p.getTiempo() > maxT) {
                maxT = p.getTiempo();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight(), m = 60;
        double maxN = 600.0; // El mismo límite que en el controlador

        // Fondo y Ejes
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        g2.drawLine(m, h - m, w - m, h - m); // X
        g2.drawLine(m, m, m, h - m);     // Y

        // Curvas
        dibujarCurva(g2, dN, Color.BLUE, w, h, m, maxN);
        dibujarCurva(g2, dNlogN, Color.GREEN, w, h, m, maxN);
        dibujarCurva(g2, dN2, Color.ORANGE, w, h, m, maxN);
        dibujarCurva(g2, dN3, Color.RED, w, h, m, maxN);
    }

    private void dibujarCurva(Graphics2D g2, ArrayList<Punto> punts, Color c, int w, int h, int m, double maxN) {
        if (punts == null || punts.size() < 2) {
            return;
        }
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < punts.size() - 1; i++) {
            Punto pActual = punts.get(i);
            Punto pSeguent = punts.get(i + 1);

            // Usem els getters de la classe Punt
            int x1 = m + (int) (pActual.getN() * (w - 2 * m) / maxN);
            int y1 = (h - m) - (int) (pActual.getTiempo() * (h - 2 * m) / maxT);

            int x2 = m + (int) (pSeguent.getN() * (w - 2 * m) / maxN);
            int y2 = (h - m) - (int) (pSeguent.getTiempo() * (h - 2 * m) / maxT);

            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
