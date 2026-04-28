package modelo;

import java.util.ArrayList;
import java.util.List;

public class FibonacciHeapQueue implements CuaPrioritat {

    private static class NodeFib {
        Node valor;
        int grau;
        NodeFib pare;
        List<NodeFib> fills;

        public NodeFib(Node valor) {
            this.valor = valor;
            this.grau = 0;
            this.pare = null;
            this.fills = new ArrayList<>();
        }
    }

    private List<NodeFib> arrels;
    private NodeFib minim;
    private int mida;

    public FibonacciHeapQueue() {
        arrels = new ArrayList<>();
        minim = null;
        mida = 0;
    }

    @Override
    public void afegir(Node node) {
        NodeFib nou = new NodeFib(node);
        arrels.add(nou);

        if (minim == null || node.getFrequencia() < minim.valor.getFrequencia()) {
            minim = nou;
        }

        mida++;
    }

    @Override
    public Node extreureMinim() {
        if (minim == null) return null;

        NodeFib z = minim;

        // Afegir fills a les arrels
        for (NodeFib fill : z.fills) {
            fill.pare = null;
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

                        if (x.valor.getFrequencia() > y.valor.getFrequencia()) {
                            NodeFib tmp = x;
                            x = y;
                            y = tmp;
                        }

                        // y es fa fill de x
                        y.pare = x;
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
            if (minim == null || n.valor.getFrequencia() < minim.valor.getFrequencia()) {
                minim = n;
            }
        }
    }

    @Override
    public int mida() {
        return mida;
    }
}