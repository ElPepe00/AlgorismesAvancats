package modelo;

import java.util.*;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 4 abr 2026
 * @name GeneradorPunts
 */
public class GeneradorPunts {

    public List<Punt> generar(int n, String tipus) {
        Random r = new Random();
        List<Punt> punts = new ArrayList<>(n); // mejora de rendimiento

        for (int i = 0; i < n; i++) {
            double x, y;

            switch (tipus) {
                case "Uniforme":
                    x = r.nextDouble();
                    y = r.nextDouble();
                    break;

                case "Gaussiana":
                    x = (r.nextGaussian() / 8.0) + 0.5;
                    y = (r.nextGaussian() / 8.0) + 0.5;
                    break;

                case "Exponencial":
                    // Aproximación usando distribución exponencial
                    x = -Math.log(1 - r.nextDouble()) / 5.0;
                    y = -Math.log(1 - r.nextDouble()) / 5.0;
                    break;

                default:
                    throw new AssertionError();
            }

            // OPCIONAL: para asegurar que ningun punto invisible rompa el programa
            if (x < 0.0) {
                x = 0.0;
            }
            if (x > 1.0) {
                x = 1.0;
            }
            if (y < 0.0) {
                y = 0.0;
            }
            if (y > 1.0) {
                y = 1.0;
            }
            // ------------

            punts.add(new Punt(x, y));
        }

        return punts;
    }
}