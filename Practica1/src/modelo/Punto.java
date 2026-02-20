

package modelo;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 20 feb 2026
 * @name Punto
 */
public class Punto {

    private final double n;
    private final double tiempo;

    /**
     * Constructor de un Punto
     * @param n valor en eje x
     * @param temps valor en eje y
     */
    public Punto(double n, double temps) {
        this.n = n;
        this.tiempo = temps;
    }

    // Getters
    public double getN() {
        return n;
    }

    public double getTiempo() {
        return tiempo;
    }
}
