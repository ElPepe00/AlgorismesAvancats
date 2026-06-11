package controlador;

import modelo.Modelo;
import modelo.Estadisticas;
import modelo.Partida;
import vista.Vista;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Controlador
 */
public class Controlador {

    private final Vista vista;
    private final Modelo modelo;
    private Partida partidaInteractiva;

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicializarEventadores();
    }

    /**
     * Vincula los estímulos físicos de la interfaz gráfica con las rutinas de negocio.
     */
    private void inicializarEventadores() {
        vista.getBtnSimular().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarSimulacionAsincrona();
            }
        });

        partidaInteractiva = new Partida(vista.getNumJugadores());
        vista.actualizarPosicionesTablero(partidaInteractiva.getPosiciones());
        vista.actualizarTurnoActual("Turno: Jugador 1", 0);

        vista.getCbNumJugadores().addActionListener(e -> {
            reiniciarPartidaInteractiva();
        });

        vista.getBtnReiniciarJuego().addActionListener(e -> {
            reiniciarPartidaInteractiva();
        });

        vista.getBtnDado().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (partidaInteractiva.isPartidaTerminada()) {
                    vista.mostrarRegistroJuego("Partida terminada. Pulsa 'Reiniciar Partida' para jugar de nuevo.");
                    return;
                }

                if (vista.getCbNumJugadores().isEnabled()) {
                    vista.getCbNumJugadores().setEnabled(false);
                }

                int tirada = (int) (Math.random() * 6) + 1;
                
                // Sincronizar la animación visual con la tirada matemática
                vista.animarDado(tirada);
                
                // Bloqueamos el dado temporalmente para evitar que el usuario pulse varias veces seguidas
                vista.getBtnDado().setEnabled(false);

                // Retrasamos el movimiento lógico 650ms para que termine primero la animación visual
                Timer timer = new Timer(650, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        String resultado = partidaInteractiva.jugarTurno(tirada);
                        
                        vista.mostrarRegistroJuego(resultado);
                        vista.actualizarPosicionesTablero(partidaInteractiva.getPosiciones());

                        if (partidaInteractiva.isPartidaTerminada()) {
                            vista.actualizarTurnoActual("¡FIN!", -1);
                            vista.getCbNumJugadores().setEnabled(true);
                        } else {
                            vista.actualizarTurnoActual("Turno: Jugador " + (partidaInteractiva.getTurnoActual() + 1), partidaInteractiva.getTurnoActual());
                        }
                        vista.getBtnDado().setEnabled(true); // Rehabilitar el botón del dado
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    /**
     * Restablece el estado de la partida interactiva y la interfaz visual.
     */
    private void reiniciarPartidaInteractiva() {
        partidaInteractiva = new Partida(vista.getNumJugadores());
        vista.getCbNumJugadores().setEnabled(true);
        vista.limpiarRegistroJuego();
        vista.mostrarRegistroJuego("--- NUEVA PARTIDA (" + vista.getNumJugadores() + " Jugadores) ---");
        vista.actualizarPosicionesTablero(partidaInteractiva.getPosiciones());
        vista.actualizarTurnoActual("Turno: Jugador 1", 0);
    }

    /**
     * Ejecuta la carga de simulación en un hilo en segundo plano (Worker Thread)
     * para evitar bloquear el hilo de despacho de eventos (EDT) de Swing.
     */
    private void ejecutarSimulacionAsincrona() {
        try {
            int nPartidas = Integer.parseInt(vista.getNumPartidas());
            
            if (nPartidas <= 0) {
                vista.mostrarError("El volumen de partidas debe ser un valor entero positivo.");
                return;
            }

            vista.getBtnSimular().setEnabled(false);
            vista.mostrarResultados("Procesando carga matemática simulando " + nPartidas + " partidas...\nPor favor, espera.");

            SwingWorker<Estadisticas, Void> worker = new SwingWorker<Estadisticas, Void>() {
                private long tiempoInicio;

                @Override
                protected Estadisticas doInBackground() throws Exception {
                    tiempoInicio = System.currentTimeMillis();
                    return modelo.ejecutarSimulacionMonteCarlo(nPartidas);
                }

                @Override
                protected void done() {
                    try {
                        Estadisticas resultados = get();
                        String top5 = modelo.obtenerTop5CasillasMasVisitadas();
                        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
                        vista.mostrarResultados(resultados.generarFormatoTexto() + 
                                "\n\n- TOP 5 CASILLAS MÁS VISITADAS -\n" + top5 +
                                "\n\n(Tiempo de ejecución: " + tiempoTotal + " ms)");
                        vista.actualizarHistograma(resultados.getTurnosPorPartida());
                    } catch (Exception ex) {
                        vista.mostrarError("Error crítico durante la simulación: " + ex.getMessage());
                    } finally {
                        vista.getBtnSimular().setEnabled(true);
                    }
                }
            };

            worker.execute(); 

        } catch (NumberFormatException ex) {
            vista.mostrarError("Formato de entrada inválido. Asegúrate de introducir un número entero sin espacios ni letras.");
        }
    }
}