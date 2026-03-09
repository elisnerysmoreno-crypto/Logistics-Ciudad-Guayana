public class Conexion {
    Punto destino;
    double distancia; // <--- Verifica que se llame exactamente 'distancia'
    boolean activa = true;

    public Conexion(Punto destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }
}