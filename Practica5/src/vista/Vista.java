package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 4 may 2026
 * @name Vista
 */
public class Vista extends JFrame {

    // Botons d'accions
    private JButton btnRestablir;
    private JButton btnMesclar;
    private JButton btnCalcular;
    private ActionListener listenerTeclat;

    // Elements de configuració
    private JSpinner spinSumaInicial;
    private JSpinner spinLimit;
    private JSpinner spinDarrerNombre;
    private JComboBox<String> comboDimensions;

    // Elements pels jugadors
    private JSpinner spinNumJugadors;
    private JLabel lblTornActual;

    // Visualització
    private JPanel panelTeclat;
    private JButton[] botonsTeclat;
    private JLabel lblGuanyador;

    // Elements d'estat (Panell Sud)
    private JLabel lblEstat;

    // Colors i Fonts globals
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Color COLOR_RESSALTAT = new Color(174, 214, 241); // Blau clar pel darrer nombre
    private final Color COLOR_TECLA_VALIDA = new Color(171, 235, 198); // Verd clar pels moviments vàlids
    private final Color COLOR_TECLA_INVALIDA = new Color(149, 165, 166); // Gris fosc pels moviments invàlids
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    private java.util.List<Integer> darrersValids = null;

    public Vista() {
        setTitle("Pràctica 5 - Joc del 31");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        inicialitzarComponents();
    }

