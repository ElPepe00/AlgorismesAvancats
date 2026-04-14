package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 4 abr 2026
 * @name Algoritmes
 */
public class Algoritmes {

    // Flag para controlar la interrupción del hilo
    private volatile boolean cancelado = false;

    /**
     * Activa el flag de cancelación para detener los bucles.
     */
    public void cancelar() {
        this.cancelado = true;
    }

    /**
     * Reinicia el flag para permitir un nuevo cálculo.
     */
    public void preparar() {
        this.cancelado = false;
    }

    public Resultat mesProperaBrut(List<Punt> punts) {
        double min = Double.MAX_VALUE;
        Punt p1 = null, p2 = null;

        for (int i = 0; i < punts.size(); i++) {
            // Comprobación de interrupción
            if (cancelado) return null;

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
            // Comprobación de interrupción
            if (cancelado) return null;

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

    private Resultat closestRecParallel(List<Punt> punts) {
        // Comprobación en cada nivel de recursión
        if (cancelado) return null;

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

            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                return null;
            }

            resEsq = r1[0];
            resDre = r2[0];
        } else {
            resEsq = closestRecParallel(esquerra);
            resDre = closestRecParallel(dreta);
        }

        // Si alguna rama devolvió null por cancelación, propagamos el null
        if (resEsq == null || resDre == null) return null;

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
            // Comprobación en el refinamiento del strip
            if (cancelado) return null;

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

    public Resultat bucketClosest(List<Punt> punts) {
        if (punts.size() < 2) return null;

        double d = Double.MAX_VALUE;
        int limit = Math.min(100, punts.size());
        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < limit; j++) {
                double dist = punts.get(i).distancia(punts.get(j));
                if (dist < d) d = dist;
            }
        }

        if (d == 0) d = 0.0001;
        Map<String, List<Punt>> grid = new HashMap<>();
        double cellSize = d;

        for (Punt p : punts) {
            if (cancelado) return null; // Comprobación en la creación de buckets
            int cx = (int) (p.x / cellSize);
            int cy = (int) (p.y / cellSize);
            String key = cx + "," + cy;
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        double min = d;
        Punt p1 = null, p2 = null;

        for (Punt p : punts) {
            // Comprobación en el bucle principal de búsqueda
            if (cancelado) return null;

            int cx = (int) (p.x / cellSize);
            int cy = (int) (p.y / cellSize);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    String key = (cx + dx) + "," + (cy + dy);
                    List<Punt> bucket = grid.get(key);
                    if (bucket == null) continue;

                    for (Punt q : bucket) {
                        if (p == q) continue;
                        double dist = p.distancia(q);
                        if (dist < min) {
                            min = dist;
                            p1 = p;
                            p2 = q;
                        }
                    }
                }
            }
        }

        return new Resultat(p1, p2, min);
    }
}