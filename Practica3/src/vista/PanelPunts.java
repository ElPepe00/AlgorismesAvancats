package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import modelo.Punt;

public class PanelPunts extends JPanel {

    private List<Punt> punts;
    private Punt p1, p2;

    public PanelPunts() {
        setBackground(Color.WHITE);
    }

    public void setPunts(List<Punt> punts) {
        this.punts = punts;
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

            int size = 6;
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
        }
    }

    public void setResultat(Punt p1, Punt p2) {
        this.p1 = p1;
        this.p2 = p2;
        repaint();
    }
}