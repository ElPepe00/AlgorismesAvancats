package modelo;

import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name Estadisticas de la Simulacion
 */
public class Estadisticas {

    private final int[] turnosPorPartida;
    private int minimo;
    private int maximo;
    private double media;
    private double mediana;
    private final double[] percentiles; // Índices 0 a 3 mapean a P1-P4, Índices 4 a 7 mapean a P6-P9

    public Estadisticas(int[] turnosPorPartida) {
        this.turnosPorPartida = turnosPorPartida;
        this.percentiles = new double[8];
        calcularEstadisticas();
    }

    /**
     * Genera la cadena de salida formateada con total fidelidad a las restricciones del PDF.
     */
    public String generarFormatoTexto() {
        return String.format(Locale.US,
                "Minimo: %d\n" +
                        "P1: %.6f\n" +
                        "P2: %.6f\n" +
                        "P3: %.6f\n" +
                        "P4: %.6f\n" +
                        "Mediana: %.6f\n" +
                        "P6: %.6f\n" +
                        "P7: %.6f\n" +
                        "P8: %.6f\n" +
                        "P9: %.6f\n" +
                        "Maximo: %d\n" +
                        "Media: %.6f",
                minimo, percentiles[0], percentiles[1], percentiles[2], percentiles[3],
                mediana, percentiles[4], percentiles[5], percentiles[6], percentiles[7],
                maximo, media);
    }

    public int getMinimo() { return minimo; }
    public int getMaximo() { return maximo; }
    public double getMedia() { return media; }
    public double getMediana() { return mediana; }
    public double[] getPercentiles() { return percentiles.clone(); }

    /**
     * Calcula los estadísticos descriptivos básicos y los percentiles empíricos requeridos.
     */
    private void calcularEstadisticas() {
        if (turnosPorPartida == null || turnosPorPartida.length == 0)
            return;

        // Se clona y ordena el array para no mutar los datos originales y asegurar los
        // percentiles empíricos
        int[] datosOrdenados = turnosPorPartida.clone();
        Arrays.sort(datosOrdenados);

        int n = datosOrdenados.length;
        this.minimo = datosOrdenados[0];
        this.maximo = datosOrdenados[n - 1];

        // Cálculo de la Media Aritmética
        long sumaTotal = 0;
        for (int valor : datosOrdenados) {
            sumaTotal += valor;
        }
        this.media = (double) sumaTotal / n;

        // Cálculo de la Mediana (Percentil 0.5)
        this.mediana = calcularPercentilEmpirico(datosOrdenados, 0.5);

        // Cálculo de Percentiles requeridos: P1 a P4
        this.percentiles[0] = calcularPercentilEmpirico(datosOrdenados, 0.1);
        this.percentiles[1] = calcularPercentilEmpirico(datosOrdenados, 0.2);
        this.percentiles[2] = calcularPercentilEmpirico(datosOrdenados, 0.3);
        this.percentiles[3] = calcularPercentilEmpirico(datosOrdenados, 0.4);

        // Cálculo de Percentiles requeridos: P6 a P9
        this.percentiles[4] = calcularPercentilEmpirico(datosOrdenados, 0.6);
        this.percentiles[5] = calcularPercentilEmpirico(datosOrdenados, 0.7);
        this.percentiles[6] = calcularPercentilEmpirico(datosOrdenados, 0.8);
        this.percentiles[7] = calcularPercentilEmpirico(datosOrdenados, 0.9);
    }

    /**
     * Algoritmo de interpolación lineal estándar para percentiles empíricos
     * continuos.
     */
    private double calcularPercentilEmpirico(int[] datos, double p) {
        int n = datos.length;
        double pos = p * (n - 1);
        int indiceBajo = (int) Math.floor(pos);
        int indiceAlto = (int) Math.ceil(pos);

        if (indiceBajo == indiceAlto) {
            return datos[indiceBajo];
        }

        // Interpolación lineal entre los dos rangos vecinos
        return datos[indiceBajo] + (pos - indiceBajo) * (datos[indiceAlto] - datos[indiceBajo]);
    }

}