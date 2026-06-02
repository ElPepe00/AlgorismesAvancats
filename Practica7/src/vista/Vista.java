

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

    public Vista() {
        // Configuración básica de la ventana
        setTitle("Simulador Monte Carlo - Juego de la Oca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800); // Ajustado a medidas ideales para albergar el tablero caracol
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // Intenta usar el diseño (Look and Feel) moderno del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se ignora y usa el L&F por defecto de Java
        }

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- PANEL LATERAL (Configuración y Resultados) ---
        JPanel panelLateral = new JPanel();
        panelLateral.setLayout(new BorderLayout(10, 15));
        panelLateral.setBorder(new EmptyBorder(15, 15, 15, 0));
        panelLateral.setPreferredSize(new Dimension(280, 0));

        // Zona superior del panel lateral: Configuración
        JPanel panelNorte = new JPanel(new BorderLayout(5, 10));
        panelNorte.setBorder(BorderFactory.createTitledBorder("Configuración"));
        
        JPanel panelConfig = new JPanel(new GridLayout(3, 1, 5, 5));
        
        panelConfig.add(new JLabel("Número de partidas a simular (N):"));
        txtNumPartidas = new JTextField("10000");
        txtNumPartidas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panelConfig.add(txtNumPartidas);
        
        btnSimular = new JButton("Ejecutar Simulación");
        btnSimular.setBackground(new Color(70, 130, 180)); // Azul acero
        btnSimular.setForeground(Color.WHITE);
        btnSimular.setFocusPainted(false);
        btnSimular.setFont(new Font("SansSerif", Font.BOLD, 14));
        panelConfig.add(btnSimular);

        // Subpanel para el dado dentro de la configuración
        JPanel panelDado = new JPanel(new FlowLayout(FlowLayout.CENTER));
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
                        SwingUtilities.invokeLater(() -> btnDado.setIcon(new ImageIcon("images/dado" + cara + ".png")));
                        Thread.sleep(60); // Pequeña pausa para simular el giro rápido
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
        
        panelDado.add(btnDado);
        panelNorte.add(panelConfig, BorderLayout.NORTH);
        panelNorte.add(panelDado, BorderLayout.CENTER);

        // Zona inferior del panel lateral: Resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Estadísticas Observadas"));
        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResultados.setText("Esperando ejecución...");
        
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        panelLateral.add(panelNorte, BorderLayout.NORTH);
        panelLateral.add(panelResultados, BorderLayout.CENTER);

        // --- TABLERO (Centro) ---
        // Aplicamos POO instanciando un panel propio que genera el circuito en espiral (caracol)
        panelTablero = new TableroPanel();

        // Añadir paneles a la ventana principal
        add(panelLateral, BorderLayout.WEST);
        add(panelTablero, BorderLayout.CENTER);
    }

    // --- Métodos de acceso para el Controlador (MVC) ---

    public JButton getBtnSimular() { return btnSimular; }
    public String getNumPartidas() { return txtNumPartidas.getText(); }
    public void mostrarResultados(String texto) { txtResultados.setText(texto); }
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error en la entrada", JOptionPane.ERROR_MESSAGE);
    }
}
