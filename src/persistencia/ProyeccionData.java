package persistencia;

import conector.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.Pelicula;
import modelo.Sala;
import modelo.Proyeccion;

/**
 * 
 * 
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class ProyeccionData {

    private Connection con;

    public ProyeccionData() {
        con = Conexion.getConexion();
    }

    // INSERTAR NUEVA PROYECCIÓN
    public void guardarProyeccion(Proyeccion proyeccion) {
        String sql = "INSERT INTO proyeccion (id_pelicula, id_sala, idioma, es3D, subtitulada, hora_inicio, hora_fin, precio_lugar, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, proyeccion.getPelicula().getIdPelicula());
            ps.setInt(2, proyeccion.getSala().getNroSala());
            ps.setString(3, proyeccion.getIdioma());
            ps.setBoolean(4, proyeccion.isEs3D());
            ps.setBoolean(5, proyeccion.isSubtitulada());
            ps.setTimestamp(6, Timestamp.valueOf(proyeccion.getHoraInicio()));
            ps.setTimestamp(7, Timestamp.valueOf(proyeccion.getHoraFin()));
            ps.setDouble(8, proyeccion.getPrecioLugar());
            ps.setBoolean(9, proyeccion.isActivo());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                proyeccion.setIdProyeccion(rs.getInt(1));
            }

            System.out.println("Proyección insertada correctamente.");
        } catch (SQLException ex) {
            System.out.println("Error insertando proyección: " + ex.getMessage());
        }
    }

    // BUSCAR PROYECCIÓN POR ID
    public Proyeccion buscarProyeccion(int id) {
        String sql = "SELECT * FROM proyeccion WHERE id_proyeccion = ?";
        Proyeccion p = null;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pelicula pelicula = new PeliculaData().buscarPeliculaPorId(rs.getInt("id_pelicula"));
                Sala sala = new SalaData().buscarSala(rs.getInt("id_sala"));

                p = new Proyeccion();
                p.setIdProyeccion(rs.getInt("id_proyeccion"));
                p.setPelicula(pelicula);
                p.setSala(sala);
                p.setIdioma(rs.getString("idioma"));
                p.setEs3D(rs.getBoolean("es3D"));
                p.setSubtitulada(rs.getBoolean("subtitulada"));
                p.setHoraInicio(rs.getTimestamp("hora_inicio").toLocalDateTime());
                p.setHoraFin(rs.getTimestamp("hora_fin").toLocalDateTime());
                p.setPrecioLugar(rs.getDouble("precio_lugar"));
                p.setActivo(rs.getBoolean("activo"));
            }
        } catch (SQLException ex) {
            System.out.println("Error buscando proyección: " + ex.getMessage());
        }
        return p;
    }

    // LISTAR TODAS LAS PROYECCIONES
    public List<Proyeccion> listarProyecciones() {
        List<Proyeccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyeccion";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula pelicula = new PeliculaData().buscarPeliculaPorId(rs.getInt("id_pelicula"));
                Sala sala = new SalaData().buscarSala(rs.getInt("id_sala"));

                Proyeccion p = new Proyeccion();
                p.setIdProyeccion(rs.getInt("id_proyeccion"));
                p.setPelicula(pelicula);
                p.setSala(sala);
                p.setIdioma(rs.getString("idioma"));
                p.setEs3D(rs.getBoolean("es3D"));
                p.setSubtitulada(rs.getBoolean("subtitulada"));
                p.setHoraInicio(rs.getTimestamp("hora_inicio").toLocalDateTime());
                p.setHoraFin(rs.getTimestamp("hora_fin").toLocalDateTime());
                p.setPrecioLugar(rs.getDouble("precio_lugar"));
                p.setActivo(rs.getBoolean("activo"));

                lista.add(p);
            }

        } catch (SQLException ex) {
            System.out.println("Error listando proyecciones: " + ex.getMessage());
        }
        return lista;
    }

    // ACTUALIZAR PROYECCIÓN
    public void actualizarProyeccion(Proyeccion proyeccion) {
        String sql = "UPDATE proyeccion SET id_pelicula=?, id_sala=?, idioma=?, es3D=?, subtitulada=?, hora_inicio=?, hora_fin=?, precio_lugar=?, activo=? WHERE id_proyeccion=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, proyeccion.getPelicula().getIdPelicula());
            ps.setInt(2, proyeccion.getSala().getNroSala());
            ps.setString(3, proyeccion.getIdioma());
            ps.setBoolean(4, proyeccion.isEs3D());
            ps.setBoolean(5, proyeccion.isSubtitulada());
            ps.setTimestamp(6, Timestamp.valueOf(proyeccion.getHoraInicio()));
            ps.setTimestamp(7, Timestamp.valueOf(proyeccion.getHoraFin()));
            ps.setDouble(8, proyeccion.getPrecioLugar());
            ps.setBoolean(9, proyeccion.isActivo());
            ps.setInt(10, proyeccion.getIdProyeccion());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Proyección actualizada correctamente.");
            } else {
                System.out.println("No se encontró proyección con ID: " + proyeccion.getIdProyeccion());
            }
        } catch (SQLException ex) {
            System.out.println("Error actualizando proyección: " + ex.getMessage());
        }
    }

    // DAR DE BAJA LÓGICA (activo = false)
    public void eliminarProyeccion(int id) {
        String sql = "UPDATE proyeccion SET activo=false WHERE id_proyeccion=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Proyección eliminada (baja lógica) correctamente.");
        } catch (SQLException ex) {
            System.out.println("Error eliminando proyección: " + ex.getMessage());
        }
    }
}
