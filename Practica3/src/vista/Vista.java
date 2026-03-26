package vista;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 24 mar 2026
 * @name Vista
 */
public class Vista extends JFrame {

    private JButton btnGenerar;
    private JButton btnCalcular;
    private JSpinner spNumPunts;
    
    private JRadioButton rbUniforme, rbGaussiana;
    private ButtonGroup grupDistribucio;

    private JRadioButton rbN2, rbNLogN, rbLlunyana;
    private ButtonGroup grupAlgoritme;

    private JLabel lblEstat;
    private PanelPunts panelPunts;

    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);

    public Vista() {
        setTitle("Pràctica 3 - Distàncies a un núvol de punts");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        inicialitzarComponents();
    }

    private void inicialitzarComponents() {
        // ==========================================
        // 1. PANELL LATERAL ESQUERRE
        // ==========================================
        
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
        lblTitolMenu.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineat a l'esquerra

        // -- Targeta 1: Generació de Punts --
        JPanel pnlGeneracio = crearTargeta("1. Generació de Punts");
        
        JLabel lblN = new JLabel("Núm. Punts (N):");
        lblN.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        spNumPunts = new JSpinner(new SpinnerNumberModel(1000, 10, 100000, 100));
        spNumPunts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        spNumPunts.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbUniforme = new JRadioButton("Uniforme", true);
        rbGaussiana = new JRadioButton("Gaussiana");
        configurarRB(rbUniforme);
        configurarRB(rbGaussiana);
        
        grupDistribucio = new ButtonGroup();
        grupDistribucio.add(rbUniforme);
        grupDistribucio.add(rbGaussiana);

        btnGenerar = crearBotoAccion("Generar Punts", COLOR_BLAU);

        pnlGeneracio.add(lblN);
        pnlGeneracio.add(Box.createVerticalStrut(5));
        pnlGeneracio.add(spNumPunts);
        pnlGeneracio.add(Box.createVerticalStrut(10));
        pnlGeneracio.add(rbUniforme);
        pnlGeneracio.add(rbGaussiana);
        pnlGeneracio.add(Box.createVerticalStrut(15));
        pnlGeneracio.add(btnGenerar);

        // -- Targeta 2: Algoritme --
        JPanel pnlAlgoritme = crearTargeta("2. Càlcul de Distàncies");

        rbN2 = new JRadioButton("Més propera O(n²)", true);
        rbNLogN = new JRadioButton("Més propera O(n·log n)");
        rbLlunyana = new JRadioButton("Més llunyana");
        
        grupAlgoritme = new ButtonGroup();
        JRadioButton[] algos = {rbN2, rbNLogN, rbLlunyana};
        for (JRadioButton rb : algos) {
            configurarRB(rb);
            grupAlgoritme.add(rb);
            pnlAlgoritme.add(rb);
        }

        btnCalcular = crearBotoAccion("Calcular Parella", COLOR_VERD);
        btnCalcular.setEnabled(false);

        pnlAlgoritme.add(Box.createVerticalStrut(15));
        pnlAlgoritme.add(btnCalcular);

        // Afegir elements al lateral
        pnlLateral.add(lblTitolMenu);
        pnlLateral.add(Box.createVerticalStrut(20));
        pnlLateral.add(pnlGeneracio);
        pnlLateral.add(Box.createVerticalStrut(20));
        pnlLateral.add(pnlAlgoritme);
        pnlLateral.add(Box.createVerticalGlue()); // Empeny tot cap amunt

        // ==========================================
        // 2. RESTA DE PANELLS (SUD I CENTRE)
        // ==========================================
        panelPunts = new PanelPunts();
        JPanel pnlEstat = new JPanel(new BorderLayout());
        pnlEstat.setBackground(COLOR_FONS_MENU);
        pnlEstat.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)));
        lblEstat = new JLabel("Tria el nombre de punts i fes clic a 'Generar Punts'.");
        lblEstat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblEstat.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlEstat.add(lblEstat, BorderLayout.CENTER);

        add(pnlLateral, BorderLayout.WEST);
        add(panelPunts, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);

    }

    // Mètode auxiliar per estalviar codi i assegurar alineació
    private void configurarRB(JRadioButton rb) {
        rb.setBackground(Color.WHITE);
        rb.setAlignmentX(Component.LEFT_ALIGNMENT);
        rb.setFocusPainted(false);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private JButton crearBotoAccion(String text, Color colorFons) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFons);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Ocupa tota l'amplada
        return btn;
    }

    private JPanel crearTargeta(String titol) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Vertical
        panel.setBackground(Color.WHITE);
        TitledBorder vora = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                titol
        );
        vora.setTitleFont(FONT_TITOLS);
        panel.setBorder(BorderFactory.createCompoundBorder(vora, BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(300, 300));
        return panel;
    }

        public void setControladorGenerar(ActionListener listener) {
        btnGenerar.addActionListener(listener);
    }

    public void setControladorCalcular(ActionListener listener) {
        btnCalcular.addActionListener(listener);
    }
        public int getNumPunts() {
        return (int) spNumPunts.getValue();
    }

    public String getDistribucio() {
        return rbUniforme.isSelected() ? "uniforme" : "gaussiana";
    }

    public void mostrarPunts(java.util.List<modelo.Punt> punts) {
    panelPunts.setPunts(punts);
}
}