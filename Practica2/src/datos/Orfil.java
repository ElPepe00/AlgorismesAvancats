

package datos;

import java.util.ArrayList;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 6 mar 2026
 * @name Orfil
 */
public class Orfil extends PecaNoBota {

    private final int[][] dirs = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    public Orfil(int id) {
        super(id);
    }

    @Override
    public ArrayList<int[]> getMovimentsPossibles(int[][] tauler, int f, int c, int dim) {
        ArrayList<int[]> movs = new ArrayList<>();
        afegirSiLliure(tauler, f, c, dim, dirs, movs);
        return movs;
    }

    @Override
    public boolean ataca(int[][] tauler, int fO, int cO, int fD, int cD, int dim) {
        return comprovarAtacLliscant(tauler, fO, cO, fD, cD, dim, dirs);
    }
}
