package vista;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name CasillaPanel
 */
public class CasillaPanel extends JPanel {
    private final int numero;
    private final List<Integer> jugadoresEnCasilla = new ArrayList<>();
    private static final String IMG_PATH = "images/";

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

    /**
     * Actualiza los jugadores en esta casilla.
     */
    public void setJugadores(List<Integer> jugadores) {
        this.jugadoresEnCasilla.clear();
        if (jugadores != null) {
            this.jugadoresEnCasilla.addAll(jugadores);
        }
        repaint();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        if (jugadoresEnCasilla.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diametro = 16;
        int padding = 4;
        int w = getWidth();
        int h = getHeight();

        Point[] esquinas = {
                new Point(padding, padding),
                new Point(w - diametro - padding, padding),
                new Point(padding, h - diametro - padding),
                new Point(w - diametro - padding, h - diametro - padding)
        };

        Color[] colores = {
                new Color(52, 152, 219),
                new Color(241, 196, 15),
                new Color(39, 174, 96),
                new Color(231, 76, 60)
        };

        for (int i = 0; i < jugadoresEnCasilla.size(); i++) {
            int jugadorIdx = jugadoresEnCasilla.get(i);
            if (i < esquinas.length && jugadorIdx >= 0 && jugadorIdx < colores.length) {
                g2.setColor(colores[jugadorIdx]);
                g2.fillOval(esquinas[i].x, esquinas[i].y, diametro, diametro);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(esquinas[i].x, esquinas[i].y, diametro, diametro);
            }
        }
        g2.dispose();
    }

    /**
     * Obtiene el número de la casilla.
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Renderiza imagen para casillas especiales.
     */
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

    /**
     * Aplica estilos visuales según el tipo de casilla.
     */
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

    /**
     * Comprueba si la casilla es una Oca.
     */
    private boolean esCasillaDeOca(int i) {
        int[] ocas = {5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59};
        for (int oca : ocas) if (i == oca) return true;
        return false;
    }
}