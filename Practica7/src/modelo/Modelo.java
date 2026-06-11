package modelo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Lógica de negocio e ingeniería del Simulador del Juego de la Oca.
 * Implementa el motor estocástico Monte Carlo.
 * * @author Josep Oliver y Hugo Valls
 * @date 10 jun 2026
 */
public class Modelo {

    // Look-Up Table (LUT) para resolver las conexiones de OCA en tiempo constante O(1)
    private static final int[] SIGUIENTE_OCA = new int[64];
    
    // Array thread-safe para contar masivamente la frecuencia de visitas a cada casilla (Data Science)
    private final AtomicLongArray frecuenciasVisitas = new AtomicLongArray(64);
    
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
    }

    /**
     * Ejecuta una simulación Monte Carlo compuesta por N iteraciones independientes.
     * * @param numeroPartidas N (número de iteraciones simuladas).
     * @return El objeto Estadisticas con los resultados calculados listos para presentarse.
     */
    public Estadisticas ejecutarSimulacionMonteCarlo(int numeroPartidas) {
        // Resetear el conteo de frecuencias antes de empezar una nueva simulación
        for (int i = 0; i < 64; i++) {
            frecuenciasVisitas.set(i, 0);
        }

        int[] resultadosTurnos = IntStream.range(0, numeroPartidas)
                .parallel()
                .map(i -> simularPartidaUnica())
                .toArray();

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
            int dado = ThreadLocalRandom.current().nextInt(6) + 1;
            
            // 1. Cálculo de avance inicial y rebote
            int nuevaPosicion = posicionActual + dado;
            if (nuevaPosicion > 63) {
                nuevaPosicion = 63 - (nuevaPosicion - 63); // Mecánica de rebote inverso
            }

            // Verificación inmediata de fin de partida antes de aplicar cualquier efecto secundario
            if (nuevaPosicion == 63) {
                posicionActual = nuevaPosicion;
                frecuenciasVisitas.incrementAndGet(63);
                break;
            }

            // Registramos la casilla física en la que ha caído el dado
            frecuenciasVisitas.incrementAndGet(nuevaPosicion);

            // 2. Evaluación de reglas de Casilla Especial (Máximo 1 efecto por tirada)
            CasillaEspecial tipoCasilla = CasillaEspecial.obtenerPorCasilla(nuevaPosicion);
            
            switch (tipoCasilla) {
                case OCA:
                    // De oca a oca y tiro porque me toca
                    posicionActual = SIGUIENTE_OCA[nuevaPosicion];
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    // Aplicamos el flag para que la nueva tirada NO sume turno según dictamina el PDF
                    turnoExtra = true;
                    break;

                case PUENTE:
                    // Intercambio simétrico 6 <-> 12
                    posicionActual = (nuevaPosicion == 6) ? 12 : 6;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;

                case DADOS:
                    // Intercambio simétrico 26 <-> 53
                    posicionActual = (nuevaPosicion == 26) ? 53 : 26;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;

                case LABERINTO:
                    // Retroceso directo a la casilla 30
                    posicionActual = 30;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;

                case MUERTE:
                    // Reseteo absoluto al origen
                    posicionActual = 0;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
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
     * Obtiene un texto formateado con las 5 casillas más visitadas durante la simulación.
     * @return String con el Top 5 listo para la Vista.
     */
    public String obtenerTop5CasillasMasVisitadas() {
        // Recorremos de 0 a 62 (excluimos la 63 ya que siempre registrará exactamente N visitas sesgando el Top)
        return IntStream.range(0, 63)
                .boxed()
                .sorted((a, b) -> Long.compare(frecuenciasVisitas.get(b), frecuenciasVisitas.get(a)))
                .limit(5)
                .map(i -> "Casilla " + String.format("%02d", i) + ": " + frecuenciasVisitas.get(i) + " visitas")
                .collect(Collectors.joining("\n"));
    }
}