package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import model.*;
import vista.Vista;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 5 may 2026
 * @name Controlador
 */
public class Controlador {

    private final Vista vista;
    private int jugadorActual = 1;

    public Controlador(Vista vista) {
        this.vista = vista;
        
        // Afegim el listener al botó de la vista
        this.vista.setControladorCalcular(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si calculem manualment, no canviem el torn, només mirem l'ajuda
                calcularGuanyador();
            }
        });
        
        // Listener per al botó de restablir
        this.vista.setControladorRestablir(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jugadorActual = 1;
                // També actualitzem botons vàlids per defecte (tots habilitats)
                String dimensions = vista.getDimensions();
                int w = dimensions.charAt(0) - '0';
                int h = dimensions.charAt(2) - '0';
                Teclat teclat = new Teclat(w, h);
                vista.habilitarBotonsValids(teclat.getMovimentsValids(0));
            }
        });
        
        // Listener per als botons de la calculadora
        this.vista.setControladorBotoTeclat(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botoTeclatClicat(e.getActionCommand());
            }
        });
    }

    private void calcularGuanyador() {
        // Bloquegem la UI mentre calculem
        vista.setProcessant(true);
        
        // Ho executem en un fil a part per no bloquejar l'EDT si el càlcul fos lent
        new Thread(() -> {
            try {
                // Llegim dades de la vista
                int sumaInicial = vista.getSumaInicial();
                int limit = vista.getLimit();
                int darrerNombre = vista.getDarrerNombre();
                String dimensions = vista.getDimensions();
                
                int w = dimensions.charAt(0) - '0';
                int h = dimensions.charAt(2) - '0';
                
                // Instanciem model
                Teclat teclat = new Teclat(w, h);
                Joc joc = new Joc(limit, teclat);
                
                // Executem DP
                boolean guanyaActual = joc.calcularEstat(sumaInicial, darrerNombre);
                long totalPartides = joc.getTotalPartides(sumaInicial, darrerNombre);
                
                // Actualitzem la UI
                SwingUtilities.invokeLater(() -> {
                    if (sumaInicial >= limit) {
                        vista.mostrarResultat("Joc ja finalitzat (Límit superat)");
                    } else {
                        if (guanyaActual) {
                            vista.mostrarResultat("Guanya el Jugador actual");
                        } else {
                            vista.mostrarResultat("Perd el Jugador actual");
                        }
                    }
                    
                    vista.setEstat("Càlcul completat. Total partides possibles: " + totalPartides, new java.awt.Color(39, 174, 96));
                    vista.setProcessant(false);
                });
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    vista.setEstat("Error: " + ex.getMessage(), java.awt.Color.RED);
                    vista.setProcessant(false);
                });
            }
        }).start();
    }

    private void botoTeclatClicat(String valorBoto) {
        try {
            int valor = Integer.parseInt(valorBoto);
            int sumaActual = vista.getSumaInicial();
            int limit = vista.getLimit();
            
            // Actualitzem la suma
            int novaSuma = sumaActual + valor;
            vista.setSumaInicial(novaSuma);
            vista.setDarrerNombre(valor);
            
            // Si la nova suma ha arribat al límit, s'acaba el joc (el que ha clicat perd)
            if (novaSuma >= limit) {
                vista.mostrarResultat("Has arribat a " + novaSuma + "! HAS PERDUT.");
                vista.setEstat("Fi del joc.", java.awt.Color.RED);
                vista.habilitarBotonsValids(new java.util.ArrayList<>()); // Cap botó habilitat
                return;
            }
            
            // Canvi de torn
            int numJugadors = vista.getNumJugadors();
            jugadorActual = (jugadorActual % numJugadors) + 1;
            vista.setTornActual(jugadorActual);
            
            // Habilitem els botons legals per al següent torn
            String dimensions = vista.getDimensions();
            int w = dimensions.charAt(0) - '0';
            int h = dimensions.charAt(2) - '0';
            Teclat teclat = new Teclat(w, h);
            java.util.ArrayList<Integer> valids = teclat.getMovimentsValids(valor);
            vista.habilitarBotonsValids(valids);
            
            // Calculem la predicció immediatament
            calcularGuanyador();
            
        } catch (NumberFormatException e) {
            // Error al parsejar
        }
    }
}
