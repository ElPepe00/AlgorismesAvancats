package modelo.estructuras;

/**
 * Interfície comuna per a les estructures de cua de prioritat
 * utilitzades en l'algorisme de Huffman.
 */
public interface CuaPrioritat {

    /** Afegeix un node a la cua. */
    void afegir(Node node);

    /** Treu i retorna el node amb la freqüència més baixa. */
    Node extreureMinim();

    /** Retorna el nombre de nodes que hi ha a la cua. */
    int mida();
}