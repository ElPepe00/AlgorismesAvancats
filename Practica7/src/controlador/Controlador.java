package controlador;

import modelo.Modelo;
import modelo.Estadisticas;
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

    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        inicializarEventadores();
    }

    /**
     * Vincula los estímulos físicos de la interfaz gráfica con las rutinas de negocio.
     */
    private void inicializarEventadores() {
        // Escuchador para el botón de simulación de Monte Carlo
        vista.getBtnSimular().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarSimulacionAsincrona();
            }
        });

        // Escuchador para el dado del juego interactivo (Ampliación)
        vista.getBtnDado().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí se integraría la lógica para avanzar las fichas visuales en el TableroPanel
                // delegando el cálculo de la posición exacta y reglas al Modelo.
                System.out.println("Lanzamiento de dado interactivo registrado para " + 
                                   vista.getNumJugadores() + " jugador(es).");
            }
        });
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

            // Deshabilitar el botón temporalmente para evitar sobrecarga de clics
            vista.getBtnSimular().setEnabled(false);
            vista.mostrarResultados("Procesando carga matemática simulando " + nPartidas + " partidas...\nPor favor, espera.");

            // SwingWorker <TipoResultadoFinal, TipoProgresoIntermedio>
            SwingWorker<Estadisticas, Void> worker = new SwingWorker<Estadisticas, Void>() {
                @Override
                protected Estadisticas doInBackground() throws Exception {
                    // Delegamos el esfuerzo intenso al motor estocástico
                    return modelo.ejecutarSimulacionMonteCarlo(nPartidas);
                }

                @Override
                protected void done() {
                    try {
                        // Recuperamos los datos de rendimiento estadístico
                        Estadisticas resultados = get();
                        vista.mostrarResultados(resultados.generarFormatoTexto());
                    } catch (Exception ex) {
                        vista.mostrarError("Error crítico durante la simulación: " + ex.getMessage());
                    } finally {
                        // Restauramos la interfaz a su estado base
                        vista.getBtnSimular().setEnabled(true);
                    }
                }
            };

            worker.execute(); // Inicia el entrenamiento en segundo plano

        } catch (NumberFormatException ex) {
            vista.mostrarError("Formato de entrada inválido. Asegúrate de introducir un número entero sin espacios ni letras.");
        }
    }
}