/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;
import conector.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
//import javax.swing.JOptionPane;
import modelo.Comprador;
/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class CompradorData {
     private Connection con;

    public CompradorData() {
        this.con = Conexion.getConexion();
    }
    
    //Hicimos los JOptionPane para cuando pasemos a vistas reales, por ahora como es por consola los comentamos nomas
    
    public void guardarComprador(Comprador comprador) {
        String sql = "INSERT INTO COMPRADOR (dni, nombre, fechaNac, password, medioPago, activo) " +"VALUES (?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, comprador.getDni());
            ps.setString(2, comprador.getNombre());
            ps.setDate(3, Date.valueOf(comprador.getFechaNac()));
            ps.setString(4, comprador.getPassword());
            ps.setString(5, comprador.getMedioPago());
            ps.setBoolean(6, comprador.isActivo());
            
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Comprador guardado");
//            System.out.println("Comprador guardado");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar comprador: " + e.getMessage());
//            System.out.println("Error al guardar comprador: " + e.getMessage());
        }
    }
    
    public Comprador buscarComprador(String dni) {
        String sql = "SELECT * FROM COMPRADOR WHERE dni = ?";
        Comprador comprador = null;
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                comprador = new Comprador();
                comprador.setDni(rs.getString("dni"));
                comprador.setNombre(rs.getString("nombre"));
                comprador.setFechaNac(rs.getDate("fechaNac").toLocalDate());
                comprador.setPassword(rs.getString("password"));
                comprador.setMedioPago(rs.getString("medioPago"));
                comprador.setActivo(rs.getBoolean("activo"));
            } else {
//                JOptionPane.showMessageDialog(null, "No existe comprador con DNI: " + dni);
                System.out.println("No existe comprador con DNI: " + dni);
            }
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar comprador: " + e.getMessage());
//            System.out.println("Error al buscar comprador: " + e.getMessage());
        }
        
        return comprador;
    }
    
    public List<Comprador> listarCompradores() {
        String sql = "SELECT * FROM COMPRADOR WHERE activo = 1";
        List<Comprador> compradores = new ArrayList<>();
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comprador comprador = new Comprador();
                comprador.setDni(rs.getString("dni"));
                comprador.setNombre(rs.getString("nombre"));
                comprador.setFechaNac(rs.getDate("fechaNac").toLocalDate());
                comprador.setPassword(rs.getString("password"));
                comprador.setMedioPago(rs.getString("medioPago"));
                comprador.setActivo(rs.getBoolean("activo"));
                
                compradores.add(comprador);
            }
            
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar compradores: " + e.getMessage());
//            System.out.println("Error al listar compradores: " + e.getMessage());
        }
        
        return compradores;
    }
    
    public void actualizarComprador(Comprador comprador) {
        String sql = "UPDATE COMPRADOR SET nombre = ?, fechaNac = ?, password = ?, medioPago = ? " + "WHERE dni = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, comprador.getNombre());
            ps.setDate(2, Date.valueOf(comprador.getFechaNac()));
            ps.setString(3, comprador.getPassword());
            ps.setString(4, comprador.getMedioPago());
            ps.setString(5, comprador.getDni());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Comprador actualizado");
//                System.out.println("Comprador actualizado");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el comprador");
//                System.out.println("No se encontró el comprador");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar comprador: " + e.getMessage());
//            System.out.println("Error al actualizar comprador: " + e.getMessage());
        }
    }
    
    public void bajaComprador(String dni) {
        String sql = "UPDATE COMPRADOR SET activo = 0 WHERE dni = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Comprador dado de baja");
//                System.out.println("Comprador dado de baja");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el comprador");
//                System.out.println("No se encontró el comprador");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al dar de baja: " + e.getMessage());
//            System.out.println("Error al dar de baja: " + e.getMessage());
        }
    }
    
    public void altaComprador(String dni) {
        String sql = "UPDATE COMPRADOR SET activo = 1 WHERE dni = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Comprador reactivado");
//                System.out.println("Comprador reactivado");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el comprador");
//                System.out.println("No se encontró el comprador");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al reactivar: " + e.getMessage());
//            System.out.println("Error al reactivar: " + e.getMessage());
        }
    }
    
    public void borrarComprador(String dni) {
        String sql = "DELETE FROM COMPRADOR WHERE dni = ?";
        
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, dni);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Comprador eliminado");
//                System.out.println("Comprador eliminado");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el comprador");
//                System.out.println("No se encontró el comprador");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al borrar: " + e.getMessage());
//            System.out.println("Error al borrar: " + e.getMessage());
        }
    }
    
    public boolean tieneTicketsAsociados(String dni) {
        String sql = "SELECT COUNT(*) as total FROM ticket_compra WHERE dniComprador = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int total = rs.getInt("total");
                return total > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error verificando tickets asociados: " + e.getMessage());
        }

        return false; // En caso de error, asumimos que tiene tickets
    }
}
