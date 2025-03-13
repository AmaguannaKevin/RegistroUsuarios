/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Conexion.Config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author USER
 */
public class Rol {

    private int id_rol;
    private String nombre;

    public Rol() {
    }

    public Rol(int idRol, String nombreRol) {
        this.id_rol = idRol;
        this.nombre = nombreRol;
    }

    public int getIdRol() {
        return id_rol;
    }

    public String getNombreRol() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Mostrar el nombre del rol en el JComboBox
    }

    public List<Rol> obtenerRolesDesdeBD() throws SQLException {
        List<Rol> roles = new ArrayList<>();
        String sql = "SELECT id_rol, nombre FROM roles";
        try (Connection c = new Config().conection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idRol = rs.getInt("id_rol");
                String nombreRol = rs.getString("nombre");
                roles.add(new Rol(idRol, nombreRol));
            }
        }
        return roles;
    }
}