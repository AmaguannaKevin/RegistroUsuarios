/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import javax.swing.*;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import Modelo.Rol;
import Modelo.Usuarios;
import Vista.LoginVista;
import Vista.RegistroVista;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author USER
 */
public class RegistroControlador {

    protected String usuario;
    protected RegistroVista regUsuV;
    private ObjectId currentId = null;
    private String currentNombre1 = "";
    private String currentNombre2 = "";
    private String currentApellido1 = "";
    private String currentApellido2 = "";
    private String currentCorreo = "";
    private String currentUsuario = "";
    private String currentPassword = "";
    private String currentCedula = "";
    private ObjectId currentIdRol = null;

    private final Usuarios modeloUsuarios;
    private final Rol modeloRoles;

    private final String ejemploNombre1 = "e. g. Juan";
    private final String ejemploNombre2 = "e. g. Luis";
    private final String ejemploApellido1 = "e. g. Rodriguez";
    private final String ejemploApellido2 = "e. g. Carlosama";
    private final String ejemploCorreo = "example@gmail.com";
    private final String ejemploUsuario = "e. g. luis.rodri_12";
    private final String ejemploContraseña = "e. g. Contraseña123";

    private ObjectId currentUserId; // Nuevo campo para el ObjectId del usuario logeado

    public RegistroControlador(String usuario, RegistroVista regUsuV, ObjectId currentUserId) {
        this.usuario = usuario;
        this.regUsuV = regUsuV;
        this.currentUserId = currentUserId; // Inicializar el ObjectId
        this.modeloUsuarios = new Usuarios();
        this.modeloRoles = new Rol();

        // Inicializar la vista
        inicializarCampos();

        // Eventos
        regUsuV.btn_registro_Usuaio.addActionListener(this::btn_registro_UsuarioActionPerformed);
        regUsuV.btn_actualizar.addActionListener(this::btn_actualizarActionPerformed);
        regUsuV.btn_finalizar_seción.addActionListener(this::btn_finalizar_seciónActionPerformed);
        regUsuV.txt_contraseña_actual.addActionListener(this::txt_contraseña_actualActionPerformed);
        regUsuV.jcmbx_roles.addActionListener(this::jcmbx_rolesActionPerformed);

        // Key Listener para la cédula (Enter)
        regUsuV.txt_cedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    verificarCedula();
                }
            }
        });

        // Focus Listener para la cédula (limpiar al ganar foco)
        regUsuV.txt_cedula.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                regUsuV.txt_cedula.setText("");
                limpiarCamposEjemplo();
            }
        });

        //Llenar combo box roles
        llenarComboBoxRoles();
    }

    public void iniciarVista() {
        regUsuV.setVisible(true);
        regUsuV.setLocationRelativeTo(null);
        regUsuV.setAutoRequestFocus(true);
        regUsuV.requestFocus();
        regUsuV.txt_cedula.requestFocusInWindow();
    }

    public void cerrarVista() {
        regUsuV.dispose();
    }

    private void inicializarCampos() {
        // Campos siempre deshabilitados al inicio
        regUsuV.txt_nombre1.setEditable(false);
        regUsuV.txt_nombre1.setFocusable(false);
        regUsuV.txt_nombre2.setEditable(false);
        regUsuV.txt_nombre2.setFocusable(false);
        regUsuV.txt_apellido1.setEditable(false);
        regUsuV.txt_apellido1.setFocusable(false);
        regUsuV.txt_apellido2.setEditable(false);
        regUsuV.txt_apellido2.setFocusable(false);
        regUsuV.txt_correo.setEditable(false);
        regUsuV.txt_correo.setFocusable(false);
        regUsuV.txt_usuario.setEditable(false);
        regUsuV.txt_usuario.setFocusable(false);
        regUsuV.txt_contraseña_actual.setEditable(false);
        regUsuV.txt_contraseña_actual.setFocusable(false);
        regUsuV.txt_clave1.setEditable(false);
        regUsuV.txt_clave1.setFocusable(false);
        regUsuV.txt_clave_comprobacion.setEditable(false);
        regUsuV.txt_clave_comprobacion.setFocusable(false);
        regUsuV.jcmbx_roles.setEnabled(false);

        // Campo cédula habilitado al inicio
        regUsuV.txt_cedula.setEditable(true);
        regUsuV.txt_cedula.setFocusable(true);

        //Desabilitar botones al inicio
        regUsuV.btn_actualizar.setEnabled(false);
        regUsuV.btn_registro_Usuaio.setEnabled(false);

        // Placeholders
        agregarPlaceholder(regUsuV.txt_nombre1, ejemploNombre1);
        agregarPlaceholder(regUsuV.txt_nombre2, ejemploNombre2);
        agregarPlaceholder(regUsuV.txt_apellido1, ejemploApellido1);
        agregarPlaceholder(regUsuV.txt_apellido2, ejemploApellido2);
        agregarPlaceholder(regUsuV.txt_correo, ejemploCorreo);
        agregarPlaceholder(regUsuV.txt_usuario, ejemploUsuario);
        agregarPlaceholder(regUsuV.txt_clave1, ejemploContraseña);
        agregarPlaceholder(regUsuV.txt_clave_comprobacion, ejemploContraseña);
    }

    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.setForeground(UIManager.getColor("textInactiveText")); // Color grisáceo

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(UIManager.getColor("TextField.foreground")); // Color normal
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(UIManager.getColor("textInactiveText")); // Color grisáceo
                }
            }
        });
    }

    private void limpiarCamposEjemplo() {
        limpiarPlaceholder(regUsuV.txt_nombre1, ejemploNombre1);
        limpiarPlaceholder(regUsuV.txt_nombre2, ejemploNombre2);
        limpiarPlaceholder(regUsuV.txt_apellido1, ejemploApellido1);
        limpiarPlaceholder(regUsuV.txt_apellido2, ejemploApellido2);
        limpiarPlaceholder(regUsuV.txt_correo, ejemploCorreo);
        limpiarPlaceholder(regUsuV.txt_usuario, ejemploUsuario);
        limpiarPlaceholder(regUsuV.txt_clave1, ejemploContraseña);
        limpiarPlaceholder(regUsuV.txt_clave_comprobacion, ejemploContraseña);
    }

    private void limpiarPlaceholder(JTextField campo, String placeholder) {
        if (campo.getText().equals(placeholder)) {
            campo.setText("");
            campo.setForeground(UIManager.getColor("TextField.foreground")); // Restablecer color
        }
    }

    private void resetearVistaInicial() {
        regUsuV.txt_nombre1.setText("");
        regUsuV.txt_nombre2.setText("");
        regUsuV.txt_apellido1.setText("");
        regUsuV.txt_apellido2.setText("");
        regUsuV.txt_correo.setText("");
        regUsuV.txt_usuario.setText("");
        regUsuV.txt_cedula.setText("");
        regUsuV.txt_contraseña_actual.setText("");
        regUsuV.txt_clave1.setText("");
        regUsuV.txt_clave_comprobacion.setText("");
        inicializarCampos();
        currentId = null;
        currentNombre1 = "";
        currentNombre2 = "";
        currentApellido1 = "";
        currentApellido2 = "";
        currentCorreo = "";
        currentUsuario = "";
        currentPassword = "";
        currentCedula = "";
        currentIdRol = null;
        llenarComboBoxRoles();
    }

    private void verificarCedula() {
        String cedula = regUsuV.txt_cedula.getText().trim();
        if (!isValidCedula(cedula)) {
            UI.showConfirmDialog(regUsuV,
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    "ERROR",
                    "La cédula ingresada no es válida.");
            regUsuV.txt_cedula.requestFocusInWindow();
            return;
        }

        if (modeloUsuarios.existeCedula(cedula)) {
            // Modificar el diálogo para incluir un botón de "Actualizar"
            int opcion = UI.showOptionDialog(regUsuV,
                    "La cédula ya se encuentra registrada.\n¿Desea actualizar la información?",
                    "AVISO",
                    JOptionPane.YES_NO_OPTION, // Usa YES_NO_OPTION para los botones "Sí" y "No"
                    JOptionPane.WARNING_MESSAGE,
                    new String[]{"Actualizar", "Cancelar"}, // Etiquetas para los botones
                    "Cancelar" // Botón predeterminado (el que se selecciona al presionar Enter)
            );

            if (opcion == JOptionPane.YES_OPTION) {  // Si el usuario elige "Actualizar"
                habilitarCamposActualizacion(); // Habilitar los campos para la actualización
                cargarDatosUsuario(cedula); // Cargar los datos del usuario
                regUsuV.btn_actualizar.setEnabled(true);
                regUsuV.btn_registro_Usuaio.setEnabled(false);
            } else {
                // Si el usuario elige "Cancelar" o cierra el diálogo, no hacer nada
                regUsuV.txt_cedula.requestFocusInWindow();
            }
        } else {
            habilitarCamposRegistro(cedula);
            regUsuV.btn_registro_Usuaio.setEnabled(true);
            regUsuV.btn_actualizar.setEnabled(false);
        }
    }

    private void habilitarCamposRegistro(String cedula) {
        regUsuV.txt_nombre1.setEditable(true);
        regUsuV.txt_nombre1.setFocusable(true);
        regUsuV.txt_nombre2.setEditable(true);
        regUsuV.txt_nombre2.setFocusable(true);
        regUsuV.txt_apellido1.setEditable(true);
        regUsuV.txt_apellido1.setFocusable(true);
        regUsuV.txt_apellido2.setEditable(true);
        regUsuV.txt_apellido2.setFocusable(true);
        regUsuV.txt_correo.setEditable(true);
        regUsuV.txt_correo.setFocusable(true);
        regUsuV.txt_usuario.setEditable(true);
        regUsuV.txt_usuario.setFocusable(true);
        regUsuV.txt_cedula.setEditable(false);
        regUsuV.txt_cedula.setFocusable(false);
        regUsuV.txt_contraseña_actual.setEditable(false);
        regUsuV.txt_contraseña_actual.setFocusable(false);
        regUsuV.txt_clave1.setEditable(true);
        regUsuV.txt_clave1.setFocusable(true);
        regUsuV.txt_clave_comprobacion.setEditable(true);
        regUsuV.txt_clave_comprobacion.setFocusable(true);
        regUsuV.jcmbx_roles.setEnabled(true);

        regUsuV.txt_cedula.setText(cedula);
        limpiarCamposEjemplo();
    }

    private void habilitarCamposActualizacion() {
        // Habilitar solo los campos que se pueden actualizar
        regUsuV.txt_correo.setEditable(true);
        regUsuV.txt_correo.setFocusable(true);
        regUsuV.txt_usuario.setEditable(true);
        regUsuV.txt_usuario.setFocusable(true);
        regUsuV.txt_contraseña_actual.setEditable(true); //Para verificar la contraseña actual y poder cambiarla
        regUsuV.txt_contraseña_actual.setFocusable(true);
        regUsuV.jcmbx_roles.setEnabled(true); //Habilitar el combobox de roles

        // Asegurarse de que los campos que NO se deben editar estén bloqueados
        regUsuV.txt_nombre1.setEditable(false);
        regUsuV.txt_nombre1.setFocusable(false);
        regUsuV.txt_nombre2.setEditable(false);
        regUsuV.txt_nombre2.setFocusable(false);
        regUsuV.txt_apellido1.setEditable(false);
        regUsuV.txt_apellido1.setFocusable(false);
        regUsuV.txt_apellido2.setEditable(false);
        regUsuV.txt_apellido2.setFocusable(false);
        regUsuV.txt_cedula.setEditable(false);
        regUsuV.txt_cedula.setFocusable(false);
    }

    private void cargarDatosUsuario(String cedula) {
        try {
            Usuarios user = modeloUsuarios.obtenerUsuarioPorCedula(cedula);
            if (user != null) {
                currentId = user.getId();
                currentNombre1 = user.getNombre1();
                currentNombre2 = user.getNombre2();
                currentApellido1 = user.getApellido1();
                currentApellido2 = user.getApellido2();
                currentCorreo = user.getCorreo();
                currentUsuario = user.getUsuario();
                currentPassword = user.getClave();
                currentCedula = user.getCedula();
                currentIdRol = user.getIdRol();

                regUsuV.txt_nombre1.setText(currentNombre1);
                regUsuV.txt_nombre2.setText(currentNombre2);
                regUsuV.txt_apellido1.setText(currentApellido1);
                regUsuV.txt_apellido2.setText(currentApellido2);
                regUsuV.txt_correo.setText(currentCorreo);
                regUsuV.txt_usuario.setText(currentUsuario);
                seleccionarRolEnComboBox(currentIdRol);

            } else {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "No se encontró el usuario con la cédula proporcionada.");
            }
            limpiarCamposEjemplo(); //Asegurar que los placeholders desaparezcan
        } catch (Exception e) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Error al cargar datos del usuario: " + e.getMessage());
            e.printStackTrace(); // Importante para el debugging, imprime el error en la consola
        }
    }

    private void llenarComboBoxRoles() {
        try {
            java.util.List<Rol> roles = modeloRoles.obtenerRolesDesdeBD();
            DefaultComboBoxModel model = new DefaultComboBoxModel(roles.toArray());
            regUsuV.jcmbx_roles.setModel(model);
        } catch (Exception e) {
            UI.showConfirmDialog(regUsuV,
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    "ERROR",
                    "Error al cargar roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seleccionarRolEnComboBox(ObjectId idRol) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) regUsuV.jcmbx_roles.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Rol rol = (Rol) model.getElementAt(i);
            if (rol.getIdRol().equals(idRol)) {
                regUsuV.jcmbx_roles.setSelectedItem(rol);
                break;
            }
        }
    }

    private boolean validarCamposRegistro() {
        String nom1 = regUsuV.txt_nombre1.getText().trim();
        if (nom1.isEmpty() || nom1.equals(ejemploNombre1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el primer nombre.");
            regUsuV.txt_nombre1.requestFocusInWindow();
            return false;
        }

        String nom2 = regUsuV.txt_nombre2.getText().trim();
        if (nom2.isEmpty() || nom2.equals(ejemploNombre2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el segundo nombre.");
            regUsuV.txt_nombre2.requestFocusInWindow();
            return false;
        }

        String ape1 = regUsuV.txt_apellido1.getText().trim();
        if (ape1.isEmpty() || ape1.equals(ejemploApellido1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el primer apellido.");
            regUsuV.txt_apellido1.requestFocusInWindow();
            return false;
        }

        String ape2 = regUsuV.txt_apellido2.getText().trim();
        if (ape2.isEmpty() || ape2.equals(ejemploApellido2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el segundo apellido.");
            regUsuV.txt_apellido2.requestFocusInWindow();
            return false;
        }

        String correo = regUsuV.txt_correo.getText().trim();
        if (correo.isEmpty() || correo.equals(ejemploCorreo)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el correo electrónico.");
            regUsuV.txt_correo.requestFocusInWindow();
            return false;
        }
        if (!isValidEmail(correo)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El correo electrónico no es válido.");
            regUsuV.txt_correo.requestFocusInWindow();
            return false;
        }

        String usu = regUsuV.txt_usuario.getText().trim();
        if (usu.isEmpty() || usu.equals(ejemploUsuario)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el nombre de usuario.");
            regUsuV.txt_usuario.requestFocusInWindow();
            return false;
        }

        String pass = regUsuV.txt_clave1.getText().trim();
        if (pass.isEmpty() || pass.equals(ejemploContraseña)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese la contraseña.");
            regUsuV.txt_clave1.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private boolean validarCamposActualizacion() {
        String correo = regUsuV.txt_correo.getText().trim();
        if (!correo.isEmpty() && !correo.equals(ejemploCorreo) && !isValidEmail(correo)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El formato del correo electrónico es incorrecto.");
            regUsuV.txt_correo.requestFocusInWindow();
            return false;
        }
        return true;
    }

    private void btn_registro_UsuarioActionPerformed(ActionEvent evt) {
        String cedula = regUsuV.txt_cedula.getText().trim();
        if (modeloUsuarios.existeCedula(cedula)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "La cédula ya está registrada. Si desea actualizar, use la opción correspondiente.");
            return;
        }
        if (!validarCamposRegistro()) {
            return;
        }
        String nombre1 = regUsuV.txt_nombre1.getText().trim();
        String nombre2 = regUsuV.txt_nombre2.getText().trim();
        String apellido1 = regUsuV.txt_apellido1.getText().trim();
        String apellido2 = regUsuV.txt_apellido2.getText().trim();
        String correo = regUsuV.txt_correo.getText().trim();
        String usuario = regUsuV.txt_usuario.getText().trim();
        String clave = regUsuV.txt_clave1.getText().trim();
        String claveComprobacion = regUsuV.txt_clave_comprobacion.getText().trim();

        if (!clave.equals(claveComprobacion)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Las contraseñas no coinciden.");
            regUsuV.txt_clave1.requestFocusInWindow();
            return;
        }

        Rol rolSeleccionado = (Rol) regUsuV.jcmbx_roles.getSelectedItem();
        if (rolSeleccionado == null) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Debe seleccionar un rol para el usuario.");
            return;
        }
        ObjectId idRol = rolSeleccionado.getIdRol();
        try {
            int resultado = modeloUsuarios.agregarUsuario(nombre1, nombre2, apellido1, apellido2, cedula, correo, usuario, clave, idRol);
            if (resultado == 1) {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        "ÉXITO", "El usuario se ha registrado correctamente, inicie sesión.");
                resetearVistaInicial();
            } else {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "No se pudo registrar el usuario.");
            }
        } catch (Exception e) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void btn_actualizarActionPerformed(ActionEvent evt) {
        String cedulaIngresada = regUsuV.txt_cedula.getText().trim();
        if (!cedulaIngresada.equals(currentCedula)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "No puede cambiar la cédula a través de la función de actualización.");
            return;
        }

        // Obtener los valores de los campos
        String correo = regUsuV.txt_correo.getText().trim();
        String usuarioNuevo = regUsuV.txt_usuario.getText().trim();

        // Si el correo o el usuario están vacíos o son iguales al placeholder, usar los valores actuales
        if (correo.isEmpty() || correo.equals(ejemploCorreo)) {
            correo = currentCorreo; // Mantener el correo actual
        }

        if (usuarioNuevo.isEmpty() || usuarioNuevo.equals(ejemploUsuario)) {
            usuarioNuevo = currentUsuario; // Mantener el usuario actual
        }

        // Validar los campos antes de la actualización
        if (correo.equals(currentCorreo) && usuarioNuevo.equals(currentUsuario)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    "AVISO", "No se han realizado cambios en la información del usuario.");
            return; // No hacer nada si no hay cambios
        }

        if (!validarCamposActualizacion()) {
            return;
        }

        Rol rolSeleccionado = (Rol) regUsuV.jcmbx_roles.getSelectedItem();
        ObjectId idRol;
        if (rolSeleccionado == null) {
            // Si no se selecciona un rol, usar el rol actual
            idRol = currentIdRol;
        } else {
            // Si se selecciona un rol, usar el nuevo rol
            idRol = rolSeleccionado.getIdRol();
        }

        try {
            int resultado = modeloUsuarios.actualizarUsuario(currentNombre1, currentNombre2, currentApellido1, currentApellido2, correo, currentPassword, currentCedula, usuarioNuevo, currentId, currentPassword, idRol);
            if (resultado == 1) {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        "ÉXITO", "El usuario se ha actualizado correctamente.");
                resetearVistaInicial();
            } else {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "No se pudo actualizar el usuario.");
            }
        } catch (Exception e) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Error al actualizar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void btn_finalizar_seciónActionPerformed(ActionEvent evt) {
        if (currentUserId == null) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                "ERROR", "Error: No se pudo obtener el ID del usuario para cerrar sesión.");
            return;
        }

        try {
            int resultado = modeloUsuarios.actualizarUltimoAcceso(currentUserId); // Usar currentUserId
            if (resultado == 0) {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "Error al actualizar el último acceso.");
                return;
            }
        } catch (Exception e) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Error al actualizar último acceso: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        cerrarVista();
        LoginVista loginVista = new LoginVista();
        new LoginControlador(loginVista);
        loginVista.setVisible(true);
    }

    private void txt_contraseña_actualActionPerformed(ActionEvent evt) {
        String contraseñaIngresada = regUsuV.txt_contraseña_actual.getText().trim();
        if (contraseñaIngresada.isEmpty()) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese la contraseña actual.");
            regUsuV.txt_contraseña_actual.requestFocusInWindow();
            return;
        }
        if (BCrypt.checkpw(contraseñaIngresada, currentPassword)) {
            regUsuV.txt_clave1.setEditable(true);
            regUsuV.txt_clave1.setFocusable(true);
            regUsuV.txt_clave_comprobacion.setEditable(true);
            regUsuV.txt_clave_comprobacion.setFocusable(true);
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    "VERIFICADO", "La contraseña actual es correcta. Ahora puede cambiar su contraseña.");
            regUsuV.txt_clave1.requestFocusInWindow();
        } else {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "La contraseña actual es incorrecta.");
            regUsuV.txt_contraseña_actual.requestFocusInWindow();
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }

    private boolean isValidCedula(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            if (i % 2 == 0) {
                int valor = digito * 2;
                if (valor > 9) {
                    valor -= 9;
                }
                suma += valor;
            } else {
                suma += digito;
            }
        }
        int verificador = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        return verificador == digitoVerificador;
    }

    private void jcmbx_rolesActionPerformed(ActionEvent evt) {
        Rol rolSeleccionado = (Rol) regUsuV.jcmbx_roles.getSelectedItem();
        if (rolSeleccionado != null) {
            currentIdRol = rolSeleccionado.getIdRol();
        }
    }

    public static class UI {

        public static int showConfirmDialog(RegistroVista regUsuV, int closedOption, int errorMessage, String error, String s) {
            JOptionPane.showMessageDialog(regUsuV, s, error, errorMessage);
            return closedOption;
        }

        public static int showOptionDialog(RegistroVista regUsuV, String s, String aviso, int yesNoOption, int warningMessage, String[] actualizar, String cancelar) {
            return JOptionPane.showOptionDialog(regUsuV, s, aviso, yesNoOption, warningMessage, null, actualizar, cancelar);
        }
    }
}
