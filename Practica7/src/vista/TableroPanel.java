package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Vista
 * Panel principal que contiene las casillas organizadas en forma de espiral (caracol).
 */
public class TableroPanel extends JPanel {
    
    private final CasillaPanel[][] matrizCasillas;

    public TableroPanel() {
        setLayout(new GridLayout(8, 8, 4, 4)); // Tablero de 8x8 con un poco de separación
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));
        
        matrizCasillas = new CasillaPanel[8][8];
        generarEspiral();
        dibujarTablero();
    }

    private void generarEspiral() {
        int val = 0;
        int top = 0, bottom = 7, left = 0, right = 7;

        // Algoritmo para llenar la matriz de manera circular (Caracol)
        while (val <= 63) {
            // De izquierda a derecha
            for (int i = left; i <= right && val <= 63; i++) {
                matrizCasillas[top][i] = new CasillaPanel(val++);
            }
            top++;
            
            // De arriba a abajo
            for (int i = top; i <= bottom && val <= 63; i++) {
                matrizCasillas[i][right] = new CasillaPanel(val++);
            }
            right--;
            
            // De derecha a izquierda
            for (int i = right; i >= left && val <= 63; i--) {
                matrizCasillas[bottom][i] = new CasillaPanel(val++);
            }
            bottom--;
            
            // De abajo a arriba
            for (int i = bottom; i >= top && val <= 63; i--) {
                matrizCasillas[i][left] = new CasillaPanel(val++);
            }
            left++;
        }
    }

    private void dibujarTablero() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (matrizCasillas[i][j] != null) add(matrizCasillas[i][j]);
            }
        }
    }

    private CasillaPanel buscarCasilla(int numero) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (matrizCasillas[i][j] != null &&
                    matrizCasillas[i][j].getNumero() == numero) {
                    return matrizCasillas[i][j];
                }
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(7));

        // 0 - 6 abajo
        for (int i = 0; i <= 6; i++)
            dibujarLado(g2, i, "ABAJO");

        // 8 - 13 izquierda
        for (int i = 8; i <= 13; i++)
            dibujarLado(g2, i, "IZQUIERDA");

        // 15 - 21 arriba
        for (int i = 15; i <= 20; i++)
            dibujarLado(g2, i, "ARRIBA");

        // 22 - 26 derecha
        for (int i = 22; i <= 26; i++)
            dibujarLado(g2, i, "DERECHA");

        // 28 - 32 abajo
        for (int i = 28; i <= 32; i++)
            dibujarLado(g2, i, "ABAJO");

        // 34 - 37 izquierda
        for (int i = 34; i <= 37; i++)
            dibujarLado(g2, i, "IZQUIERDA");

        // 39 - 42 arriba
        for (int i = 39; i <= 42; i++)
            dibujarLado(g2, i, "ARRIBA");

        // 44 - 46 derecha
        for (int i = 44; i <= 46; i++)
            dibujarLado(g2, i, "DERECHA");

        // 48 - 50 abajo
        for (int i = 48; i <= 50; i++)
            dibujarLado(g2, i, "ABAJO");

        // 52 - 53 izquierda
        for (int i = 52; i <= 53; i++)
            dibujarLado(g2, i, "IZQUIERDA");

        // 55 - 56 arriba
        for (int i = 55; i <= 56; i++)
            dibujarLado(g2, i, "ARRIBA");

        // 58 derecha
        dibujarLado(g2, 58, "DERECHA");

        // 60 abajo
        dibujarLado(g2, 60, "ABAJO");

        g2.dispose();
    }


    private void dibujarLado(Graphics2D g2, int numero, String lado) {

        CasillaPanel casilla = buscarCasilla(numero);

        if (casilla == null) return;

        Rectangle r = SwingUtilities.convertRectangle(
                casilla.getParent(),
                casilla.getBounds(),
                this);

        switch (lado) {

            case "ARRIBA":
                g2.drawLine(r.x, r.y, r.x + r.width, r.y);
                break;

            case "ABAJO":
                g2.drawLine(r.x, r.y + r.height,
                        r.x + r.width, r.y + r.height);
                break;

            case "IZQUIERDA":
                g2.drawLine(r.x, r.y,
                        r.x, r.y + r.height);
                break;

            case "DERECHA":
                g2.drawLine(r.x + r.width, r.y,
                        r.x + r.width, r.y + r.height);
                break;
        }
    }

    


   
}