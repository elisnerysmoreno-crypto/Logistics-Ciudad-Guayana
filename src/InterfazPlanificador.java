import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class InterfazPlanificador extends JFrame {
    // ATRIBUTOS DE DATOS
    private List<Punto> mapa = new ArrayList<>();
    private List<Punto> rutaResaltada = new ArrayList<>();
    private PanelMapa panelDibujo;
    private JButton btnPlanificar;
    private JEditorPane txtResultado; 

    // COMPONENTES DEL DASHBOARD
    private JLabel lblKpiEntregas, lblKpiEficiencia, lblKpiCarga, lblKpiDistancia;
    private JProgressBar barraCargaGlobal;
    private JSlider sliderTrafico; // NUEVO: Simulador de tráfico
    
    private final int CAPACIDAD_MAXIMA_KG = 300;
    private double distanciaTotalAcumulada = 0;
    private final double PRECIO_GASOLINA_LITRO = 0.50; // Configurado para Ciudad Guayana

    public InterfazPlanificador(List<Punto> mapaCargado) {
    this.mapa = mapaCargado;
        setTitle("Sistema de Rutas Mínimas - Dashboard Pro");
        setSize(1250, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        for (Punto p1 : mapa) {
    for (Punto p2 : mapa) {
        if (p1 != p2) {
            double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
            // Creamos la conexión si no existe
            p1.adyacentes.add(new Conexion(p2, dist));
        }
    }
}

        if (mapa == null) mapa = new ArrayList<>(); 
// --- PANEL LATERAL (Control y Reporte) ---
       // --- PANEL LATERAL (Control y Reporte) ---
JPanel panelControl = new JPanel();
panelControl.setLayout(new BoxLayout(panelControl, BoxLayout.Y_AXIS));
// CAMBIO: Azul marino profundo con transparencia (Alpha = 200)
panelControl.setBackground(new Color(10, 25, 50, 200)); 
panelControl.setOpaque(true); // Para que se vea el color
panelControl.setPreferredSize(new Dimension(260, 0));
panelControl.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(46, 204, 113))); // Borde neón izquierdo

        // 1. BOTÓN OPTIMIZAR
        btnPlanificar = new JButton("OPTIMIZAR LOGÍSTICA");
        btnPlanificar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPlanificar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnPlanificar.setBackground(new Color(46, 204, 113)); 
        btnPlanificar.setForeground(Color.WHITE);
        btnPlanificar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPlanificar.setCursor(new Cursor(Cursor.HAND_CURSOR));
// Busca donde creaste btnPlanificar y agrega esto:
btnPlanificar.addActionListener(e -> ejecutarPlanificacion());
        // 2. BOTÓN EXPORTAR
        JButton btnExportar = new JButton("EXPORTAR REPORTE (HTML)");
        btnExportar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExportar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnExportar.setBackground(new Color(52, 152, 219)); 
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExportar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExportar.addActionListener(e -> exportarGuiaEntrega());

        // 3. BOTÓN REINICIAR (RESET)
        JButton btnReset = new JButton("REINICIAR PLANIFICACIÓN");
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnReset.setBackground(new Color(192, 57, 43)); 
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> {
            rutaResaltada.clear();
            distanciaTotalAcumulada = 0;
            panelDibujo.setRutaResaltada(new ArrayList<>());
            panelDibujo.setIndiceAnimacion(-1);
            actualizarDashboard(0, mapa.size() - 1, 0);
            generarInformeTexto();
            panelDibujo.repaint();
        });

        // 4. SIMULADOR DE TRÁFICO
        JLabel lblTrafico = new JLabel("SIMULADOR DE TRÁFICO (ETA)");
        lblTrafico.setForeground(new Color(150, 150, 150));
        lblTrafico.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTrafico.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sliderTrafico = new JSlider(1, 5, 1);
        sliderTrafico.setBackground(new Color(30, 30, 30));
        sliderTrafico.setPaintTicks(true);
        sliderTrafico.setMajorTickSpacing(1);
        sliderTrafico.setSnapToTicks(true);
        sliderTrafico.addChangeListener(e -> generarInformeTexto());

        // 5. ÁREA DE TEXTO (REPORTE)
        txtResultado = new JEditorPane();
        txtResultado.setEditable(false);
        txtResultado.setContentType("text/html");
        txtResultado.setBackground(new Color(25, 25, 25));
        JScrollPane scroll = new JScrollPane(txtResultado);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        // --- AGREGAR TODO AL PANEL CONTROL EN ORDEN ---
        panelControl.add(btnPlanificar);
        panelControl.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControl.add(btnExportar);
        panelControl.add(Box.createRigidArea(new Dimension(0, 10)));
        panelControl.add(btnReset);
        panelControl.add(Box.createRigidArea(new Dimension(0, 20)));
        panelControl.add(lblTrafico);
        panelControl.add(sliderTrafico);
        panelControl.add(Box.createRigidArea(new Dimension(0,15)));
        panelControl.add(scroll);

        // --- ENSAMBLE FINAL ---
    // --- ENSAMBLE FINAL MODERNO ---
