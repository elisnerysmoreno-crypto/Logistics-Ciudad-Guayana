import java.util.*;

public class MotorLogistico {
    
    // Esta es la única versión que necesitamos: static y con lógica
    public static void calcularCaminos(Punto origen, List<Punto> todosLosPuntos) {
        // 1. Resetear datos de cálculos anteriores
        for (Punto p : todosLosPuntos) {
            p.distanciaMinima = Double.POSITIVE_INFINITY;
            p.anterior = null;
        }
        
        origen.distanciaMinima = 0;
        PriorityQueue<Punto> cola = new PriorityQueue<>();
        cola.add(origen);

        while (!cola.isEmpty()) {
            Punto u = cola.poll();
            
            for (Conexion e : u.adyacentes) {
                Punto v = e.destino;
                double distancia = e.distancia;
                double distanciaTravesia = u.distanciaMinima + distancia;
                
                if (distanciaTravesia < v.distanciaMinima) {
                    cola.remove(v); // Quitar si ya estaba para actualizar posición
                    v.distanciaMinima = distanciaTravesia;
                    v.anterior = u;
                    cola.add(v);
                }
            }
        }
    }

    public static List<Punto> obtenerRutaCorta(Punto destino) {
        List<Punto> ruta = new ArrayList<>();
        for (Punto p = destino; p != null; p = p.anterior) {
            ruta.add(p);
        }
        Collections.reverse(ruta);
        return ruta;
    }
}