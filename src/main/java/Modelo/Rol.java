/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;
import Conexion.Config;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USER
 */


public class Rol {

    private ObjectId idRol;
    private String nombre;

    public Rol() {
    }

    public Rol(ObjectId idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombre = nombreRol;
    }

    public ObjectId getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Se mostrará el nombre del rol (por ejemplo, en un JComboBox)
    }

    public List<Rol> obtenerRolesDesdeBD() {
        List<Rol> roles = new ArrayList<>();
        MongoDatabase database = Config.conectar();
        MongoCollection<Document> collection = database.getCollection("roles");

        FindIterable<Document> docs = collection.find();
        for (Document doc : docs) {
            ObjectId id = doc.getObjectId("_id");
            String nombreRol = doc.getString("nombre");
            roles.add(new Rol(id, nombreRol));
        }
        return roles;
    }
}
