
package datos;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 6 mar 2026
 * @name Tablero
 */

/**
 * Classe responsable de dibuixar el tauler d'escacs.
 * Rep directament els objectes Image.
 */
public class Tablero extends JPanel {
    
    private int[][] dadesTauler;
    private Image imatgeP1;
    private Image imatgeP2;
    private int dimensioBuit = 8; // Dimensió per defecte abans d'iniciar

    /**
     * Actualitza l'estat del tauler i rep les imatges exactes a dibuixar.
     * @param tauler matriu del tauler
     * @param img1 imatge jugador 1
     * @param img2 imatge jugador 2
     */
    public void setEstat(int[][] tauler, Image img1, Image img2) {
        this.dadesTauler = tauler;
        if (tauler != null) {
            this.dimensioBuit = tauler.length; // Actualitzem la dimensió si canvia
        }
        this.imatgeP1 = img1;
        this.imatgeP2 = img2;
        repaint(); 
    }

    /**
     * Permet actualitzar la dimensió de la graella buida des de la Vista 
     * abans de començar la partida.
     */
    public void setDimensioBuit(int dim) {
        this.dimensioBuit = dim;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Si no hi ha tauler iniciat, agafem la dimensió per defecte per pintar el fons
        int dim = (dadesTauler != null) ? dadesTauler.length : dimensioBuit;
        
        int casellaAmplada = getWidth() / dim;
        int casellaAlcada = getHeight() / dim;

        for (int f = 0; f < dim; f++) {
            for (int c = 0; c < dim; c++) {
                
                // 1. Dibuixar el fons de la casella (Blanc i Negre)
                if ((f + c) % 2 == 0) {
                    g.setColor(Color.WHITE); 
                } else {
                    g.setColor(Color.BLACK); 
                }
                g.fillRect(c * casellaAmplada, f * casellaAlcada, casellaAmplada, casellaAlcada);
                
                g.setColor(Color.DARK_GRAY);
                g.drawRect(c * casellaAmplada, f * casellaAlcada, casellaAmplada, casellaAlcada);

                // 2. Només dibuixem peces i números si la partida ha començat
                if (dadesTauler != null) {
                    int passa = dadesTauler[f][c];
                    if (passa > 0) {
                        // Tria l'objecte imatge en funció de si la passa és parell o imparell
                        Image imgActual = (passa % 2 != 0) ? imatgeP1 : imatgeP2;
                        g.drawImage(imgActual, c * casellaAmplada + 2, f * casellaAlcada + 2, casellaAmplada - 4, casellaAlcada - 4, this);

                        // Dibuixar el número de la passa
                        if (passa % 2 != 0) {
                            g.setColor(Color.GREEN);
                        } else {
                             g.setColor(Color.BLUE);
                        }
                        
                        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        g.drawString(String.valueOf(passa), (c * casellaAmplada) + 4, (f * casellaAlcada) + 14);
                    }
                }
            }
        }
    }
}