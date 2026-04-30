package modelo.estructuras;

/**
 * Representa un node de l'arbre binari de Huffman.
 * * @author Josep Oliver i Hugo Valls
 * @name Node
 */
public class Node {

    private final int simbol;
    private final long frequencia;
    
    private final Node fillEsquerre;
    private final Node fillDret;
    private final long seqId;
    private static long contadorGlobal = 0;

    /** Crea un nou node amb un símbol, la seva freqüència i els seus fills. */
    public Node(int simbol, long frequencia, Node fillEsquerre, Node fillDret) {
        this.simbol = simbol;
        this.frequencia = frequencia;
        this.fillEsquerre = fillEsquerre;
        this.fillDret = fillDret;
        this.seqId = contadorGlobal++;
    }

    /** Indica si el node és una fulla (no té fills). */
    public boolean esFulla() {
        return (this.fillEsquerre == null && this.fillDret == null);
    }

    // --- GETTERS ---

    /** Retorna el símbol (byte) que conté el node. */
    public int getSimbol() {
        return simbol;
    }

    /** Retorna el nombre de vegades que apareix el símbol. */
    public long getFrequencia() {
        return frequencia;
    }

    /** Retorna el fill de l'esquerra (bit 0). */
    public Node getFillEsquerre() {
        return fillEsquerre;
    }

    /** Retorna el fill de la dreta (bit 1). */
    public Node getFillDret() {
        return fillDret;
    }

    /** Retorna l'ID de seqüència per al desempat. */
    public long getSeqId() {
        return seqId;
    }

    /** Reinicia el contador per garantir determinisme absolut si cal. */
    public static void reiniciarContador() {
        contadorGlobal = 0;
    }
}