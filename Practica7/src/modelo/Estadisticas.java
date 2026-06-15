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
     * Genera la cadena formateada con los resultados estadísticos.
     */
    public String generarFormatoTexto() {
        return String.format(Locale.US,
                "Minimo: %d\n" +
                        "P1: %s\n" +
                        "P2: %s\n" +
                        "P3: %s\n" +
                        "P4: %s\n" +
                        "Mediana: %s\n" +
                        "P6: %s\n" +
                        "P7: %s\n" +
                        "P8: %s\n" +
                        "P9: %s\n" +
                        "Maximo: %d\n" +
                        "Media: %.6f",
                minimo, formatear(percentiles[0]), formatear(percentiles[1]), formatear(percentiles[2]), formatear(percentiles[3]),
                formatear(mediana), formatear(percentiles[4]), formatear(percentiles[5]), formatear(percentiles[6]), formatear(percentiles[7]),
                maximo, media);
    }

    /**
     * Formatea un valor numérico eliminando decimales innecesarios.
     */
    private String formatear(double valor) {
        if (valor == (long) valor) return String.format(Locale.US, "%d", (long) valor);
        return String.valueOf(valor);
    }

    public int getMinimo() { return minimo; }
    public int getMaximo() { return maximo; }
    public double getMedia() { return media; }
    public double getMediana() { return mediana; }
    public double[] getPercentiles() { return percentiles.clone(); }

    /**
     * Retorna el conjunto de turnos de todas las partidas simuladas.
     */
    public int[] getTurnosPorPartida() {
        return turnosPorPartida;
    }

    /**
     * Calcula estadísticos y percentiles de los datos.
     */
    private void calcularEstadisticas() {
        if (turnosPorPartida == null || turnosPorPartida.length == 0)
            return;

        int[] datosOrdenados = turnosPorPartida.clone();
        Arrays.sort(datosOrdenados);

        int n = datosOrdenados.length;
        this.minimo = datosOrdenados[0];
        this.maximo = datosOrdenados[n - 1];

        long sumaTotal = 0;
        for (int valor : datosOrdenados) {
            sumaTotal += valor;
        }
        this.media = (double) sumaTotal / n;
        this.mediana = calcularPercentilEmpirico(datosOrdenados, 0.5);
        this.percentiles[0] = calcularPercentilEmpirico(datosOrdenados, 0.1);
        this.percentiles[1] = calcularPercentilEmpirico(datosOrdenados, 0.2);
        this.percentiles[2] = calcularPercentilEmpirico(datosOrdenados, 0.3);
        this.percentiles[3] = calcularPercentilEmpirico(datosOrdenados, 0.4);

        this.percentiles[4] = calcularPercentilEmpirico(datosOrdenados, 0.6);
        this.percentiles[5] = calcularPercentilEmpirico(datosOrdenados, 0.7);
        this.percentiles[6] = calcularPercentilEmpirico(datosOrdenados, 0.8);
        this.percentiles[7] = calcularPercentilEmpirico(datosOrdenados, 0.9);
    }

    /**
     * Calcula percentil empírico con interpolación lineal.
     */
    private double calcularPercentilEmpirico(int[] datos, double p) {
        int n = datos.length;
        double pos = p * (n - 1);
        int indiceBajo = (int) Math.floor(pos);
        int indiceAlto = (int) Math.ceil(pos);

        if (indiceBajo == indiceAlto) {
            return datos[indiceBajo];
        }

        return datos[indiceBajo] + (pos - indiceBajo) * (datos[indiceAlto] - datos[indiceBajo]);
    }

}