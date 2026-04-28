package modelo;

/**
 * Interfície comuna per a les estructures de cua de prioritat
 * utilitzades en l'algorisme de Huffman.
 */
public interface CuaPrioritat {

    // Afegir un node a la cua
    void afegir(Node node);

    // Extreure el node amb menor freqüència
    Node extreureMinim();

    // Nombre d'elements
    int mida();
}