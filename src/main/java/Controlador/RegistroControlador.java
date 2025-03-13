/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Rol;
import Modelo.Usuarios;
import Vista.LoginVista;
import Vista.RegistroVista;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author USER
 */
public class RegistroControlador {

    protected String usuario;
    protected RegistroVista regUsuV;

    private int currentId = 0;
    private String currentNombre1 = "";
    private String currentNombre2 = "";
    private String currentApellido1 = "";
    private String currentApellido2 = "";
    private String currentCorreo = "";
    private String currentUsuario = "";
    private String currentPassword = "";
    private String currentCedula = "";
    private int currentIdRol = 0;

    private final Usuarios modeloUsuarios;
    private final Rol modeloRoles;

    // Textos de ejemplo para cada campo
    private final String ejemploNombre1 = "e. g. Juan";
    private final String ejemploNombre2 = "e. g. Luis";
    private final String ejemploApellido1 = "e. g. Rodriguez";
    private final String ejemploApellido2 = "e. g. Carlosama";
    private final String ejemploCorreo = "example@gmail.com";
    private final String ejemploUsuario = "e. g. luis.rodri_12";
    private final String ejemploContraseña = "e. g. Contraseña123";

    public RegistroControlador(String usuario, RegistroVista regUsuV) {
        this.usuario = usuario;
        this.regUsuV = regUsuV;
        this.modeloUsuarios = new Usuarios();
        this.modeloRoles = new Rol();

        Usuarios user = modeloUsuarios.obtenerUsuarioPorNombre(usuario);
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
        }

        this.regUsuV.btn_registro_Usuaio.addActionListener(this::btn_registro_UsuarioActionPerformed);

        // Ejemplo: para la cédula se mantiene el listener actual
        this.regUsuV.txt_cedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    verificarCedula();
                }
            }
        });

        this.regUsuV.txt_cedula.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                regUsuV.txt_cedula.setText(""); // Limpiar el campo al obtener el foco
                limpiarCamposEjemplo();
            }
        });

        this.regUsuV.btn_actualizar.addActionListener(this::btn_actualizarActionPerformed);

        this.regUsuV.btn_finalizar_seción.addActionListener(this::btn_finalizar_seciónActionPerformed);

        regUsuV.txt_contraseña_actual.addActionListener(this::txt_contraseña_actualActionPerformed);

        regUsuV.txt_clave1.setEditable(false);
        regUsuV.txt_clave1.setFocusable(false);
        regUsuV.txt_clave_comprobacion.setEditable(false);
        regUsuV.txt_clave_comprobacion.setFocusable(false);
        llenarComboBoxRoles();

        this.regUsuV.jcmbx_roles.addActionListener(this::jcmbx_rolesActionPerformed);

        // Inicializa los campos al crear el controlador
        inicializarCampos();
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

    /**
     * Inicializa los campos y asigna los placeholders correspondientes.
     */
    // Métodos de inicialización, limpieza y reinicio de la vista

    private void inicializarCampos() {
        // Deshabilita todos los campos al inicio
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
        regUsuV.txt_cedula.setEditable(true);  // Cédula siempre editable al inicio
        regUsuV.txt_cedula.setFocusable(true);
        regUsuV.txt_contraseña_actual.setEditable(false);
        regUsuV.txt_contraseña_actual.setFocusable(false);
        regUsuV.txt_clave1.setEditable(false);
        regUsuV.txt_clave1.setFocusable(false);
        regUsuV.txt_clave_comprobacion.setEditable(false);
        regUsuV.txt_clave_comprobacion.setFocusable(false);

        // Asigna los placeholders usando el método auxiliar
        agregarPlaceholder(regUsuV.txt_nombre1, ejemploNombre1);
        agregarPlaceholder(regUsuV.txt_nombre2, ejemploNombre2);
        agregarPlaceholder(regUsuV.txt_apellido1, ejemploApellido1);
        agregarPlaceholder(regUsuV.txt_apellido2, ejemploApellido2);
        agregarPlaceholder(regUsuV.txt_correo, ejemploCorreo);
        agregarPlaceholder(regUsuV.txt_usuario, ejemploUsuario);
        agregarPlaceholder(regUsuV.txt_clave1, ejemploContraseña);
        agregarPlaceholder(regUsuV.txt_clave_comprobacion, ejemploContraseña);
    }

    /**
     * Método auxiliar para asignar un placeholder a un JTextField.
     */
    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(placeholder);
                }
            }
        });
    }

    /**
     * Restaura el texto de ejemplo en cada campo.
     */
    private void limpiarCamposEjemplo() {
        regUsuV.txt_nombre1.setText(ejemploNombre1);
        regUsuV.txt_nombre2.setText(ejemploNombre2);
        regUsuV.txt_apellido1.setText(ejemploApellido1);
        regUsuV.txt_apellido2.setText(ejemploApellido2);
        regUsuV.txt_correo.setText(ejemploCorreo);
        regUsuV.txt_usuario.setText(ejemploUsuario);
        regUsuV.txt_clave1.setText(ejemploContraseña);
        regUsuV.txt_clave_comprobacion.setText(ejemploContraseña);
    }

    /**
     * Reinicia la vista: limpia los campos, vuelve a asignar los placeholders y
     * restablece las variables.
     */
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

        // Vuelve a asignar los placeholders
        inicializarCampos();

        currentId = 0;
        currentNombre1 = "";
        currentNombre2 = "";
        currentApellido1 = "";
        currentApellido2 = "";
        currentCorreo = "";
        currentUsuario = "";
        currentPassword = "";
        currentCedula = "";
        currentIdRol = 0;

        llenarComboBoxRoles();
    }

