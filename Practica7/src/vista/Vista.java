

package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Vista
 */
public class Vista extends JFrame {

    private JTextField txtNumPartidas;
    private JButton btnSimular;
    private JTextArea txtResultados;
    private JButton btnDado;
    private TableroPanel panelTablero;
    private JComboBox<Integer> cbNumJugadores;
    private JTabbedPane tabbedPane;

    public Vista() {
        // Configuración básica de la ventana
        setTitle("Simulador Monte Carlo - Juego de la Oca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800); // Ajustado a medidas ideales para albergar el tablero caracol
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Intenta usar el diseño (Look and Feel) moderno del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se ignora y usa el L&F por defecto de Java
        }

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // =====================================================================
        // PESTAÑA 1: SIMULACIÓN ESTADÍSTICA (Requisito estricto del PDF)
        // =====================================================================
        JPanel panelSimulacion = new JPanel(new BorderLayout(15, 15));
        panelSimulacion.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior: Configuración
        JPanel panelConfiguracion = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelConfiguracion.setBorder(BorderFactory.createTitledBorder("Configuración de Simulación"));
        
        panelConfiguracion.add(new JLabel("Número de partidas a simular (N):"));
        txtNumPartidas = new JTextField("10000", 10);
        txtNumPartidas.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelConfiguracion.add(txtNumPartidas);
        
        btnSimular = new JButton("Ejecutar Simulación");
        btnSimular.setBackground(new Color(70, 130, 180)); // Azul acero
        btnSimular.setForeground(Color.WHITE);
        btnSimular.setFocusPainted(false);
        btnSimular.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelConfiguracion.add(btnSimular);

        // Panel central: Resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Estadísticas Observadas"));
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 15));
        txtResultados.setText("Esperando ejecución...\n\n(El formato de salida seguirá exactamente lo especificado en el PDF)");
        txtResultados.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        panelSimulacion.add(panelConfiguracion, BorderLayout.NORTH);
        panelSimulacion.add(panelResultados, BorderLayout.CENTER);

        // =====================================================================
        // PESTAÑA 2: JUEGO INTERACTIVO (Ampliación: 1 a 4 jugadores + Tablero)
        // =====================================================================
        JPanel panelJuego = new JPanel(new BorderLayout(15, 15));
        panelJuego.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel lateral del juego (Controles y dado)
        JPanel panelLateralJuego = new JPanel();
        panelLateralJuego.setLayout(new BorderLayout(10, 15));
        panelLateralJuego.setPreferredSize(new Dimension(220, 0));

        JPanel panelControlesJuego = new JPanel(new GridLayout(3, 1, 5, 5));
        panelControlesJuego.setBorder(BorderFactory.createTitledBorder("Ajustes del Juego"));
        panelControlesJuego.add(new JLabel("Número de jugadores:"));
        cbNumJugadores = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        panelControlesJuego.add(cbNumJugadores);

        JPanel panelDado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelDado.setBorder(BorderFactory.createTitledBorder("Lanzar Dado"));
        btnDado = new JButton();
        btnDado.setIcon(new ImageIcon("images/dado1.png")); // Asegúrate de tener dado1.png a dado6.png
        btnDado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDado.setContentAreaFilled(false);
        btnDado.setBorderPainted(false);
        btnDado.setFocusPainted(false);
        btnDado.setToolTipText("Haz clic para lanzar el dado");
        
        // Animación simple de giro del dado al hacer clic
        btnDado.addActionListener(e -> {
            new Thread(() -> {
                try {
                    Random rand = new Random();
                    for (int i = 0; i < 10; i++) {
                        int cara = rand.nextInt(6) + 1;
                        SwingUtilities.invokeLater(() -> btnDado.setIcon(new ImageIcon("images/cara" + cara + ".png")));
                        Thread.sleep(60); // Pequeña pausa para simular el giro rápido
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
        
        panelDado.add(btnDado);
        
        panelLateralJuego.add(panelControlesJuego, BorderLayout.NORTH);
        panelLateralJuego.add(panelDado, BorderLayout.CENTER);

        // Aplicamos POO instanciando un panel propio que genera el circuito en espiral (caracol)
        panelTablero = new TableroPanel();

        panelJuego.add(panelLateralJuego, BorderLayout.WEST);
        panelJuego.add(panelTablero, BorderLayout.CENTER);

        // Añadir pestañas al contenedor principal
        tabbedPane.addTab("Simulación Estadística", panelSimulacion);
        tabbedPane.addTab("Juego Interactivo (Tablero)", panelJuego);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- Métodos de acceso para el Controlador (MVC) ---

    public JButton getBtnSimular() { return btnSimular; }
    public String getNumPartidas() { return txtNumPartidas.getText(); }
    public void mostrarResultados(String texto) { txtResultados.setText(texto); }
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error en la entrada", JOptionPane.ERROR_MESSAGE);
    }
    public JButton getBtnDado() { return btnDado; }
    public Integer getNumJugadores() { return (Integer) cbNumJugadores.getSelectedItem(); }
}
