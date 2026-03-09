import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.ArrayList;

public class PanelMapa extends JPanel {
    private List<Punto> puntos;
    private List<Punto> rutaResaltada;
    private int indiceAnimacion = 0;
    private Point posicionVehiculo;
    private InterfazPlanificador interfazPadre;
    

  // Reemplaza el constructor en PanelMapa.java
public PanelMapa(List<Punto> puntos, InterfazPlanificador padre) {
    this.puntos = puntos;
    this.interfazPadre = padre;
    this.setBackground(new Color(20, 20, 20));

  MouseAdapter ma = new MouseAdapter() {
    private Point puntoPresionado; // Para evitar el salto inicial

    @Override
    public void mousePressed(MouseEvent e) {
        puntoPresionado = e.getPoint(); // Guardamos donde empezamos
    }

    @Override
  
public void mouseDragged(MouseEvent e) {
    if (puntoPresionado != null) {
        // Al dividir por escalaZoom, el mapa se mueve a la misma velocidad que el mouse
      
        
        puntoPresionado = e.getPoint();
        repaint();
    }
}

    @Override
    public void mouseReleased(MouseEvent e) {
        puntoPresionado = null; // Limpiamos al soltar
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Lógica de detección de puntos (usando la fórmula de escala que ya corregimos)
        Punto p = obtenerPuntoEn(e.getX(), e.getY());
        if (p != null) {
            JOptionPane.showMessageDialog(null, "Cliente: " + p.nombre + "\nCarga: " + p.cargaPedido + " kg");
        }
    }
};
    this.addMouseListener(ma);
    this.addMouseMotionListener(ma);
}

// Mejora en dibujarFondo para que no se desfase
private void dibujarFondo(Graphics2D g2) {
    try {
        java.net.URL imgURL = getClass().getResource("/mi_mapa.jpeg");
        if (imgURL != null) {
            Image img = new ImageIcon(imgURL).getImage();
            // El mapa ahora se estira para cubrir todo el panel perfectamente
            g2.drawImage(img, 0, 0, getWidth(), getHeight(), this); 
        }
    } catch (Exception e) {}
}
@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    
    // Suavizado de bordes para que las líneas se vean profesionales
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 1. Dibujamos el fondo estático
    dibujarFondo(g2); 

    if (puntos.isEmpty()) return;

    // 2. Dibujar la ruta resaltada (las líneas verdes)
    if (rutaResaltada != null) {
        dibujarLineaRuta(g2);
    }

    // 3. Dibujar los marcadores de los clientes
 for (Punto p : puntos) {
    // 1. Dibujar el punto (Rojo para Depósito, Verde para Clientes)
    if (p.nombre.equalsIgnoreCase("Depósito") || p.nombre.equalsIgnoreCase("Mi Depósito")) {
        g2.setColor(Color.RED);
    } else {
        g2.setColor(Color.GREEN);
    }
    g2.fillOval(p.x - 5, p.y - 5, 10, 10);

    // 2. TRUCO DE CONTRASTE: Dibujar sombra negra primero
    g2.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Una fuente más gruesa ayuda
    g2.setColor(Color.BLACK);
    // Dibujamos el mismo nombre pero desplazado 1 píxel para crear el efecto de borde/sombra
    g2.drawString(p.nombre, p.x + 8, p.y + 1); 

    // 3. Dibujar el texto principal en blanco o amarillo brillante
    g2.setColor(Color.WHITE); 
    g2.drawString(p.nombre, p.x + 7, p.y);
}

    // 4. DIBUJAR EL VEHÍCULO (Solo si la posición no es nula)
    if (posicionVehiculo != null) {
        dibujarVehiculo(g2);
    }

    // 5. Dibujar la leyenda (encima de todo)
    dibujarLeyenda(g2);
}

 

