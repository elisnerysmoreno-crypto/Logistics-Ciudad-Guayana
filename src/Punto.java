
import java.util.ArrayList;
import java.util.List;

public class Punto implements Comparable<Punto>  {
    String nombre, direccion, telefono, producto;
    int x, y;
    int cargaPedido;
    int prioridad;
    private boolean entregado = false;
    // Gestión de Ventanas Horarias
    int horaInicio, horaFin; // Ejemplo: 8 para 8:00 AM, 18 para 6:00 PM
    public int tiempoServicio = 15;
    public String ventanaEntrega = "18:00"; // Hora límite pactada
    public String eta = "--:--";          // Hora estimada de llegada (calculada)
    public boolean esRetraso = false;      // Bandera visual

    // Atributos para el algoritmo
    List<Conexion> adyacentes = new ArrayList<>();
    double distanciaMinima = Double.POSITIVE_INFINITY;
    Punto anterior;
public Punto antecesor;
   
public void setEntregado(boolean estado) {
        this.entregado = estado;
    }

    // Opcional: añade este por si lo necesitas luego
    public boolean isEntregado() {
        return entregado;
    }

    @Override
    public int compareTo(Punto otro) {
        return Double.compare(this.distanciaMinima, otro.distanciaMinima);
    }
    public Punto(String nombre, int x, int y, int cargaPedido, int horaFin) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.cargaPedido = cargaPedido;
        this.horaFin = horaFin;
        this.prioridad = 3;
        // Valores por defecto para evitar campos nulos
        this.direccion = "Sin dirección";
        this.telefono = "000-0000";
        this.producto = "General";
        this.horaInicio = 8; // Abre a las 8 AM por defecto
        this.tiempoServicio = 15; // 15 minutos de parada
    }
    
    // CONSTRUCTOR RÁPIDO (Para clics en el mapa)
    public Punto(String nombre, int x, int y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        
        // Valores por defecto para que no de error
        this.cargaPedido = 20;   // Le asignamos una carga base
        this.horaFin = 20;       // Hora límite por defecto (8 PM)
        this.direccion = "Creado en mapa";
        this.telefono = "S/N";
        this.producto = "Nuevo";
    }
}