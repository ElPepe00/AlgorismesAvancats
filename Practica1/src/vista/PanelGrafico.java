

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
 * PanelGrafico es un componente JPanel para dibujar las graficas
 */
public class PanelGrafico extends JPanel {

    private ArrayList<Punto> dN, dNlogN, dN2, dN3;
    private double maxT = 1.0;
    private double maxN = 2000.0;

    /**
     * Método que actualiza el valor maximo de N
     * @param maxN 
     */
    public void setMaxN(double maxN) {
        this.maxN = maxN;
    }
    
    /**
     * Método que actualiza los datos para pintar las gráficas
     * @param n lista de puntos de 0(n)
     * @param nlog lista de puntos de 0(n log_n)
     * @param n2 lista de puntos de 0(n^2)
     * @param n3 lista de puntos de 0(n^3)
     */
    public void setDatos(ArrayList<Punto> n, ArrayList<Punto> nlog, ArrayList<Punto> n2, ArrayList<Punto> n3) {
        this.dN = n;
        this.dNlogN = nlog;
        this.dN2 = n2;
        this.dN3 = n3;

        // Buscamos el tiempo máximo actual para escalar el eje Y dinámicamente
        maxT = 1.0;
        actualizarMaxT(dN);
        actualizarMaxT(dNlogN);
        actualizarMaxT(dN2);
        actualizarMaxT(dN3);

        // Repintar
        repaint();
    }
    
    /**
     * Método que actualiza el valor de maxT para buscar el tiempo máximo y asi
     * poder escalar la gráfica
     * @param lista 
     */
    private void actualizarMaxT(ArrayList<Punto> lista) {
        if (lista == null) {
            return;
        }
        
        synchronized (lista) {
            for (Punto p : lista) {
                if (p.getTiempo() > maxT) {
                    maxT = p.getTiempo();
                }
            }
        }
    }

    /**
     * Método que pinta las 4 gráficas
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int m = 30;

        // Fondo y Ejes
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        g2.drawLine(m, h - m, w - m, h - m);    // X
        g2.drawLine(m, m, m, h - m);            // Y

        // Curvas de gráficas
        dibujarLineaGrafica(g2, dN, Color.BLUE, w, h, m);
        dibujarLineaGrafica(g2, dNlogN, Color.GREEN, w, h, m);
        dibujarLineaGrafica(g2, dN2, Color.ORANGE, w, h, m);
        dibujarLineaGrafica(g2, dN3, Color.RED, w, h, m);
    }

    /**
     * Método que dibuja en el panel grafico los puntos y lineas correspondientes
     * a las complejidades algoritmicas
     * @param g2 Objeto graphics 2d
     * @param puntos Array de puntos de la gráfica a pintar
     * @param c Color especifico para cada complejidad
     * @param w width
     * @param h height
     * @param m margin
     */
    private void dibujarLineaGrafica(Graphics2D g2, ArrayList<Punto> puntos, Color c, int w, int h, int m) {
        
        // Mirar si hay almenos 2 puntos
        if (puntos == null || puntos.size() < 2) {
            return;
        }
        
        g2.setColor(c);
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < puntos.size() - 1; i++) {
            Punto p1 = puntos.get(i);
            Punto p2 = puntos.get(i + 1);

            // Obtenemos coordenadas de los puntos
            int x1 = m + (int) (p1.getN() * (w - 2 * m) / maxN);
            int y1 = (h - m) - (int) (p1.getTiempo() * (h - 2 * m) / maxT);

            int x2 = m + (int) (p2.getN() * (w - 2 * m) / maxN);
            int y2 = (h - m) - (int) (p2.getTiempo() * (h - 2 * m) / maxT);

            // Dibujamos linea entre dos puntos y dibujamos los puntos
            g2.drawLine(x1, y1, x2, y2);
            g2.fillOval(x2-2, y2-2, 4, 4);
        }
    }
}
