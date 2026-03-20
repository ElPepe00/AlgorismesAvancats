

package controlador;

import datos.Peca;
import java.util.ArrayList;
import java.util.List;
import vista.Vista;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 5 mar 2026
 * @name Practica 2
 */

/**
 * Classe que s'encarrega de la lògica de resolució (Backtracking).
 * S'executa com un Thread per no bloquejar la Vista principal.
 */
public class MotorCerca extends Thread {
    private int[][] t;
    private Peca p1, p2;
    private int dim;
    private int retard;
    private volatile boolean aturat = false;
    private Vista vista; // Referència per enviar ordres a la UI

    // CONSTRUCTOR
    public MotorCerca(int[][] tauler, Peca p1, Peca p2, int dim, int retard, Vista vista) {
        this.t = tauler;
        this.p1 = p1;
        this.p2 = p2;
        this.dim = dim;
        this.retard = retard;
        this.vista = vista;
    }

    public void aturar() {
        aturat = true;
    }

    @Override
    public void run() {
        long tempsInici = System.currentTimeMillis();
        
        // Cridem al Backtracking
        boolean trobat = resoldre(1, -1, -1, -1, -1);

        long tempsTotal = System.currentTimeMillis() - tempsInici;

        // Avisem a la Vista que hem acabat
        vista.fiCerca(aturat, trobat, tempsTotal);
    }

    /**
     * Mètode que aplica l'algorisme de backtracking
     * @return 
     */
    private boolean resoldre(int passa, int f1, int c1, int f2, int c2) {
        
        if (aturat) {
            return false;
        }
        
        // Cas base
        if (passa > dim * dim) {
            return true;
        }

        boolean tornPeca1 = (passa % 2 != 0);
        Peca pecaActual = tornPeca1 ? p1 : p2;
        Peca pecaAltra = tornPeca1 ? p2 : p1;
        
        int fActual = tornPeca1 ? f1 : f2;
        int cActual = tornPeca1 ? c1 : c2;
        int fAltre = tornPeca1 ? f2 : f1;
        int cAltre = tornPeca1 ? c2 : c1;

        List<int[]> moviments;

        if (passa <= 2) {
            moviments = obtenirTotesCasellesLliures();
        } else {
            moviments = pecaActual.getMovimentsPossibles(t, fActual, cActual, dim);
            
            //RREGLA DE WARNSDORFF
            //Ordenam prioritzant les caselles amb menys sortides futures lliures
            moviments.sort((m1, m2) -> {
                int accessos1 = comptarMovimentsFuturs(m1[0], m1[1], pecaActual);
                int accessos2 = comptarMovimentsFuturs(m2[0], m2[1], pecaActual);
                return Integer.compare(accessos1, accessos2);
            });
        }

        for (int[] mov : moviments) {
            int nf = mov[0];
            int nc = mov[1];

            // REGLA DE NO CAPTURA
            if (fAltre != -1) { 
                if (pecaActual.ataca(t, nf, nc, fAltre, cAltre, dim)){
                    continue;
                }
                if (pecaAltra.ataca(t, fAltre, cAltre, nf, nc, dim)) {
                    continue;
                }
            }

            // Marcar
            t[nf][nc] = passa;
            
            // Renderitzar i pausar si s'ha demanat
            if (retard > 0) {
                vista.repintarTauler();
                try { Thread.sleep(retard); } catch (InterruptedException e) { aturat = true; }
            }

            // Crida recursiva
            boolean trobat;
            if (tornPeca1) {
                trobat = resoldre(passa + 1, nf, nc, fAltre, cAltre);
            } else {
                trobat = resoldre(passa + 1, fAltre, cAltre, nf, nc);
            }

            if (trobat) return true;

            // Desmarcar (Backtracking)
            t[nf][nc] = 0;
        }

        return false;
    }

    /**
     * Mètode que retorna totes les caselles lliures
     * @return una llista amb totes les caselles lliures
     */
    private ArrayList<int[]> obtenirTotesCasellesLliures() {
        ArrayList<int[]> lliures = new ArrayList<>();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (t[i][j] == 0) lliures.add(new int[]{i, j});
            }
        }
        return lliures;
    }
    
    /**
     * Mètode que simula quants de moviments tindria la peça si es col·loqués a la casella (f,c)
     * @return el nombre de moviments futurs
     */
    private int comptarMovimentsFuturs(int f, int c, Peca peca) {
        List<int[]> futurs = peca.getMovimentsPossibles(t, f, c, dim);
        return futurs.size();
    }
}
