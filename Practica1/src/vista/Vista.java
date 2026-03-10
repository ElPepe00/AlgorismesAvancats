package vista;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Vista
 */
public class Vista extends JFrame {

    private JMenuItem itemSalir, itemIniciar, itemParar;
    private JRadioButtonMenuItem optN, optNlogN, optN2, optN3, optTots;
    private JLabel lblTiempoN, lblTiempoNlogN, lblTiempoN2, lblTiempoN3;
    private JButton btnIniciar, btnParar;
    private PanelGrafico panelGrafico;

    /**
     * Constructor de la interficie
     *
     * @param titulo nombre de la ventana
     */
    public Vista(String titulo) {
        super(titulo);
        configVentana();
        crearMenus();
        inicialitzarComponents();
    }

    /**
     * Método que configura la ventana
     */
    private void configVentana() {
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Método que crea toda la parte de barra de menú
     */
    private void crearMenus() {
        JMenuBar mb = new JMenuBar();

        // Menú Ventana
        JMenu menuArxiu = new JMenu("Finestra");
        itemIniciar = new JMenuItem("Iniciar Simulació");
        menuArxiu.add(itemIniciar);
        itemParar = new JMenuItem("Aturar Simulació");
        menuArxiu.add(itemParar);
        itemSalir = new JMenuItem("Sortir");
        menuArxiu.add(itemSalir);

        // Menú Complejidad (RadioButtons para seleccionar solo uno)
        JMenu menuComp = new JMenu("Algorismes");
        ButtonGroup grup = new ButtonGroup();

        optN = new JRadioButtonMenuItem("O(n)");
        optNlogN = new JRadioButtonMenuItem("O(n log n)");
        optN2 = new JRadioButtonMenuItem("O(n^2)");
        optN3 = new JRadioButtonMenuItem("O(n^3)");
        optTots = new JRadioButtonMenuItem("Tots", true); // Seleccionado por defecto

        JRadioButtonMenuItem[] opts = {optN, optNlogN, optN2, optN3, optTots};
        for (JRadioButtonMenuItem o : opts) {
            grup.add(o);
            menuComp.add(o);
        }

        mb.add(menuArxiu);
        mb.add(menuComp);
        setJMenuBar(mb);
    }

    /**
     * Método que inicializa los componentes principales de la pantalla
     */
    private void inicialitzarComponents() {
        //panel grafico
        panelGrafico = new PanelGrafico();
        add(panelGrafico, BorderLayout.CENTER);

        //panel lateral derecho con etiquetas de tiempo de cada algoritmo
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBorder(BorderFactory.createTitledBorder("Temps real (ns)"));
        pnlInfo.setPreferredSize(new Dimension(200, 0));

        lblTiempoN = new JLabel("O(n):       -");
        lblTiempoNlogN = new JLabel("O(n log n): -");
        lblTiempoN2 = new JLabel("O(n²):      -");
        lblTiempoN3 = new JLabel("O(n³):      -");

        // Colorear igual que las curvas
        lblTiempoN.setForeground(Color.BLUE);
        lblTiempoNlogN.setForeground(new Color(0, 150, 0));
        lblTiempoN2.setForeground(Color.ORANGE.darker());
        lblTiempoN3.setForeground(Color.RED);

        pnlInfo.add(Box.createVerticalStrut(20));
        pnlInfo.add(lblTiempoN);
        pnlInfo.add(Box.createVerticalStrut(10));
        pnlInfo.add(lblTiempoNlogN);
        pnlInfo.add(Box.createVerticalStrut(10));
        pnlInfo.add(lblTiempoN2);
        pnlInfo.add(Box.createVerticalStrut(10));
        pnlInfo.add(lblTiempoN3);

        add(pnlInfo, BorderLayout.EAST);
        //panel boton iniciar simulacion
        JPanel pnlSud = new JPanel();
        btnIniciar = new JButton("Iniciar Simulació");
        pnlSud.add(btnIniciar);
        btnParar = new JButton("Aturar Simulació");
        pnlSud.add(btnParar);
        add(pnlSud, BorderLayout.SOUTH);
    }

    /**
     * Retorna el nombre del algoritmo seleccionado en el menú
     */
    public String getSeleccion() {
        if (optN.isSelected()) {
            return "O(n)";
        }
        if (optNlogN.isSelected()) {
            return "O(n log n)";
        }
        if (optN2.isSelected()) {
            return "O(n^2)";
        }
        if (optN3.isSelected()) {
            return "O(n^3)";
        }
        return "Tots";
    }

    /**
     * Método que actualiza las etiquetas de los tiempos
     */
    public void actualizarTiempos(long tN, long tNlogN, long tN2, long tN3) {
        lblTiempoN.setText("O(n):       " + (tN < 0 ? "-" : String.format("%,d ns", tN)));
        lblTiempoNlogN.setText("O(nlogn): " + (tNlogN < 0 ? "-" : String.format("%,d ns", tNlogN)));
        lblTiempoN2.setText("O(n^2):      " + (tN2 < 0 ? "-" : String.format("%,d ns", tN2)));
        lblTiempoN3.setText("O(n^3):      " + (tN3 < 0 ? "-" : String.format("%,d ns", tN3)));
    }

    // Getters para el controlador
    public JMenuItem getItemSortir() {
        return itemSalir;
    }

    public JButton getBtnIniciar() {
        return btnIniciar;
    }

    public JButton getBtnParar() {
        return btnParar;
    }

    public PanelGrafico getPanelGrafica() {
        return panelGrafico;
    }

    public JMenuItem getitemIniciar() {
        return itemIniciar;
    }

    public JMenuItem getitemParar() {
        return itemParar;
    }

    public JLabel getLblTiempoN() {
        return lblTiempoN;
    }

    public JLabel getLblTiempoNlogN() {
        return lblTiempoNlogN;
    }

    public JLabel getLblTiempoN2() {
        return lblTiempoN2;
    }

    public JLabel getLblTiempoN3() {
        return lblTiempoN3;
    }

}
