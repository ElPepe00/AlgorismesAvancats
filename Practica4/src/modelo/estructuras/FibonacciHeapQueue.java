package modelo.estructuras;

import java.util.ArrayList;
import java.util.List;

public class FibonacciHeapQueue implements CuaPrioritat {

    private static class NodeFib {
        Node valor;
        int grau;
        List<NodeFib> fills;

        /** Constructor del node intern del Fibonacci Heap. */
        public NodeFib(Node valor) {
            this.valor = valor;
            this.grau = 0;
            this.fills = new ArrayList<>();
        }
    }

    private List<NodeFib> arrels;
    private NodeFib minim;
    private int mida;

    /** Inicialitza les llistes d'arrels i el comptador. */
    public FibonacciHeapQueue() {
        arrels = new ArrayList<>();
        minim = null;
        mida = 0;
    }

    /** Insereix un nou node a la llista d'arrels. */
    @Override
    public void afegir(Node node) {
        NodeFib nou = new NodeFib(node);
        arrels.add(nou);

        if (minim == null || esMenor(node, minim.valor)) {
            minim = nou;
        }

        mida++;
    }

    /** Extreu el node mínim i reorganitza l'estructura. */
    @Override
    public Node extreureMinim() {
        if (minim == null) return null;

        NodeFib z = minim;

        for (NodeFib fill : z.fills) {
            arrels.add(fill);
        }

        arrels.remove(z);

        if (arrels.isEmpty()) {
            minim = null;
        } else {
            minim = arrels.get(0);
            consolidar();
        }

        mida--;
        return z.valor;
    }

    /** Uneix arbres del mateix grau per optimitzar la cua. */
    private void consolidar() {
        List<NodeFib> nova = new ArrayList<>();

        while (!arrels.isEmpty()) {
            NodeFib x = arrels.remove(0);

            while (true) {
                boolean trobat = false;

                for (int i = 0; i < nova.size(); i++) {
                    NodeFib y = nova.get(i);

                    if (y.grau == x.grau) {
                        nova.remove(i);

                        if (esMenor(y.valor, x.valor)) {
                            NodeFib tmp = x;
                            x = y;
                            y = tmp;
                        }

                        x.fills.add(y);
                        x.grau++;

                        trobat = true;
                        break;
                    }
                }

                if (!trobat) break;
            }

            nova.add(x);
        }

        minim = null;

        for (NodeFib n : nova) {
            arrels.add(n);
            if (minim == null || esMenor(n.valor, minim.valor)) {
                minim = n;
            }
        }
    }

    /** Retorna el nombre total de nodes. */
    @Override
    public int mida() {
        return mida;
    }

    /** Compara dos nodes de forma determinista (freqüència i ID). */
    private boolean esMenor(Node n1, Node n2) {
        long f1 = n1.getFrequencia();
        long f2 = n2.getFrequencia();
        if (f1 < f2) return true;
        if (f1 > f2) return false;
        return n1.getSeqId() < n2.getSeqId();
    }
}