panelDibujo = new PanelMapa(mapa, this);

// Creamos un contenedor para que el mapa no pegue de los bordes
JPanel contenedorMapa = new JPanel(new BorderLayout());
contenedorMapa.setBackground(new Color(20, 40, 80)); // Tu azul de fondo
contenedorMapa.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Márgenes
contenedorMapa.add(panelDibujo, BorderLayout.CENTER);

add(panelControl, BorderLayout.EAST);
add(contenedorMapa, BorderLayout.CENTER); // El mapa ahora está "enmarcado"
add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }
    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));
        panel.setBackground(new Color(18, 18, 18));
        panel.setPreferredSize(new Dimension(0, 100));
        panel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(46, 204, 113)));
        

        lblKpiEntregas = new JLabel("0 / 0", SwingConstants.CENTER);
        panel.add(crearCajaKpi("ESTADO ENTREGAS", lblKpiEntregas, new Color(52, 152, 219)));

        lblKpiEficiencia = new JLabel("0%", SwingConstants.CENTER);
        panel.add(crearCajaKpi("EFICIENCIA", lblKpiEficiencia, new Color(241, 196, 15)));

        lblKpiDistancia = new JLabel("0.00 km", SwingConstants.CENTER);
        panel.add(crearCajaKpi("DISTANCIA TOTAL", lblKpiDistancia, new Color(155, 89, 182)));

        lblKpiCarga = new JLabel("0 kg", SwingConstants.CENTER);
        panel.add(crearCajaKpi("CARGA ACTUAL", lblKpiCarga, Color.WHITE));
        

        JPanel pProgreso = new JPanel(new BorderLayout());
        pProgreso.setOpaque(false);
        pProgreso.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));
        barraCargaGlobal = new JProgressBar(0, CAPACIDAD_MAXIMA_KG);
        barraCargaGlobal.setStringPainted(true);
        barraCargaGlobal.setForeground(new Color(46, 204, 113));
       JLabel lblOcu = new JLabel("OCUPACIÓN", SwingConstants.CENTER);
lblOcu.setForeground(Color.WHITE); // Esto la hace visible sobre el fondo negro
pProgreso.add(lblOcu, BorderLayout.NORTH);
        pProgreso.add(barraCargaGlobal, BorderLayout.CENTER);
        panel.add(pProgreso);

        return panel;
    }

 private JPanel crearCajaKpi(String titulo, JLabel lblValor, Color colorValor) {
    JPanel p = new JPanel(new BorderLayout());
    p.setOpaque(false);
    
    JLabel lblT = new JLabel(titulo, SwingConstants.CENTER);
    lblT.setFont(new Font("Segoe UI", Font.BOLD, 11)); // Más grande
    lblT.setForeground(new Color(180, 180, 180));
    
    lblValor.setFont(new Font("Arial Black", Font.BOLD, 18)); // Una fuente más pesada
    lblValor.setForeground(colorValor);
    
    p.add(lblT, BorderLayout.NORTH);
    p.add(lblValor, BorderLayout.CENTER);
    return p;
}

   private void actualizarDashboard(int entregas, int total, int carga) {
    lblKpiEntregas.setText(entregas + " / " + total);
    int porc = (total == 0) ? 0 : (entregas * 100 / total);
    lblKpiEficiencia.setText(porc + "%");
    lblKpiCarga.setText(carga + " kg");
    
    // CAMBIO: Lógica de alerta visual
    if (carga > CAPACIDAD_MAXIMA_KG) {
        lblKpiCarga.setForeground(Color.RED);
        // Efecto opcional: Si quieres que "brille", podemos usar un rojo neón
        lblKpiCarga.setText("" + carga + " kg"); 
    } else {
        lblKpiCarga.setForeground(Color.WHITE);
    }
    
    lblKpiDistancia.setText(String.format("%.2f km", distanciaTotalAcumulada));
    barraCargaGlobal.setValue(carga);
    barraCargaGlobal.setForeground(carga > CAPACIDAD_MAXIMA_KG ? new Color(231, 76, 60) : new Color(46, 204, 113));
}

