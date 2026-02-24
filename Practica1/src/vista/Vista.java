

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

    private JMenuItem itemSalir;
    private JRadioButtonMenuItem optN, optNlogN, optN2, optN3, optTots;
    private JButton btnIniciar;
    private PanelGrafico panelGrafico;

    /**
     * Constructor de la interficie
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

        //panel boton iniciar simulacion
        JPanel pnlSud = new JPanel();
        btnIniciar = new JButton("Iniciar Simulació");
        pnlSud.add(btnIniciar);
        add(pnlSud, BorderLayout.SOUTH);
    }
    
    /**
     * Retorna el nombre del algoritmo seleccionado en el menú
     */
    public String getSeleccio() {
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

    // Getters para el controlador
    public JMenuItem getItemSortir() {
        return itemSalir;
    }

    public JButton getBtnIniciar() {
        return btnIniciar;
    }

    public PanelGrafico getPanelGrafica() {
        return panelGrafico;
    }
}
