package modelo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import modelo.Estadisticas;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Modelo
 */
public class Modelo {

    // Constante estática para mapear los saltos de "Oca a Oca" de forma eficiente
    private static final Map<Integer, Integer> OCAS = new HashMap<>();

    static {
        int[] origen = { 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 };
        int[] destino = { 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59, 63 };
        for (int i = 0; i < origen.length; i++) {
            OCAS.put(origen[i], destino[i]);
        }
    }

    private final Random random;

    public Modelo() {
        this.random = new Random();
    }

    /**
     * Método principal del Modelo. Ejecuta las simulaciones y devuelve los
     * resultados.
     * 
     * @param nPartidas Número de partidas a simular.
     * @return Objeto Estadisticas con todos los cálculos solicitados.
     */
    public Estadisticas simular(int nPartidas) {
        if (nPartidas <= 0) {
            throw new IllegalArgumentException("El número de partidas debe ser mayor que 0.");
        }

        int[] resultados = new int[nPartidas];
        for (int i = 0; i < nPartidas; i++) {
            resultados[i] = jugarPartida();
        }

        // Ordenar es necesario para calcular los percentiles correctamente
        Arrays.sort(resultados);

        return calcularEstadisticas(resultados);
    }

    /**
     * Lógica pura del Juego de la Oca para una sola partida.
     */
    private int jugarPartida() {
        int pos = 0;
        int turnos = 0;
        int penalizaciones = 0;

        while (pos != 63) {
            turnos++;

            // Gestión de tiradas anuladas (penalizaciones)
            if (penalizaciones > 0) {
                penalizaciones--;
                continue; // Pasa al siguiente turno sin mover ni tirar el dado
            }

            boolean volverATirar = true;

            // Bucle que maneja el tiro extra de la oca en el mismo turno
            while (volverATirar && pos != 63) {
                volverATirar = false;
                int dado = random.nextInt(6) + 1; // Dado de 1 a 6
                pos += dado;

                // Efecto de rebote al final del tablero
                if (pos > 63) {
                    pos = 63 - (pos - 63);
                }

                // Condición de fin inmediata si cae exactamente en 63
                if (pos == 63) {
                    break;
                }

                // Casillas especiales (Se aplica máximo 1 efecto por tirada mediante if-else)
                if (OCAS.containsKey(pos)) {
                    pos = OCAS.get(pos);
                    volverATirar = true; // "De oca a oca y tiro porque me toca"
                } else if (pos == 6) {
                    pos = 12; // Puente
                } else if (pos == 12) {
                    pos = 6; // Puente
                } else if (pos == 26) {
                    pos = 53; // Dados
                } else if (pos == 53) {
                    pos = 26; // Dados
                } else if (pos == 19) {
                    penalizaciones = 1; // Posada
                } else if (pos == 31) {
                    penalizaciones = 2; // Pozo
                } else if (pos == 42) {
                    pos = 30; // Laberinto
                } else if (pos == 52) {
                    penalizaciones = 3; // Cárcel
                } else if (pos == 58) {
                    pos = 0; // Muerte
                }
            }
        }
        return turnos;
    }

    /**
     * Calcula un percentil utilizando interpolación lineal.
     */
    private double percentilEmpirico(int[] datosOrdenados, double p) {
        double k = (datosOrdenados.length - 1) * p;
        int f = (int) Math.floor(k);
        int c = (int) Math.ceil(k);

        if (f == c) {
            return datosOrdenados[(int) k];
        }
        return datosOrdenados[f] * (c - k) + datosOrdenados[c] * (k - f);
    }

}
