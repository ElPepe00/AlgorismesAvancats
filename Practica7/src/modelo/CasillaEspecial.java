package modelo;

import java.util.Arrays;

/**
 * Representa los diferentes tipos de casillas especiales del Juego de la Oca
 * y sus respectivas reglas de negocio asociadas.
 * @author Josep Oliver y Hugo Valls
 * 
 * @date 10 jun 2026
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

    CasillaEspecial(int turnosPenalizacion, boolean esOca) {
        this.turnosPenalizacion = turnosPenalizacion;
        this.esOca = esOca;
    }

    public int getTurnosPenalizacion() {
        return turnosPenalizacion;
    }

    public boolean esOca() {
        return esOca;
    }

    /**
     * Obtiene el tipo de casilla especial asociado a un número de casilla
     * específico.
     */
    public static CasillaEspecial obtenerPorCasilla(int casilla) {
        int[] ocas = { 5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59 };
        if (Arrays.stream(ocas).anyMatch(x -> x == casilla)) {
            return OCA;
        }

        switch (casilla) {
            case 6:
            case 12:
                return PUENTE;
            case 26:
            case 53:
                return DADOS;
            case 19:
                return POSADA;
            case 31:
                return POZO;
            case 42:
                return LABERINTO;
            case 52:
                return CARCEL;
            case 58:
                return MUERTE;
            default:
                return NORMAL;
        }
    }
}