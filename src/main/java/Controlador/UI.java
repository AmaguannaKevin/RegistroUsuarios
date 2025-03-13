/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import org.imgscalr.Scalr;

/**
 *
 * @author Alejandro
 */
public class UI {
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Ghost White </p> <br> 
     * <b>HEX</b> <br>&emsp;&emsp; #FAFBFF <br>
     * <b>RGB</b> <br>&emsp;&emsp; 250, 251, 255
     */
    public static final Color GHOST_WHITE = new Color(250, 251, 255);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Columbia Blue </p> <br>
     * <b>HEX</b> <br>&emsp;&emsp; #D0EAFF <br>
     * <b>RGB</b> <br>&emsp;&emsp; 208, 234, 255
     */
    public static final Color COLUMBIA_BLUE = new Color(208, 234, 255);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Vista Blue </p> <br> 
     * <b>HEX</b> <br>&emsp;&emsp; #7E9CC4 <br>
     * <b>RGB</b> <br>&emsp;&emsp; 126, 156, 196
     */
    public static final Color VISTA_BLUE = new Color(126, 156, 196);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> YinMn Blue </p> <br>
     * <b>HEX</b> <br>&emsp;&emsp; #2C4D89 <br>
     * <b>RGB</b> <br>&emsp;&emsp; 44, 77, 137
     */
    public static final Color YINMN_BLUE = new Color(44, 77, 137);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Delft Blue </p> <br>
     * <b>HEX</b> <br>&emsp;&emsp; #213665 <br>
     * <b>RGB</b> <br>&emsp;&emsp; 33, 54, 101
     */
    public static final Color DELFT_BLUE = new Color(33, 54, 101);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Spacial Cadet </p> <br>
     * <b>HEX</b> <br>&emsp;&emsp; #151F41 <br>
     * <b>RGB</b> <br>&emsp;&emsp; 21, 31, 65
     */
    public static final Color SPACIAL_CADET = new Color(21, 31, 65);
    
    /**
     * <b> Color del Sistema <br> </b>
     * 
     * <p> Rich Black </p> <br>
     * <b>HEX</b> <br>&emsp;&emsp; #02081F <br>
     * <b>RGB</b> <br>&emsp;&emsp; 2, 8, 31
     */
    public static final Color RICH_BLACK = new Color(2, 8, 31);
    
    /**
     * Sirve para modificar el diseño de la interfaz
     * utilizando la librería <b>Look and Feel</b> y su tipo de
     * interfaz <b>MacLight</b> (Parecido a aplicaciones
     * MAC con tema claro)
     */
    public static void setLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sirve para modificar el diseño de la interfaz
     * utilizando la librería <b>Look and Feel</b> y su tipo de
     * interfaz <b>MacDark</b> (Parecido a aplicaciones
     * MAC con tema oscuro)
     */
    public static void setDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
    /**
    * Sirve para mostrar tipos de mensaje <b>JOptionPane ConfirmDialog</b> <br>
    * en dónde:<br>
    * &emsp;&emsp; <b>parentComponent | Component</b> - Componente padre del dialogo<br> 
    * &emsp;&emsp; <b>typeButton | int</b> - Es el tipo de botones que se desea<br> 
    * &emsp;&emsp; <b>typeDialog | int</b> - Es el tipo de dialogo que se desea<br> 
    * &emsp;&emsp; <b>title | String</b> - Titulo del dialogo<br> 
    * &emsp;&emsp; <b>message | String</b> - Mensaje del dialogo<br> 
     * @param parentComponent
     * @param typeButton
     * @param typeDialog
     * @param title
     * @param message
    */
    public static void showConfirmDialog(Component parentComponent,int typeButton, int typeDialog, String title, String message){
        JLabel lbl_message = new FlatLabel();
        Font font = new Font("Leelawadee", Font.PLAIN, 14);
        lbl_message.setFont(font);
        lbl_message.setForeground(SPACIAL_CADET);
        lbl_message.setText("<html>" + message + "</html>");
        lbl_message.setPreferredSize(new Dimension(200, 50));
        JOptionPane.showConfirmDialog(parentComponent, lbl_message, title, typeButton, typeDialog);
    }
    
    /**
     * Obtener botón redondo, en dónde:<br>
     * &emsp;&emsp; <b>button | JButton</b> - Botón a transformar<br> 
     * &emsp;&emsp; <b>width | int</b> - Tamaño del botón<br> 
     * &emsp;&emsp; <b>height | int</b> - Altura del botón<br> 
     * @param button
     * @param width
     * @param height
     * @return 
     */
    public static JButton getButtonRound (JButton button, int width, int height) {

        button.setPreferredSize(new Dimension(width, height));
        button.setContentAreaFilled(false);

        button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        button.setOpaque(false);

        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                JButton button = (JButton) c;
                int diameter = Math.min(button.getWidth(), button.getHeight());

                Ellipse2D circle = new Ellipse2D.Double(1, 1, diameter - 2, diameter - 2);
                g2.setColor(YINMN_BLUE);
                g2.fill(circle);

//                g2.setColor(SPACIAL_CADET);
//                g2.draw(circle);
                g2.dispose();

                super.paint(g, c);
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                return new Dimension(width, height);
            }
        });
        return button;
    }
    
    /**
     * Escalar imagenes de manera sencilla y seleccionar su tipo de escalado,
     * en dónde:<br>
     * &emsp;&emsp; <b>path | String</b> - Es la ruta del archivo<br> 
     * &emsp;&emsp; <b>width | int</b> - Tamaño resultante de la imagen<br> 
     * &emsp;&emsp; <b>height | int</b> - Altura resultante de la imagen<br>
     * &emsp;&emsp; <b>typeMethod | Scalr.Method</b> - El tipo de método para escalar la imagen<br> 
     * @param path
     * @param width
     * @param height
     * @param typeMethod
     * @return 
     */
    public static ImageIcon scaleImage(String path, int width, int height, Scalr.Method typeMethod){
        try {
            BufferedImage originalImage = ImageIO.read(new File(path));
            BufferedImage scaledImage = Scalr.resize(originalImage, typeMethod, width, height);
            ImageIcon image = new ImageIcon(scaledImage);
            return image;
        } catch (IOException ex) {
            showConfirmDialog(null, 
                    JOptionPane.CLOSED_OPTION, 
                    JOptionPane.ERROR_MESSAGE, 
                    "Error", 
                    ex.getMessage());
        }
        return null;
    }
    
    /**
     * Obtener borde de error para JTextFields, en dónde se retorna un borde
     * de color rojo.
     * @return 
     */
    public static Border getBorderError(){
        Border colorBorder = BorderFactory.createLineBorder(Color.RED, 3);
        Border margin = new EmptyBorder(0, 6, 0, 6);
        return BorderFactory.createCompoundBorder(colorBorder, margin);
    }
}
