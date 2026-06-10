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

        // La partida finaliza estrictamente en la casilla de meta 63
        while (posicionActual < 63) {
            totalTurnos++;

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
                    // De oca a oca y tiro porque me toca (No computa turno adicional)
                    posicionActual = obtenerSiguienteOca(nuevaPosicion);
                    // Como indica el PDF, saltar a la 63 desde la 59 finaliza de inmediato
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

    /**
     * Calcula estáticamente la correspondencia de saltos entre casillas de ocas.
     */
    private int obtenerSiguienteOca(int casillaActual) {
        switch (casillaActual) {
            case 5: return 9;
            case 9: return 14;
            case 14: return 18;
            case 18: return 23;
            case 23: return 27;
            case 27: return 32;
            case 32: return 36;
            case 36: return 41;
            case 41: return 45;
            case 45: return 50;
            case 50: return 54;
            case 54: return 59;
            case 59: return 63; // Fin de circuito
            default: return casillaActual;
        }
    }
}