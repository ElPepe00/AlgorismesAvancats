package vista;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name CasillaPanel
 */
public class CasillaPanel extends JPanel {
    
    private final int numero;

    public CasillaPanel(int numero) {
        this.numero = numero;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel lblNumero = new JLabel(String.valueOf(numero), SwingConstants.CENTER);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNumero.setForeground(Color.DARK_GRAY);

        aplicarEstiloEspecial(lblNumero);

        add(lblNumero, BorderLayout.CENTER);

        
    }

    private void aplicarEstiloEspecial(JLabel lblNumero) {
        if (esCasillaDeOca(numero)) {
            setBackground(new Color(255, 235, 153)); // Amarillo pastel para ocas
        } else if (numero == 6 || numero == 12) {
            setBackground(new Color(173, 216, 230)); // Puente
        } else if (numero == 26 || numero == 53) {
            setBackground(new Color(221, 160, 221)); // Dados
        } else if (numero == 19) {
            setBackground(new Color(255, 182, 193)); // Posada
        } else if (numero == 31) {
            setBackground(new Color(210, 180, 140)); // Pozo
        } else if (numero == 42) {
            setBackground(new Color(144, 238, 144)); // Laberinto
        } else if (numero == 52) {
            setBackground(new Color(192, 192, 192)); // Cárcel
        } else if (numero == 58) {
            setBackground(Color.BLACK); lblNumero.setForeground(Color.WHITE); // Muerte
        } else if (numero == 63) {
            setBackground(new Color(50, 205, 50)); lblNumero.setForeground(Color.WHITE); // Meta Final
        }
    }

    private boolean esCasillaDeOca(int i) {
        int[] ocas = {5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59};
        for (int oca : ocas) if (i == oca) return true;
        return false;
    }
}