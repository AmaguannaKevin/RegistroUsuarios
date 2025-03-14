/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Rol;
import Modelo.Usuarios;
import Vista.LoginVista;
import Vista.RegistroVista;
import Vista.DatosUsuarioVista;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.bson.types.ObjectId;
/**
 *
 * @author USER
 */

public class LoginControlador {

    private LoginVista loginV;
    private ObjectId currentUserId; // Nuevo campo para guardar el ObjectId

    public LoginControlador(LoginVista loginV) {
        this.loginV = loginV;
        initController();
        loginV.setLocationRelativeTo(null);
    }

    private void initController() {
        // Asignar el action listener al botón de login
        loginV.btn_login.addActionListener(e -> autenticarUsuario());
        loginV.jtbn_ver_clave.addActionListener(e -> jtbn_ver_claveActionPerformed(e));

        // Listener para txt_usuario: al presionar Enter se valida el contenido
        loginV.txt_usuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String usuario = loginV.txt_usuario.getText().trim();
                    if (usuario.isEmpty() || !usuario.matches("[a-z0-9]+")) {
                        loginV.txt_usuario.setBorder(BorderFactory.createLineBorder(Color.RED));
                    } else {
                        loginV.txt_usuario.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                        loginV.txt_contraseña.requestFocus();
                    }
                }
            }
        });

        // Listener para txt_contraseña: al presionar Enter se simula el clic en el botón de login
        loginV.txt_contraseña.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contraseña = new String(loginV.txt_contraseña.getPassword()).trim();
                    if (contraseña.isEmpty()) {
                        loginV.txt_contraseña.setBorder(BorderFactory.createLineBorder(Color.RED));
                    } else {
                        loginV.txt_contraseña.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));
                        loginV.btn_login.doClick();
                    }
                }
            }
        });
    }

    private void autenticarUsuario() {
        String usuario = loginV.txt_usuario.getText().trim();
        String contraseña = new String(loginV.txt_contraseña.getPassword()).trim();

        if (usuario.isEmpty() || contraseña.isEmpty()) {
            JOptionPane.showMessageDialog(loginV,
                    "Por favor, ingrese usuario y contraseña.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Instancia de Usuarios para validar
        Usuarios usuarioDB = new Usuarios();
        usuarioDB.setUsuario(usuario);
        usuarioDB.setClave(contraseña);

        boolean esValido = usuarioDB.validarUsuario();

        if (esValido) {
            // Registrar log y actualizar último acceso usando ObjectId
            // *** IMPORTANTE: Guardar el ObjectId del usuario autenticado ***
            currentUserId = usuarioDB.getId(); 
            usuarioDB.registrarLogAcceso(currentUserId, "login", "Inicio de sesión exitoso");
            usuarioDB.actualizarUltimoAcceso(currentUserId);

            // Recuperar el rol consultando la colección de roles
            ObjectId idRol = usuarioDB.getIdRol();
            String nombreRol = obtenerNombreRol(idRol);

            loginV.dispose();
            if (nombreRol.equalsIgnoreCase("Administrador")) {
                RegistroVista vista = new RegistroVista();
                // Se pasa el usuario real y el ID del usuario autenticado
                RegistroControlador registroControlador = new RegistroControlador(usuario, vista, currentUserId); 
                registroControlador.iniciarVista();
            } else if (nombreRol.equalsIgnoreCase("Usuario")) {
                new DatosUsuarioVista().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(loginV,
                        "Rol desconocido.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // En caso de fallo, se registra el intento (se utiliza un ObjectId temporal)
            usuarioDB.registrarLogAcceso(new ObjectId(), "intento_fallido", "Intento fallido de inicio de sesión para usuario: " + usuario);
            JOptionPane.showMessageDialog(loginV,
                    "Usuario o contraseña incorrectos.",
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para obtener el nombre del rol a partir del ObjectId
    private String obtenerNombreRol(ObjectId idRol) {
        Rol rolModelo = new Rol();
        for (Rol rol : rolModelo.obtenerRolesDesdeBD()) {
            if (rol.getIdRol().equals(idRol)) {
                return rol.getNombreRol();
            }
        }
        return "";
    }

    private void jtbn_ver_claveActionPerformed(ActionEvent evt) {
        if (loginV.jtbn_ver_clave.isSelected()) {
            // Mostrar la contraseña: sin enmascaramiento
            loginV.txt_contraseña.setEchoChar((char) 0);
        } else {
            // Ocultar la contraseña: restablecer el enmascaramiento
            loginV.txt_contraseña.setEchoChar('*');
        }
    }
}
