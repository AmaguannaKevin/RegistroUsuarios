/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Conexion.Config;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
 *
 * @author USER
 */
public class Usuarios {

    private ObjectId id; 
    private String nombre1;
    private String nombre2;
    private String apellido1;
    private String apellido2;
    private String correo;
    private String usuario;
    private String clave;
    private String cedula;
    private String fechaRegistro; 
    private ObjectId idRol;      

    public Usuarios() {
    }

    public Usuarios(ObjectId id, String nombre1, String nombre2, String apellido1, String apellido2,
                    String correo, String usuario, String clave, String cedula, String fechaRegistro, ObjectId idRol) {
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
                              String cedula, String correo, String usuario, String clave, ObjectId idRol) {
        if (usuarioExiste(usuario, correo, cedula)) {
            return 2;
        }
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        String nombres = nombre1 + " " + nombre2;
        String apellidos = apellido1 + " " + apellido2;
        String contrasenaCifrada = cifrarContrasena(clave);

        Document userDoc = new Document("nombres", nombres)
                .append("apellidos", apellidos)
                .append("correo", correo)
                .append("nombre_usuario", usuario)
                .append("cedula", cedula)
                .append("contrasena", contrasenaCifrada)
                .append("id_rol", idRol)
                .append("fecha_registro", new Date())
                .append("ultimo_acceso", new Date())
                .append("estado", true);

        collection.insertOne(userDoc);
        return 1;
    }

