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
    private JButton btnCalcular;
    private JButton btnRestablir;

    // Elements de configuració
    private JSpinner spinSumaInicial;
    private JSpinner spinLimit;
    private JSpinner spinDarrerNombre;
    private JComboBox<String> comboDimensions;

    // Visualització
    private JPanel panelTeclat;
    private JButton[] botonsTeclat; // Guardem els botons per poder-los pintar
    private JLabel lblGuanyador;

    // Elements d'estat (Panell Sud)
    private JLabel lblEstat;

    // Colors i Fonts globals
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Color COLOR_RESSALTAT = new Color(174, 214, 241); // Blau clar pel darrer nombre
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    public Vista() {
        setTitle("Pràctica 5 - Joc del 31");
        setSize(1100, 750);
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

        panelTeclat = new JPanel();
        panelTeclat.setBackground(Color.WHITE);
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

        add(pnlLateral, BorderLayout.WEST);
        add(pnlCentral, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);

        // ==========================================
        // LISTENERS INTERNS
        // ==========================================
        // Escoltador per canvi de dimensions
        comboDimensions.addActionListener(e -> actualitzarTeclat((String) comboDimensions.getSelectedItem()));

        // Escoltador per ressaltar el número al teclat (Punt 2)
        spinDarrerNombre.addChangeListener(e -> ressaltarNombreSeleccionat());

        // Escoltador per restablir valors (Punt 5)
        btnRestablir.addActionListener(e -> restablirValors());
    }

    private void actualitzarTeclat(String dimensions) {
        panelTeclat.removeAll();

        int w = dimensions.charAt(0) - '0';
        int h = dimensions.charAt(2) - '0';
        int maxNombre = w * h;

        // Punt 1: Ajustem el límit del darrer nombre dinàmicament
        SpinnerNumberModel model = (SpinnerNumberModel) spinDarrerNombre.getModel();
        model.setMaximum(maxNombre);
        if ((int) spinDarrerNombre.getValue() > maxNombre) {
            spinDarrerNombre.setValue(0); // Reiniciem si s'escapa del rang
        }

        panelTeclat.setLayout(new GridLayout(h, w, 8, 8));
        botonsTeclat = new JButton[maxNombre];

        int nombre = maxNombre;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                JButton btn = new JButton(String.valueOf(nombre));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 24));
                btn.setBackground(new Color(245, 245, 245));
                btn.setFocusPainted(false);
                btn.setEnabled(false);

                // Guardem el botó a l'array (índex basat en el valor del número)
                botonsTeclat[nombre - 1] = btn;
                panelTeclat.add(btn);
                nombre--;
            }
        }

        ressaltarNombreSeleccionat(); // Pintem si ja hi ha un valor seleccionat
        panelTeclat.revalidate();
        panelTeclat.repaint();
    }

    private void ressaltarNombreSeleccionat() {
        int seleccionat = (int) spinDarrerNombre.getValue();

        // Primer netegem tots els botons
        for (JButton btn : botonsTeclat) {
            if (btn != null) {
                btn.setBackground(new Color(245, 245, 245));
            }
        }

        // Si el valor és > 0, pintem el botó corresponent (Punt 2)
        if (seleccionat > 0 && seleccionat <= botonsTeclat.length) {
            botonsTeclat[seleccionat - 1].setBackground(COLOR_RESSALTAT);
        }
    }

    private void restablirValors() {
        spinSumaInicial.setValue(0);
        spinLimit.setValue(31);
        comboDimensions.setSelectedItem("3x3");
        spinDarrerNombre.setValue(0);
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

    public void mostrarResultat(String guanyador) {
        lblGuanyador.setText(guanyador);
        lblGuanyador.setForeground(COLOR_VERD);
    }

    public void setEstat(String text, Color color) {
        lblEstat.setText(text);
        lblEstat.setForeground(color);
    }

    public void setProcessant(boolean processant) {
        btnCalcular.setEnabled(!processant);
        btnRestablir.setEnabled(!processant);
        spinSumaInicial.setEnabled(!processant);
        spinLimit.setEnabled(!processant);
        spinDarrerNombre.setEnabled(!processant);
        comboDimensions.setEnabled(!processant);
        setCursor(processant ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
