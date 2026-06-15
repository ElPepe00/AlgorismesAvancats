package vista;

import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name HistogramaPanel
 */
public class HistogramaPanel extends JPanel {
    private int[] frecuencias;
    private int minTurnos;
    private int maxTurnos;
    private int maxFrecuencia;
    private double mediaTurnos;

    public HistogramaPanel() {
        setBackground(Color.WHITE);
    }

    public void actualizarHistograma(int[] datos) {
        if (datos == null || datos.length == 0) return;
        int min = datos[0];
        int max = datos[0];
        long suma = 0;
        for (int turnos : datos) {
            if (turnos < min) min = turnos;
            if (turnos > max) max = turnos;
            suma += turnos;
        }
        this.mediaTurnos = (double) suma / datos.length;

        int rango = max - min + 1;
        int[] frecs = new int[rango];
        int maxFrec = 0;
        for (int turnos : datos) {
            int idx = turnos - min;
            frecs[idx]++;
            if (frecs[idx] > maxFrec) {
                maxFrec = frecs[idx];
            }
        }

        this.frecuencias = frecs;
        this.minTurnos = min;
        this.maxTurnos = max;
        this.maxFrecuencia = maxFrec;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frecuencias == null || frecuencias.length == 0) {
            String msg = "Ejecuta una simulación en el panel de control para visualizar el histograma.";
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(new Color(149, 165, 166)); // Gris plomo
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int paddingX = 70;
        int paddingY = 50;
        int width = getWidth();
        int height = getHeight();
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g2.setColor(new Color(44, 62, 80));
        String title = "Distribución del Volumen de Partidas según Turnos Consumidos";
        g2.drawString(title, (width - g2.getFontMetrics().stringWidth(title)) / 2, 30);
        g2.setColor(new Color(248, 250, 252));
        g2.fillRect(paddingX, paddingY, width - 2 * paddingX, height - 2 * paddingY);
        g2.setColor(new Color(220, 225, 230));
        for (int i = 0; i <= 5; i++) {
            int y = paddingY + i * (height - 2 * paddingY) / 5;
            g2.drawLine(paddingX, y, width - paddingX, y);
            
            String label = String.valueOf(maxFrecuencia - (i * maxFrecuencia / 5));
            g2.setColor(new Color(127, 140, 141));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString(label, paddingX - g2.getFontMetrics().stringWidth(label) - 10, y + 5);
            g2.setColor(new Color(220, 225, 230)); 
        }

        // Dibujar el histograma calculando las áreas
        int nBarras = frecuencias.length;
        double widthBarra = (double) (width - 2 * paddingX) / nBarras;
        g2.setColor(new Color(52, 152, 219)); // Azul corporativo sólido
        
        for (int i = 0; i < nBarras; i++) {
            if (frecuencias[i] == 0) continue;
            
            int x = paddingX + (int) (i * widthBarra);
            int barHeight = (int) ((double) frecuencias[i] / maxFrecuencia * (height - 2 * paddingY));
            int y = height - paddingY - barHeight;
            
            // Utilizamos Math.ceil para evitar huecos en blanco entre barras debido al redondeo de píxeles
            int barW = Math.max(1, (int) Math.ceil(widthBarra));
            g2.fillRect(x, y, barW, barHeight);
        }

        // Dibujar ejes principales X e Y
        g2.setColor(new Color(44, 62, 80));
        g2.drawLine(paddingX, height - paddingY, paddingX, paddingY);
        g2.drawLine(paddingX, height - paddingY, width - paddingX, height - paddingY);

        // Añadir etiquetas del eje X (límites)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString(minTurnos + " turnos (Mínimo)", paddingX, height - paddingY + 20);
        g2.drawString(maxTurnos + " turnos (Máximo)", width - paddingX - 110, height - paddingY + 20);
        
        // Añadir etiqueta del eje Y rotada verticalmente
        g2.translate(20, height / 2 + 50);
        g2.rotate(-Math.PI / 2);
        g2.drawString("Volumen de Partidas", 0, 0);
        g2.rotate(Math.PI / 2);
        g2.translate(-20, -(height / 2 + 50));

        // Dibujar línea vertical discontinua roja indicando la posición exacta de la Media
        if (mediaTurnos > 0) {
            // +0.5 ajusta la coordenada exactamente al centro visual de su barra correspondiente
            int xMedia = paddingX + (int) ((mediaTurnos - minTurnos + 0.5) * widthBarra);
            g2.setColor(new Color(231, 76, 60)); // Rojo brillante (Estilo corporativo)
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
            g2.drawLine(xMedia, paddingY, xMedia, height - paddingY);
            
            g2.setStroke(new BasicStroke(1f)); // Resetear el trazo por defecto para las letras
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String textoMedia = String.format("Media: %.2f", mediaTurnos);
            g2.drawString(textoMedia, xMedia - (g2.getFontMetrics().stringWidth(textoMedia) / 2), paddingY - 10);
        }
    }
}