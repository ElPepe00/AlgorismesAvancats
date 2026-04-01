package modelo;

import java.util.List;

public class Algoritmes {

    public Resultat mesProperaBrut(List<Punt> punts) {
        double min = Double.MAX_VALUE;
        Punt p1 = null, p2 = null;

        for (int i = 0; i < punts.size(); i++) {
            for (int j = i + 1; j < punts.size(); j++) {
                double d = punts.get(i).distancia(punts.get(j));
                if (d < min) {
                    min = d;
                    p1 = punts.get(i);
                    p2 = punts.get(j);
                }
            }
        }

        return new Resultat(p1, p2, min);
    }

    public Resultat mesLlunyana(List<Punt> punts) {
    double max = Double.MIN_VALUE;
    Punt p1 = null, p2 = null;

    for (int i = 0; i < punts.size(); i++) {
        for (int j = i + 1; j < punts.size(); j++) {
            double d = punts.get(i).distancia(punts.get(j));
            if (d > max) {
                max = d;
                p1 = punts.get(i);
                p2 = punts.get(j);
            }
        }
    }

    return new Resultat(p1, p2, max);
}
}