import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JFrame {
    private static List<Punto> mapa = new ArrayList<>();
    private List<Punto> rutaResaltada = new ArrayList<>();
    private JLabel lblContador;
    private JLabel lblPesoTotal;
    private DefaultTableModel modeloTabla;
    private JTable tablaHistorial;

    // --- COLORES DE MARCA (Declarados aquí para que todos los métodos los vean) ---
    private Color rojoLogo = new Color(195, 40, 40);      // Rojo Pin LogiPath
    private Color verdeMapa = new Color(144, 190, 109);    // Verde Mapa
    private Color azulFondoClaro = new Color(173, 216, 230); 
    private Color textoOscuro = new Color(45, 45, 45);

    public MenuPrincipal() {
        if (mapa.isEmpty()) {
            Punto deposito = new Punto("MI DEPÓSITO", 450, 300, 0, 24);
            deposito.direccion = "Sede Principal";
            deposito.prioridad = 1;
            mapa.add(deposito);
        }

        setTitle("Logistics");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // --- PANEL BASE CON IMAGEN ---
        JPanel panelBase = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Asegúrate de que la ruta sea correcta según tus carpetas
                    Image fondo = new ImageIcon(getClass().getResource("/imagenes/mi_presentacionmejorada.png")).getImage();
                    g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(azulFondoClaro);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        panelBase.setBorder(BorderFactory.createLineBorder(verdeMapa, 1));

        // --- PANEL IZQUIERDO: DASHBOARD ---
    JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setOpaque(false);
        panelInfo.setPreferredSize(new Dimension(380, 0));
        // Aumentamos el margen superior (120) para que el texto baje y no choque con el logo
        panelInfo.setBorder(BorderFactory.createEmptyBorder(120, 25, 30, 15)); 

        lblContador = new JLabel("Paquetes: 0");
        lblContador.setFont(new Font("Segoe UI", Font.BOLD, 22)); // Un poco más grande
        lblContador.setForeground(Color.WHITE); // Blanco para que resalte sobre el mapa azul
        lblContador.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblPesoTotal = new JLabel("Peso Total: 0 kg");
        lblPesoTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPesoTotal.setForeground(new Color(220, 220, 220)); // Gris claro/Blanco
        lblPesoTotal.setAlignmentX(Component.CENTER_ALIGNMENT);

        configurarTablaEstilizada();
        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);
        scrollTabla.setOpaque(false);
        scrollTabla.getViewport().setOpaque(false);
        scrollTabla.setPreferredSize(new Dimension(350, 250));
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));

        JButton btnBorrar = new JButton("BORRAR SELECCIONADO");
        estilizarBoton(btnBorrar);
        btnBorrar.setBackground(rojoLogo); 
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBorrar.addActionListener(e -> borrarSeleccionado());

        // Añadimos los elementos al panel izquierdo
        panelInfo.add(lblContador);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));
        panelInfo.add(lblPesoTotal);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 25))); // Espacio antes de la tabla
        panelInfo.add(scrollTabla);
        panelInfo.add(Box.createRigidArea(new Dimension(0, 15)));
        panelInfo.add(btnBorrar);
        
        // --- PANEL DERECHO: ACCIONES ---
// --- PANEL DERECHO: ACCIONES CENTRADAS ---
JPanel panelAcciones = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        // Esto permite que el fondo de la ventana (tu mapa de Venezuela) 
        // se vea a través del panel de botones sin errores visuales.
        super.paintComponent(g); 
    }
};

panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
// Volvemos a false para que se vea el camión y el mapa de fondo
panelAcciones.setOpaque(false); 
panelAcciones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

// --- CREACIÓN Y ESTILO DE BOTONES (Igual que antes) ---
JButton btnNuevo = crearBotonMenu(" REGISTRAR ENCOMIENDA");
JButton btnCargar = crearBotonMenu(" IMPORTAR LISTADO (.TXT)");
JButton btnMapa = crearBotonMenu("VER MAPA Y OPTIMIZAR");
JButton btnSalir = crearBotonMenu("CERRAR SISTEMA");

btnNuevo.addActionListener(e -> abrirFormularioRegistro(btnMapa));
        btnCargar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (cargarDatosDesdeArchivo(fc.getSelectedFile())) {
                    actualizarDashboardYTabla();
                }
            }
        });
        btnMapa.addActionListener(e -> {
            new InterfazPlanificador(mapa).setVisible(true);
            this.dispose();
        });
        btnSalir.addActionListener(e -> System.exit(0));
estilizarBoton(btnNuevo);
estilizarBoton(btnCargar);
estilizarBoton(btnMapa);
estilizarBoton(btnSalir);

btnMapa.setBackground(verdeMapa); 
btnMapa.setForeground(new Color(30, 60, 30));

// --- ORGANIZACIÓN CON CENTRADO VERTICAL ---
panelAcciones.add(Box.createVerticalGlue()); // Empuja hacia abajo