    // Verifica si existe un usuario con el mismo nombre_usuario, correo o cédula
    private boolean usuarioExiste(String usuario, String correo, String cedula) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("$or", Arrays.asList(
                new Document("nombre_usuario", usuario),
                new Document("correo", correo),
                new Document("cedula", cedula)
        ));
        Document found = collection.find(query).first();
        return found != null;
    }

    // Cifra la contraseña usando BCrypt
    private String cifrarContrasena(String clave) {
        return BCrypt.hashpw(clave, BCrypt.gensalt());
    }

    // Valida al usuario comparando la contraseña ingresada con la almacenada en la BD
    public boolean validarUsuario() {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("nombre_usuario", this.usuario);
        Document found = collection.find(query).first();
        if (found != null) {
            String contrasenaBD = found.getString("contrasena");
            if (BCrypt.checkpw(this.clave, contrasenaBD)) {
                this.id = found.getObjectId("_id");
                this.correo = found.getString("correo");
                this.cedula = found.getString("cedula");
                this.idRol = found.get("id_rol", ObjectId.class);
                Date fecha = found.getDate("fecha_registro");
                this.fechaRegistro = (fecha != null) ? fecha.toString() : "";
                return true;
            }
        }
        return false;
    }

    // Retorna el nombre completo concatenando los campos "nombres" y "apellidos"
    public String obtenerNombreCompleto() {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("nombre_usuario", this.usuario);
        Document found = collection.find(query).first();
        if (found != null) {
            return found.getString("nombres") + " " + found.getString("apellidos");
        }
        return "";
    }

    // Busca usuarios cuyo nombre de usuario contenga el patrón proporcionado (búsqueda case-insensitive)
    public static List<Usuarios> buscarUsuariosPorUsuario(String nombreUsuario) {
        List<Usuarios> listaUsuarios = new ArrayList<>();
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("nombre_usuario", new Document("$regex", nombreUsuario).append("$options", "i"));
        FindIterable<Document> docs = collection.find(query);
        for (Document doc : docs) {
            Usuarios u = new Usuarios();
            u.setId(doc.getObjectId("_id"));
            String fullNames = doc.getString("nombres");
            String[] nombresArr = fullNames.split(" ", 2);
            u.setNombre1(nombresArr[0]);
            u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");
            String fullApellidos = doc.getString("apellidos");
            String[] apellidosArr = fullApellidos.split(" ", 2);
            u.setApellido1(apellidosArr[0]);
            u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");
            u.setCorreo(doc.getString("correo"));
            u.setUsuario(doc.getString("nombre_usuario"));
            u.setCedula(doc.getString("cedula"));
            Date fecha = doc.getDate("fecha_registro");
            u.setFechaRegistro(fecha != null ? fecha.toString() : "");
            u.setIdRol(doc.get("id_rol", ObjectId.class));
            listaUsuarios.add(u);
        }
        return listaUsuarios;
    }

    // Carga los datos completos del usuario basándose en el nombre de usuario
    public void buscarDatosUsuario() {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("nombre_usuario", this.usuario);
        Document doc = collection.find(query).first();
        if (doc != null) {
            this.id = doc.getObjectId("_id");
            String[] nombresArr = doc.getString("nombres").split(" ", 2);
            this.nombre1 = nombresArr[0];
            this.nombre2 = (nombresArr.length > 1 ? nombresArr[1] : "");
            String[] apellidosArr = doc.getString("apellidos").split(" ", 2);
            this.apellido1 = apellidosArr[0];
            this.apellido2 = (apellidosArr.length > 1 ? apellidosArr[1] : "");
            this.correo = doc.getString("correo");
            this.cedula = doc.getString("cedula");
            this.clave = doc.getString("contrasena");
            this.idRol = doc.get("id_rol", ObjectId.class);
            Date fecha = doc.getDate("fecha_registro");
            this.fechaRegistro = (fecha != null) ? fecha.toString() : "";
        }
    }

    // Obtiene un usuario por su cédula
    public Usuarios obtenerUsuarioPorCedula(String cedula) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("cedula", cedula);
        Document doc = collection.find(query).first();
        if (doc != null) {
            Usuarios u = new Usuarios();
            u.setId(doc.getObjectId("_id"));

            String nombres = doc.getString("nombres");
            String[] nombresArr = nombres.split(" ", 2);
            u.setNombre1(nombresArr[0]);
            u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");

            String apellidos = doc.getString("apellidos");
            String[] apellidosArr = apellidos.split(" ", 2);
            u.setApellido1(apellidosArr[0]);
            u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");

            u.setCorreo(doc.getString("correo"));
            u.setUsuario(doc.getString("nombre_usuario"));
            u.setCedula(doc.getString("cedula"));
            u.setClave(doc.getString("contrasena"));
            Date fecha = doc.getDate("fecha_registro");
            u.setFechaRegistro(fecha != null ? fecha.toString() : "");
            u.setIdRol(doc.get("id_rol", ObjectId.class));
            return u;
        }
        return null;
    }

    // Verifica si existe un usuario con la cédula indicada
    public boolean existeCedula(String cedula) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("cedula", cedula);
        Document doc = collection.find(query).first();
        return doc != null;
    }

    // Actualiza el campo "ultimo_acceso" para el usuario dado
    public int actualizarUltimoAcceso(ObjectId idUsuario) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("_id", idUsuario);
        Document update = new Document("$set", new Document("ultimo_acceso", new Date()));
        UpdateResult result = collection.updateOne(query, update);
        return (int) result.getModifiedCount();
    }

    public void registrarLogAcceso(ObjectId idUsuario) {
        registrarLogAcceso(idUsuario, "login", "Acceso al sistema");
    }

    public void registrarLogAcceso(ObjectId idUsuario, String accion, String detalles) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> usuariosCollection = database.getCollection("usuarios");

        Document usuarioDoc = usuariosCollection.find(new Document("_id", idUsuario)).first();
        if (usuarioDoc == null) {
            System.err.println("Error: El usuario con ID " + idUsuario + " no existe");
            return;
        }

        MongoCollection<Document> logCollection = database.getCollection("log_acceso");
        Document logDoc = new Document("id_usuario", idUsuario)
                .append("fecha_hora", new Date())
                .append("accion", accion)
                .append("ip", "") 
                .append("detalles", detalles);
        logCollection.insertOne(logDoc);
    }

    // Actualiza la información del usuario
    public int actualizarUsuario(String nuevoNombre1, String nuevoNombre2, String nuevoApellido1, String nuevoApellido2,
                                 String nuevoCorreo, String nuevaClave, String nuevaCedula, String nuevoUsuario,
                                 ObjectId idUsuario, String currentHashedPassword, ObjectId idRol) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        String nuevosNombres = nuevoNombre1 + " " + nuevoNombre2;
        String nuevosApellidos = nuevoApellido1 + " " + nuevoApellido2;
        String contrasenaCifrada = nuevaClave.equals(currentHashedPassword)
                ? currentHashedPassword
                : cifrarContrasena(nuevaClave);

        Document query = new Document("_id", idUsuario);
        Document update = new Document("$set", new Document("nombres", nuevosNombres)
                .append("apellidos", nuevosApellidos)
                .append("correo", nuevoCorreo)
                .append("contrasena", contrasenaCifrada)
                .append("cedula", nuevaCedula)
                .append("nombre_usuario", nuevoUsuario)
                .append("id_rol", idRol));
        UpdateResult result = collection.updateOne(query, update);
        return result.getModifiedCount() > 0 ? 1 : 0;
    }

    // Obtiene un usuario a partir del nombre de usuario
    public Usuarios obtenerUsuarioPorNombre(String nombreUsuario) {
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("usuarios");

        Document query = new Document("nombre_usuario", nombreUsuario);
        Document doc = collection.find(query).first();
        if (doc != null) {
            Usuarios u = new Usuarios();
            u.setId(doc.getObjectId("_id"));

            String nombres = doc.getString("nombres");
            String[] nombresArr = nombres.split(" ", 2);
            u.setNombre1(nombresArr[0]);
            u.setNombre2(nombresArr.length > 1 ? nombresArr[1] : "");

            String apellidos = doc.getString("apellidos");
            String[] apellidosArr = apellidos.split(" ", 2);
            u.setApellido1(apellidosArr[0]);
            u.setApellido2(apellidosArr.length > 1 ? apellidosArr[1] : "");

            u.setCorreo(doc.getString("correo"));
            u.setUsuario(doc.getString("nombre_usuario"));
            u.setCedula(doc.getString("cedula"));
            u.setClave(doc.getString("contrasena"));
            Date fecha = doc.getDate("fecha_registro");
            u.setFechaRegistro(fecha != null ? fecha.toString() : "");
            u.setIdRol(doc.get("id_rol", ObjectId.class));
            return u;
        }
        return null;
    }

    // Getters y Setters

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public ObjectId getIdRol() {
        return idRol;
    }

    public void setIdRol(ObjectId idRol) {
        this.idRol = idRol;
    }
}
