package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 3 may 2026
 * @name Vista
 */
public class Vista extends JFrame {

    // Botons d'accions
    private JButton btnCalcular;

    // Elements de configuració
    private JSpinner spinSumaInicial;
    private JSpinner spinLimit;
    private JSpinner spinDarrerNombre;
    private JComboBox<String> comboDimensions;

    // Visualització
    private JPanel panelTeclat;
    private JLabel lblGuanyador;

    // Elements d'estat (Panell Sud)
    private JLabel lblEstat;

    // Colors i Fonts globals
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    public Vista() {
        setTitle("Pràctica 5 - Joc del 31");
        setSize(1200, 800);
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
        afegirCampConfiguracio(pnlConfig, "Darrer Nombre (0=Cap):", spinDarrerNombre = new JSpinner(new SpinnerNumberModel(0, 0, 25, 1)));

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

        // Panell superior de resultats dins del central
        JPanel pnlResultat = new JPanel(new BorderLayout());
        pnlResultat.setBackground(Color.WHITE);
        pnlResultat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitolRes = new JLabel("Resultat de la Partida", SwingConstants.CENTER);
        lblTitolRes.setFont(FONT_TITOLS);
        lblTitolRes.setForeground(Color.GRAY);

        lblGuanyador = new JLabel("Pendent de càlcul...", SwingConstants.CENTER);
        lblGuanyador.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGuanyador.setForeground(new Color(44, 62, 80));

        pnlResultat.add(lblTitolRes, BorderLayout.NORTH);
        pnlResultat.add(lblGuanyador, BorderLayout.CENTER);

        // Panell del Teclat
        panelTeclat = new JPanel();
        panelTeclat.setBackground(Color.WHITE);
        // El teclat es genera inicialment mitjançant un mètode auxiliar
        actualitzarTeclat("3x3");

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

        // Afegir tot a la finestra
        add(pnlLateral, BorderLayout.WEST);
        add(pnlCentral, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);

        // Listeners interns d'UI (que no depenen del controlador)
        comboDimensions.addActionListener(e -> actualitzarTeclat((String) comboDimensions.getSelectedItem()));
    }

    // ==========================================
    // MÈTODES AUXILIARS D'INTERFÍCIE
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
        panel.setMaximumSize(new Dimension(250, 300));
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

    private void actualitzarTeclat(String dimensions) {
        panelTeclat.removeAll();

        int w = dimensions.charAt(0) - '0';
        int h = dimensions.charAt(2) - '0';

        panelTeclat.setLayout(new GridLayout(h, w, 8, 8));

        int nombre = w * h;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                JButton btn = new JButton(String.valueOf(nombre));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
                btn.setBackground(new Color(245, 245, 245));
                btn.setFocusPainted(false);
                btn.setEnabled(false); // Són visuals
                panelTeclat.add(btn);
                nombre--;
            }
        }

        panelTeclat.revalidate();
        panelTeclat.repaint();
    }

    // ==========================================
    // GETTERS, SETTERS I LISTENERS (Pel Controlador)
    // ==========================================
    public void setControladorCalcular(ActionListener listener) {
        btnCalcular.addActionListener(listener);
    }

    // Getters per recuperar l'estat des del Controlador
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

    /**
     * Mostra qui és el guanyador a la pantalla central
     */
    public void mostrarResultat(String guanyador) {
        lblGuanyador.setText(guanyador);
        lblGuanyador.setForeground(COLOR_VERD); // Destaquem el resultat en verd
    }

    /**
     * Canvia el text del missatge d'estat a la part inferior
     */
    public void setEstat(String text, Color color) {
        lblEstat.setText(text);
        lblEstat.setForeground(color);
    }

    /**
     * Activa o desactiva components mentre es calcula (útil si l'algorisme
     * tarda)
     */
    public void setProcessant(boolean processant) {
        btnCalcular.setEnabled(!processant);
        spinSumaInicial.setEnabled(!processant);
        spinLimit.setEnabled(!processant);
        spinDarrerNombre.setEnabled(!processant);
        comboDimensions.setEnabled(!processant);

        if (processant) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
