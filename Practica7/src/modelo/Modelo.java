package modelo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Modelo
 */
public class Modelo {

    private static final int[] SIGUIENTE_OCA = new int[64];
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
     * Ejecuta simulación Monte Carlo con N iteraciones independientes.
     */
    public Estadisticas ejecutarSimulacionMonteCarlo(int numeroPartidas) {
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
     * Simula una partida completa de un jugador único.
     */
    private int simularPartidaUnica() {
        int posicionActual = 0;
        int totalTurnos = 0;
        int turnosPenalizacionPendientes = 0;
        boolean turnoExtra = false;

        while (posicionActual < 63) {
            if (!turnoExtra) {
                totalTurnos++;
            }
            turnoExtra = false;

            if (turnosPenalizacionPendientes > 0) {
                turnosPenalizacionPendientes--;
                continue;
            }

            int dado = ThreadLocalRandom.current().nextInt(6) + 1;
            int nuevaPosicion = posicionActual + dado;
            if (nuevaPosicion > 63) {
                nuevaPosicion = 63 - (nuevaPosicion - 63);
            }

            if (nuevaPosicion == 63) {
                posicionActual = nuevaPosicion;
                frecuenciasVisitas.incrementAndGet(63);
                break;
            }

            frecuenciasVisitas.incrementAndGet(nuevaPosicion);
            CasillaEspecial tipoCasilla = CasillaEspecial.obtenerPorCasilla(nuevaPosicion);
            
            switch (tipoCasilla) {
                case OCA:
                    posicionActual = SIGUIENTE_OCA[nuevaPosicion];
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    turnoExtra = true;
                    break;
                case PUENTE:
                    posicionActual = (nuevaPosicion == 6) ? 12 : 6;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;
                case DADOS:
                    posicionActual = (nuevaPosicion == 26) ? 53 : 26;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;
                case LABERINTO:
                    posicionActual = 30;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;
                case MUERTE:
                    posicionActual = 0;
                    frecuenciasVisitas.incrementAndGet(posicionActual);
                    break;
                case POSADA:
                case POZO:
                case CARCEL:
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
     * Obtiene las 5 casillas más visitadas durante la simulación.
     */
    public String obtenerTop5CasillasMasVisitadas() {
        return IntStream.range(0, 63)
                .boxed()
                .sorted((a, b) -> Long.compare(frecuenciasVisitas.get(b), frecuenciasVisitas.get(a)))
                .limit(5)
                .map(i -> "Casilla " + String.format("%02d", i) + ": " + frecuenciasVisitas.get(i) + " visitas")
                .collect(Collectors.joining("\n"));
    }
}