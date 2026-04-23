package vista;

import java.awt.*;
import javax.swing.JPanel;
import modelo.Node;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 23 abr 2026
 * @name Panel Arbre Huffman
 */
public class PanelArbreHuffman extends JPanel {

    private Node arrel;
    private final int RADI_NODE = 15;
    private final int SEPARACIO_Y = 50; // Distància vertical entre nivells

    public PanelArbreHuffman() {
        this.setBackground(new Color(40, 42, 54)); // Un color fosc estil IDE
    }

    /**
     * Rep l'arrel de l'arbre acabat de generar pel Model i força un redibuixat.
     */
    // 1. MODIFICA EL MÈTODE setArrelArbre
    public void setArrelArbre(Node arrel) {
        this.arrel = arrel;

        if (arrel != null) {
            // Calculem la profunditat per saber quina alçada necessitem
            int profunditat = calcularProfunditat(arrel);

            // Cada nivell necessita SEPARACIO_Y píxels. Afegim marge.
            int alcadaNecessaria = (profunditat * SEPARACIO_Y) + 100;

            // L'amplada depèn de quantes branques tingui. Posem una fòrmula generosa.
            // Si l'arbre és molt profund, demanem més espai horitzontal.
            int ampladaNecessaria = Math.max(800, profunditat * 120);

            // Avisem al sistema de Swing que necessitem aquest espai real
            this.setPreferredSize(new java.awt.Dimension(ampladaNecessaria, alcadaNecessaria));
            this.revalidate(); // Clau: Avisa a l'JScrollPane que s'ha de recalcular
        }

        this.repaint();
    }

    // 2. AFEGEIX AQUEST MÈTODE AUXILIAR AL FINAL DE LA CLASSE
    /**
     * Calcula el nombre de nivells que té l'arbre de forma recursiva (DFS).
     */
    private int calcularProfunditat(Node node) {
        if (node == null) {
            return 0;
        }
        int profunditatEsq = calcularProfunditat(node.getFillEsquerre());
        int profunditatDret = calcularProfunditat(node.getFillDret());

        return Math.max(profunditatEsq, profunditatDret) + 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (arrel == null) {
            return;
        }

        // Graphics2D permet antialiasing perquè les línies i els cercles es vegin suaus i professionals
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(new Font("Consolas", Font.BOLD, 12));

        // Comencem a dibuixar des de la part superior central
        int xInicial = this.getWidth() / 2;
        int yInicial = 30;

        // L'espaiat inicial X ha de ser prou gran perquè l'arbre s'obri
        int espaiXInicial = this.getWidth() / 4;

        dibuixarNodeRecursiu(g2d, arrel, xInicial, yInicial, espaiXInicial);
    }

    /**
     * Mètode recursiu DFS per dibuixar línies, cercles i textos.
     */
    private void dibuixarNodeRecursiu(Graphics2D g2d, Node node, int x, int y, int espaiX) {
        if (node == null) {
            return;
        }

        // 1. Dibuixar les línies cap als fills (es fan abans perquè quedin per sota del cercle)
        g2d.setColor(Color.GRAY);
        if (node.getFillEsquerre() != null) {
            g2d.drawLine(x, y, x - espaiX, y + SEPARACIO_Y);
            // Crida recursiva cap a l'esquerra reduint l'espai horitzontal a la meitat
            dibuixarNodeRecursiu(g2d, node.getFillEsquerre(), x - espaiX, y + SEPARACIO_Y, espaiX / 2);
        }
        if (node.getFillDret() != null) {
            g2d.drawLine(x, y, x + espaiX, y + SEPARACIO_Y);
            // Crida recursiva cap a la dreta
            dibuixarNodeRecursiu(g2d, node.getFillDret(), x + espaiX, y + SEPARACIO_Y, espaiX / 2);
        }

        // 2. Dibuixar el cercle del node actual
        if (node.esFulla()) {
            g2d.setColor(new Color(46, 204, 113)); // Verd per a les fulles
        } else {
            g2d.setColor(new Color(52, 152, 219)); // Blau per als nodes interns
        }
        g2d.fillOval(x - RADI_NODE, y - RADI_NODE, RADI_NODE * 2, RADI_NODE * 2);

        // Vora del cercle
        g2d.setColor(Color.WHITE);
        g2d.drawOval(x - RADI_NODE, y - RADI_NODE, RADI_NODE * 2, RADI_NODE * 2);

        // 3. Dibuixar el text a l'interior del node
        String text;
        if (node.esFulla()) {
            // Si és un caràcter imprimible normal, el mostrem
            int simbol = node.getSimbol();
            if (simbol >= 32 && simbol <= 126) {
                text = String.valueOf((char) simbol);
            } else {
                text = String.format("%02X", simbol); // Hexadecimal per caràcters estranys
            }
        } else {
            // Si és node intern, mostrem la seva freqüència acumulada
            text = String.valueOf(node.getFrequencia());
        }

        // Calcular el centre exacte del text per estètica
        int ampladaText = g2d.getFontMetrics().stringWidth(text);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, x - (ampladaText / 2), y + 4);
    }
}
