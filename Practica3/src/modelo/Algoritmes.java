package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 4 abr 2026
 * @name Algoritmes
 */
public class Algoritmes {

    // Flag para controlar la interrupción del hilo
    private volatile boolean cancelado = false;

    // Tamaño de celda utilizado en bucket
    private double lastCellSize = -1;

    public double getLastCellSize() {
        return lastCellSize;
    }

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
        this.lastCellSize = -1;
    }

    public Resultat mesProperaBrut(List<Punt> punts) {
        double min = Double.MAX_VALUE;
        Punt p1 = null, p2 = null;

        for (int i = 0; i < punts.size(); i++) {
            // Comprobación de interrupción
            if (cancelado) {
                return null;
            }

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
            if (cancelado) {
                return null;
            }

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

    //**************************************************************************
    //**************************************************************************
    //**************************************************************************
    public Resultat mesLlunyana2(List<Punt> punts, String tipo) {
        if (punts == null || punts.size() < 2) {
            return null;
        }

        if (punts.size() <= 100) {
            return mesLlunyana(punts);
        }
        
        // 1. Filtrado inteligente (Heurística de contexto)
        List<Punt> candidatos;
        switch (tipo) {
            case "Uniforme":
                candidatos = filtrarExtremos(punts);
                break;
            case "Gaussiana":
                candidatos = filtrarGaussiana(punts, 0.10); // Top 10%
                break;
            case "Exponencial":
                candidatos = filtrarExponencial(punts, 0.20); // Top 20%
                break;
            default:
                candidatos = punts;
        }

        // 2. Cálculo final sobre el subconjunto reducido
        double max = Double.NEGATIVE_INFINITY;
        Punt p1 = null, p2 = null;

        for (int i = 0; i < candidatos.size(); i++) {
            if (cancelado) {
                return null;
            }
            for (int j = i + 1; j < candidatos.size(); j++) {
                double d = candidatos.get(i).distancia(candidatos.get(j));
                if (d > max) {
                    max = d;
                    p1 = candidatos.get(i);
                    p2 = candidatos.get(j);
                }
            }
        }
        return new Resultat(p1, p2, max);
    }

    private List<Punt> filtrarExtremos(List<Punt> punts) {
        // 1. Encontrar los límites del rectángulo
        double minX = punts.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = punts.stream().mapToDouble(p -> p.x).max().orElse(0);
        double minY = punts.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = punts.stream().mapToDouble(p -> p.y).max().orElse(0);

        // 2. Variables para almacenar el punto más cercano a cada esquina
        Punt[] corners = new Punt[4];
        double[] minDists = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        double[] targetX = {minX, minX, maxX, maxX};
        double[] targetY = {minY, maxY, minY, maxY};

        // 3. Pasada única para encontrar los puntos más cercanos a las 4 esquinas (O(n))
        for (Punt p : punts) {
            for (int i = 0; i < 4; i++) {
                double dx = p.x - targetX[i];
                double dy = p.y - targetY[i];
                double distSq = dx * dx + dy * dy; // Distancia al cuadrado

                if (distSq < minDists[i]) {
                    minDists[i] = distSq;
                    corners[i] = p;
                }
            }
        }

        // 4. Retornar los candidatos encontrados
        List<Punt> lista = new ArrayList<>();
        for (Punt p : corners) {
            if (p != null) {
                lista.add(p);
            }
        }
        return lista;
    }

    private List<Punt> filtrarGaussiana(List<Punt> punts, double porcentaje) {
        double avgX = punts.stream().mapToDouble(p -> p.x).average().orElse(0);
        double avgY = punts.stream().mapToDouble(p -> p.y).average().orElse(0);

        return punts.stream()
                .sorted((p1, p2) -> Double.compare(
                (p2.x - avgX) * (p2.x - avgX) + (p2.y - avgY) * (p2.y - avgY),
                (p1.x - avgX) * (p1.x - avgX) + (p1.y - avgY) * (p1.y - avgY)
        ))
                .limit(Math.max(2, (int) (punts.size() * porcentaje)))
                .collect(Collectors.toList());
    }

    private List<Punt> filtrarExponencial(List<Punt> punts, double porcentaje) {
        return punts.stream()
                .sorted((p1, p2) -> Double.compare(
                (p2.x * p2.x + p2.y * p2.y),
                (p1.x * p1.x + p1.y * p1.y)
        ))
                .limit(Math.max(2, (int) (punts.size() * porcentaje)))
                .collect(Collectors.toList());
    }

    //**************************************************************************
    //**************************************************************************
    //**************************************************************************
    public Resultat mesProperaDivideParallel(List<Punt> punts) {
        List<Punt> ordenats = new ArrayList<>(punts);
        ordenats.sort((a, b) -> Double.compare(a.x, b.x));

        return closestRecParallel(ordenats);
    }

    private Resultat closestRecParallel(List<Punt> punts) {
        // Comprobación en cada nivel de recursión
        if (cancelado) {
            return null;
        }

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
        if (resEsq == null || resDre == null) {
            return null;
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
            // Comprobación en el refinamiento del strip
            if (cancelado) {
                return null;
            }

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
        if (punts.size() < 2) {
            return null;
        }

        double d = Double.MAX_VALUE;

        int limit = Math.min(100, punts.size());
        for (int i = 0; i < limit; i++) {
            for (int j = i + 1; j < limit; j++) {
                double dist = punts.get(i).distancia(punts.get(j));
                if (dist < d) {
                    d = dist;
                }
            }
        }

        if (d == 0) {
            d = 0.0001;
        }

        Map<String, List<Punt>> grid = new HashMap<>();
        double cellSize = d;

        // Guardar cellSize para la vista
        lastCellSize = cellSize;

        for (Punt p : punts) {
            if (cancelado) {
                return null; // Comprobación en la creación de buckets
            }
            int cx = (int) (p.x / cellSize);
            int cy = (int) (p.y / cellSize);
            String key = cx + "," + cy;
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
        }

        double min = d;
        Punt p1 = null, p2 = null;

        for (Punt p : punts) {
            // Comprobación en el bucle principal de búsqueda
            if (cancelado) {
                return null;
            }

            int cx = (int) (p.x / cellSize);
            int cy = (int) (p.y / cellSize);

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    String key = (cx + dx) + "," + (cy + dy);
                    List<Punt> bucket = grid.get(key);
                    if (bucket == null) {
                        continue;
                    }

                    for (Punt q : bucket) {
                        if (p == q) {
                            continue;
                        }
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