public void generarInformeTexto() {
    int entregasOk = 0;
    int totalClientes = 0;
    int tiempoInicial = 480; // 08:00 AM
    int factorTrafico = sliderTrafico.getValue();
    int indiceActualVehiculo = panelDibujo.getIndiceAnimacion();

    // CALCULO DEL TIEMPO ACTUAL EN VIVO
    int minutosTranscurridos = (indiceActualVehiculo * 15 * factorTrafico);
    int tiempoSimulacionActual = tiempoInicial + minutosTranscurridos;
    
    int horaActual = tiempoSimulacionActual / 60;
    int minActual = tiempoSimulacionActual % 60;
    String relojVivo = String.format("%02d:%02d %s", 
                        (horaActual > 12 ? horaActual - 12 : (horaActual == 0 ? 12 : horaActual)), 
                        minActual, 
                        (horaActual >= 12 ? "PM" : "AM"));

    StringBuilder sb = new StringBuilder("<html><body style='font-family:Segoe UI; background:#191919; margin:5px;'>");
    
    // --- RELOJ DE OPERACIONES ---
    sb.append("<div style='background:#002b5b; color:white; padding:8px; border-radius:5px; text-align:center; margin-bottom:10px; border:1px solid #3498db;'>");
    sb.append("<small style='opacity:0.8; font-size:9px;'>RELOJ DE OPERACIONES</small><br>");
    sb.append("<b style='font-size:16px; color:#51ff00;'> ").append(relojVivo).append("</b>");
    sb.append("</div>");

    // --- TABLA DE COSTOS ---
    double litrosTotales = distanciaTotalAcumulada * 0.15; 
    double costoTotal = litrosTotales * PRECIO_GASOLINA_LITRO;
    
    sb.append("<div style='background:#252525; padding:6px; border-radius:5px; border:1px solid #444; margin-bottom:10px;'>");
    sb.append("<table width='100%'><tr>")
      .append("<td style='color:#f1c40f; font-size:11px;'><b> COSTOS:</b></td>")
      .append("<td style='color:white; font-size:11px;'>⛽ ").append(String.format("%.2f L", litrosTotales)).append("</td>")
      .append("<td align='right' style='color:#2ecc71; font-size:11px;'><b>$").append(String.format("%.2f", costoTotal)).append("</b></td>")
      .append("</tr></table></div>");

    int tiempoAcumuladoMinutos = tiempoInicial;

    for (Punto p : rutaResaltada) {
        if (p.nombre.equalsIgnoreCase("Depósito")) continue;
        totalClientes++;
        
        boolean visitado = indiceActualVehiculo >= rutaResaltada.indexOf(p);
        boolean enCamino = (indiceActualVehiculo + 1) == rutaResaltada.indexOf(p);
        
        int horaLlegada = tiempoAcumuladoMinutos / 60;
        int minLlegada = tiempoAcumuladoMinutos % 60;
        String etaStr = String.format("%02d:%02d", horaLlegada, minLlegada);
        boolean fueraDeHorario = (horaLlegada >= p.horaFin);

        if (visitado) entregasOk++;

        // --- AJUSTE DE COLORES SEGÚN TU PEDIDO ---
        String statusText;
        String colorBadge;
        String colorFondoStatus;
        String colorTextoStatus;

        if (visitado) {
            statusText = "LISTO";
            colorBadge = "#2ecc71";      // Verde esmeralda
            colorFondoStatus = "#2ecc71"; // Fondo verde sólido
            colorTextoStatus = "white";   // Texto blanco para contraste
        } else if (enCamino) {
            statusText = "EN RUTA";
            colorBadge = "#f1c40f";      // Amarillo
            colorFondoStatus = "#fff3cd"; 
            colorTextoStatus = "#856404";
        } else {
            statusText = fueraDeHorario ? "RETRASO" : "PEND.";
            colorBadge = fueraDeHorario ? "#e74c3c" : "#7f8c8d"; // Rojo o Gris
            colorFondoStatus = "#eee";
            colorTextoStatus = "#444";
        }

        // --- DISEÑO DE TARJETA ---
        sb.append("<div style='background:white; border-radius:8px; padding:10px; margin-bottom:8px; border-left:5px solid ").append(colorBadge).append(";'>");
        sb.append("<table width='100%'><tr>")
          .append("<td width='45%'><b style='font-size:12px; color:#002b5b;'>").append(p.nombre.toUpperCase()).append("</b></td>")
          .append("<td width='35%' style='font-size:10px; color:#333;'>🕒 ").append(etaStr).append("<br>📦 ").append(p.cargaPedido).append(" kg</td>")
          .append("<td width='20%' align='right'>")
          .append("<div style='background:").append(colorFondoStatus).append("; color:").append(colorTextoStatus)
          .append("; padding:3px; border-radius:4px; font-size:8px; font-weight:bold; text-align:center;'>")
          .append(statusText).append("</div></td>")
          .append("</tr></table></div>");
        
        tiempoAcumuladoMinutos += (15 * factorTrafico); 
    }

    lblKpiEntregas.setText(entregasOk + " / " + totalClientes);
    sb.append("</body></html>");
    txtResultado.setText(sb.toString());
}
private void ejecutarPlanificacion() {
    if (mapa.size() < 2) return;
    
    rutaResaltada = new ArrayList<>();
    distanciaTotalAcumulada = 0; 
    int cargaTotalRuta = 0; 

    // 1. Limpiar estados previos (Usamos 'anterior' para coincidir con tu Dijkstra)
    for (Punto p : mapa) {
        p.distanciaMinima = Double.POSITIVE_INFINITY;
        p.anterior = null; // Cambiado de antecesor a anterior
    }

    Punto inicio = mapa.get(0);
    rutaResaltada.add(inicio); // El camino empieza en el depósito

    for (int i = 1; i < mapa.size(); i++) {
        Punto destino = mapa.get(i);
        cargaTotalRuta += destino.cargaPedido; 

        calcularRuta(inicio); 
        
        if (destino.distanciaMinima != Double.POSITIVE_INFINITY) {
            distanciaTotalAcumulada += (destino.distanciaMinima * 0.12);
            
            // --- 2. RECONSTRUIR EL CAMINO (¡ESTO ES LO QUE FALTABA!) ---
            List<Punto> tramo = new ArrayList<>();
            Punto aux = destino;
            while (aux != null && aux != inicio) {
                tramo.add(0, aux); // Agregamos al inicio para mantener el orden
                aux = aux.anterior;
            }
            rutaResaltada.addAll(tramo);
            // ---------------------------------------------------------
            
        } else {
            double distDirecta = Math.sqrt(Math.pow(destino.x - inicio.x, 2) + Math.pow(destino.y - inicio.y, 2));
            distanciaTotalAcumulada += (distDirecta * 0.12);
            destino.distanciaMinima = distDirecta;
            rutaResaltada.add(destino); // Agregamos el punto aunque no haya "calle"
        }
        inicio = destino; 
    }
    // --- REGRESO AL DEPÓSITO ---
Punto deposito = mapa.get(0);
Punto ultimoCliente = mapa.get(mapa.size() - 1);

// Calculamos la vuelta
calcularRuta(ultimoCliente); // Dijkstra desde el último cliente al depósito
if (deposito.distanciaMinima != Double.POSITIVE_INFINITY) {
    distanciaTotalAcumulada += (deposito.distanciaMinima * 0.12);
    
    // Reconstruimos el camino de regreso
    Punto aux = deposito;
    List<Punto> tramoRegreso = new ArrayList<>();
    while (aux != null && aux != ultimoCliente) {
        tramoRegreso.add(0, aux);
        aux = aux.anterior;
    }
    rutaResaltada.addAll(tramoRegreso);
}

    actualizarDashboard(mapa.size() - 1, mapa.size() - 1, cargaTotalRuta);
    
    // 3. Pasamos la lista con los puntos al panel
    panelDibujo.setRutaResaltada(rutaResaltada);
    generarInformeTexto(); 
    panelDibujo.iniciarAnimacionVehiculo();
}
    public void calcularRuta(Punto origen) {
        for (Punto p : mapa) { p.distanciaMinima = Double.POSITIVE_INFINITY; p.anterior = null; }
        origen.distanciaMinima = 0;
        PriorityQueue<Punto> cola = new PriorityQueue<>();
        cola.add(origen);
        while (!cola.isEmpty()) {
            Punto u = cola.poll();
            for (Conexion c : u.adyacentes) {
                if (!c.activa) continue;
                double v = u.distanciaMinima + c.distancia;
                if (v < c.destino.distanciaMinima) {
                    cola.remove(c.destino);
                    c.destino.distanciaMinima = v;
                    c.destino.anterior = u;
                    cola.add(c.destino);
                }
            }
        }
    }

   private void exportarGuiaEntrega() {
  StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html lang='es'><head>");
    html.append("<meta charset='UTF-8'><title>Reporte de Ruta - LogiPath Pro</title>");
    // Importamos Bootstrap para que se vea moderno sin esfuerzo
    html.append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css' rel='stylesheet'>");
    html.append("<style>");
    html.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; }");
    html.append(".header-blue { background: #002b5b; color: white; padding: 30px; border-radius: 0 0 20px 20px; }");
    html.append(".card-resumen { border: none; border-radius: 15px; shadow: 0 4px 6px rgba(0,0,0,0.1); margin-top: -20px; }");
    html.append(".status-ok { color: #2ecc71; font-weight: bold; }");
    html.append(".table thead { background: #eef2f7; }");
    html.append("@media print { .no-print { display: none; } }"); // Para ocultar botones al imprimir a PDF
    html.append("</style></head><body>");

    // --- ENCABEZADO ---
    html.append("<div class='header-blue text-center'>");
    html.append("<h1>LOGIPATH PRO <small style='font-size: 0.5em; opacity: 0.8;'>Sistema de Rutas Mínimas</small></h1>");
    html.append("<p>Reporte de Operaciones | Ciudad Guayana | ").append(java.time.LocalDate.now()).append("</p>");
    html.append("</div>");

    html.append("<div class='container my-4'>");

    // --- FILA DE KPIs ---
    html.append("<div class='row g-3 card-resumen bg-white p-4 shadow-sm mb-4'>");
    html.append("<div class='col-md-3 text-center'><h6>Distancia Total</h6><h4 class='text-primary'>").append(lblKpiDistancia.getText()).append("</h4></div>");
    html.append("<div class='col-md-3 text-center'><h6>Eficiencia</h6><h4 class='text-success'>").append(lblKpiEficiencia.getText()).append("</h4></div>");
    html.append("<div class='col-md-3 text-center'><h6>Carga Total</h6><h4 class='text-warning'>").append(lblKpiCarga.getText()).append("</h4></div>");
    html.append("<div class='col-md-3 text-center'><h6>Gasto Estimado</h6><h4 class='text-danger'>$").append(String.format("%.2f", (distanciaTotalAcumulada * 0.15 * PRECIO_GASOLINA_LITRO))).append("</h4></div>");
    html.append("</div>");

    // --- TABLA DE ENTREGAS ---
