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
}