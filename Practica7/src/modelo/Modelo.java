

package modelo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        int[] origen = {5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59};
        int[] destino = {9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59, 63};
        for (int i = 0; i < origen.length; i++) {
            OCAS.put(origen[i], destino[i]);
        }
    }

    private final Random random;

    public Modelo() {
        this.random = new Random();
    }

    /**
     * Método principal del Modelo. Ejecuta las simulaciones y devuelve los resultados.
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
                    pos = 6;  // Puente
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
                    pos = 0;  // Muerte
                }
            }
        }
        return turnos;
    }

    /**
     * Procesa el array de resultados ordenados para extraer métricas estadísticas.
     */
    private Estadisticas calcularEstadisticas(int[] datosOrdenados) {
        int n = datosOrdenados.length;
        Estadisticas est = new Estadisticas();
        
        est.min = datosOrdenados[0];
        est.max = datosOrdenados[n - 1];

        long suma = 0;
        for (int dato : datosOrdenados) {
            suma += dato;
        }
        est.media = (double) suma / n;

        est.p10 = percentilEmpirico(datosOrdenados, 0.1);
        est.p20 = percentilEmpirico(datosOrdenados, 0.2);
        est.p30 = percentilEmpirico(datosOrdenados, 0.3);
        est.p40 = percentilEmpirico(datosOrdenados, 0.4);
        est.mediana = percentilEmpirico(datosOrdenados, 0.5);
        est.p60 = percentilEmpirico(datosOrdenados, 0.6);
        est.p70 = percentilEmpirico(datosOrdenados, 0.7);
        est.p80 = percentilEmpirico(datosOrdenados, 0.8);
        est.p90 = percentilEmpirico(datosOrdenados, 0.9);

        return est;
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

    // =========================================================================
    // DTO (Data Transfer Object) para enviar los datos a la Vista
    // =========================================================================
    public static class Estadisticas {
        private int min;
        private int max;
        private double media;
        private double p10, p20, p30, p40, mediana, p60, p70, p80, p90;

        // Getters para acceder a la información de forma segura
        public int getMin() { return min; }
        public int getMax() { return max; }
        public double getMedia() { return media; }
        public double getP10() { return p10; }
        public double getP20() { return p20; }
        public double getP30() { return p30; }
        public double getP40() { return p40; }
        public double getMediana() { return mediana; }
        public double getP60() { return p60; }
        public double getP70() { return p70; }
        public double getP80() { return p80; }
        public double getP90() { return p90; }
    }
}
