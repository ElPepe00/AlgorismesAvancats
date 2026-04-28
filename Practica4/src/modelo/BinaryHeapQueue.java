package modelo;

import java.util.PriorityQueue;

public class BinaryHeapQueue implements CuaPrioritat {

    private PriorityQueue<Node> pq = new PriorityQueue<>(
        (n1, n2) -> Long.compare(n1.getFrequencia(), n2.getFrequencia())
    );

    @Override
    public void afegir(Node node) {
        pq.add(node);
    }

    @Override
    public Node extreureMinim() {
        return pq.poll();
    }

    @Override
    public int mida() {
        return pq.size();
    }
}