package model;

import java.util.ArrayList;

/**
 * @author Josep Oliver y Hugo Valls
 * @date 5 may 2026
 * @name Joc
 */
public class Joc {

    private boolean[][] dpGuanyador;
    private long[][] dpPartides;
    
    // Matriu per saber si ja hem calculat un estat (memoization)
    private boolean[][] calculat;
    
    private final int limit;
    private final Teclat teclat;

    public Joc(int limit, Teclat teclat) {
        this.limit = limit;
        this.teclat = teclat;
        
        int maxNombre = teclat.getMaxNombre();
        this.dpGuanyador = new boolean[limit][maxNombre + 1];
        this.dpPartides = new long[limit][maxNombre + 1];
        this.calculat = new boolean[limit][maxNombre + 1];
    }

    /**
     * Inicia el càlcul per saber qui guanyarà la partida des d'un estat inicial.
     * @param sumaInicial Suma actual a la calculadora.
     * @param darrerNombre Darrer nombre triat (0 si és el primer torn).
     * @return true si el jugador actual (a qui li toca triar) té estratègia guanyadora.
     */
    public boolean calcularEstat(int sumaInicial, int darrerNombre) {
        // Si la suma inicial ja supera o iguala el límit, el jugador anterior va perdre.
        // Tècnicament el joc ja hauria d'haver acabat.
        if (sumaInicial >= limit) {
            return true; // El jugador actual ha guanyat (perquè l'altre ja ha passat el límit)
        }
        
        // Cridem al mètode recursiu amb memoization
        return avaluarEstat(sumaInicial, darrerNombre);
    }
    
    /**
     * Mètode de Programació Dinàmica (Top-Down amb Memoization)
     */
    private boolean avaluarEstat(int suma, int darrerNombre) {
        // Si ja l'hem calculat prèviament, retornem el resultat guardat
        if (calculat[suma][darrerNombre]) {
            return dpGuanyador[suma][darrerNombre];
        }
        
        ArrayList<Integer> movimentsValids = teclat.getMovimentsValids(darrerNombre);
        
        boolean guanyador = false;
        long partides = 0;
        
        for (int m : movimentsValids) {
            int novaSuma = suma + m;
            
            if (novaSuma >= limit) {
                // Si amb aquest moviment arribem o superem el límit,
                // és una jugada que fa perdre immediatament a qui la fa.
                // Aquest moviment acaba una partida (perdem nosaltres).
                partides++;
            } else {
                // El joc continua, passem el torn a l'altre jugador.
                // Hem de veure si l'altre jugador perd amb aquest nou estat.
                boolean guanyaSeguent = avaluarEstat(novaSuma, m);
                partides += dpPartides[novaSuma][m];
                
                // Si l'altre jugador perd (guanyaSeguent == false), 
                // significa que AQUEST moviment ens fa guanyar a nosaltres.
                if (!guanyaSeguent) {
                    guanyador = true;
                }
            }
        }
        
        // Guardem els resultats a les taules DP
        dpGuanyador[suma][darrerNombre] = guanyador;
        dpPartides[suma][darrerNombre] = partides;
        calculat[suma][darrerNombre] = true;
        
        return guanyador;
    }
    
    public long getTotalPartides(int sumaInicial, int darrerNombre) {
        if (sumaInicial >= limit) return 0;
        // Ens assegurem que s'hagi calculat
        if (!calculat[sumaInicial][darrerNombre]) {
            avaluarEstat(sumaInicial, darrerNombre);
        }
        return dpPartides[sumaInicial][darrerNombre];
    }
}
