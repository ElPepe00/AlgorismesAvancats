package modelo;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 4 abr 2026
 * @name Resultat
 */
public class Resultat {
    
    public Punt p1, p2;
    public double distancia;

    public Resultat(Punt p1, Punt p2, double distancia) {
        this.p1 = p1;
        this.p2 = p2;
        this.distancia = distancia;
    }
}