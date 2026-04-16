package modelo;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 16 abr 2026
 * @name Node
 */
public class Node implements Comparable<Node> {

    private final Integer simbol; // Integer permet null (necessari pels nodes interns de l'arbre)
    private final long frequencia; // long evita overflows en fitxers multimegabyte/gigabyte
    private final Node fillEsquerre;
    private final Node fillDret;

    /**
     * Constructor per als nodes fulla (els que contenen un símbol real de
     * l'arxiu).
     */
    public Node(Integer simbol, long frequencia) {
        this.simbol = simbol;
        this.frequencia = frequencia;
        this.fillEsquerre = null;
        this.fillDret = null;
    }

    /**
     * Constructor per als nodes interns (creat per l'algorisme àvid al fusionar
     * dos subarbres). El símbol és null perquè només actua com a pont.
     */
    public Node(Node esquerre, Node dret) {
        this.simbol = null;
        this.frequencia = esquerre.getFrequencia() + dret.getFrequencia();
        this.fillEsquerre = esquerre;
        this.fillDret = dret;
    }

    public boolean esFulla() {
        return (this.fillEsquerre == null && this.fillDret == null);
    }

    public Integer getSimbol() {
        return simbol;
    }

    public long getFrequencia() {
        return frequencia;
    }

    public Node getFillEsquerre() {
        return fillEsquerre;
    }

    public Node getFillDret() {
        return fillDret;
    }

    /**
     * Criteri de l'Algorisme Àvid: Volem sempre els elements de MENOR
     * freqüència primer.
     */
    @Override
    public int compareTo(Node altre) {
        return Long.compare(this.frequencia, altre.frequencia);
    }

    @Override
    public String toString() {
        if (esFulla()) {
            // Mostrar caràcter si és imprimible, o el valor del byte/Integer si no ho és
            return "Fulla['" + (char) simbol.intValue() + "' (F=" + frequencia + ")]";
        } else {
            return "Intern[F=" + frequencia + "]";
        }
    }
}
