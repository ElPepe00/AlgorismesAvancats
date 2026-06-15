package modelo;

/**
 *
 * @author Josep Oliver y Hugo Valls
 * @date 28 may 2026
 * @name CasillaEspecial
 */
public enum CasillaEspecial {
    NORMAL(0, false),
    OCA(0, true),
    PUENTE(0, false),
    DADOS(0, false),
    POSADA(1, false), // 1 turno de penalización
    POZO(2, false), // 2 turnos de penalización
    LABERINTO(0, false),
    CARCEL(3, false), // 3 turnos de penalización
    MUERTE(0, false);

    private final int turnosPenalizacion;
    private final boolean esOca;

    // Tabla de búsqueda estática (O(1)) para optimizar la simulación Monte Carlo
    private static final CasillaEspecial[] TABLERO = new CasillaEspecial[64];

    static {
        for (int i = 0; i < 64; i++) {
            TABLERO[i] = NORMAL;
        }
        int[] ocas = { 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 };
        for (int oca : ocas) {
            TABLERO[oca] = OCA;
        }
        TABLERO[6] = PUENTE;
        TABLERO[12] = PUENTE;
        TABLERO[26] = DADOS;
        TABLERO[53] = DADOS;
        TABLERO[19] = POSADA;
        TABLERO[31] = POZO;
        TABLERO[42] = LABERINTO;
        TABLERO[52] = CARCEL;
        TABLERO[58] = MUERTE;
    }

    CasillaEspecial(int turnosPenalizacion, boolean esOca) {
        this.turnosPenalizacion = turnosPenalizacion;
        this.esOca = esOca;
    }

    /**
     * Obtiene el número de turnos de penalización asociados a la casilla.
     */
    public int getTurnosPenalizacion() {
        return turnosPenalizacion;
    }

    /**
     * Indica si la casilla es de tipo Oca.
     */
    public boolean esOca() {
        return esOca;
    }

    /**
     * Obtiene el tipo de casilla especial asociado a un número de casilla
     * específico.
     */
    public static CasillaEspecial obtenerPorCasilla(int casilla) {
        if (casilla < 0 || casilla > 63) return NORMAL;
        return TABLERO[casilla];
    }
}