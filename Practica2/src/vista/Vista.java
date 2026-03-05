package vista;

import datos.Pieza;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Vista
 */
public class Vista extends JFrame {

    private JMenuItem itemSalir;
    private JPanel panelCentral;
    private JButton btnEjecutar;

    private JComboBox<String> cbPieza1;
    private JComboBox<String> cbPieza2;
    private JSpinner spDimension;
    private JButton btnIniciar;
    private JButton btnParar;
    private JLabel lblEstado;
    private JSlider slVelocidad;

    private Tablero panelTablero;
    //private MotorCerca filCerca;
    private int[][] tablero;

    /**
     * Constructor de la interficie
     *
     * @param titol texto que aparece en la barra de título de la ventana
     */
    public Vista(String titol) {
        super(titol);
        configurarVentana();
        inicializarComponentes();
    }

    /**
     * Configuración de las propiedades básicas de la ventana
     */
    private void configurarVentana() {
        setSize(800, 700);
        setMinimumSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Método que instancia i posiciona los componentes de la ventana
     */
    private void inicializarComponentes() {

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Configuració"));

        String[] nombrePiezas = {"Cavall", "Torre", "Òrfil", "Reina", "Cangur (Inv)", "Assassí (Inv)"};
        cbPieza1 = new JComboBox<>(nombrePiezas);
        cbPieza2 = new JComboBox<>(nombrePiezas);
        cbPieza2.setSelectedIndex(1);

        spDimension = new JSpinner(new SpinnerNumberModel(5, 3, 12, 1));
        btnIniciar = new JButton("Iniciar / Cercar");
        btnParar = new JButton("Aturar");
        btnParar.setEnabled(false);

        panelSuperior.add(new JLabel("Dimensió:"));
        panelSuperior.add(spDimension);
        panelSuperior.add(new JLabel("Peça 1 (Imp):"));
        panelSuperior.add(cbPieza1);
        panelSuperior.add(new JLabel("Peça 2 (Par):"));
        panelSuperior.add(cbPieza2);
        panelSuperior.add(btnIniciar);
        panelSuperior.add(btnParar);

        // Panel inferior para controles
        JPanel panelEstado = new JPanel(new BorderLayout());
        lblEstado = new JLabel("Preparat. Configura les peces i prem Iniciar.");
        lblEstado.setFont(new Font("Arial", Font.BOLD, 14));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        slVelocidad = new JSlider(JSlider.HORIZONTAL, 0, 500, 50);
        slVelocidad.setToolTipText("Velocitat d'animació (ms)");
        slVelocidad.setBorder(BorderFactory.createTitledBorder("Retard per pas (ms)"));

        panelEstado.add(lblEstado, BorderLayout.CENTER);
        panelEstado.add(slVelocidad, BorderLayout.EAST);

        // Panel central que sera el TABLERO
        panelTablero = new Tablero();

        add(panelSuperior, BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);
        add(panelEstado, BorderLayout.SOUTH);

        // EVENTOS
        btnIniciar.addActionListener(e -> iniciarJuego());
        btnParar.addActionListener(e -> aturarJuego());
    }

    private void iniciarJuego() {
        int dim = (int) spDimension.getValue();
        tablero = new int[dim][dim];

        // Utilitzem el mètode Factoria de la classe Peca
        Pieza p1 = Pieza.crearPeca((String) cbPieza1.getSelectedItem(), 1);
        Pieza p2 = Pieza.crearPeca((String) cbPieza2.getSelectedItem(), 2);

        panelTablero.setEstat(tablero);

        // Bloqueig de la interfície
        btnIniciar.setEnabled(false);
        cbPieza1.setEnabled(false);
        cbPieza2.setEnabled(false);
        spDimension.setEnabled(false);
        btnParar.setEnabled(true);

        lblEstado.setText("Calculant solució...");
        lblEstado.setForeground(Color.BLUE);

        // Llancem el Motor de Cerca en un Thread separat passant 'this' per poder actualitzar la UI
        filCerca = new MotorCerca(tauler, p1, p2, dimensio, slVelocitat.getValue(), this);
        filCerca.start();
    }

    private void aturarJoc() {
        if (filCerca != null && filCerca.isAlive()) {
            filCerca.aturar();
        }
    }

    /**
     * Mètode cridat des del MotorCerca per refrescar el dibuix
     */
    public void repintarTauler() {
        SwingUtilities.invokeLater(() -> panelTauler.repaint());
    }

    /**
     * Mètode cridat pel MotorCerca quan acaba o s'atura
     */
    public void fiCerca(boolean aturat, boolean trobat, long tempsTotal) {
        SwingUtilities.invokeLater(() -> {
            btnIniciar.setEnabled(true);
            cbPeca1.setEnabled(true);
            cbPeca2.setEnabled(true);
            spDimensio.setEnabled(true);
            btnAturar.setEnabled(false);

            if (aturat) {
                lblEstat.setText("Aturat per l'usuari. Temps utilitzat: " + tempsTotal + " ms.");
                lblEstat.setForeground(Color.RED);
            } else if (trobat) {
                lblEstat.setText("SOLUCIÓ TROBADA en " + tempsTotal + " ms!");
                lblEstat.setForeground(new Color(0, 150, 0));
            } else {
                lblEstat.setText("Cap solució possible trobada (" + tempsTotal + " ms).");
                lblEstat.setForeground(Color.RED);
            }
            panelTauler.repaint();
        });
    }

    // -- Getters para que el Controlador pueda acceder
    public JMenuItem getItemSortir() {
        return itemSalir;
    }

    public JButton getBtnExecutar() {
        return btnEjecutar;
    }

    public JPanel getPanellCentral() {
        return panelCentral;
    }

}
