
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author USUARIO
 */
public class PanelConFondo extends JPanel{
    
    private Image imagen;

    public PanelConFondo(String ruta) {
        this.imagen = new ImageIcon(getClass().getResource(ruta)).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Dibujar la imagen escalada al tamaño del panel
        g2.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        
        // CAPA DE ESTILO: Añadimos un filtro oscuro traslúcido para que el texto sea legible
        g2.setPaint(new Color(0, 0, 0, 180)); // Negro con 70% de opacidad
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.dispose();
    }
}
    

