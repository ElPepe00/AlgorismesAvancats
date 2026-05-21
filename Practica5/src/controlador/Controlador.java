package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import model.*;
import vista.Vista;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 18 may 2026
 * @name Controlador
 */
public class Controlador {

    private final Vista vista;
    private int jugadorActual = 1;
    private Teclat teclat;
    private boolean ignorarEvents = false;

    public Controlador(Vista vista) {
        this.vista = vista;
        assegurarTeclat();
        
        // Listener per calcular guanyador
        this.vista.setControladorCalcular(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ignorarEvents) return;
                
                assegurarTeclat();
                int darrer = vista.getDarrerNombre();
                vista.habilitarBotonsValids(teclat.getMovimentsValids(darrer));
                vista.setEstat("Càlcul manual en procés...", java.awt.Color.GRAY);
                
                System.out.println("Càlcul llançat manualment. Suma: " + vista.getSumaInicial() + ", Límit: " + vista.getLimit() + ", Darrer: " + darrer);
                calcularGuanyador();
            }
        });
        
        // Listener per mesclar el teclat
        this.vista.setControladorMesclar(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dimensions = vista.getDimensions();
                int w = dimensions.charAt(0) - '0';
                int h = dimensions.charAt(2) - '0';
                
                teclat = new Teclat(w, h, true);
                vista.actualitzarTeclat(teclat.getGrid(), w, h, teclat.getMaxNombre());
                
                int darrer = vista.getDarrerNombre();
                vista.habilitarBotonsValids(teclat.getMovimentsValids(darrer));
                vista.setEstat("Teclat mesclat aleatòriament.", new java.awt.Color(155, 89, 182));
                
                System.out.println("S'ha mesclat el teclat aleatòriament.");
                calcularGuanyador();
            }
        });
        
        // Listener per restablir valors
        this.vista.setControladorRestablir(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ignorarEvents = true;
                
                jugadorActual = 1;
                teclat = null;
                assegurarTeclat();
                vista.setTornActual(1);
                vista.habilitarBotonsValids(teclat.getMovimentsValids(0));
                
                System.out.println("Partida restablida.");
                
                // Desactivem el flag un cop processats els esdeveniments asíncrons de Swing
                SwingUtilities.invokeLater(() -> {
                    ignorarEvents = false;
                });
            }
        });
        
        // Listener per a les dimensions del tauler (POO i MVC pur)
        this.vista.setControladorDimensions(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ignorarEvents) return;
                
                String dimensions = vista.getDimensions();
                int w = dimensions.charAt(0) - '0';
                int h = dimensions.charAt(2) - '0';
                
                teclat = new Teclat(w, h, false);
                vista.actualitzarTeclat(teclat.getGrid(), w, h, teclat.getMaxNombre());
                
                int darrer = vista.getDarrerNombre();
                vista.habilitarBotonsValids(teclat.getMovimentsValids(darrer));
                vista.setEstat("Dimensions canviades a " + dimensions, java.awt.Color.GRAY);
                
                System.out.println("Dimensions canviades a " + dimensions + ".");
                calcularGuanyador();
            }
        });
        
        // Listener per a les tecles
        this.vista.setControladorBotoTeclat(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botoTeclatClicat(e.getActionCommand());
            }
        });
    }

    // Inicialitza o assegura el teclat segons les dimensions triades
    private void assegurarTeclat() {
        String dimensions = vista.getDimensions();
        int w = dimensions.charAt(0) - '0';
        int h = dimensions.charAt(2) - '0';
        
        if (teclat == null || teclat.getAmplada() != w || teclat.getAlcada() != h) {
            teclat = new Teclat(w, h, false);
            vista.actualitzarTeclat(teclat.getGrid(), w, h, teclat.getMaxNombre());
        }
    }

    // Calcula les prediccions en un fil de fons
    private void calcularGuanyador() {
        vista.setProcessant(true);
        
        new Thread(() -> {
            try {
                int sumaInicial = vista.getSumaInicial();
                int limit = vista.getLimit();
                int darrerNombre = vista.getDarrerNombre();
                int numJugadors = vista.getNumJugadors();
                
                assegurarTeclat();
                
                Joc joc = new Joc(limit, teclat, numJugadors);
                int perdedorRelatiu = joc.calcularEstat(sumaInicial, darrerNombre);
                long totalPartides = joc.getTotalPartides(sumaInicial, darrerNombre);
                
                SwingUtilities.invokeLater(() -> {
                    if (sumaInicial >= limit) {
                        vista.mostrarResultat("Joc ja finalitzat (Límit superat)");
                    } else {
                        // Traducció de perdedor relatiu a absolut
                        int perdedorAbsolut = ((jugadorActual - 1 + perdedorRelatiu) % numJugadors) + 1;
                        
                        if (numJugadors == 2) {
                            if (perdedorAbsolut == jugadorActual) {
                                int guanyador = (jugadorActual % 2) + 1;
                                vista.mostrarResultat("Perd el Jugador " + jugadorActual + " (Guanya el Jugador " + guanyador + ")");
                            } else {
                                vista.mostrarResultat("Guanya el Jugador " + jugadorActual + " (Perd el Jugador " + perdedorAbsolut + ")");
                            }
                        } else {
                            vista.mostrarResultat("Perdrà el Jugador " + perdedorAbsolut);
                        }
                        
                        System.out.println("Suma: " + sumaInicial + ". Perdedor previst: Jugador " + perdedorAbsolut);
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

    // Gestiona la pulsació d'una tecla
    private void botoTeclatClicat(String valorBoto) {
        try {
            int valor = Integer.parseInt(valorBoto);
            int sumaActual = vista.getSumaInicial();
            int limit = vista.getLimit();
            int jugadorQueJuga = jugadorActual;
            
            int novaSuma = sumaActual + valor;
            System.out.println("Jugador " + jugadorQueJuga + " prem " + valor + ". Suma: " + sumaActual + " -> " + novaSuma + " (Límit: " + limit + ")");
            
            vista.setSumaInicial(novaSuma);
            vista.setDarrerNombre(valor);
            
            // Derrota per superar el límit
            if (novaSuma >= limit) {
                System.out.println("FI DE LA PARTIDA. Jugador " + jugadorQueJuga + " perd per superar el límit!");
                vista.mostrarResultat("Jugador " + jugadorQueJuga + " ha arribat a " + novaSuma + "! HA PERDUT.");
                vista.setEstat("Fi del joc.", java.awt.Color.RED);
                vista.habilitarBotonsValids(new java.util.ArrayList<>());
                return;
            }
            
            // Canvi de torn
            int numJugadors = vista.getNumJugadors();
            jugadorActual = (jugadorActual % numJugadors) + 1;
            vista.setTornActual(jugadorActual);
            
            assegurarTeclat();
            java.util.ArrayList<Integer> valids = teclat.getMovimentsValids(valor);
            
            // Comprovació de derrota inevitable
            boolean totesExcedeixen = true;
            for (int m : valids) {
                if (novaSuma + m < limit) {
                    totesExcedeixen = false;
                    break;
                }
            }
            
            if (totesExcedeixen) {
                System.out.println("FI DE LA PARTIDA. Jugador " + jugadorActual + " perd per derrota inevitable!");
                vista.mostrarResultat("Jugador " + jugadorActual + " no té moviments segurs! HA PERDUT.");
                vista.setEstat("Fi del joc (Derrota inevitable).", java.awt.Color.RED);
                vista.habilitarBotonsValids(new java.util.ArrayList<>());
                return;
            }
            
            vista.habilitarBotonsValids(valids);
            System.out.println("Torn del Jugador " + jugadorActual + ". Tecles vàlides: " + valids);
            
            calcularGuanyador();
            
        } catch (NumberFormatException e) {
        }
    }
}
