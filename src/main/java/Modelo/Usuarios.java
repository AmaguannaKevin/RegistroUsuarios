/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.sql.Timestamp;
import org.mindrot.jbcrypt.BCrypt;
import Conexion.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USER
 */
public class Usuarios {

    private int id;
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String correo;
    private String usuario;
    private String clave;
    private String cedula;
    private String fechaRegistro;
    private int idRol;

    public Usuarios() {
    }

    public Usuarios(int id, String nombre1, String nombre2, String apellido1, String apellido2,
            String correo, String usuario, String clave, String cedula, String fechaRegistro, int idRol) {
        this.id = id;
        this.nombre1 = nombre1;
        this.nombre2 = nombre2;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.correo = correo;
        this.usuario = usuario;
        this.clave = clave;
        this.cedula = cedula;
        this.fechaRegistro = fechaRegistro;
        this.idRol = idRol;
    }

    public int agregarUsuario(String nombre1, String nombre2, String apellido1, String apellido2,
            String cedula, String correo, String usuario, String clave, int idRol) throws SQLException {

        if (usuarioExiste(usuario, correo, cedula)) {
            return 2; // El usuario ya existe
        }

        String sql = "INSERT INTO usuarios (nombres, apellidos, correo, nombre_usuario, cedula, contrasena, id_rol) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            String nombres = nombre1 + " " + nombre2;
            String apellidos = apellido1 + " " + apellido2;
            String contrasenaCifrada = cifrarContrasena(clave);

            ps.setString(1, nombres);
            ps.setString(2, apellidos);
            ps.setString(3, correo);
            ps.setString(4, usuario);
            ps.setString(5, cedula);
            ps.setString(6, contrasenaCifrada);
            ps.setInt(7, idRol);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0 ? 1 : 0;
        }
    }

    private boolean usuarioExiste(String usuario, String correo, String cedula) {
        String sql = "SELECT id_usuario FROM usuarios WHERE nombre_usuario = ? OR correo = ? OR cedula = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, correo);
            ps.setString(3, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // En caso de error, se evita agregar el usuario.
            return true;
        }
    }

    private String cifrarContrasena(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt());
    }

    public boolean validarUsuario() {
        String sql = "SELECT id_usuario, nombres, apellidos, correo, cedula, contrasena, id_rol, fecha_registro "
                + "FROM usuarios WHERE nombre_usuario = ?";

        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, this.usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String contrasenaBD = rs.getString("contrasena");
                    if (BCrypt.checkpw(this.clave, contrasenaBD)) {
                        this.id = rs.getInt("id_usuario");
                        this.correo = rs.getString("correo");
                        this.cedula = rs.getString("cedula");
                        this.idRol = rs.getInt("id_rol");
                        this.fechaRegistro = rs.getString("fecha_registro");
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String obtenerNombreCompleto() {
        String nombreCompleto = "";
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE nombre_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, this.usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreCompleto = rs.getString("nombres") + " " + rs.getString("apellidos");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return nombreCompleto;
    }

    public static List<Usuarios> buscarUsuariosPorUsuario(String nombreUsuario) {
        List<Usuarios> listaUsuarios = new ArrayList<>();
        String sql = "SELECT id_usuario, nombres, apellidos, correo, nombre_usuario, cedula, fecha_registro, id_rol "
                + "FROM usuarios WHERE nombre_usuario LIKE ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + nombreUsuario + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuarios u = new Usuarios();
                    u.setId(rs.getInt("id_usuario"));
                    // Separar nombres y apellidos en dos partes
                    String fullNames = rs.getString("nombres");
                    String[] nombresArr = fullNames.split(" ", 2);
                    u.setNombre1(nombresArr[0]);
                    u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");
                    String fullApellidos = rs.getString("apellidos");
                    String[] apellidosArr = fullApellidos.split(" ", 2);
                    u.setApellido1(apellidosArr[0]);
                    u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");
                    u.setCorreo(rs.getString("correo"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setCedula(rs.getString("cedula"));
                    u.setFechaRegistro(rs.getString("fecha_registro"));
                    u.setIdRol(rs.getInt("id_rol"));
                    listaUsuarios.add(u);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listaUsuarios;
    }

    public void buscarDatosUsuario() {
        String sql = "SELECT id_usuario, nombres, apellidos, correo, cedula, contrasena, id_rol, fecha_registro "
                + "FROM usuarios WHERE nombre_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, this.usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    this.id = rs.getInt("id_usuario");
                    String[] nombresArr = rs.getString("nombres").split(" ", 2);
                    this.nombre1 = nombresArr[0];
                    this.nombre2 = nombresArr.length > 1 ? nombresArr[1] : "";
                    String[] apellidosArr = rs.getString("apellidos").split(" ", 2);
                    this.apellido1 = apellidosArr[0];
                    this.apellido2 = apellidosArr.length > 1 ? apellidosArr[1] : "";
                    this.correo = rs.getString("correo");
                    this.cedula = rs.getString("cedula");
                    this.clave = rs.getString("contrasena");
                    this.idRol = rs.getInt("id_rol");
                    this.fechaRegistro = rs.getString("fecha_registro");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Se optimiza la carga de datos por cédula para separar nombres y apellidos correctamente
    public Usuarios obtenerUsuarioPorCedula(String cedula) {
        String sql = "SELECT id_usuario, nombres, apellidos, correo, nombre_usuario, cedula, contrasena, fecha_registro, id_rol FROM usuarios WHERE cedula = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuarios u = new Usuarios();
                    u.setId(rs.getInt("id_usuario"));

                    String nombres = rs.getString("nombres");
                    String[] nombresArr = nombres.split(" ", 2);
                    u.setNombre1(nombresArr[0]);
                    u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");

                    String apellidos = rs.getString("apellidos");
                    String[] apellidosArr = apellidos.split(" ", 2);
                    u.setApellido1(apellidosArr[0]);
                    u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");

                    u.setCorreo(rs.getString("correo"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setCedula(rs.getString("cedula"));
                    u.setClave(rs.getString("contrasena"));
                    u.setFechaRegistro(rs.getString("fecha_registro"));
                    u.setIdRol(rs.getInt("id_rol"));
                    return u;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error al obtener usuario por cedula: " + ex.getMessage());
            return null;
        }
        return null;
    }

    public boolean existeCedula(String cedula) {
        String sql = "SELECT id_usuario FROM usuarios WHERE cedula = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error al verificar la existencia de la cedula: " + ex.getMessage());
            return true;
        }
    }

    public int actualizarUltimoAcceso(int idUsuario) {
        String sql = "UPDATE usuarios SET ultimo_acceso = ? WHERE id_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(1, timestamp);
            ps.setInt(2, idUsuario);

            //Si llegamos a este punto, es que no hubo errores
            return ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar el último acceso: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    // Sobrecarga para el método registrarLogAcceso con un solo parámetro
    public void registrarLogAcceso(int idUsuario) {
        registrarLogAcceso(idUsuario, "login", "Acceso al sistema");
    }

    public void registrarLogAcceso(int idUsuario, String accion, String detalles) {
        // Primero verificar si el usuario existe
        String verificarUsuario = "SELECT id_usuario FROM usuarios WHERE id_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement stmt = c.prepareStatement(verificarUsuario)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                System.err.println("Error: El usuario con ID " + idUsuario + " no existe");
                return;
            }

            // Si el usuario existe, continuar con el registro del log
            String sql = "INSERT INTO log_acceso (id_usuario, accion, detalles) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, accion);
                ps.setString(3, detalles);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            System.err.println("Error al registrar log de acceso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public int actualizarUsuario(String nuevoNombre1, String nuevoNombre2, String nuevoApellido1, String nuevoApellido2,
            String nuevoCorreo, String nuevaClave, String nuevaCedula, String nuevoUsuario, int idUsuario, String currentHashedPassword, int idRol) throws SQLException {
        String sql = "UPDATE usuarios SET nombres = ?, apellidos = ?, correo = ?, contrasena = ?, cedula = ?, nombre_usuario = ?, id_rol = ? "
                + "WHERE id_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {

            String nuevosNombres = nuevoNombre1 + " " + nuevoNombre2;
            String nuevosApellidos = nuevoApellido1 + " " + nuevoApellido2;
            String contrasenaCifrada;
            // Si la contraseña nueva es igual a la actual (ya cifrada), no se vuelve a cifrar.
            if (nuevaClave.equals(currentHashedPassword)) {
                contrasenaCifrada = currentHashedPassword;
            } else {
                contrasenaCifrada = cifrarContrasena(nuevaClave);
            }

            ps.setString(1, nuevosNombres);
            ps.setString(2, nuevosApellidos);
            ps.setString(3, nuevoCorreo);
            ps.setString(4, contrasenaCifrada);
            ps.setString(5, nuevaCedula);
            ps.setString(6, nuevoUsuario);
            ps.setInt(7, idRol);
            ps.setInt(8, idUsuario);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0 ? 1 : 0;

        }
    }

    public Usuarios obtenerUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT id_usuario, nombres, apellidos, correo, nombre_usuario, cedula, contrasena, fecha_registro, id_rol FROM usuarios WHERE nombre_usuario = ?";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuarios u = new Usuarios();
                    u.setId(rs.getInt("id_usuario"));

                    String nombres = rs.getString("nombres");
                    String[] nombresArr = nombres.split(" ", 2);
                    u.setNombre1(nombresArr[0]);
                    u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");

                    String apellidos = rs.getString("apellidos");
                    String[] apellidosArr = apellidos.split(" ", 2);
                    u.setApellido1(apellidosArr[0]);
                    u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");

                    u.setCorreo(rs.getString("correo"));
                    u.setUsuario(rs.getString("nombre_usuario"));
                    u.setCedula(rs.getString("cedula"));
                    u.setClave(rs.getString("contrasena"));
                    u.setFechaRegistro(rs.getString("fecha_registro"));
                    u.setIdRol(rs.getInt("id_rol"));
                    return u;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.err.println("Error al obtener usuario por nombre: " + ex.getMessage());
        }
        return null;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre1() {
        return nombre1;
    }

    public void setNombre1(String nombre1) {
        this.nombre1 = nombre1;
    }

    public String getNombre2() {
        return nombre2;
    }

    public void setNombre2(String nombre2) {
        this.nombre2 = nombre2;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
}