panelAcciones.add(btnNuevo);
panelAcciones.add(Box.createRigidArea(new Dimension(0, 15)));
panelAcciones.add(btnCargar);
panelAcciones.add(Box.createRigidArea(new Dimension(0, 15)));
panelAcciones.add(btnMapa);

panelAcciones.add(Box.createVerticalGlue()); // Mantiene el bloque en el centro

panelAcciones.add(btnSalir);
panelAcciones.add(Box.createRigidArea(new Dimension(0, 20)));

// --- ENSAMBLAJE FINAL ---
panelBase.add(panelInfo, BorderLayout.WEST); 
panelBase.add(panelAcciones, BorderLayout.CENTER);

add(panelBase);
actualizarDashboardYTabla();
    }
    private void abrirFormularioRegistro(JButton btnIniciar) {
        JTextField txtNombre = new JTextField();
        JTextField txtCarga = new JTextField();
        String[] zonas = {"Alta Vista", "Unare", "San Félix", "Puerto Ordaz", "Chilemex"};
        JComboBox<String> comboZonas = new JComboBox<>(zonas);

        Object[] formulario = {"Nombre:", txtNombre, "Zona:", comboZonas, "Peso (kg):", txtCarga};

        int option = JOptionPane.showConfirmDialog(this, formulario, "RECEPCIÓN", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText();
                int carga = Integer.parseInt(txtCarga.getText());
                String zona = comboZonas.getSelectedItem().toString();
                
                // COORDENADAS SEGÚN ZONA
                int x = 0, y = 0;
            switch(zona) {
    case "Alta Vista":   x = 550; y = 480; break; 
    case "Unare":        x = 320; y = 600; break; 
    case "San Félix":    x = 850; y = 400; break; 
    case "Puerto Ordaz": x = 450; y = 520; break; 
    case "Chilemex":     x = 520; y = 450; break;
    default:             x = 500; y = 300; break; // Centro del mapa si algo falla
}

                Punto p = new Punto(nombre, x, y, carga, 18);
                p.direccion = zona;
                p.prioridad = 2;
                mapa.add(p);
                
                generarComprobante(nombre, zona, carga, p.prioridad);
                actualizarDashboardYTabla();
                activarBotonInicio(btnIniciar);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en los datos.");
            }
        }
    }

  private void actualizarDashboardYTabla() {
    modeloTabla.setRowCount(0); // Limpiar tabla
    int totalKg = 0;
    int totalPaquetes = 0;

    for (Punto p : mapa) {
        // No mostramos el depósito en la lista de entregas
        if (!p.nombre.equals("MI DEPÓSITO")) {
            modeloTabla.addRow(new Object[]{p.nombre, "Importado", p.cargaPedido + " kg"});
            totalKg += p.cargaPedido;
            totalPaquetes++;
        }
    }

    lblContador.setText("Paquetes: " + totalPaquetes);
    lblPesoTotal.setText("Peso Total: " + totalKg + " kg");
    
    // Si hay paquetes, el peso total se resalta en verde
    if (totalKg > 0) lblPesoTotal.setForeground(new Color(46, 204, 113));
}

  
private void borrarSeleccionado() {
    int fila = tablaHistorial.getSelectedRow();
    if (fila >= 0) {
        String nombre = (String) modeloTabla.getValueAt(fila, 0);
        mapa.removeIf(p -> p.nombre.equals(nombre));
        actualizarDashboardYTabla();
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente de la tabla.");
    }
}
    private void activarBotonInicio(JButton btn) {
        btn.setEnabled(true);
        btn.setBackground(new Color(46, 204, 113));
        btn.setForeground(Color.BLACK);
    }

    private void generarComprobante(String nombre, String zona, int carga, int prioridad) {
        try {
            File carpeta = new File("Recibos");
            if (!carpeta.exists()) carpeta.mkdir();
            java.io.PrintWriter writer = new java.io.PrintWriter("Recibos/Recibo_" + nombre.replace(" ", "_") + ".txt");
            writer.println("LOGISTICS \nCLIENTE: " + nombre + "\nZONA: " + zona + "\nPESO: " + carga + "kg");
            writer.close();
        } catch (Exception e) {}
    }

private JButton crearBotonMenu(String texto) {
    JButton btn = new JButton(texto);
    btn.setMaximumSize(new Dimension(350, 50));
    btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btn.setFocusPainted(false);
    btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1));
    btn.setContentAreaFilled(false); // Lo hacemos transparente
    btn.setOpaque(true);
    btn.setBackground(new Color(30, 30, 30, 200)); // Fondo oscuro traslúcido
    btn.setForeground(Color.WHITE);
    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            btn.setBackground(new Color(46, 204, 113)); // Verde LogiPath al entrar
            btn.setForeground(Color.BLACK);
            btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            btn.setBorder(BorderFactory.createLineBorder(verdeMapa, 2));
        }
        public void mouseExited(java.awt.event.MouseEvent evt) {
            btn.setBackground(new Color(30, 30, 30, 200));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1));
        }
    });
    return btn;
}

