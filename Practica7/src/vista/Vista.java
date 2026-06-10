
package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Vista
 */
public class Vista extends JFrame {

    // Colores y Fuentes globales (Estilo heredado del Joc del 31)
    private final Color COLOR_FONS_MENU = new Color(240, 244, 248);
    private final Color COLOR_VERD = new Color(39, 174, 96);
    private final Color COLOR_BLAU = new Color(52, 152, 219);
    private final Font FONT_TITOLS = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);

    private JTextField txtNumPartidas;
    private JButton btnSimular;
    private JTextArea txtResultados;
    private JButton btnDado;
    private TableroPanel panelTablero;
    private JComboBox<Integer> cbNumJugadores;
    private JButton btnReiniciarJuego;
    private JTabbedPane tabbedPane;
    private JTextArea txtRegistroJuego;
    private JLabel lblTurnoActual;

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

    // ==========================================
    // MÉTODOS PÚBLICOS (Controlador / MVC)
    // ==========================================

    public JButton getBtnSimular() {
        return btnSimular;
    }

    public String getNumPartidas() {
        return txtNumPartidas.getText();
    }

    public JButton getBtnDado() {
        return btnDado;
    }

    public Integer getNumJugadores() {
        return (Integer) cbNumJugadores.getSelectedItem();
    }

    public JComboBox<Integer> getCbNumJugadores() {
        return cbNumJugadores;
    }

    public JButton getBtnReiniciarJuego() {
        return btnReiniciarJuego;
    }

    /**
     * Muestra los resultados de la simulación en el panel central.
     */
    public void mostrarResultados(String texto) {
        txtResultados.setText(texto);
    }

    /**
     * Muestra una alerta de error al usuario.
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Añade un evento al registro de la partida interactiva.
     */
    public void mostrarRegistroJuego(String texto) {
        txtRegistroJuego.append("\n" + texto);
        txtRegistroJuego.setCaretPosition(txtRegistroJuego.getDocument().getLength());
    }

    /**
     * Transmite al tablero interactivo el vector con las posiciones actualizadas.
     */
    public void actualizarPosicionesTablero(int[] posiciones) {
        panelTablero.actualizarPosiciones(posiciones);
    }

    /**
     * Limpia el registro de eventos de la partida.
     */
    public void limpiarRegistroJuego() {
        txtRegistroJuego.setText("");
    }

    /**
     * Actualiza el indicador del turno actual y le asigna el color corporativo del jugador.
     */
    public void actualizarTurnoActual(String texto, int jugadorIndex) {
        lblTurnoActual.setText(texto);
        
        Color[] coloresTexto = {
                new Color(52, 152, 219),  // Azul (J1)
                new Color(230, 126, 34),  // Naranja oscuro (J2) en vez de amarillo para que se lea en fondo blanco
                new Color(39, 174, 96),   // Verde (J3)
                new Color(231, 76, 60)    // Rojo (J4)
        };
        
        if (jugadorIndex >= 0 && jugadorIndex < coloresTexto.length) {
            lblTurnoActual.setForeground(coloresTexto[jugadorIndex]);
        } else {
            lblTurnoActual.setForeground(Color.GRAY);
        }
    }

    /**
     * Ejecuta una animación visual del dado y se asegura de que la última imagen
     * coincida con la cara real obtenida en la tirada lógica.
     */
    public void animarDado(int caraFinal) {
        new Thread(() -> {
            try {
                Random rand = new Random();
                for (int i = 0; i < 10; i++) {
                    int cara = rand.nextInt(6) + 1;
                    SwingUtilities.invokeLater(() -> btnDado.setIcon(new ImageIcon("images/cara" + cara + ".png")));
                    Thread.sleep(60); // Pequeña pausa para simular el giro rápido
                }
                // Establecer la cara definitiva calculada por el Controlador
                SwingUtilities.invokeLater(() -> btnDado.setIcon(new ImageIcon("images/cara" + caraFinal + ".png")));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE INICIALIZACIÓN Y DISEÑO
    // ==========================================

    /**
     * Inicializa y organiza todos los componentes visuales de la ventana.
     */
    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // PESTAÑA 1: SIMULACIÓN ESTADÍSTICA
        JPanel panelSimulacion = new JPanel(new BorderLayout(15, 15));
        panelSimulacion.setBackground(Color.WHITE);

        // Panel lateral izquierdo: Configuración
        JPanel pnlLateralSim = new JPanel();
        pnlLateralSim.setLayout(new BoxLayout(pnlLateralSim, BoxLayout.Y_AXIS));
        pnlLateralSim.setBackground(COLOR_FONS_MENU);
        pnlLateralSim.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        pnlLateralSim.setPreferredSize(new Dimension(320, 0));

        JLabel lblTitolMenuSim = new JLabel("Panel de Control");
        lblTitolMenuSim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitolMenuSim.setForeground(new Color(44, 62, 80));
        lblTitolMenuSim.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel targetaConfigSim = crearTargeta("1. Parámetros");
        txtNumPartidas = new JTextField("10000");
        afegirCampConfiguracio(targetaConfigSim, "Partidas a simular:", txtNumPartidas);

        JPanel targetaAccionsSim = crearTargeta("2. Ejecución");
        btnSimular = crearBotoAccion("Ejecutar Simulación", COLOR_BLAU);
        targetaAccionsSim.add(btnSimular);

        pnlLateralSim.add(lblTitolMenuSim);
        pnlLateralSim.add(Box.createVerticalStrut(20));
        pnlLateralSim.add(targetaConfigSim);
        pnlLateralSim.add(Box.createVerticalStrut(15));
        pnlLateralSim.add(targetaAccionsSim);
        pnlLateralSim.add(Box.createVerticalGlue());

        // Panel central: Resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(Color.WHITE);
        TitledBorder voraResultat = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "Estadísticas Observadas");
        voraResultat.setTitleFont(FONT_TITOLS);
        panelResultados.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 0, 20, 20), // Mantiene la caja alejada del borde derecho
                BorderFactory.createCompoundBorder(voraResultat, BorderFactory.createEmptyBorder(10, 15, 10, 15))));

        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        // Diseño innovador: estilo consola oscura (Tema Dracula) para resaltar los
        // datos
        txtResultados.setBackground(new Color(40, 42, 54));
        txtResultados.setForeground(new Color(80, 250, 123)); // Texto Verde Brillante
        txtResultados.setFont(new Font("Consolas", Font.BOLD, 16));
        txtResultados.setMargin(new Insets(20, 20, 20, 20));
        txtResultados.setCaretColor(Color.WHITE);
        txtResultados.setText(
                "Esperando ejecución...\n\n(El formato de salida seguirá exactamente lo especificado en el PDF)");

        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        scrollResultados.setBorder(BorderFactory.createEmptyBorder()); // Eliminar borde interno
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        panelSimulacion.add(pnlLateralSim, BorderLayout.WEST);
        panelSimulacion.add(panelResultados, BorderLayout.CENTER);

        // PESTAÑA 2: JUEGO INTERACTIVO
        JPanel panelJuego = new JPanel(new BorderLayout(15, 15));
        panelJuego.setBackground(Color.WHITE);

        // Panel lateral del juego (Controles y dado)
        JPanel pnlLateralJuego = new JPanel();
        pnlLateralJuego.setLayout(new BoxLayout(pnlLateralJuego, BoxLayout.Y_AXIS));
        pnlLateralJuego.setBackground(COLOR_FONS_MENU);
        pnlLateralJuego.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        pnlLateralJuego.setPreferredSize(new Dimension(320, 0));

        JLabel lblTitolMenuJuego = new JLabel("Ajustes del Juego");
        lblTitolMenuJuego.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitolMenuJuego.setForeground(new Color(44, 62, 80));
        lblTitolMenuJuego.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel targetaConfigJuego = crearTargeta("1. Configuración");
        cbNumJugadores = new JComboBox<>(new Integer[] { 1, 2, 3, 4 });
        afegirCampConfiguracio(targetaConfigJuego, "Número de jugadores:", cbNumJugadores);

        targetaConfigJuego.add(Box.createVerticalStrut(10));
        btnReiniciarJuego = crearBotoAccion("Reiniciar Partida", new Color(149, 165, 166)); // Gris plomo
        targetaConfigJuego.add(btnReiniciarJuego);

        JPanel targetaDado = crearTargeta("2. Lanzar Dado");
        btnDado = new JButton();
        btnDado.setIcon(new ImageIcon("images/cara1.png")); // Asegúrate de tener dado1.png a dado6.png
        btnDado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDado.setContentAreaFilled(false);
        btnDado.setBorderPainted(false);
        btnDado.setFocusPainted(false);
        btnDado.setToolTipText("Haz clic para lanzar el dado");

        // Contenedor para que el botón no se estire ocupando todo el layout
        JPanel contenedorBtnDado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedorBtnDado.setBackground(Color.WHITE);
        contenedorBtnDado.add(btnDado);
        targetaDado.add(contenedorBtnDado);

        pnlLateralJuego.add(lblTitolMenuJuego);
        pnlLateralJuego.add(Box.createVerticalStrut(20));
        pnlLateralJuego.add(targetaConfigJuego);
        pnlLateralJuego.add(Box.createVerticalStrut(15));
        pnlLateralJuego.add(targetaDado);
        pnlLateralJuego.add(Box.createVerticalGlue());

        // Aplicamos POO instanciando un panel propio que genera el circuito en espiral
        // (caracol)
        panelTablero = new TableroPanel();

        // Panel Inferior: Información y Registro de Eventos
        JPanel panelInferiorJuego = new JPanel(new BorderLayout(15, 0));
        panelInferiorJuego.setBackground(COLOR_FONS_MENU);
        panelInferiorJuego.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        lblTurnoActual = new JLabel("Turno: Jugador 1", SwingConstants.CENTER);
        lblTurnoActual.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTurnoActual.setForeground(COLOR_BLAU);
        lblTurnoActual.setPreferredSize(new Dimension(150, 0));

        txtRegistroJuego = new JTextArea(3, 50);
        txtRegistroJuego.setEditable(false);
        txtRegistroJuego.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtRegistroJuego.setBackground(Color.WHITE);
        txtRegistroJuego.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        txtRegistroJuego.setText(
                "¡Bienvenido al Juego de la Oca!\nSelecciona el número de jugadores y lanza el dado para empezar.");
        JScrollPane scrollRegistro = new JScrollPane(txtRegistroJuego);

        panelInferiorJuego.add(lblTurnoActual, BorderLayout.WEST);
        panelInferiorJuego.add(scrollRegistro, BorderLayout.CENTER);

        panelJuego.add(pnlLateralJuego, BorderLayout.WEST);
        panelJuego.add(panelTablero, BorderLayout.CENTER);
        panelJuego.add(panelInferiorJuego, BorderLayout.SOUTH);

        // Añadir pestañas al contenedor principal
        tabbedPane.addTab("Simulación Estadística", panelSimulacion);
        tabbedPane.addTab("Juego Interactivo", panelJuego);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE DISEÑO (Estilo Joc del 31)
    // ==========================================
    private void afegirCampConfiguracio(JPanel panel, String etiqueta, JComponent component) {
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
            }
        };
        row.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(FONT_NORMAL);
        component.setFont(FONT_NORMAL);
        row.add(lbl, BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        panel.add(row);
    }

    private JPanel crearTargeta(String titol) {
        JPanel panel = new JPanel() {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        TitledBorder vora = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), titol);
        vora.setTitleFont(FONT_TITOLS);
        panel.setBorder(BorderFactory.createCompoundBorder(vora, BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JButton crearBotoAccion(String text, Color colorFons) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(colorFons);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // Desactiva el borde nativo del SO para que se vea el fondo
        btn.setOpaque(true); // Fuerza a Java a pintar el color de fondo
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

}
