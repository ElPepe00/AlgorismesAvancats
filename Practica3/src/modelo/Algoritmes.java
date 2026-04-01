package modelo;

import java.util.ArrayList;
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
    public Resultat mesProperaDivideParallel(List<Punt> punts) {
        List<Punt> ordenats = new ArrayList<>(punts);
        ordenats.sort((a, b) -> Double.compare(a.x, b.x));

        return closestRecParallel(ordenats);
    }
    private Resultat closestRecParallel2(List<Punt> punts) {

        if (punts.size() <= 5000){
            return mesProperaBrut(punts); // base
        }

        int mid = punts.size() / 2;
        Punt midPunt = punts.get(mid);

        List<Punt> esquerra = new ArrayList<>(punts.subList(0, mid));
        List<Punt> dreta = new ArrayList<>(punts.subList(mid, punts.size()));

        final Resultat[] resEsq = new Resultat[1];
        final Resultat[] resDre = new Resultat[1];

        Thread t1 = new Thread(() -> {
            resEsq[0] = closestRecParallel(esquerra);
        });

        Thread t2 = new Thread(() -> {
            resDre[0] = closestRecParallel(dreta);
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Resultat millor = (resEsq[0].distancia < resDre[0].distancia)
                ? resEsq[0] : resDre[0];

        List<Punt> strip = new ArrayList<>();

        for (Punt p : punts) {
            if (Math.abs(p.x - midPunt.x) < millor.distancia) {
                strip.add(p);
            }
        }

        return millorStrip(strip, millor);
    }

    private Resultat closestRecParallel(List<Punt> punts) {

        if (punts.size() <= 3) {
            return mesProperaBrut(punts);
        }

        int mid = punts.size() / 2;
        Punt midPunt = punts.get(mid);

        List<Punt> esquerra = new ArrayList<>(punts.subList(0, mid));
        List<Punt> dreta = new ArrayList<>(punts.subList(mid, punts.size()));

        Resultat resEsq, resDre;

        if (punts.size() > 5000) {
            final Resultat[] r1 = new Resultat[1];
            final Resultat[] r2 = new Resultat[1];

            Thread t1 = new Thread(() -> r1[0] = closestRecParallel(esquerra));
            Thread t2 = new Thread(() -> r2[0] = closestRecParallel(dreta));

            t1.start(); t2.start();
            try { t1.join(); t2.join(); } catch (InterruptedException e) {}

            resEsq = r1[0];
            resDre = r2[0];

        } else {
            resEsq = closestRecParallel(esquerra);
            resDre = closestRecParallel(dreta);
        }

        Resultat millor = (resEsq.distancia < resDre.distancia) ? resEsq : resDre;

        List<Punt> strip = new ArrayList<>();
        for (Punt p : punts) {
            if (Math.abs(p.x - midPunt.x) < millor.distancia) {
                strip.add(p);
            }
        }

        return millorStrip(strip, millor);
    }

    private Resultat millorStrip(List<Punt> strip, Resultat millor) {

        strip.sort((a, b) -> Double.compare(a.y, b.y));

        double min = millor.distancia;
        Punt p1 = millor.p1;
        Punt p2 = millor.p2;

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && j <= i + 7; j++) {

                double d = strip.get(i).distancia(strip.get(j));

                if (d < min) {
                    min = d;
                    p1 = strip.get(i);
                    p2 = strip.get(j);
                }
            }
        }

        return new Resultat(p1, p2, min);
    }
}