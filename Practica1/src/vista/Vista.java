

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
    private JPanel panelCentral;
    private JButton btnEjecutar;

    /**
     * Constructor de la interficie
     * @param titol texto que aparece en la barra de título de la ventana
     */
    public Vista(String titol) {
        super(titol);
        configurarVentana();
        crearMenu();
        inicializarComponentes();
    }

    /**
     * Configuración de las propiedades básicas de la ventana
     */
    private void configurarVentana() {
        setSize(850, 650);
        setMinimumSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * Crea la barra de menú superior y sus opciones
     */
    private void crearMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuVentana = new JMenu("Ventana");
        itemSalir = new JMenuItem("Salir");
        menuVentana.add(itemSalir);

        menuBar.add(menuVentana);
        setJMenuBar(menuBar);
    }

    /**
     * Método que instancia i posiciona los componentes de la ventana
     */
    private void inicializarComponentes() {
        // Panel central para dibujos, graficas u otros
        panelCentral = new JPanel();
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        
        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior para controles
        JPanel panellSud = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnEjecutar = new JButton("Executar");
        panellSud.add(btnEjecutar);
        
        add(panellSud, BorderLayout.SOUTH);
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