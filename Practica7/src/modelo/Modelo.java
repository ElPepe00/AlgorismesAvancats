package modelo;

import java.util.Random;

/**
 * Lógica de negocio e ingeniería del Simulador del Juego de la Oca.
 * Implementa el motor estocástico Monte Carlo.
 * * @author Josep Oliver y Hugo Valls
 * @date 10 jun 2026
 */
public class Modelo {

    private final Random pseudoRandom;

    // Look-Up Table (LUT) para resolver las conexiones de OCA en tiempo constante O(1)
    private static final int[] SIGUIENTE_OCA = new int[64];
    
    static {
        for (int i = 0; i < 64; i++) SIGUIENTE_OCA[i] = i;
        SIGUIENTE_OCA[5] = 9;
        SIGUIENTE_OCA[9] = 14;
        SIGUIENTE_OCA[14] = 18;
        SIGUIENTE_OCA[18] = 23;
        SIGUIENTE_OCA[23] = 27;
        SIGUIENTE_OCA[27] = 32;
        SIGUIENTE_OCA[32] = 36;
        SIGUIENTE_OCA[36] = 41;
        SIGUIENTE_OCA[41] = 45;
        SIGUIENTE_OCA[45] = 50;
        SIGUIENTE_OCA[50] = 54;
        SIGUIENTE_OCA[54] = 59;
        SIGUIENTE_OCA[59] = 63;
    }

    public Modelo() {
        this.pseudoRandom = new Random();
    }

    /**
     * Ejecuta una simulación Monte Carlo compuesta por N iteraciones independientes.
     * * @param numeroPartidas N (número de iteraciones simuladas).
     * @return El objeto Estadisticas con los resultados calculados listos para presentarse.
     */
    public Estadisticas ejecutarSimulacionMonteCarlo(int numeroPartidas) {
        int[] resultadosTurnos = new int[numeroPartidas];

        for (int i = 0; i < numeroPartidas; i++) {
            resultadosTurnos[i] = simularPartidaUnica();
        }

        return new Estadisticas(resultadosTurnos);
    }

    /**
     * Simula el ciclo de vida completo de una partida para un jugador único.
     * * @return Cantidad total de turnos (tiradas normales + penalizaciones consumidas).
     */
    private int simularPartidaUnica() {
        int posicionActual = 0;
        int totalTurnos = 0;
        int turnosPenalizacionPendientes = 0;
        boolean turnoExtra = false;

        // La partida finaliza estrictamente en la casilla de meta 63
        while (posicionActual < 63) {
            
            // Solo computamos el turno de dado si no provenimos de una Oca
            if (!turnoExtra) {
                totalTurnos++;
            }
            turnoExtra = false; // Resetear la bandera para la iteración actual

            // Manejo de penalizaciones de posada, pozo y cárcel
            if (turnosPenalizacionPendientes > 0) {
                turnosPenalizacionPendientes--;
                continue; // El turno se consume pero el jugador permanece inmóvil
            }

            // Lanzamiento del dado idóneo (1 a 6)
            int dado = pseudoRandom.nextInt(6) + 1;
            
            // 1. Cálculo de avance inicial y rebote
            int nuevaPosicion = posicionActual + dado;
            if (nuevaPosicion > 63) {
                nuevaPosicion = 63 - (nuevaPosicion - 63); // Mecánica de rebote inverso
            }

            // Verificación inmediata de fin de partida antes de aplicar cualquier efecto secundario
            if (nuevaPosicion == 63) {
                posicionActual = nuevaPosicion;
                break;
            }

            // 2. Evaluación de reglas de Casilla Especial (Máximo 1 efecto por tirada)
            CasillaEspecial tipoCasilla = CasillaEspecial.obtenerPorCasilla(nuevaPosicion);
            
            switch (tipoCasilla) {
                case OCA:
                    // De oca a oca y tiro porque me toca
                    posicionActual = SIGUIENTE_OCA[nuevaPosicion];
                    // Aplicamos el flag para que la nueva tirada NO sume turno según dictamina el PDF
                    turnoExtra = true;
                    break;

                case PUENTE:
                    // Intercambio simétrico 6 <-> 12
                    posicionActual = (nuevaPosicion == 6) ? 12 : 6;
                    break;

                case DADOS:
                    // Intercambio simétrico 26 <-> 53
                    posicionActual = (nuevaPosicion == 26) ? 53 : 26;
                    break;

                case LABERINTO:
                    // Retroceso directo a la casilla 30
                    posicionActual = 30;
                    break;

                case MUERTE:
                    // Reseteo absoluto al origen
                    posicionActual = 0;
                    break;

                case POSADA:
                case POZO:
                case CARCEL:
                    // Almacena la penalización correspondiente y sitúa al jugador en la casilla
                    turnosPenalizacionPendientes = tipoCasilla.getTurnosPenalizacion();
                    posicionActual = nuevaPosicion;
                    break;

                case NORMAL:
                default:
                    posicionActual = nuevaPosicion;
                    break;
            }
        }

        return totalTurnos;
    }
}