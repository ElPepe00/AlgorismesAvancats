package modelo;

import java.util.*;

public class GeneradorPunts {

    public List<Punt> generar(int n, String tipus) {
        Random r = new Random();
        List<Punt> punts = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            double x, y;

            if (tipus.equals("gaussiana")) {
                x = (r.nextGaussian()/6.0) + 0.5;
                y = (r.nextGaussian()/6.0) + 0.5;
            } else {
                x = r.nextDouble();
                y = r.nextDouble();
            }

            punts.add(new Punt(x, y));
        }

        return punts;
    }
}