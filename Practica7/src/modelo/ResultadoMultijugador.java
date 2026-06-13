package modelo;

public class ResultadoMultijugador {

    private final long[] victorias;
    private final Estadisticas estadisticas;

    public ResultadoMultijugador(
            long[] victorias,
            Estadisticas estadisticas) {

        this.victorias = victorias;
        this.estadisticas = estadisticas;
    }

    public long[] getVictorias() {
        return victorias;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
}