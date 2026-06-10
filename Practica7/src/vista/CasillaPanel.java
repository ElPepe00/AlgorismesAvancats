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

    private static final String IMG_PATH ="images/";

    public CasillaPanel(int numero) {
        this.numero = numero;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel lblNumero = new JLabel(String.valueOf(numero), SwingConstants.CENTER);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNumero.setForeground(Color.DARK_GRAY);

        aplicarEstiloEspecial(lblNumero);

        if (getComponentCount() == 0) {
            add(lblNumero, BorderLayout.CENTER);
        }

        
    }

    private void mostrarImagen(String nombreArchivo) {

        ImageIcon icon = new ImageIcon(IMG_PATH + nombreArchivo);

        Image img = icon.getImage().getScaledInstance(
                50,
                50,
                Image.SCALE_SMOOTH
        );

        JLabel lblImagen = new JLabel(new ImageIcon(img));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);

        removeAll();
        add(lblImagen, BorderLayout.CENTER);

        JLabel lblNumero = new JLabel(String.valueOf(numero), SwingConstants.CENTER);
        lblNumero.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(lblNumero, BorderLayout.SOUTH);
    }

    private void aplicarEstiloEspecial(JLabel lblNumero) {
       if (esCasillaDeOca(numero)) {
            setBackground(new Color(255, 235, 153));
            mostrarImagen("oca.png");

        } else if (numero == 6 || numero == 12) {
            setBackground(new Color(173, 216, 230));
            mostrarImagen("puente.png");

        } else if (numero == 26 || numero == 53) {
            setBackground(new Color(221, 160, 221));
            mostrarImagen("dados.png");

        } else if (numero == 19) {
            setBackground(new Color(255, 182, 193));
            mostrarImagen("posada.png");

        } else if (numero == 31) {
            setBackground(new Color(210, 180, 140));
            mostrarImagen("pozo.png");

        } else if (numero == 42) {
            setBackground(new Color(144, 238, 144));
            mostrarImagen("laberinto.jpg");

        } else if (numero == 52) {
            setBackground(new Color(192, 192, 192));
            mostrarImagen("carcel.png");

        } else if (numero == 58) {
            setBackground(Color.BLACK);
            mostrarImagen("skullemoji.png");

        } else if (numero == 63) {
            setBackground(new Color(50, 205, 50));
            mostrarImagen("win.png");
        }
    }

    private boolean esCasillaDeOca(int i) {
        int[] ocas = {5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59};
        for (int oca : ocas) if (i == oca) return true;
        return false;
    }

    public int getNumero() {
        return numero;
    }
}