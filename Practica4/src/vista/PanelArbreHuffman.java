package vista;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

import modelo.estructuras.Node;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 23 abr 2026
 * @name Panel Arbre Huffman
 */
public class PanelArbreHuffman extends JPanel {

    private Node arrel;
    private final int RADI_NODE = 15;
    private final int SEPARACIO_Y = 60; // Distància vertical entre nivells
    private final int DISTANCIA_H = 80; // Espai horitzontal per garantir no-solapament

    // Map per guardar la posició X calculada de cada node
    private final Map<Node, Integer> mapaX = new HashMap<>();
    private int leafCount = 0;

    /** Configura el color de fons del panell. */
    public PanelArbreHuffman() {
        this.setBackground(new Color(40, 42, 54));
    }

    /** Assigna l'arbre que s'ha de dibuixar i en calcula les dimensions. */
    public void setArrelArbre(Node arrel) {
        this.arrel = arrel;
        this.mapaX.clear();
        this.leafCount = 0;

        if (arrel != null) {
            precalcularX(arrel);
            int profunditat = calcularProfunditat(arrel);
            int alcadaNecessaria = (profunditat * SEPARACIO_Y) + 100;
            int ampladaNecessaria = Math.max(800, (leafCount + 1) * DISTANCIA_H);

            this.setPreferredSize(new Dimension(ampladaNecessaria, alcadaNecessaria));
            this.revalidate();
        }
        this.repaint();
    }

    /** Calcula la posició horitzontal de cada node per evitar solapaments. */
    private int precalcularX(Node node) {
        if (node == null) return -1;

        if (node.esFulla()) {
            int x = leafCount++;
            mapaX.put(node, x);
            return x;
        }

        int xEsq = precalcularX(node.getFillEsquerre());
        int xDret = precalcularX(node.getFillDret());

        // El pare es situa exactament a la meitat dels seus dos fills
        int xPare = (xEsq + xDret) / 2;
        mapaX.put(node, xPare);
        return xPare;
    }

    /** Calcula quants nivells té l'arbre recursivament. */
    private int calcularProfunditat(Node node) {
        if (node == null) return 0;
        return Math.max(calcularProfunditat(node.getFillEsquerre()), 
                        calcularProfunditat(node.getFillDret())) + 1;
    }

    /** Mètode principal de dibuix de Swing. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arrel == null || mapaX.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Consolas", Font.BOLD, 12));

        dibuixarNodeRecursiu(g2d, arrel, 1, 50);
    }

    /** Dibuixa un node, les seves línies cap als fills i el seu text. */
    private void dibuixarNodeRecursiu(Graphics2D g2d, Node node, int nivell, int y) {
        if (node == null) return;

        int x = (mapaX.get(node) + 1) * DISTANCIA_H;
        g2d.setStroke(new BasicStroke(1.2f));
        
        if (node.getFillEsquerre() != null) {
            int xFill = (mapaX.get(node.getFillEsquerre()) + 1) * DISTANCIA_H;
            int yFill = y + SEPARACIO_Y;
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawLine(x, y, xFill, yFill);
            dibuixarBit(g2d, x, y, xFill, yFill, "0");
            dibuixarNodeRecursiu(g2d, node.getFillEsquerre(), nivell + 1, yFill);
        }

        if (node.getFillDret() != null) {
            int xFill = (mapaX.get(node.getFillDret()) + 1) * DISTANCIA_H;
            int yFill = y + SEPARACIO_Y;
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawLine(x, y, xFill, yFill);
            dibuixarBit(g2d, x, y, xFill, yFill, "1");
            dibuixarNodeRecursiu(g2d, node.getFillDret(), nivell + 1, yFill);
        }

        if (node.esFulla()) {
            g2d.setColor(new Color(46, 204, 113));
        } else {
            g2d.setColor(new Color(52, 152, 219));
        }
        g2d.fillOval(x - RADI_NODE, y - RADI_NODE, RADI_NODE * 2, RADI_NODE * 2);
        g2d.setColor(Color.WHITE);
        g2d.drawOval(x - RADI_NODE, y - RADI_NODE, RADI_NODE * 2, RADI_NODE * 2);

        String text;
        if (node.esFulla()) {
            int simbol = node.getSimbol();
            text = (simbol >= 32 && simbol <= 126) ? String.valueOf((char) simbol) : String.format("%02X", simbol);
        } else {
            text = String.valueOf(node.getFrequencia());
        }

        int ampladaText = g2d.getFontMetrics().stringWidth(text);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x - (ampladaText / 2), y + 5);
    }

    /** Pinta el bit (0 o 1) damunt de la branca corresponent. */
    private void dibuixarBit(Graphics2D g2d, int x1, int y1, int x2, int y2, String bit) {
        int xMig = (x1 + x2) / 2;
        int yMig = (y1 + y2) / 2;
        g2d.setColor(new Color(241, 196, 15));
        g2d.drawString(bit, xMig + (bit.equals("0") ? -12 : 5), yMig);
    }
}
