

package datos;

import java.util.ArrayList;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 6 mar 2026
 * @name PecaNoBota
 */

/**
 * Classe intermèdia per agrupar la lògica de les peces que "rellisquen"
 * i es bloquegen quan troben un obstacle (Torre, Òrfil, Reina).
 */
public class PecaNoBota extends Peca {

    public PecaNoBota(int id) {
        super(id);
    }

    protected void afegirSiLliure(int[][] tauler, int f, int c, int dim, int[][] dirs, ArrayList<int[]> movs) {
        for (int[] d : dirs) {
            int nf = f + d[0];
            int nc = c + d[1];
            while (dinsTauler(nf, nc, dim)) {
                if (tauler[nf][nc] == 0) {
                    movs.add(new int[]{nf, nc});
                } else {
                    break; // Bloquejat per una altra casella ocupada
                }
                nf += d[0];
                nc += d[1];
            }
        }
    }

    protected boolean comprovarAtacLliscant(int[][] tauler, int fO, int cO, int fD, int cD, int dim, int[][] dirs) {
        for (int[] d : dirs) {
            int nf = fO + d[0];
            int nc = cO + d[1];
            while (dinsTauler(nf, nc, dim)) {
                if (nf == fD && nc == cD) {
                    return true;
                }
                if (tauler[nf][nc] != 0) {
                    break; // Línia de visió bloquejada
                }
                nf += d[0];
                nc += d[1];
            }
        }
        return false;
    }

    @Override
    public ArrayList<int[]> getMovimentsPossibles(int[][] tauler, int f, int c, int dim) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean ataca(int[][] tauler, int fO, int cO, int fD, int cD, int dim) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
