package modelo;

/**
 * Representa un node de l'arbre binari de Huffman.
 * * @author Josep Oliver i Hugo Valls
 * @name Node
 */
public class Node {

    // Utilitzem 'int' en lloc de 'byte' perquè a Java els bytes són des de -128 a 127.
    // L'int ens permet guardar còmodament valors de 0 a 255 per a símbols reals, 
    // i utilitzar el -1 per als nodes interns (que no contenen lletres).
    private final int simbol;
    private final long frequencia;
    
    private final Node fillEsquerre;
    private final Node fillDret;

    /**
     * Constructor per crear tant fulles com nodes interns.
     */
    public Node(int simbol, long frequencia, Node fillEsquerre, Node fillDret) {
        this.simbol = simbol;
        this.frequencia = frequencia;
        this.fillEsquerre = fillEsquerre;
        this.fillDret = fillDret;
    }

    /**
     * Comprova matemàticament si aquest node és el final d'una branca.
     * En un arbre de Huffman estricte, un node o té 2 fills, o no en té cap.
     */
    public boolean esFulla() {
        return (this.fillEsquerre == null && this.fillDret == null);
    }

    // --- GETTERS ---

    public int getSimbol() {
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
}