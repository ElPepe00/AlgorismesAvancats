package modelo;

import java.util.HashMap;
import java.util.Map;

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

    public AlgorismeHuffman(long[] frequencies, CuaPrioritat cua) {
        this.frequencies = frequencies;
        this.codisHuffman = new HashMap<>();
        this.cua = cua;
    }

    /**
     * Construeix l'arbre de Huffman aplicant l'estratègia àvida.
     */
    public void construirArbre() {

        // 1. Crear hojas
        for (int i = 0; i < MIDA_ALFABET; i++) {
            if (frequencies[i] > 0) {
                cua.afegir(new Node(i, frequencies[i], null, null));
            }
        }

        // Cas especial: arxiu buit
        if (cua.mida() == 0) {
            this.arrel = null;
            return;
        }

        // Cas especial: un sol símbol
        if (cua.mida() == 1) {
            Node unicNode = cua.extreureMinim();
            this.arrel = new Node(-1, unicNode.getFrequencia(), unicNode, null);
            return;
        }

        // 2. Construcció de l'arbre
        while (cua.mida() > 1) {
            Node fillEsquerre = cua.extreureMinim();
            Node fillDret = cua.extreureMinim();

            long suma = fillEsquerre.getFrequencia() + fillDret.getFrequencia();
            Node pare = new Node(-1, suma, fillEsquerre, fillDret);

            cua.afegir(pare);
        }

        this.arrel = cua.extreureMinim();
    }

    /**
     * Inicia el recorregut DFS per generar codis.
     */
    public void generarCodis() {
        if (this.arrel == null) return;
        generarCodisRecursiu(this.arrel, "");
    }

    private void generarCodisRecursiu(Node node, String codiActual) {
        if (node == null) return;

        if (node.esFulla()) {
            codisHuffman.put(node.getSimbol(), codiActual);
        } else {
            generarCodisRecursiu(node.getFillEsquerre(), codiActual + "0");
            generarCodisRecursiu(node.getFillDret(), codiActual + "1");
        }
    }

    public Node getArrel() {
        return arrel;
    }

    public Map<Integer, String> getCodisHuffman() {
        return codisHuffman;
    }
}