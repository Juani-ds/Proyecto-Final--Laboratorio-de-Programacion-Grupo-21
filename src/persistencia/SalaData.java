

package persistencia;
/*
 *@author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */

import conector.Conexion;
import modelo.Sala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaData {

    // INSERTAR SALA
    public void insertarSala(Sala sala) {
        String sql = "INSERT INTO sala(apta3D, capacidad, estado, activo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, sala.isApta3D());
            ps.setInt(2, sala.getCapacidad());
            ps.setString(3, sala.getEstado());
            ps.setBoolean(4, sala.isActivo());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                sala.setNroSala(rs.getInt(1));
            }

            System.out.println("Sala insertada correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error insertando sala: " + ex.getMessage());
        }
    }

    // LISTAR TODAS LAS SALAS
    public List<Sala> listarSalas() {
        List<Sala> lista = new ArrayList<>();
        String sql = "SELECT * FROM sala";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sala sala = new Sala(
                        rs.getInt("nroSala"),
                        rs.getBoolean("apta3D"),
                        rs.getInt("capacidad"),
                        rs.getString("estado"),
                        rs.getBoolean("activo")
                );
                lista.add(sala);
            }

        } catch (SQLException ex) {
            System.out.println("Error listando salas: " + ex.getMessage());
        }

        return lista;
    }

    // BUSCAR POR NRO DE SALA
    public Sala buscarSala(int nroSala) {
        String sql = "SELECT * FROM sala WHERE nroSala = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, nroSala);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Sala(
                        rs.getInt("nroSala"),
                        rs.getBoolean("apta3D"),
                        rs.getInt("capacidad"),
                        rs.getString("estado"),
                        rs.getBoolean("activo")
                );
            }

        } catch (SQLException ex) {
            System.out.println("Error buscando sala: " + ex.getMessage());
        }

        return null;
    }

    // ACTUALIZAR SALA
    public void actualizarSala(Sala sala) {
        String sql = "UPDATE sala SET apta3D=?, capacidad=?, estado=?, activo=? WHERE nroSala=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setBoolean(1, sala.isApta3D());
            ps.setInt(2, sala.getCapacidad());
            ps.setString(3, sala.getEstado());
            ps.setBoolean(4, sala.isActivo());
            ps.setInt(5, sala.getNroSala());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Sala actualizada correctamente.");
            } else {
                System.out.println("No se encontró sala con nro: " + sala.getNroSala());
            }

        } catch (SQLException ex) {
            System.out.println("Error actualizando sala: " + ex.getMessage());
        }
    }

    // CAMBIAR ESTADO DE SALA
    public void cambiarEstadoSala(int nroSala, boolean activo) {
        String sql = "UPDATE sala SET activo=? WHERE nroSala=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, nroSala);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Estado de la sala actualizado correctamente.");
            } else {
                System.out.println("No se encontró sala con nro: " + nroSala);
            }

        } catch (SQLException ex) {
            System.out.println("Error cambiando estado de sala: " + ex.getMessage());
        }
    }

    // ELIMINAR SALA
    public void eliminarSala(int nroSala) {
        String sql = "DELETE FROM sala WHERE nroSala=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, nroSala);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Sala eliminada correctamente.");
            } else {
                System.out.println("No se encontró sala con nro: " + nroSala);
            }

        } catch (SQLException ex) {
            System.out.println("Error eliminando sala: " + ex.getMessage());
        }
    }
}