html.append("<table class='table table-hover mb-0'><thead><tr>");
html.append("<th>Orden</th><th>Cliente / Destino</th><th>Carga (kg)</th><th>Costo de Envío</th><th>Estado</th>");
html.append("</tr></thead><tbody>");

int i = 1;
for (Punto p : rutaResaltada) {
    if (p.nombre.toLowerCase().contains("depósito")) continue;

    // Lógica de precio diferente: $5 base + $0.25 por cada kilo
    double costoIndividual = 5.0 + (p.cargaPedido * 0.25);
    // Recargo si es carga pesada (más de 50kg)
    String estiloCosto = (p.cargaPedido > 50) ? "text-danger font-weight-bold" : "text-dark";

    html.append("<tr>");
    html.append("<td>").append(i++).append("</td>");
    html.append("<td><strong>").append(p.nombre).append("</strong></td>");
    html.append("<td>").append(p.cargaPedido).append(" kg</td>");
    html.append("<td class='").append(estiloCosto).append("'>$").append(String.format("%.2f", costoIndividual)).append("</td>");
    html.append("<td><span class='badge bg-success'>ENTREGADO</span></td>");
    html.append("</tr>");
}
html.append("</tbody></table>");

    // --- BOTÓN DE IMPRESIÓN ---
    html.append("<div class='text-center mt-5 no-print'>");
    html.append("<button onclick='window.print()' class='btn btn-lg btn-primary'>Imprimir / Guardar como PDF</button>");
    html.append("</div>");

    html.append("</div></body></html>");

    // Guardar el archivo
    try (java.io.PrintWriter out = new java.io.PrintWriter("Reporte_LogiPath.html")) {
        out.println(html.toString());
        // Abrir automáticamente en el navegador
        java.awt.Desktop.getDesktop().browse(new java.io.File("Reporte_LogiPath.html").toURI());
    } catch (Exception e) {
        e.printStackTrace();
    }
}
   // En tu clase principal
public void actualizarProgresoSimulador(int pasoActual) {
    // 1. Movemos el slider visualmente
    int valorMaximo = sliderTrafico.getMaximum();
    int nuevoValor = (pasoActual * valorMaximo) / rutaResaltada.size();
    sliderTrafico.setValue(nuevoValor);

    // 2. Refrescamos la lista lateral para que cambie de color
    generarInformeTexto(); 
}
  
}