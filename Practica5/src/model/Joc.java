package model;

import java.util.ArrayList;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 18 may 2026
 * @name Joc
 */
public class Joc {

    // Classe per encapsular l'estat memoitzat (POO)
    private static class EstatCalculat {
        private final int perdedor;
        private final long partides;

        public EstatCalculat(int perdedor, long partides) {
            this.perdedor = perdedor;
            this.partides = partides;
        }

        public int getPerdedor() {
            return perdedor;
        }

        public long getPartides() {
            return partides;
        }
    }

    // Una sola matriu d'objectes en lloc de tres primitives
    private final EstatCalculat[][] dp;
    private final int limit;
    private final Teclat teclat;
    private final int numJugadors;

    public Joc(int limit, Teclat teclat, int numJugadors) {
        this.limit = limit;
        this.teclat = teclat;
        this.numJugadors = numJugadors;
        
        int maxNombre = teclat.getMaxNombre();
        this.dp = new EstatCalculat[limit][maxNombre + 1];
    }

    // Retorna el perdedor relatiu (0 = actual, 1 = següent...)
    public int calcularEstat(int sumaInicial, int darrerNombre) {
        if (sumaInicial >= limit) {
            return 0; 
        }
        return avaluarEstat(sumaInicial, darrerNombre).getPerdedor();
    }
    
    // Lògica recursiva de programació dinàmica
    private EstatCalculat avaluarEstat(int suma, int darrerNombre) {
        if (dp[suma][darrerNombre] != null) {
            return dp[suma][darrerNombre];
        }
        
        ArrayList<Integer> movimentsValids = teclat.getMovimentsValids(darrerNombre);
        long partides = 0;
        int millorPerdedorRelatiu = 0; 
        boolean trobatAlgunaSalvacio = false;
        
        for (int m : movimentsValids) {
            int novaSuma = suma + m;
            int perdedorAquestMoviment;
            
            if (novaSuma >= limit) {
                // El jugador actual perd immediatament
                perdedorAquestMoviment = 0; 
                partides++;
            } else {
                // El torn passa al següent jugador
                EstatCalculat estatSeguent = avaluarEstat(novaSuma, m);
                partides += estatSeguent.getPartides();
                perdedorAquestMoviment = (estatSeguent.getPerdedor() + 1) % numJugadors;
            }
            
            // Elecció racional del moviment (evitar perdre i fer perdre el següent)
            if (perdedorAquestMoviment != 0) {
                if (!trobatAlgunaSalvacio) {
                    millorPerdedorRelatiu = perdedorAquestMoviment;
                    trobatAlgunaSalvacio = true;
                } else {
                    if (perdedorAquestMoviment < millorPerdedorRelatiu) {
                        millorPerdedorRelatiu = perdedorAquestMoviment;
                    }
                }
            }
        }
        
        if (!trobatAlgunaSalvacio) {
            millorPerdedorRelatiu = 0;
        }
        
        dp[suma][darrerNombre] = new EstatCalculat(millorPerdedorRelatiu, partides);
        return dp[suma][darrerNombre];
    }
    
    // Retorna el total de partides possibles des d'un estat
    public long getTotalPartides(int sumaInicial, int darrerNombre) {
        if (sumaInicial >= limit) return 0;
        if (dp[sumaInicial][darrerNombre] == null) {
            avaluarEstat(sumaInicial, darrerNombre);
        }
        return dp[sumaInicial][darrerNombre].getPartides();
    }
}