private void estilizarBoton(JButton boton) {
    // 1. Tamaño: Los hacemos más grandes (Ancho: 300, Alto: 50)
    boton.setPreferredSize(new java.awt.Dimension(300, 50));
    
    // 2. Redondeo: Usamos la propiedad de FlatLaf para el arco
    // El valor 999 crea el efecto de "píldora" (bordes totalmente circulares)
    boton.putClientProperty("JButton.buttonType", "roundRect");
    
    // 3. Opcional: Si quieres un redondeo específico (ej. 20px) usa:
    // boton.putClientProperty("JComponent.roundRect", 20);

    // 4. Fuente: Un poco más grande para que combine con el botón
    boton.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
}


  private boolean cargarDatosDesdeArchivo(File archivo) {
    try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
        String linea; 
        mapa.clear();
        
        // 1. Re-añadir depósito
        Punto deposito = new Punto("MI DEPÓSITO", 450, 300, 0, 24);
        mapa.add(deposito);

        // 2. Leer puntos del archivo
        while ((linea = br.readLine()) != null) {
            String[] d = linea.split(",");
            if (d.length >= 6) {
                Punto p = new Punto(d[0].trim(), 
                                   Integer.parseInt(d[1].trim()), 
                                   Integer.parseInt(d[2].trim()), 
                                   Integer.parseInt(d[3].trim()), 
                                   Integer.parseInt(d[4].trim()));
                p.prioridad = Integer.parseInt(d[5].trim()); 
                p.direccion = "Importado"; 
                mapa.add(p);
            }
        }

        // --- EL PASO VITAL: CREAR CONEXIONES (CARRETERAS) ---
        // Sin esto, Dijkstra siempre da Infinity
        for (int i = 0; i < mapa.size(); i++) {
            for (int j = i + 1; j < mapa.size(); j++) {
                Punto p1 = mapa.get(i);
                Punto p2 = mapa.get(j);
                // Calculamos la distancia real entre coordenadas
                double dist = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
                // Creamos la conexión en ambos sentidos
                p1.adyacentes.add(new Conexion(p2, dist));
                p2.adyacentes.add(new Conexion(p1, dist));
            }
        }

        return mapa.size() > 1;
    } catch (Exception e) { 
        e.printStackTrace(); // Para ver errores en consola
        return false; 
    }
}
  private JPanel crearPanelTimeline() {
    JPanel panelDerecho = new JPanel();
    panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
    panelDerecho.setBackground(new Color(255, 255, 255, 50)); // Blanco traslúcido sobre tu fondo azul
    panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    JLabel titulo = new JLabel("CRONOGRAMA DE ENTREGA");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titulo.setForeground(Color.WHITE);
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    panelDerecho.add(titulo);
    panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));

    // Aquí iteramos sobre los puntos de la ruta optimizada
    for (Punto p : rutaResaltada) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setMaximumSize(new Dimension(280, 70));
        card.setBackground(new Color(255, 255, 255, 180)); // Tarjeta sólida suave
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(46, 204, 113)), // Borde verde neón
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblNombre = new JLabel(p.nombre.toUpperCase());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Lógica de alerta de tiempo
        JLabel lblTiempo = new JLabel("Límite: " + p.ventanaEntrega + " | ETA: " + p.eta);
        lblTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if(p.esRetraso) lblTiempo.setForeground(Color.RED);

        card.add(lblNombre, BorderLayout.NORTH);
        card.add(lblTiempo, BorderLayout.SOUTH);

        panelDerecho.add(card);
        panelDerecho.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre tarjetas
    }

    return panelDerecho;
}
  private void configurarTablaEstilizada() {
    String[] columnas = {"CLIENTE", "ZONA", "KG"};
    modeloTabla = new DefaultTableModel(columnas, 0);
    tablaHistorial = new JTable(modeloTabla);

    // Hacer la tabla traslúcida
tablaHistorial.setOpaque(false);
    tablaHistorial.setBackground(new Color(255, 255, 255, 180)); // Más sólido (180 en lugar de 40)
    tablaHistorial.setForeground(new Color(30, 30, 30)); // Texto casi negro para que se vea SIEMPRE
    
    tablaHistorial.setRowHeight(30); 
    tablaHistorial.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Texto en negrita para mejor lectura
    
    // La rejilla (grid) la ponemos oscura pero suave
    tablaHistorial.setGridColor(new Color(0, 0, 0, 30)); 
    tablaHistorial.setShowGrid(true);
    
    // Estilo del encabezado
    tablaHistorial.getTableHeader().setReorderingAllowed(false);
    tablaHistorial.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    tablaHistorial.getTableHeader().setBackground(new Color(30, 30, 30));
    tablaHistorial.getTableHeader().setForeground(Color.WHITE);
    
    // Color de selección (Verde LogiPath)
    tablaHistorial.setSelectionBackground(verdeMapa);
    tablaHistorial.setSelectionForeground(Color.BLACK);
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }
}