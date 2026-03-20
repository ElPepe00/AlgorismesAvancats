

package datos;

import java.util.ArrayList;

/**
 *
 * @author Josep Oliver i Hugo Valls
 * @date 5 mar 2026
 * @name Practica 2
 */
/**
 * Classe base abstracta de la que hereten totes les peces. Conté la factoria
 * per crear les peces des de la Vista.
 */
public abstract class Peca {

    protected int id; // 1 (Peca 1) o 2 (Peca 2)

    public Peca(int id) {
        this.id = id;
    }

    public abstract ArrayList<int[]> getMovimentsPossibles(int[][] tauler, int f, int c, int dim);

    public abstract boolean ataca(int[][] tauler, int fO, int cO, int fD, int cD, int dim);

    protected boolean dinsTauler(int f, int c, int dim) {
        return f >= 0 && f < dim && c >= 0 && c < dim;
    }

    /**
     * Mètode estàtic per instanciar la peça corresponent segons el nom
     */
    public static Peca crearPeca(String nom, int id) {
        if (nom.startsWith("Cavall")) {
            return new Cavall(id);
        }
        if (nom.startsWith("Torre")) {
            return new Torre(id);
        }
        if (nom.startsWith("Òrfil")) {
            return new Orfil(id);
        }
        if (nom.startsWith("Reina")) {
            return new Reina(id);
        }
        if (nom.startsWith("Cangur")) {
            return new Cangur(id);
        }
        if (nom.startsWith("Assassí")) {
            return new Assassi(id);
        }
        return new Cavall(id); // Per defecte
    }
}
