package modelo;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simulador Monte Carlo multijugador basado en las mismas reglas que la
 * simulación individual.
 *
 * @author Josep Oliver y Hugo Valls
 */
public class SimuladorMultijugador {

    private static final int[] SIGUIENTE_OCA = new int[64];

    private final AtomicLongArray frecuenciasVisitas =
            new AtomicLongArray(64);

    static {

        for (int i = 0; i < 64; i++) {
            SIGUIENTE_OCA[i] = i;
        }

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

    public ResultadoMultijugador ejecutarSimulacion(
            int numeroPartidas,
            int numeroJugadores) {

        for (int i = 0; i < 64; i++) {
            frecuenciasVisitas.set(i, 0);
        }

        long[] victorias = new long[numeroJugadores];
        int[] turnosPorPartida = new int[numeroPartidas];

        for (int partida = 0; partida < numeroPartidas; partida++) {

            int[] posiciones = new int[numeroJugadores];
            int[] penalizaciones = new int[numeroJugadores];

            int ganador = -1;
            int rondas = 0;

            while (ganador == -1) {

                rondas++;

                for (int jugador = 0;
                     jugador < numeroJugadores && ganador == -1;
                     jugador++) {

                    boolean turnoExtra = false;

                    do {

                        if (penalizaciones[jugador] > 0) {
                            penalizaciones[jugador]--;
                            break;
                        }

                        int dado =
                                ThreadLocalRandom.current().nextInt(1, 7);

                        int nuevaPos =
                                posiciones[jugador] + dado;

                        if (nuevaPos > 63) {
                            nuevaPos =
                                    63 - (nuevaPos - 63);
                        }

                        if (nuevaPos == 63) {

                            posiciones[jugador] = 63;
                            frecuenciasVisitas.incrementAndGet(63);

                            ganador = jugador;
                            break;
                        }

                        frecuenciasVisitas.incrementAndGet(nuevaPos);

                        CasillaEspecial tipo =
                                CasillaEspecial.obtenerPorCasilla(nuevaPos);

                        turnoExtra = false;

                        switch (tipo) {

                            case OCA:

                                posiciones[jugador] =
                                        SIGUIENTE_OCA[nuevaPos];

                                frecuenciasVisitas.incrementAndGet(
                                        posiciones[jugador]
                                );

                                if (posiciones[jugador] == 63) {
                                    ganador = jugador;
                                } else {
                                    turnoExtra = true;
                                }

                                break;

                            case PUENTE:

                                posiciones[jugador] =
                                        (nuevaPos == 6) ? 12 : 6;

                                frecuenciasVisitas.incrementAndGet(
                                        posiciones[jugador]
                                );

                                break;

                            case DADOS:

                                posiciones[jugador] =
                                        (nuevaPos == 26) ? 53 : 26;

                                frecuenciasVisitas.incrementAndGet(
                                        posiciones[jugador]
                                );

                                break;

                            case LABERINTO:

                                posiciones[jugador] = 30;

                                frecuenciasVisitas.incrementAndGet(30);

                                break;

                            case MUERTE:

                                posiciones[jugador] = 0;

                                frecuenciasVisitas.incrementAndGet(0);

                                break;

                            case POSADA:
                            case POZO:
                            case CARCEL:

                                posiciones[jugador] = nuevaPos;

                                penalizaciones[jugador] =
                                        tipo.getTurnosPenalizacion();

                                break;

                            case NORMAL:
                            default:

                                posiciones[jugador] = nuevaPos;
                                break;
                        }

                    } while (turnoExtra && ganador == -1);
                }
            }

            victorias[ganador]++;
            turnosPorPartida[partida] = rondas;
        }

        Estadisticas estadisticas =
                new Estadisticas(turnosPorPartida);

        return new ResultadoMultijugador(
                victorias,
                estadisticas
        );
    }

    public String obtenerTop5CasillasMasVisitadas() {

        return IntStream.range(0, 63)
                .boxed()
                .sorted((a, b) ->
                        Long.compare(
                                frecuenciasVisitas.get(b),
                                frecuenciasVisitas.get(a)
                        ))
                .limit(5)
                .map(i ->
                        "Casilla "
                                + String.format("%02d", i)
                                + ": "
                                + frecuenciasVisitas.get(i)
                                + " visitas")
                .collect(Collectors.joining("\n"));
    }

    public String generarInforme(
            ResultadoMultijugador resultado,
            int totalPartidas) {

        StringBuilder sb = new StringBuilder();

        sb.append(resultado.getEstadisticas().generarFormatoTexto());

        sb.append("\n\n- TOP 5 CASILLAS MÁS VISITADAS -\n");
        sb.append(obtenerTop5CasillasMasVisitadas());

        sb.append("\n\nRESULTADOS MULTIJUGADOR\n\n");

        long[] victorias = resultado.getVictorias();

        for (int i = 0; i < victorias.length; i++) {

            double porcentaje =
                    (double) victorias[i] * 100.0 / totalPartidas;

            sb.append(String.format(
                    Locale.US,
                    "Jugador %d: %d victorias (%.4f%%)%n",
                    i + 1,
                    victorias[i],
                    porcentaje
            ));
        }

        return sb.toString();
    }
}