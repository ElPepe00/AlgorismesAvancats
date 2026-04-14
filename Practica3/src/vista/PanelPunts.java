package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import modelo.Punt;
import java.awt.geom.AffineTransform; //para hacer la forma de elipse en el circulo de mas cercanos

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 24 mar 2026
 * @name PanelPunts
 */
public class PanelPunts extends JPanel {

    private List<Punt> punts;
    private Punt p1, p2;
    private boolean esMesPropera;

    private double cellSize = -1;
    private int numPunts = 0;

    public PanelPunts() {
        setBackground(Color.WHITE);
    }

    public void setPunts(List<Punt> punts) {
        this.punts = punts;

        this.p1 = null;  //limpiar línea
        this.p2 = null;

        repaint();
    }

    public void setNumPunts(int numPunts) {
        this.numPunts = numPunts;
    }

    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (punts == null) return;

        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        // ---------------- DIBUJO DE PUNTOS ----------------
        g2.setColor(Color.BLUE);
        for (Punt p : punts) {
            double px = Math.max(0, Math.min(1, p.x));
            double py = Math.max(0, Math.min(1, p.y));

            int x = (int) (px * width);
            int y = (int) (py * height);

            int size = 2;
            g2.fillOval(x - size/2, y - size/2, size, size);
        }

        // ---------------- DIBUJO DE RESULTADO ----------------
        if (p1 != null && p2 != null) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));

            int x1 = (int) (Math.max(0, Math.min(1, p1.x)) * width);
            int y1 = (int) (Math.max(0, Math.min(1, p1.y)) * height);

            int x2 = (int) (Math.max(0, Math.min(1, p2.x)) * width);
            int y2 = (int) (Math.max(0, Math.min(1, p2.y)) * height);

            g2.drawLine(x1, y1, x2, y2);

            if (esMesPropera) {
                int centerX = (x1 + x2) / 2;
                int centerY = (y1 + y2) / 2;

                double dx = x2 - x1;
                double dy = y2 - y1;
                double dist = Math.sqrt(dx * dx + dy * dy);

                int majorAxis = (int) dist + 20;
                int minorAxis = Math.max(15, (int)(dist / 2));

                double angle = Math.atan2(dy, dx);

                AffineTransform old = g2.getTransform();

                g2.translate(centerX, centerY);
                g2.rotate(angle);

                g2.setColor(Color.RED);
                g2.drawOval(-majorAxis / 2, -minorAxis / 2, majorAxis, minorAxis);

                g2.setTransform(old);
            }
        }

        // ---------------- DIBUJO DE CELDAS (SOLO BUCKET) ----------------
        if (cellSize > 0 && numPunts > 0) {

            // Escalado progresivo según número de puntos
            double factor = Math.log10(numPunts);
            double t = (factor - 4) / (6 - 4); // normaliza entre 10^4 y 10^6
            t = Math.max(0, Math.min(1, t));

            int maxCells = (int)(4 + t * (60 - 4));

            double step = 1.0 / maxCells;

            g2.setColor(new Color(200, 200, 200));

            for (int i = 0; i <= maxCells; i++) {
                int x = (int)(i * step * width);
                g2.drawLine(x, 0, x, height);
            }

            for (int j = 0; j <= maxCells; j++) {
                int y = (int)(j * step * height);
                g2.drawLine(0, y, width, y);
            }
        }
    }

    public void setResultat(Punt p1, Punt p2, boolean esMesPropera) {
        this.p1 = p1;
        this.p2 = p2;
        this.esMesPropera = esMesPropera;
        repaint(); 
    }
}