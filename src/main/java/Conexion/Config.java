/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
/**
 *
 * @author USER
 */


public class Config {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    // Método para conectar con MongoDB
    public static MongoDatabase conectar() {
        try {
            if (mongoClient == null) {
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                mongoClient = new MongoClient(uri);
                database = mongoClient.getDatabase("sistema_usuarios");
                System.out.println("✅ Conexión exitosa a MongoDB");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al conectar a MongoDB: " + e.getMessage());
        }
        return database;
    }

    // Método para cerrar la conexión
    public static void desconectar() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                mongoClient = null;
                System.out.println("🔌 Conexión cerrada");
            }
        } catch (Exception ex) {
            System.out.println("❌ Error al cerrar la conexión: " + ex.getMessage());
        }
    }
}
