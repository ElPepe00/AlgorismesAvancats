package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import modelo.Punt;

public class PanelPunts extends JPanel {

    private List<Punt> punts;

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
        g2.setColor(Color.BLUE);

        int width = getWidth();
        int height = getHeight();

        for (Punt p : punts) {
            // Escalar (importante)
            int x = (int) (p.x * width);
            int y = (int) (p.y * height);

            g2.fillOval(x, y, 5, 5);
        }
    }
}