    private void inicialitzarComponents() {
        // ==========================================
        // 1. PANELL LATERAL ESQUERRE (Configuració)
        // ==========================================
        JPanel pnlLateral = new JPanel();
        pnlLateral.setLayout(new BoxLayout(pnlLateral, BoxLayout.Y_AXIS));
        pnlLateral.setBackground(COLOR_FONS_MENU);
        pnlLateral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        pnlLateral.setPreferredSize(new Dimension(320, 0));

        JLabel lblTitolMenu = new JLabel("Panell de Control");
        lblTitolMenu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitolMenu.setForeground(new Color(44, 62, 80));
        lblTitolMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -- Targeta 1: Configuració Numèrica --
        JPanel pnlConfig = crearTargeta("1. Estat Inicial");

        afegirCampConfiguracio(pnlConfig, "Suma Inicial:", spinSumaInicial = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1)));
        pnlConfig.add(Box.createVerticalStrut(10));
        afegirCampConfiguracio(pnlConfig, "Límit (Perdre):", spinLimit = new JSpinner(new SpinnerNumberModel(31, 10, 100, 1)));
        pnlConfig.add(Box.createVerticalStrut(10));

        // El límit del darrer nombre s'ajustarà dinàmicament segons el tauler
        spinDarrerNombre = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
        afegirCampConfiguracio(pnlConfig, "Darrer Nombre (0=Cap):", spinDarrerNombre);
        pnlConfig.add(Box.createVerticalStrut(10));

        // Nombre de jugadors de la partida
        spinNumJugadors = new JSpinner(new SpinnerNumberModel(2, 2, 4, 1)); // Per defecte 2, màxim 4 (per exemple)
        afegirCampConfiguracio(pnlConfig, "Núm. Jugadors:", spinNumJugadors);

        // -- Targeta 2: Dimensions Teclat --
        JPanel pnlDimensions = crearTargeta("2. Tauler");
        JPanel rowDim = new JPanel(new BorderLayout());
        rowDim.setBackground(Color.WHITE);
        JLabel lblDim = new JLabel("Dimensions:");
        lblDim.setFont(FONT_NORMAL);
        String[] opcions = {"2x2", "3x3", "4x4", "5x5"};
        comboDimensions = new JComboBox<>(opcions);
        comboDimensions.setSelectedItem("3x3");
        comboDimensions.setFont(FONT_NORMAL);
        rowDim.add(lblDim, BorderLayout.WEST);
        rowDim.add(comboDimensions, BorderLayout.EAST);
        pnlDimensions.add(rowDim);

        // -- Targeta 3: Accions --
        JPanel pnlAccions = crearTargeta("3. Execució");
        btnCalcular = crearBotoAccion("Calcular Guanyador", COLOR_BLAU);
        pnlAccions.add(btnCalcular);
        pnlAccions.add(Box.createVerticalStrut(10));
        btnMesclar = crearBotoAccion("Mesclar Teclat (Aleatori)", new Color(155, 89, 182));
        pnlAccions.add(btnMesclar);
        pnlAccions.add(Box.createVerticalStrut(10));
        btnRestablir = crearBotoAccion("Restablir Valors", Color.GRAY);
        pnlAccions.add(btnRestablir);

        // Muntar el lateral
        pnlLateral.add(lblTitolMenu);
        pnlLateral.add(Box.createVerticalStrut(20));
        pnlLateral.add(pnlConfig);
        pnlLateral.add(Box.createVerticalStrut(15));
        pnlLateral.add(pnlDimensions);
        pnlLateral.add(Box.createVerticalStrut(15));
        pnlLateral.add(pnlAccions);
        pnlLateral.add(Box.createVerticalGlue());

        // ==========================================
        // 2. PANELL CENTRAL (Teclat i Resultats)
        // ==========================================
        JPanel pnlCentral = new JPanel(new BorderLayout(20, 20));
        pnlCentral.setBackground(Color.WHITE);
        pnlCentral.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // MODIFICACIÓ: Panell de resultats amb l'estil "Targeta"
        JPanel pnlResultat = new JPanel(new GridLayout(2, 1, 10, 10)); // Dues files, espaiades
        pnlResultat.setBackground(Color.WHITE);

        // Creem la mateixa vora que als panells laterals
        TitledBorder voraResultat = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Control de Torns i Resultat"
        );
        voraResultat.setTitleFont(FONT_TITOLS);
        pnlResultat.setBorder(BorderFactory.createCompoundBorder(voraResultat, BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        lblTornActual = new JLabel("Torn: Jugador 1", SwingConstants.CENTER);
        lblTornActual.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTornActual.setForeground(COLOR_BLAU);

        lblGuanyador = new JLabel("Pendent de càlcul...", SwingConstants.CENTER);
        lblGuanyador.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblGuanyador.setForeground(new Color(44, 62, 80));

        // Els afegim al GridLayout
        pnlResultat.add(lblTornActual);
        pnlResultat.add(lblGuanyador);

        // Panell del teclat
        panelTeclat = new JPanel();
        panelTeclat.setBackground(Color.WHITE);

        pnlCentral.add(pnlResultat, BorderLayout.NORTH);
        pnlCentral.add(panelTeclat, BorderLayout.CENTER);

        // ==========================================
        // 3. PANELL SUD (Estat)
        // ==========================================
        JPanel pnlEstat = new JPanel(new BorderLayout(15, 0));
        pnlEstat.setBackground(COLOR_FONS_MENU);
        pnlEstat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        lblEstat = new JLabel("Esperant configuració...");
        lblEstat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstat.setForeground(Color.GRAY);
        pnlEstat.add(lblEstat, BorderLayout.WEST);

        add(pnlLateral, BorderLayout.WEST);
        add(pnlCentral, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);

        // ==========================================
        // LISTENERS INTERNS
        // ==========================================
        btnRestablir.addActionListener(e -> restablirValors());
    }

    public void actualitzarTeclat(int[][] grid, int w, int h, int maxNombre) {
        panelTeclat.removeAll();

        SpinnerNumberModel model = (SpinnerNumberModel) spinDarrerNombre.getModel();
        model.setMaximum(maxNombre);
        if ((int) spinDarrerNombre.getValue() > maxNombre) {
            spinDarrerNombre.setValue(0);
        }

        panelTeclat.setLayout(new GridLayout(h, w, 8, 8));
        botonsTeclat = new JButton[maxNombre];

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int val = grid[i][j];
                JButton btn = new JButton(String.valueOf(val));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
                btn.setBackground(new Color(245, 245, 245));
                btn.setFocusPainted(false);
                btn.setEnabled(true);
                btn.setActionCommand(String.valueOf(val));
                if (listenerTeclat != null) {
                    btn.addActionListener(listenerTeclat);
                }
                botonsTeclat[val - 1] = btn;
                panelTeclat.add(btn);
            }
        }

        actualitzarColorsTeclat();
        panelTeclat.revalidate();
        panelTeclat.repaint();
    }

    private void actualitzarColorsTeclat() {
        if (botonsTeclat == null) return;
        
        int seleccionat = (int) spinDarrerNombre.getValue();

        for (int i = 0; i < botonsTeclat.length; i++) {
            JButton btn = botonsTeclat[i];
            if (btn != null) {
                int val = i + 1;
                if (val == seleccionat) {
                    btn.setBackground(COLOR_RESSALTAT);
                    btn.setEnabled(false); // El darrer premut no es pot tornar a premer en el mateix torn
                } else if (darrersValids == null || darrersValids.contains(val)) {
                    btn.setBackground(COLOR_TECLA_VALIDA);
                    btn.setEnabled(true);
                } else {
                    btn.setBackground(COLOR_TECLA_INVALIDA);
                    btn.setEnabled(false);
                }
            }
        }
    }

    private void restablirValors() {
        spinSumaInicial.setValue(0);
        spinLimit.setValue(31);
        comboDimensions.setSelectedItem("3x3");
        spinDarrerNombre.setValue(0);
        spinNumJugadors.setValue(2);
        lblTornActual.setText("Torn: Jugador 1");
        lblGuanyador.setText("Pendent de càlcul...");
        lblGuanyador.setForeground(new Color(44, 62, 80));
        setEstat("Configuració restablida.", Color.GRAY);
    }

    // ==========================================
    // MÈTODES AUXILIARS DE DISSENY
    // ==========================================
    private void afegirCampConfiguracio(JPanel panel, String etiqueta, JSpinner spinner) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FONT_NORMAL);
        spinner.setFont(FONT_NORMAL);
        spinner.setPreferredSize(new Dimension(60, 25));
        row.add(lbl, BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        panel.add(row);
    }

    private JPanel crearTargeta(String titol) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        TitledBorder vora = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), titol
        );
        vora.setTitleFont(FONT_TITOLS);
        panel.setBorder(BorderFactory.createCompoundBorder(vora, BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 300));
        return panel;
    }

    private JButton crearBotoAccion(String text, Color colorFons) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFons);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    // ==========================================
    // GETTERS, SETTERS I LISTENERS (Pel Controlador)
    // ==========================================
    public void setControladorCalcular(ActionListener listener) {
        btnCalcular.addActionListener(listener);
    }

    public void setControladorRestablir(ActionListener listener) {
        btnRestablir.addActionListener(listener);
    }

    public void setControladorMesclar(ActionListener listener) {
        btnMesclar.addActionListener(listener);
    }

    public void setControladorDimensions(ActionListener listener) {
        comboDimensions.addActionListener(listener);
    }

    public void setControladorBotoTeclat(ActionListener listener) {
        this.listenerTeclat = listener;
        if (botonsTeclat != null) {
            for (JButton btn : botonsTeclat) {
                if (btn != null) {
                    // Evitem afegir-ho dues vegades si ja ho estava
                    btn.removeActionListener(listener);
                    btn.addActionListener(listener);
                }
            }
        }
    }

    public void habilitarBotonsValids(java.util.List<Integer> valids) {
        this.darrersValids = valids;
        actualitzarColorsTeclat();
    }
    
    public void setHabilitarMesclar(boolean habilitar) {
        btnMesclar.setEnabled(habilitar);
    }

    public void setHabilitarConfiguracio(boolean habilitar) {
        spinSumaInicial.setEnabled(habilitar);
        spinLimit.setEnabled(habilitar);
        spinDarrerNombre.setEnabled(habilitar);
        comboDimensions.setEnabled(habilitar);
        spinNumJugadors.setEnabled(habilitar);
    }

    public void setSumaInicial(int suma) {
        spinSumaInicial.setValue(suma);
    }

    public void setDarrerNombre(int darrer) {
        spinDarrerNombre.setValue(darrer);
    }

    public int getSumaInicial() {
        return (int) spinSumaInicial.getValue();
    }

    public int getLimit() {
        return (int) spinLimit.getValue();
    }

    public int getDarrerNombre() {
        return (int) spinDarrerNombre.getValue();
    }

    public String getDimensions() {
        return (String) comboDimensions.getSelectedItem();
    }

    public int getNumJugadors() {
        return (int) spinNumJugadors.getValue();
    }

    public void mostrarResultat(String guanyador) {
        lblGuanyador.setText(guanyador);
        lblGuanyador.setForeground(COLOR_VERD);
    }

    public void setTornActual(int numJugador) {
        lblTornActual.setText("Torn: Jugador " + numJugador);
    }

    public void setEstat(String text, Color color) {
        lblEstat.setText(text);
        lblEstat.setForeground(color);
    }

    public void setProcessant(boolean processant) {
        btnCalcular.setEnabled(!processant);
        btnRestablir.setEnabled(!processant);
        setCursor(processant ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
