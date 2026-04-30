package modelo.algoritmos;

import java.util.HashMap;
import java.util.Map;

import modelo.estructuras.CuaPrioritat;
import modelo.estructuras.Node;

/**
 * @author Josep Oliver i Hugo Valls
 * @name AlgorismeHuffman
 */
public class AlgorismeHuffman {

    private final long[] frequencies;
    private Node arrel;
    private final Map<Integer, String> codisHuffman;

    private CuaPrioritat cua; // ⭐ NUEVO

    private static final int MIDA_ALFABET = 256;

    /** Constructor que rep les freqüències i la cua de prioritats a utilitzar. */
    public AlgorismeHuffman(long[] frequencies, CuaPrioritat cua) {
        this.frequencies = frequencies;
        this.codisHuffman = new HashMap<>();
        this.cua = cua;
    }

    /** Crea l'arbre de Huffman unint els nodes amb menys freqüència. */
    public void construirArbre() {
        Node.reiniciarContador();
        for (int i = 0; i < MIDA_ALFABET; i++) {
            if (frequencies[i] > 0) {
                cua.afegir(new Node(i, frequencies[i], null, null));
            }
        }

        if (cua.mida() == 0) {
            this.arrel = null;
            return;
        }

        if (cua.mida() == 1) {
            Node unicNode = cua.extreureMinim();
            this.arrel = new Node(-1, unicNode.getFrequencia(), unicNode, null);
            return;
        }

        while (cua.mida() > 1) {
            Node fillEsquerre = cua.extreureMinim();
            Node fillDret = cua.extreureMinim();
            long suma = fillEsquerre.getFrequencia() + fillDret.getFrequencia();
            Node pare = new Node(-1, suma, fillEsquerre, fillDret);
            cua.afegir(pare);
        }

        this.arrel = cua.extreureMinim();
    }

    /** Genera el codi de bits per a cada símbol recorrent l'arbre. */
    public void generarCodis() {
        if (this.arrel == null) return;
        generarCodisRecursiu(this.arrel, "");
    }

    /** Mètode intern recursiu per assignar 0s i 1s als camins de l'arbre. */
    private void generarCodisRecursiu(Node node, String codiActual) {
        if (node == null) return;

        if (node.esFulla()) {
            codisHuffman.put(node.getSimbol(), codiActual);
        } else {
            generarCodisRecursiu(node.getFillEsquerre(), codiActual + "0");
            generarCodisRecursiu(node.getFillDret(), codiActual + "1");
        }
    }

    /** Retorna l'arrel de l'arbre generat. */
    public Node getArrel() {
        return arrel;
    }

    /** Retorna el mapa amb els codis resultants. */
    public Map<Integer, String> getCodisHuffman() {
        return codisHuffman;
    }
}