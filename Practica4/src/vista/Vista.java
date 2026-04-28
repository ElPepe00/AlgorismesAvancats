package vista;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import modelo.Node;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 16 abr 2026
 * @name Vista
 */
public class Vista extends JFrame {

    // Botons d'accions principals
    private JButton btnCarregar;
    private JButton btnComprimir;
    private JButton btnDescomprimir;
    private JButton btnGuardar;
    private JButton btnAturar;

    // Elements d'informació del fitxer
    private JLabel lblFitxerSeleccionat;
    private File fitxerActual;

    // Configuració (Opcional avaluada positivament)
    private JRadioButton rbBinaryHeap, rbFibonacci, rbLlistaOrd, rbLlistaDico;
    private ButtonGroup grupEstructures;

    // Elements d'estat i progrés (Panell Sud)
    private JLabel lblEstat;
    private JProgressBar pbProgres;
    private JLabel lblTempsRestant;

    // Estadístiques (Panell Central)
    private JLabel lblPercentatgeComp;
    private JLabel lblTempsExecucio;
    private JLabel lblLongitudMitjana;

    // Visualització
    private PanelArbreHuffman panelArbre; // Aquí dibuixarem l'arbre
    private JTable taulaFrequencies;
    private DefaultTableModel modelTaula;

    // Colors i Fonts globals (Mantenint l'estil de les P2 i P3)
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Color COLOR_VERMELL = new Color(192, 57, 43);
    private final Color COLOR_TARONJA = new Color(230, 126, 34);
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);


    private boolean usarFibonacci = false;

    public Vista() {
        setTitle("Pràctica 4 - Compressor d'arxius basat en Huffman");
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
        pnlLateral.setPreferredSize(new Dimension(240, 0));

        JLabel lblTitolMenu = new JLabel("Panell de Control");
        lblTitolMenu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitolMenu.setForeground(new Color(44, 62, 80));
        lblTitolMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        // -- Targeta 1: Fitxer --
        JPanel pnlFitxer = crearTargeta("1. Gestió de Fitxers");
        btnCarregar = crearBotoAccion("Carregar Arxiu", COLOR_BLAU);
        lblFitxerSeleccionat = new JLabel("Cap arxiu seleccionat.");
        lblFitxerSeleccionat.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFitxerSeleccionat.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlFitxer.add(btnCarregar);
        pnlFitxer.add(Box.createVerticalStrut(10));
        pnlFitxer.add(lblFitxerSeleccionat);

        // -- Targeta 2: Estructura de Dades (Opcional) --
        JPanel pnlEstructura = crearTargeta("2. Cua de Prioritats");

        rbBinaryHeap = new JRadioButton("Binary Heap", true);
        rbFibonacci = new JRadioButton("Fibonacci Heap");
        rbLlistaOrd = new JRadioButton("Llista Ordenada");
        rbLlistaDico = new JRadioButton("Llista Dicotòmica");

        grupEstructures = new ButtonGroup();
        JRadioButton[] rbArray = {rbBinaryHeap, rbFibonacci, rbLlistaOrd, rbLlistaDico};
        for (JRadioButton rb : rbArray) {
            configurarRB(rb);
            grupEstructures.add(rb);
            pnlEstructura.add(rb);
        }

        // -- Targeta 3: Accions --
        JPanel pnlAccions = crearTargeta("3. Execució");
        btnComprimir = crearBotoAccion("Comprimir", COLOR_VERD);
        btnDescomprimir = crearBotoAccion("Descomprimir", COLOR_TARONJA);
        btnGuardar = crearBotoAccion("Guardar Com...", COLOR_BLAU);
        btnAturar = crearBotoAccion("Aturar", COLOR_VERMELL);
        btnComprimir.setEnabled(false);
        btnDescomprimir.setEnabled(false);
        btnGuardar.setEnabled(false);
        btnAturar.setEnabled(false);

        pnlAccions.add(btnComprimir);
        pnlAccions.add(Box.createVerticalStrut(10));
        pnlAccions.add(btnDescomprimir);
        pnlAccions.add(Box.createVerticalStrut(10));
        pnlAccions.add(btnGuardar);
        pnlAccions.add(Box.createVerticalStrut(10));
        pnlAccions.add(btnAturar);

        // Muntar el lateral
        pnlLateral.add(lblTitolMenu);
        pnlLateral.add(Box.createVerticalStrut(20));
        pnlLateral.add(pnlFitxer);
        pnlLateral.add(Box.createVerticalStrut(15));
        pnlLateral.add(pnlEstructura);
        pnlLateral.add(Box.createVerticalStrut(15));
        pnlLateral.add(pnlAccions);
        pnlLateral.add(Box.createVerticalGlue());

        // ==========================================
        // 2. PANELL CENTRAL (Visualització i Stats)
        // ==========================================
        JPanel pnlCentral = new JPanel(new BorderLayout());
        pnlCentral.setBackground(Color.WHITE);

        // 2.1 Arbre de Huffman (Embolicat en un JScrollPane)
        panelArbre = new PanelArbreHuffman();
        JScrollPane scrollArbre = new JScrollPane(panelArbre);

        // Fem que l'scroll es mogui de forma suau (més ràpid que per defecte)
        scrollArbre.getVerticalScrollBar().setUnitIncrement(16);
        scrollArbre.getHorizontalScrollBar().setUnitIncrement(16);

        // Llevem la vora per estètica
        scrollArbre.setBorder(null);

        // 2.2 Estadístiques i Taula
        JPanel pnlDades = new JPanel(new BorderLayout(10, 10));
        pnlDades.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlDades.setBackground(Color.WHITE);
        pnlDades.setPreferredSize(new Dimension(350, 0));

        JPanel pnlStats = crearTargeta("Estadístiques");
        lblPercentatgeComp = new JLabel("Taxa de compressió: - %");
        lblTempsExecucio = new JLabel("Temps d'execució: - ms");
        lblLongitudMitjana = new JLabel("Longitud mitjana: - bits/símbol");
        pnlStats.add(lblPercentatgeComp);
        pnlStats.add(Box.createVerticalStrut(5));
        pnlStats.add(lblTempsExecucio);
        pnlStats.add(Box.createVerticalStrut(5));
        pnlStats.add(lblLongitudMitjana);

        // Taula per mostrar Símbol | Freqüència | Codi
        String[] columnes = {"Símbol", "Freqüència", "Codi Huffman"};
        modelTaula = new DefaultTableModel(columnes, 0);
        taulaFrequencies = new JTable(modelTaula);
        JScrollPane scrollTaula = new JScrollPane(taulaFrequencies);
        scrollTaula.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Diccionari / Entropia"));

        pnlDades.add(pnlStats, BorderLayout.NORTH);
        pnlDades.add(scrollTaula, BorderLayout.CENTER);

        // JSplitPane per permetre redimensionar l'arbre vs taula
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollArbre, pnlDades);
        splitPane.setResizeWeight(0.7); // 70% espai per l'arbre
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(650);

        pnlCentral.add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 3. PANELL SUD (Estat i Progrés)
        // ==========================================
        JPanel pnlEstat = new JPanel(new BorderLayout(15, 0));
        pnlEstat.setBackground(COLOR_FONS_MENU);
        pnlEstat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        lblEstat = new JLabel("Esperant fitxer...");
        lblEstat.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel pnlProgres = new JPanel(new BorderLayout(10, 0));
        pnlProgres.setBackground(COLOR_FONS_MENU);
        pbProgres = new JProgressBar(0, 100);
        pbProgres.setStringPainted(true);
        pbProgres.setForeground(COLOR_VERD);
        lblTempsRestant = new JLabel("Temps restant: --:--");
        lblTempsRestant.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        pnlProgres.add(pbProgres, BorderLayout.CENTER);
        pnlProgres.add(lblTempsRestant, BorderLayout.EAST);
        pnlProgres.setPreferredSize(new Dimension(400, 25));

        pnlEstat.add(lblEstat, BorderLayout.WEST);
        pnlEstat.add(pnlProgres, BorderLayout.EAST);

        // Afegir tot a la finestra
        add(pnlLateral, BorderLayout.WEST);
        add(pnlCentral, BorderLayout.CENTER);
        add(pnlEstat, BorderLayout.SOUTH);
    }

    // ==========================================
    // MÈTODES AUXILIARS (Reutilitzats P2/P3)
    // ==========================================
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
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
        panel.setMaximumSize(new Dimension(230, 300));
        return panel;
    }

    // ==========================================
    // GETTERS, SETTERS I LISTENERS (Pel Controlador)
    // ==========================================
    public void setControladorCarregar(ActionListener listener) {
        btnCarregar.addActionListener(listener);
    }

    public void setControladorComprimir(ActionListener listener) {
        btnComprimir.addActionListener(listener);
    }

    public void setControladorDescomprimir(ActionListener listener) {
        btnDescomprimir.addActionListener(listener);
    }

    public void setControladorGuardar(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void setControladorAturar(ActionListener listener) {
        btnAturar.addActionListener(listener);
    }

    public void setFitxerActual(File fitxer) {
        this.fitxerActual = fitxer;
        if (fitxer != null) {
            String midaFormatada = formatarMidaArxiu(fitxer.length());

            // Utilitzem HTML per permetre el salt de línia (<br/>)
            // També podem aprofitar per posar el pes en negreta o un color més suau
            lblFitxerSeleccionat.setText("<html>" + fitxer.getName() + "<br/><font color='gray'>Pes: " + midaFormatada + "</font></html>");

            btnComprimir.setEnabled(true);
            btnDescomprimir.setEnabled(true);

            // Opcional: Si el text és molt alt, potser cal revalidar el contenidor
            this.revalidate();
            this.repaint();
        }
    }

    // Afegeix aquest mètode auxiliar just a sota per formatar els bytes:
    private String formatarMidaArxiu(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public void actualitzarProgres(int percentatge, String tempsRestant) {
        pbProgres.setValue(percentatge);
        lblTempsRestant.setText("Temps restant: " + tempsRestant);
    }

    public void mostrarEstadistiques(double taxa, long tempsMs, double longMitjana) {
        lblPercentatgeComp.setText(String.format("Taxa de compressió: %.2f %%", taxa));
        lblTempsExecucio.setText("Temps d'execució: " + tempsMs + " ms");
        lblLongitudMitjana.setText(String.format("Longitud mitjana: %.2f bits/símbol", longMitjana));
    }

    public void afegirFilaTaula(String simbol, int frequencia, String codi) {
        modelTaula.addRow(new Object[]{simbol, frequencia, codi});
    }

    public void netejarTaula() {
        modelTaula.setRowCount(0);
    }

    public void setEstat(String text, Color color) {
        lblEstat.setText(text);
        lblEstat.setForeground(color);
    }

    public void habilitarGuardar(boolean habilitat) {
        btnGuardar.setEnabled(habilitat);
    }

    /**
     * Bloqueja o desbloqueja la interfície durant un procés llarg.
     *
     * @param processant true si s'està comprimint/descomprimint, false si està
     * lliure.
     */
    public void setProcessant(boolean processant) {
        btnCarregar.setEnabled(!processant);
        btnComprimir.setEnabled(!processant && fitxerActual != null);
        btnDescomprimir.setEnabled(!processant && fitxerActual != null);

        // El botó Aturar NOMÉS s'activa mentre estem treballant
        btnAturar.setEnabled(processant);

        // El botó Guardar s'activa quan NO estem processant (com a "Guardar com...")
        btnGuardar.setEnabled(!processant && fitxerActual != null);

        if (processant) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Passa l'arrel de l'arbre generat al panell gràfic perquè el dibuixi.
     */
    public void mostrarArbreHuffman(Node arrel) {
        if (panelArbre != null) {
            panelArbre.setArrelArbre(arrel);
        }
    }

    public boolean isFibonacciSeleccionat() {
        return rbFibonacci.isSelected();
    }

    public boolean isBinarySeleccionat() {
        return rbBinaryHeap.isSelected();
    }

    
}
