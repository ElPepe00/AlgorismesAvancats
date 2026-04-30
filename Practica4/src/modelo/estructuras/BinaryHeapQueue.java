package modelo.estructuras;

import java.util.PriorityQueue;

public class BinaryHeapQueue implements CuaPrioritat {

    private PriorityQueue<Node> pq = new PriorityQueue<>((n1, n2) -> {
        int comp = Long.compare(n1.getFrequencia(), n2.getFrequencia());
        if (comp == 0) return Long.compare(n1.getSeqId(), n2.getSeqId());
        return comp;
    });

    /** Afegeix el node a la cua de prioritat interna. */
    @Override
    public void afegir(Node node) {
        pq.add(node);
    }

    /** Extreu el node amb el valor mínim de la cua. */
    @Override
    public Node extreureMinim() {
        return pq.poll();
    }

    /** Retorna el nombre d'elements actuals. */
    @Override
    public int mida() {
        return pq.size();
    }
}