private void dibujarMarcador(Graphics2D g2, Punto p) {
    // Usamos el rojo del pin de LogiPath para el depósito
    if (p.nombre.equalsIgnoreCase("MI DEPÓSITO") || p.nombre.equalsIgnoreCase("Depósito")) {
        g2.setColor(new Color(195, 40, 40));
    } else {
        // Verde neón para los clientes para que resalten sobre el azul
        g2.setColor(new Color(46, 204, 113));
    }
    
    // Dibujamos el punto (usamos un tamaño de 12 para que se vea mejor)
    g2.fillOval(p.x - 6, p.y - 6, 12, 12);
    
    // Texto con "borde" para legibilidad
    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
    g2.setColor(Color.BLACK);
    g2.drawString(p.nombre, p.x + 10, p.y + 5); // Sombra
    g2.setColor(Color.WHITE);
    g2.drawString(p.nombre, p.x + 9, p.y + 4);  // Texto principal
}

    private void dibujarVehiculo(Graphics2D g2) {
        g2.setColor(new Color(46, 204, 113));
        g2.fillRoundRect(posicionVehiculo.x - 10, posicionVehiculo.y - 6, 20, 12, 5, 5);
        g2.setColor(Color.WHITE);
        g2.fillRect(posicionVehiculo.x + 2, posicionVehiculo.y - 4, 6, 8); // Ventana
    }

    // ... (Mantén tus métodos dibujarLeyenda y dibujarLineaRuta igual, 
    // solo asegúrate de que usen p1.x, p1.y sin multiplicarlos por escalaZoom 
    // manualmente, ya que el AffineTransform lo hace por ti) ...
     private void dibujarLeyenda(Graphics2D g2) {

    int x = 20;

    int y = 20;

    int ancho = 180;

    int alto = 110;



    // Fondo semitransparente para la leyenda

    g2.setColor(new Color(0, 0, 0, 180));

    g2.fillRoundRect(x, y, ancho, alto, 15, 15);

    g2.setColor(new Color(255, 255, 255, 100));

    g2.drawRoundRect(x, y, ancho, alto, 15, 15);



    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

    

    // Elementos de la leyenda

    dibujarItemLeyenda(g2, x + 15, y + 25, Color.RED, "Depósito / Origen");

    dibujarItemLeyenda(g2, x + 15, y + 45, new Color(46, 204, 113), "Cliente Entregado");

    dibujarItemLeyenda(g2, x + 15, y + 65, new Color(241, 196, 15), "Cliente en Espera");

    

    // Línea de ruta

    g2.setColor(new Color(46, 204, 113));

    g2.setStroke(new BasicStroke(3));

    g2.drawLine(x + 15, y + 85, x + 30, y + 85);

    g2.setColor(Color.WHITE);

    g2.drawString("Ruta Óptima", x + 40, y + 90);

}
private void dibujarItemLeyenda(Graphics2D g2, int x, int y, Color color, String texto) {

    g2.setColor(color);

    g2.fillOval(x, y - 10, 12, 12);

    g2.setColor(Color.WHITE);

    g2.drawString(texto, x + 25, y);



    }

private void dibujarLineaRuta(Graphics2D g2) {

    for (int i = 0; i < rutaResaltada.size() - 1; i++) {

        Punto p1 = rutaResaltada.get(i);

        Punto p2 = rutaResaltada.get(i + 1);



        // --- CAPA 1: Brillo Exterior (Resplandor) ---

        g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.setColor(new Color(46, 204, 113, 40)); // Verde esmeralda muy transparente

        g2.drawLine(p1.x, p1.y, p2.x, p2.y);



        // --- CAPA 2: Línea Principal ---

        g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.setColor(new Color(46, 204, 113, 180)); // Verde sólido

        g2.drawLine(p1.x, p1.y, p2.x, p2.y);



        // --- CAPA 3: Núcleo de Luz (Efecto fibra óptica) ---

        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.setColor(Color.WHITE);

        g2.drawLine(p1.x, p1.y, p2.x, p2.y);

    }

}
// Dentro de PanelMapa.java
public void iniciarAnimacionVehiculo() {
    if (rutaResaltada == null || rutaResaltada.isEmpty()) return;
    
    indiceAnimacion = 0; 
    
    Timer timer = new Timer(2000, e -> { // Un poco más lento para que se aprecie
        if (indiceAnimacion < rutaResaltada.size()) {
            Punto pActual = rutaResaltada.get(indiceAnimacion);
            posicionVehiculo = new Point(pActual.x, pActual.y);
            
            // --- AQUÍ ESTÁ LA MAGIA ---
            // Hacemos que el slider de la interfaz principal se mueva solo
            // Esto disparará automáticamente el método generarInformeTexto()
            interfazPadre.actualizarProgresoSimulador(indiceAnimacion);
            
            indiceAnimacion++;
            repaint();
        } else {
            ((Timer)e.getSource()).stop();
            mostrarResumenViaje();
        }
    });
    timer.start();
}
private void mostrarResumenViaje() {
    // Panel personalizado para el mensaje final
    JPanel panel = new JPanel(new GridLayout(0, 1));
    panel.add(new JLabel("¡RUTA FINALIZADA CON ÉXITO!"));
    panel.add(new JLabel("Destinos visitados: " + (rutaResaltada.size() - 1)));
    panel.add(new JLabel("Eficiencia de combustible: 100%"));
    
    JOptionPane.showMessageDialog(this, panel, "LogiPath - Sistema de Rutas Mínimas", JOptionPane.INFORMATION_MESSAGE);
}
       public int getIndiceAnimacion() { return indiceAnimacion; }
public void setIndiceAnimacion(int indice) {

    this.indiceAnimacion = indice;

}

public void setRutaResaltada(List<Punto> ruta) {
    this.rutaResaltada = ruta;
    this.indiceAnimacion = 0; // Resetear para que la animación empiece de cero
    repaint();
}
private Punto obtenerPuntoEn(int x, int y) {
    // Ya no dividimos por escalaZoom ni restamos desplazamientos
    for (Punto p : puntos) {
        if (Math.abs(p.x - x) < 20 && Math.abs(p.y - y) < 20) {
            return p;
        }
    }
    return null;
}
public void setPuntos(List<Punto> nuevosPuntos) {
    this.puntos = nuevosPuntos;
    repaint();
}
}