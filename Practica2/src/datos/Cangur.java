

package datos;

import java.util.ArrayList;

/**
 *
 * @author Josep Oliver
 * @date 6 mar 2026
 * @name Cangur
 */
public class Cangur extends Peca {

    // Salta exactament 2 caselles en qualsevol direcció
    private final int[][] dirs = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}, {2, 2}, {-2, -2}, {2, -2}, {-2, 2}};
    
    public Cangur(int id) { 
        super(id); 
    }

    @Override 
    public ArrayList<int[]> getMovimentsPossibles(int[][] tauler, int f, int c, int dim) {
        ArrayList<int[]> movs = new ArrayList<>();
        for (int[] d : dirs) {
            int nf = f + d[0];
            int nc = c + d[1];
            if (dinsTauler(nf, nc, dim) && tauler[nf][nc] == 0) {
                movs.add(new int[]{nf, nc});
            }
        }
        return movs;
    }

    @Override 
    public boolean ataca(int[][] tauler, int fO, int cO, int fD, int cD, int dim) {
        for (int[] d : dirs) {
            if (fO + d[0] == fD && cO + d[1] == cD) return true;
        }
        return false;
    }

}
