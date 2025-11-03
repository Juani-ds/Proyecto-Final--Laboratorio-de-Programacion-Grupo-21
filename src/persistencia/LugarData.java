/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import conector.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Lugar;
import modelo.Proyeccion;

/**
 *
 * @author Usuario
 */
public class LugarData {
    private Connection con;
    private ProyeccionData proyeccionData;    
    
    public LugarData(){
        this.con = Conexion.getConexion();
        proyeccionData = new ProyeccionData();
    }
    
    public void guardarLugar(Lugar lugar){
        String sql = "INSERT INTO lugar(codLugar, idProyeccion, fila, numero, estado) VALUES(?, ?, ?, ?, ?)";

        try(PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            
            ps.setInt(1, lugar.getCodLugar());
            ps.setInt(2, lugar.getProyeccion().getIdProyeccion());
            ps.setString(3, String.valueOf(lugar.getFila()));
            ps.setInt(4, lugar.getNumero());
            ps.setString(5, lugar.getEstado());

            ps.executeUpdate();
            System.out.println("Lugar guardado correctamente");
            
        }catch(SQLException e){
            System.out.println("Error al guardar lugar: " + e.getMessage());           
        }
    }
    
    public void actualizarLugar(Lugar lugar) {
        String sql = "UPDATE lugar SET idProyeccion = ?, fila = ?, numero = ?, estado = ? WHERE codLugar = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lugar.getProyeccion().getIdProyeccion());
            ps.setString(2, String.valueOf(lugar.getFila()));
            ps.setInt(3, lugar.getNumero());
            ps.setString(4, lugar.getEstado());
            ps.setInt(5, lugar.getCodLugar());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Lugar actualizado correctamente");
            } else {
                System.out.println("No se encontró el lugar a actualizar");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar lugar: " + e.getMessage());
        }
    }
    
    public void eliminarLugar(int codLugar) {
        String sql = "DELETE FROM lugar WHERE codLugar = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codLugar);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Lugar eliminado correctamente");
            } else {
                System.out.println("No se encontró el lugar a eliminar");
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar lugar: " + e.getMessage());
        }
    }
    
    public Lugar buscarLugar(int codLugar) {
        String sql = "SELECT * FROM lugar WHERE codLugar = ?";
        Lugar lugar = null;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codLugar);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lugar = new Lugar();
                lugar.setCodLugar(rs.getInt("codLugar"));

                int idProyeccion = rs.getInt("idProyeccion");
                Proyeccion proyeccion = proyeccionData.buscarProyeccion(idProyeccion);
                lugar.setProyeccion(proyeccion);

                lugar.setFila(rs.getString("fila").charAt(0));
                lugar.setNumero(rs.getInt("numero"));
                lugar.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar lugar: " + e.getMessage());
        }
        return lugar;
    }
    
    public List<Lugar> listarLugaresPorProyeccion(int idProyeccion) {
        List<Lugar> lugares = new ArrayList<>();
        String sql = "SELECT * FROM lugar WHERE idProyeccion = ? ORDER BY fila, numero";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProyeccion);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Lugar lugar = new Lugar();
                lugar.setCodLugar(rs.getInt("codLugar"));

                Proyeccion proyeccion = proyeccionData.buscarProyeccion(idProyeccion);
                lugar.setProyeccion(proyeccion);

                lugar.setFila(rs.getString("fila").charAt(0));
                lugar.setNumero(rs.getInt("numero"));
                lugar.setEstado(rs.getString("estado"));
                lugares.add(lugar);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar lugares: " + e.getMessage());
        }
        return lugares;
    }
    
    public void cambiarEstadoLugar(int codLugar, String nuevoEstado) {
        String sql = "UPDATE lugar SET estado = ? WHERE codLugar = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, codLugar);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado del lugar: " + e.getMessage());
        }
    }
    
    public List<Lugar> listarLugares() {
        List<Lugar> lugares = new ArrayList<>();
        String sql = "SELECT * FROM lugar ORDER BY idProyeccion, fila, numero";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Lugar lugar = new Lugar();
                lugar.setCodLugar(rs.getInt("codLugar"));

                int idProyeccion = rs.getInt("idProyeccion");
                Proyeccion proyeccion = proyeccionData.buscarProyeccion(idProyeccion);
                lugar.setProyeccion(proyeccion);

                lugar.setFila(rs.getString("fila").charAt(0));
                lugar.setNumero(rs.getInt("numero"));
                lugar.setEstado(rs.getString("estado"));
                lugares.add(lugar);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar todos los lugares: " + e.getMessage());
        }
        return lugares;
    }
    
}