// Métodos para habilitar campos y cargar datos
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
            UI.showConfirmDialog(regUsuV,
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    "AVISO",
                    "La cédula ya se encuentra registrada.\nPuede actualizar la información.");
            cargarDatosUsuario(cedula);
        } else {
            habilitarCamposRegistro(cedula);
        }
    }

    private void habilitarCamposRegistro(String cedula) {
        // Habilita todos los campos para el registro de un nuevo usuario
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
        regUsuV.txt_cedula.setEditable(true);
        regUsuV.txt_cedula.setFocusable(true);
        regUsuV.txt_contraseña_actual.setEditable(false);
        regUsuV.txt_contraseña_actual.setFocusable(false);
        regUsuV.txt_clave1.setEditable(true);
        regUsuV.txt_clave1.setFocusable(true);
        regUsuV.txt_clave_comprobacion.setEditable(true);
        regUsuV.txt_clave_comprobacion.setFocusable(true);

        regUsuV.txt_cedula.setText(cedula);
        limpiarCamposEjemplo();
    }

    private void cargarDatosUsuario(String cedula) {
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

            // Bloquea los campos de nombre y apellido
            regUsuV.txt_nombre1.setEditable(false);
            regUsuV.txt_nombre1.setFocusable(false);
            regUsuV.txt_nombre2.setEditable(false);
            regUsuV.txt_nombre2.setFocusable(false);
            regUsuV.txt_apellido1.setEditable(false);
            regUsuV.txt_apellido1.setFocusable(false);
            regUsuV.txt_apellido2.setEditable(false);
            regUsuV.txt_apellido2.setFocusable(false);

            // Habilita la edición de correo, usuario y contraseña
            regUsuV.txt_correo.setEditable(true);
            regUsuV.txt_correo.setFocusable(true);
            regUsuV.txt_usuario.setEditable(true);
            regUsuV.txt_usuario.setFocusable(true);
            regUsuV.txt_contraseña_actual.setEditable(true);
            regUsuV.txt_contraseña_actual.setFocusable(true);

            regUsuV.txt_clave1.setText("");
            regUsuV.txt_clave_comprobacion.setText("");
            regUsuV.txt_clave1.setEditable(false);
            regUsuV.txt_clave1.setFocusable(false);
            regUsuV.txt_clave_comprobacion.setEditable(false);
            regUsuV.txt_clave_comprobacion.setFocusable(false);

            seleccionarRolEnComboBox(currentIdRol);
        }
        limpiarCamposEjemplo();
    }

    private void llenarComboBoxRoles() {
        try {
            List<Rol> roles = modeloRoles.obtenerRolesDesdeBD();
            DefaultComboBoxModel model = new DefaultComboBoxModel(roles.toArray());
            regUsuV.jcmbx_roles.setModel(model);
        } catch (SQLException e) {
            UI.showConfirmDialog(regUsuV,
                    JOptionPane.CLOSED_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    "ERROR",
                    "Error al cargar roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seleccionarRolEnComboBox(int idRol) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) regUsuV.jcmbx_roles.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Rol rol = (Rol) model.getElementAt(i);
            if (rol.getIdRol() == idRol) {
                regUsuV.jcmbx_roles.setSelectedItem(rol);
                break;
            }
        }
    }

// Métodos de validación
    private boolean validarCamposRegistro() {
        // Primer nombre
        String nom1 = regUsuV.txt_nombre1.getText().trim();
        if (nom1.isEmpty() || nom1.equals(ejemploNombre1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el primer nombre.");
            regUsuV.txt_nombre1.requestFocusInWindow();
            return false;
        }
        // Segundo nombre
        String nom2 = regUsuV.txt_nombre2.getText().trim();
        if (nom2.isEmpty() || nom2.equals(ejemploNombre2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el segundo nombre.");
            regUsuV.txt_nombre2.requestFocusInWindow();
            return false;
        }
        // Primer apellido
        String ape1 = regUsuV.txt_apellido1.getText().trim();
        if (ape1.isEmpty() || ape1.equals(ejemploApellido1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el primer apellido.");
            regUsuV.txt_apellido1.requestFocusInWindow();
            return false;
        }
        // Segundo apellido
        String ape2 = regUsuV.txt_apellido2.getText().trim();
        if (ape2.isEmpty() || ape2.equals(ejemploApellido2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el segundo apellido.");
            regUsuV.txt_apellido2.requestFocusInWindow();
            return false;
        }
        // Correo
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
        // Nombre de usuario
        String usu = regUsuV.txt_usuario.getText().trim();
        if (usu.isEmpty() || usu.equals(ejemploUsuario)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Por favor, ingrese el nombre de usuario.");
            regUsuV.txt_usuario.requestFocusInWindow();
            return false;
        }
        // Contraseña
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
        // Se espera que los datos cargados desde la BD sean reales; se validan de nuevo
        String nom1 = regUsuV.txt_nombre1.getText().trim();
        if (nom1.isEmpty() || nom1.equals(ejemploNombre1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El primer nombre no es válido.");
            regUsuV.txt_nombre1.requestFocusInWindow();
            return false;
        }
        String nom2 = regUsuV.txt_nombre2.getText().trim();
        if (nom2.isEmpty() || nom2.equals(ejemploNombre2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El segundo nombre no es válido.");
            regUsuV.txt_nombre2.requestFocusInWindow();
            return false;
        }
        String ape1 = regUsuV.txt_apellido1.getText().trim();
        if (ape1.isEmpty() || ape1.equals(ejemploApellido1)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El primer apellido no es válido.");
            regUsuV.txt_apellido1.requestFocusInWindow();
            return false;
        }
        String ape2 = regUsuV.txt_apellido2.getText().trim();
        if (ape2.isEmpty() || ape2.equals(ejemploApellido2)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El segundo apellido no es válido.");
            regUsuV.txt_apellido2.requestFocusInWindow();
            return false;
        }
        String correo = regUsuV.txt_correo.getText().trim();
        if (correo.isEmpty() || correo.equals(ejemploCorreo)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El correo electrónico no es válido.");
            regUsuV.txt_correo.requestFocusInWindow();
            return false;
        }
        if (!isValidEmail(correo)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El formato del correo electrónico es incorrecto.");
            regUsuV.txt_correo.requestFocusInWindow();
            return false;
        }
        String usu = regUsuV.txt_usuario.getText().trim();
        if (usu.isEmpty() || usu.equals(ejemploUsuario)) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "El nombre de usuario no es válido.");
            regUsuV.txt_usuario.requestFocusInWindow();
            return false;
        }
        return true;
    }

// Eventos de registro, actualización, finalización y cambio de contraseña
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
        int idRol = rolSeleccionado.getIdRol();
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
        } catch (SQLException e) {
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
        if (!validarCamposActualizacion()) {
            return;
        }
        String nombre1 = regUsuV.txt_nombre1.getText().trim().isEmpty() || regUsuV.txt_nombre1.getText().equals(ejemploNombre1)
                ? currentNombre1 : regUsuV.txt_nombre1.getText().trim();
        String nombre2 = regUsuV.txt_nombre2.getText().trim().isEmpty() || regUsuV.txt_nombre2.getText().equals(ejemploNombre2)
                ? currentNombre2 : regUsuV.txt_nombre2.getText().trim();
        String apellido1 = regUsuV.txt_apellido1.getText().trim().isEmpty() || regUsuV.txt_apellido1.getText().equals(ejemploApellido1)
                ? currentApellido1 : regUsuV.txt_apellido1.getText().trim();
        String apellido2 = regUsuV.txt_apellido2.getText().trim().isEmpty() || regUsuV.txt_apellido2.getText().equals(ejemploApellido2)
                ? currentApellido2 : regUsuV.txt_apellido2.getText().trim();
        String correo = regUsuV.txt_correo.getText().trim().isEmpty() || regUsuV.txt_correo.getText().equals(ejemploCorreo)
                ? currentCorreo : regUsuV.txt_correo.getText().trim();
        String cedula = regUsuV.txt_cedula.getText().trim().isEmpty() ? currentCedula : regUsuV.txt_cedula.getText().trim();
        String usuarioNuevo = regUsuV.txt_usuario.getText().trim().isEmpty() || regUsuV.txt_usuario.getText().equals(ejemploUsuario)
                ? currentUsuario : regUsuV.txt_usuario.getText().trim();
        String nuevaClave = regUsuV.txt_clave1.getText().trim();
        if (nuevaClave.isEmpty()) {
            nuevaClave = currentPassword;
        } else if (!nuevaClave.equals(regUsuV.txt_clave_comprobacion.getText().trim())) {
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
        int idRol = rolSeleccionado.getIdRol();
        try {
            int resultado = modeloUsuarios.actualizarUsuario(nombre1, nombre2, apellido1, apellido2, correo, nuevaClave, cedula, usuarioNuevo, currentId, currentPassword, idRol);
            if (resultado == 1) {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE,
                        "ÉXITO", "El usuario se ha actualizado correctamente.");
                resetearVistaInicial();
            } else {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "No se pudo actualizar el usuario.");
            }
        } catch (SQLException e) {
            UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                    "ERROR", "Error al actualizar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void btn_finalizar_seciónActionPerformed(java.awt.event.ActionEvent evt) {
        if (currentId == 0) {
            Usuarios user = modeloUsuarios.obtenerUsuarioPorNombre(this.usuario);
            if (user != null) {
                currentId = user.getId();
            } else {
                UI.showConfirmDialog(regUsuV, JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE,
                        "ERROR", "Error: usuario no encontrado. Verifica que el nombre de usuario (" + usuario + ") sea correcto.");
                return;
            }
        }
        try {
            int resultado = modeloUsuarios.actualizarUltimoAcceso(currentId);
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
}
