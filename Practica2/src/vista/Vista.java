

package vista;

import datos.Tablero;
import datos.Peca;
import controlador.MotorCerca;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;
import java.util.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 6 mar 2026
 * @name Tablero
 */

/**
 * Classe principal de la Interfície d'Usuari (GUI).
 */
public class Vista extends JFrame {

    private JButton btnIniciar;
    private JButton btnAturar;
    private JSpinner spDimensio;
    private JButton btnActualitzarTauler;

    private JRadioButton rbPeca1;
    private JRadioButton rbPeca2;
    private String nomPeca1 = "Cavall"; //Peça1 per defecte
    private String nomPeca2 = "Reina";  //Peça2 per defecte

    private Map<String, JButton> botonsPeces;
    private Map<String, Image> imatgesOriginals;

    private JLabel lblEstat;
    private JSlider slVelocitat;

    private Tablero panelTauler;
    private MotorCerca filCerca;
    private int[][] tauler;

    // Colors globals
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_P1 = new Color(46, 204, 113);
    private final Color COLOR_P2 = new Color(52, 152, 219);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_VERMELL = new Color(192, 57, 43);
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);

    public Vista() {
        setTitle("Pràctica 2 - Variant del Recorregut d'Escacs");
        setSize(980, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        botonsPeces = new HashMap<>();
        imatgesOriginals = new HashMap<>();
        inicialitzarComponents();
    }

    private void inicialitzarComponents() {

        // PANELL LATERAL ESQUERRE
        JPanel pnlLateral = new JPanel();
        pnlLateral.setLayout(new BoxLayout(pnlLateral, BoxLayout.Y_AXIS));
        pnlLateral.setBackground(COLOR_FONS_MENU);
        pnlLateral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlLateral.setPreferredSize(new Dimension(320, 0));

        JLabel lblTitolMenu = new JLabel("Configuració");
        lblTitolMenu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitolMenu.setForeground(new Color(44, 62, 80));
        lblTitolMenu.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnIniciar = crearBotoAccion("Iniciar partida", COLOR_VERD);
        btnAturar = crearBotoAccion("Aturar", COLOR_VERMELL);
        btnAturar.setEnabled(false);

        // -- Mida del tauler --
        JPanel pnlMida = crearTargeta("1. Mida del tauler");
        pnlMida.setLayout(new FlowLayout(FlowLayout.CENTER));

        spDimensio = new JSpinner(new SpinnerNumberModel(8, 3, 12, 1));
        spDimensio.setFont(new Font("Segoe UI", Font.BOLD, 16));

        btnActualitzarTauler = new JButton("Aplicar");
        btnActualitzarTauler.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnActualitzarTauler.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualitzarTauler.setBackground(Color.WHITE);
        btnActualitzarTauler.setFocusPainted(false);

        pnlMida.add(new JLabel("Dimensió: "));
        pnlMida.add(spDimensio);
        pnlMida.add(btnActualitzarTauler);

        // -- Selector de Peça 1 o 2 --
        JPanel pnlRadios = crearTargeta("2. Tria la Peça");
        pnlRadios.setLayout(new GridLayout(2, 1, 5, 5));

        rbPeca1 = new JRadioButton("Peça 1: " + nomPeca1, true);
        rbPeca2 = new JRadioButton("Peça 2: " + nomPeca2);
        rbPeca1.setFont(FONT_TITOLS);
        rbPeca2.setFont(FONT_TITOLS);
        rbPeca1.setBackground(Color.WHITE);
        rbPeca2.setBackground(Color.WHITE);
        rbPeca1.setForeground(COLOR_P1);
        rbPeca2.setForeground(COLOR_P2);

        ButtonGroup bgPeces = new ButtonGroup();
        bgPeces.add(rbPeca1);
        bgPeces.add(rbPeca2);

        pnlRadios.add(rbPeca1);
        pnlRadios.add(rbPeca2);

        // -- Graella de les Peces --
        JPanel pnlGraellaContenidor = crearTargeta("3. Assigna la peça");
        JPanel pnlGraella = new JPanel(new GridLayout(3, 2, 8, 8));
        pnlGraella.setBackground(Color.WHITE);

        crearBotoPeca("Òrfil", "alfil.png", pnlGraella);
        crearBotoPeca("Cavall", "caballo.png", pnlGraella);
        crearBotoPeca("Torre", "torre.png", pnlGraella);
        crearBotoPeca("Reina", "reina.png", pnlGraella);
        crearBotoPeca("Assassí", "asesino.png", pnlGraella);
        crearBotoPeca("Cangur", "canguro.png", pnlGraella);

        pnlGraellaContenidor.add(pnlGraella);

        actualitzarVoresBotons();

        pnlLateral.add(lblTitolMenu);
        pnlLateral.add(Box.createRigidArea(new Dimension(0, 20)));
        pnlLateral.add(btnIniciar);
        pnlLateral.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlLateral.add(btnAturar);
        pnlLateral.add(Box.createRigidArea(new Dimension(0, 25)));
        pnlLateral.add(pnlMida);
        pnlLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlLateral.add(pnlRadios);
        pnlLateral.add(Box.createRigidArea(new Dimension(0, 15)));
        pnlLateral.add(pnlGraellaContenidor);

        // ==========================================
        // 2. PANELL SUD
        // ==========================================
        JPanel pnlEstat = new JPanel(new BorderLayout());
        pnlEstat.setBackground(COLOR_FONS_MENU);
        pnlEstat.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)));

        lblEstat = new JLabel("Selecciona la Peça (1 o 2) i assignali el tipus de peça. A continuació inicia la partida.");
        lblEstat.setForeground(Color.DARK_GRAY);
        lblEstat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstat.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        slVelocitat = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slVelocitat.setBackground(COLOR_FONS_MENU);
        TitledBorder voraSlider = BorderFactory.createTitledBorder("Retard de visualització (ms)");
        voraSlider.setTitleFont(new Font("Segoe UI", Font.PLAIN, 12));
        slVelocitat.setBorder(voraSlider);

        pnlEstat.add(lblEstat, BorderLayout.CENTER);
        pnlEstat.add(slVelocitat, BorderLayout.EAST);

        // ==========================================
        // 3. PANELL CENTRE
        // ==========================================
        panelTauler = new Tablero();
        panelTauler.setBackground(Color.WHITE);
        panelTauler.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(pnlLateral, BorderLayout.WEST);
        add(panelTauler, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);

        // EVENTS (Lísteners)
        btnIniciar.addActionListener(e -> iniciarJoc());
        btnAturar.addActionListener(e -> aturarJoc());

        // ESDEVENIMENT DEL NOU BOTÓ (Actualitzar tauler buit)
        btnActualitzarTauler.addActionListener(e -> {
            int novaDim = (int) spDimensio.getValue();
            // Esborrem qualsevol matriu que hi hagués de la partida anterior
            panelTauler.setEstat(null, null, null); 
            // Li diem que dibuixi el fons buit amb la nova dimensió
            panelTauler.setDimensioBuit(novaDim);
            
            lblEstat.setText("Tauler actualitzat a " + novaDim + "x" + novaDim + ".");
            lblEstat.setForeground(Color.DARK_GRAY);
        });
    }

    private JButton crearBotoAccion(String text, Color colorFons) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(colorFons);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setMaximumSize(new Dimension(250, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel crearTargeta(String titol) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        TitledBorder vora = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                titol
        );
        vora.setTitleFont(FONT_TITOLS);
        vora.setTitleColor(new Color(100, 100, 100));
        panel.setBorder(vora);
        panel.setMaximumSize(new Dimension(280, 1000));
        return panel;
    }

    private void crearBotoPeca(String nomPeca, String nomFitxer, JPanel panell) {
        JButton btn = new JButton();
        btn.setToolTipText(nomPeca);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        URL imgURL = getClass().getResource("/imagenes/" + nomFitxer);

        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image imgOriginal = icon.getImage();

            imatgesOriginals.put(nomPeca, imgOriginal);

            Image imgEscalada = imgOriginal.getScaledInstance(55, 55, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(imgEscalada));

            btn.addActionListener(e -> {
                if (rbPeca1.isSelected()) {
                    nomPeca1 = nomPeca;
                    rbPeca1.setText("Peça 1: " + nomPeca);
                } else {
                    nomPeca2 = nomPeca;
                    rbPeca2.setText("Peça 2: " + nomPeca);
                }
                actualitzarVoresBotons();
            });

            botonsPeces.put(nomPeca, btn);
            panell.add(btn);
        }
    }

    private void actualitzarVoresBotons() {
        Border voraDefecte = BorderFactory.createLineBorder(new Color(230, 230, 230), 1);
        Border voraP1 = BorderFactory.createLineBorder(COLOR_P1, 4);
        Border voraP2 = BorderFactory.createLineBorder(COLOR_P2, 4);
        Border voraDoble = BorderFactory.createCompoundBorder(voraP1, voraP2);

        for (Map.Entry<String, JButton> entry : botonsPeces.entrySet()) {
            String nom = entry.getKey();
            JButton btn = entry.getValue();

            if (nom.equals(nomPeca1) && nom.equals(nomPeca2)) {
                btn.setBorder(voraDoble);
            } else if (nom.equals(nomPeca1)) {
                btn.setBorder(voraP1);
            } else if (nom.equals(nomPeca2)) {
                btn.setBorder(voraP2);
            } else {
                btn.setBorder(voraDefecte);
            }
        }
    }

    private void iniciarJoc() {
        int dimensio = (int) spDimensio.getValue();
        tauler = new int[dimensio][dimensio];

        Peca p1 = Peca.crearPeca(nomPeca1, 1);
        Peca p2 = Peca.crearPeca(nomPeca2, 2);

        Image img1 = imatgesOriginals.get(nomPeca1);
        Image img2 = imatgesOriginals.get(nomPeca2);
        panelTauler.setEstat(tauler, img1, img2);

        btnIniciar.setEnabled(false);
        btnIniciar.setBackground(Color.GRAY);
        spDimensio.setEnabled(false);
        btnActualitzarTauler.setEnabled(false); // Bloquegem el botó d'actualitzar també
        rbPeca1.setEnabled(false);
        rbPeca2.setEnabled(false);

        btnAturar.setEnabled(true);
        btnAturar.setBackground(new Color(231, 76, 60));

        lblEstat.setText("Cercant una solució... Aquest procés pot tardar en funció del tauler i les peces.");
        lblEstat.setForeground(Color.DARK_GRAY);

        filCerca = new MotorCerca(tauler, p1, p2, dimensio, slVelocitat.getValue(), this);
        filCerca.start();
    }

    private void aturarJoc() {
        if (filCerca != null && filCerca.isAlive()) {
            filCerca.aturar();
        }
    }

    public void repintarTauler() {
        SwingUtilities.invokeLater(() -> panelTauler.repaint());
    }

    public void fiCerca(boolean aturat, boolean trobat, long tempsTotal) {
        SwingUtilities.invokeLater(() -> {
            btnIniciar.setEnabled(true);
            btnIniciar.setBackground(COLOR_VERD);
            spDimensio.setEnabled(true);
            btnActualitzarTauler.setEnabled(true); // Desbloquegem el botó
            rbPeca1.setEnabled(true);
            rbPeca2.setEnabled(true);

            btnAturar.setEnabled(false);
            btnAturar.setBackground(Color.GRAY);

            if (aturat) {
                lblEstat.setText("Cerca aturada per l'usuari. (" + tempsTotal + " ms) ");
                lblEstat.setForeground(COLOR_VERMELL);
            } else if (trobat) {
                lblEstat.setText("SOLUCIÓ TROBADA! Temps total: " + tempsTotal + " ms.");
                lblEstat.setForeground(COLOR_VERD);
            } else {
                lblEstat.setText("Sense solució possible. (" + tempsTotal + " ms)");
                lblEstat.setForeground(COLOR_VERMELL);
            }
            panelTauler.repaint();
        });
    }
}
