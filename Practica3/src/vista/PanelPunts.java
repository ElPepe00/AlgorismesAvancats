package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import modelo.Punt;
import java.awt.geom.AffineTransform; //para hacer la forma de elipse en el circulo de mas cercanos

public class PanelPunts extends JPanel {

    private List<Punt> punts;
    private Punt p1, p2;
    private boolean esMesPropera;

    public PanelPunts() {
        setBackground(Color.WHITE);
    }

    public void setPunts(List<Punt> punts) {
        this.punts = punts;

        this.p1 = null;  // 🔥 limpiar línea
        this.p2 = null;

        repaint();
    }

        @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (punts == null) return;

        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Color.BLUE);
        for (Punt p : punts) {
            double px = Math.max(0, Math.min(1, p.x));
            double py = Math.max(0, Math.min(1, p.y));

            int x = (int) (px * width);
            int y = (int) (py * height);

            int size = 2;
            g2.fillOval(x - size/2, y - size/2, size, size);
        }

        if (p1 != null && p2 != null) {
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));

            int x1 = (int) (Math.max(0, Math.min(1, p1.x)) * width);
            int y1 = (int) (Math.max(0, Math.min(1, p1.y)) * height);

            int x2 = (int) (Math.max(0, Math.min(1, p2.x)) * width);
            int y2 = (int) (Math.max(0, Math.min(1, p2.y)) * height);

            g2.drawLine(x1, y1, x2, y2);
            if(esMesPropera){
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
    }

    public void setResultat(Punt p1, Punt p2, boolean esMesPropera) {
        this.p1 = p1;
        this.p2 = p2;
        this.esMesPropera = esMesPropera;
        repaint(); 
    }
}