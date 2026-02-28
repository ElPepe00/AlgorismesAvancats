package vista;

import javax.swing.*;
import java.awt.*;
import controlador.Controlador;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 19 feb 2026
 * @name Vista
 */
public class Vista extends JFrame {

    private PanelGrafico panelGrafico;
    private Controlador controlador;

    private JMenuItem itemSalir, itemIniciar, itemParar;
    private JRadioButtonMenuItem optN, optNlogN, optN2, optN3, optTots;
    private JButton btnIniciar, btnParar;

    /**
     * Constructor de la interficie
     *
     * @param titulo nombre de la ventana
     */
    public Vista(String titulo, Controlador c) {
        super(titulo);
        this.controlador = c;
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
        
        itemIniciar.addActionListener(e -> controlador.notificar("iniciar"));
        itemParar.addActionListener(e -> controlador.notificar("parar"));

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
        panelGrafico = new PanelGrafico(controlador);
        add(panelGrafico, BorderLayout.CENTER);

        //panel boton iniciar simulacion
        JPanel pnlSud = new JPanel();
        btnIniciar = new JButton("Iniciar Simulació");
        pnlSud.add(btnIniciar);
        btnParar = new JButton("Aturar Simulació");
        pnlSud.add(btnParar);
        add(pnlSud, BorderLayout.SOUTH);
        
        btnIniciar.addActionListener(e -> controlador.notificar("iniciar"));
        btnParar.addActionListener(e -> controlador.notificar("parar"));
    }

    public void pintar() {
        panelGrafico.repaint();
    }
}
