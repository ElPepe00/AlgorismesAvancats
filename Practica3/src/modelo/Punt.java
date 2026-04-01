package modelo;

public class Punt {
    public double x, y;

    public Punt(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distancia(Punt altre) {
        double dx = this.x - altre.x;
        double dy = this.y - altre.y;
        return Math.sqrt(dx * dx + dy * dy);
}
}