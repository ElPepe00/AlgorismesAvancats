package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Josep Oliver i Hugo Valls
 * @name AlgorismeHuffman
 */
public class AlgorismeHuffman {

    private final long[] frequencies;
    private Node arrel;
    private final Map<Integer, String> codisHuffman;

    // Utilitzem la constant de disseny de la qual vam parlar a la memòria
    private static final int MIDA_ALFABET = 256;

    public AlgorismeHuffman(long[] frequencies) {
        this.frequencies = frequencies;
        this.codisHuffman = new HashMap<>();
    }

    /**
     * Construeix l'arbre de Huffman aplicant l'estratègia àvida.
     */
    public void construirArbre() {
        // Utilitzem la PriorityQueue nativa (Min-Binary Heap O(log N)).
        // L'ordenem utilitzant una expressió Lambda basada en la freqüència.
        PriorityQueue<Node> cuaPrioritat = new PriorityQueue<>(
                (n1, n2) -> Long.compare(n1.getFrequencia(), n2.getFrequencia())
        );

        // 1. Creem les fulles inicials per a cada byte que aparegui a l'arxiu
        for (int i = 0; i < MIDA_ALFABET; i++) {
            if (frequencies[i] > 0) {
                // Assumim que el Node rep: (simbol, frequencia, fillEsquerre, fillDret)
                cuaPrioritat.add(new Node(i, frequencies[i], null, null));
            }
        }

        // Cas especial de prevenció 1: Arxiu buit
        if (cuaPrioritat.isEmpty()) {
            this.arrel = null;
            return;
        }

        // Cas especial de prevenció 2: Arxiu amb un sol tipus de byte
        if (cuaPrioritat.size() == 1) {
            Node unicNode = cuaPrioritat.poll();
            // Creem un pare irrellevant perquè el símbol estigui almenys a la branca '0'
            this.arrel = new Node(-1, unicNode.getFrequencia(), unicNode, null);
            return;
        }

        // 2. Construïm l'arbre fusionant els dos nodes de menor freqüència
        while (cuaPrioritat.size() > 1) {
            Node fillEsquerre = cuaPrioritat.poll();
            Node fillDret = cuaPrioritat.poll();

            // Creem un node pare intern. No és una fulla, li posem un símbol irrellevant (-1)
            long sumaFrequencies = fillEsquerre.getFrequencia() + fillDret.getFrequencia();
            Node pare = new Node(-1, sumaFrequencies, fillEsquerre, fillDret);

            cuaPrioritat.add(pare);
        }

        // L'últim node que queda al Binary Heap és l'arrel completa de l'arbre
        this.arrel = cuaPrioritat.poll();
    }

    /**
     * Inicia el recorregut DFS des de l'arrel per generar el diccionari.
     */
    public void generarCodis() {
        if (this.arrel == null) return;
        generarCodisRecursiu(this.arrel, "");
    }

    /**
     * Mètode recursiu que assigna '0' per l'esquerra i '1' per la dreta.
     */
    private void generarCodisRecursiu(Node node, String codiActual) {
        if (node == null) return;

        // Si és una fulla, guardem la ruta de bits que hem anat acumulant
        if (node.esFulla()) {
            codisHuffman.put(node.getSimbol(), codiActual);
        } else {
            // Si és node intern, baixem recursivament sumant la ruta
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