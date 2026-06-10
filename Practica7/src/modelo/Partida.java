package modelo;

/**
 * Gestiona el estado interactivo de una partida multijugador del Juego de la Oca.
 * Mantiene el control de posiciones, turnos y penalizaciones en tiempo real.
 */
public class Partida {
    
    private final int numJugadores;
    private final int[] posiciones;
    private final int[] penalizaciones;
    private int turnoActual;
    private boolean partidaTerminada;

    public Partida(int numJugadores) {
        this.numJugadores = numJugadores;
        this.posiciones = new int[numJugadores];
        this.penalizaciones = new int[numJugadores];
        this.turnoActual = 0; // Empieza el Jugador 1 (índice 0)
        this.partidaTerminada = false;
    }

    /**
     * Ejecuta el avance del jugador actual en base a una tirada de dado real.
     * @return Mensaje descriptivo con lo que ha sucedido en el turno para mostrar en pantalla.
     */
    public String jugarTurno(int dado) {
        if (partidaTerminada) return "La partida ya ha concluido.";

        // Comprobar penalizaciones (Cárcel, Pozo, Posada)
        if (penalizaciones[turnoActual] > 0) {
            penalizaciones[turnoActual]--;
            String msg = "El Jugador " + (turnoActual + 1) + " pierde el turno por penalización (" + penalizaciones[turnoActual] + " restantes).";
            avanzarTurno();
            return msg;
        }

        int posInicial = posiciones[turnoActual];
        int nuevaPos = posInicial + dado;
        
        // Rebote si se pasa de 63
        if (nuevaPos > 63) {
            nuevaPos = 63 - (nuevaPos - 63);
        }

        String mensaje = "Jugador " + (turnoActual + 1) + " saca un " + dado + " y avanza a la casilla " + nuevaPos + ". ";

        if (nuevaPos == 63) {
            posiciones[turnoActual] = 63;
            partidaTerminada = true;
            return mensaje + "¡HA GANADO LA PARTIDA!";
        }

        // Efectos especiales
        CasillaEspecial efecto = CasillaEspecial.obtenerPorCasilla(nuevaPos);
        boolean turnoExtra = false;

        switch (efecto) {
            case OCA:
                // Reutilizamos la lógica del PDF: 5->9, 9->14...
                nuevaPos = obtenerSiguienteOca(nuevaPos);
                mensaje += "¡De Oca a Oca! Salta a la casilla " + nuevaPos + " y tira de nuevo.";
                turnoExtra = true;
                break;
            case PUENTE:
                nuevaPos = (nuevaPos == 6) ? 12 : 6;
                mensaje += "¡De puente a puente! Salta a la casilla " + nuevaPos + ".";
                break;
            case DADOS:
                nuevaPos = (nuevaPos == 26) ? 53 : 26;
                mensaje += "¡De dado a dado! Salta a la casilla " + nuevaPos + ".";
                break;
            case LABERINTO:
                nuevaPos = 30;
                mensaje += "¡Laberinto! Retrocede a la casilla 30.";
                break;
            case MUERTE:
                nuevaPos = 0;
                mensaje += "¡Calavera! Vuelve a la casilla de inicio.";
                break;
            case POSADA: case POZO: case CARCEL:
                penalizaciones[turnoActual] = efecto.getTurnosPenalizacion();
                mensaje += "¡Cae en casilla especial! Pierde " + efecto.getTurnosPenalizacion() + " turno(s).";
                break;
            default:
                break; // Casilla NORMAL
        }

        posiciones[turnoActual] = nuevaPos;

        if (nuevaPos == 63) {
            partidaTerminada = true;
            mensaje += " ¡HA GANADO LA PARTIDA!";
        } else if (!turnoExtra) {
            avanzarTurno();
        }

        return mensaje;
    }

    /**
     * Obtiene el estado actual de las posiciones de todos los jugadores.
     */
    public int[] getPosiciones() {
        return posiciones.clone();
    }

    /**
     * Obtiene el índice del jugador cuyo turno está activo.
     */
    public int getTurnoActual() { return turnoActual; }

    /**
     * Indica si la partida ha finalizado.
     */
    public boolean isPartidaTerminada() { return partidaTerminada; }

    /**
     * Avanza el turno al siguiente jugador de forma cíclica.
     */
    private void avanzarTurno() {
        turnoActual = (turnoActual + 1) % numJugadores;
    }

    /**
     * Obtiene la siguiente casilla de Oca en el tablero.
     */
    private int obtenerSiguienteOca(int c) {
        switch (c) {
            case 5: return 9; case 9: return 14; case 14: return 18; case 18: return 23; case 23: return 27;
            case 27: return 32; case 32: return 36; case 36: return 41; case 41: return 45; case 45: return 50;
            case 50: return 54; case 54: return 59; case 59: return 63;
            default: return c;
        }
    }
}