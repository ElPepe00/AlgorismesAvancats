package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Josep Oliver i Hugo Valls
 * @date 20 abr 2026
 * @name Algorisme de Huffman
 */
public class AlgorismeHuffman {

    private Node arrelArbre;
    private Map<Integer, String> codisHuffman;

    public AlgorismeHuffman(long[] frequencies) {
        this.codisHuffman = new HashMap<>();
        construirArbre(frequencies);
        generarCodisRecursiu(arrelArbre, "");
    }

    private void construirArbre(long[] frequencies) {
        PriorityQueue<Node> cua = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (frequencies[i] > 0) {
                cua.add(new Node(i, frequencies[i]));
            }
        }

        if (cua.isEmpty()) {
            return;
        }
        if (cua.size() == 1) {
            cua.add(new Node(null, 1));
        }

        while (cua.size() > 1) {
            Node esquerre = cua.poll();
            Node dret = cua.poll();
            cua.add(new Node(esquerre, dret));
        }
        arrelArbre = cua.poll();
    }

    private void generarCodisRecursiu(Node node, String codiActual) {
        if (node == null) {
            return;
        }
        if (node.esFulla()) {
            codisHuffman.put(node.getSimbol(), codiActual.isEmpty() ? "0" : codiActual);
            return;
        }
        generarCodisRecursiu(node.getFillEsquerre(), codiActual + "0");
        generarCodisRecursiu(node.getFillDret(), codiActual + "1");
    }

    public Node getArrelArbre() {
        return arrelArbre;
    }

    public Map<Integer, String> getCodisHuffman() {
        return codisHuffman;
    }
}
