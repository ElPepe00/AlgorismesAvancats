package datos;

import java.util.ArrayList;

/**
 *
 * @author Josep Oliver
 * @date 6 mar 2026
 * @name Reina
 */
public class Reina extends PecaNoBota {

    private final int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    public Reina(int id) {
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
