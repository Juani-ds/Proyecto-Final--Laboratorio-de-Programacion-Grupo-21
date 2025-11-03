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
        String sql = "INSERT INTO proyeccion (idPelicula, nroSala, idioma, es3D, subtitulada, horaInicio, horaFin, precioLugar, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
        String sql = "SELECT * FROM proyeccion WHERE idProyeccion = ?";
        Proyeccion p = null;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pelicula pelicula = new PeliculaData().buscarPeliculaPorId(rs.getInt("idPelicula"));
                Sala sala = new SalaData().buscarSala(rs.getInt("nroSala"));

                p = new Proyeccion();
                p.setIdProyeccion(rs.getInt("idProyeccion"));
                p.setPelicula(pelicula);
                p.setSala(sala);
                p.setIdioma(rs.getString("idioma"));
                p.setEs3D(rs.getBoolean("es3D"));
                p.setSubtitulada(rs.getBoolean("subtitulada"));
                p.setHoraInicio(rs.getTimestamp("horaInicio").toLocalDateTime());
                p.setHoraFin(rs.getTimestamp("horaFin").toLocalDateTime());
                p.setPrecioLugar(rs.getDouble("precioLugar"));
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
                Pelicula pelicula = new PeliculaData().buscarPeliculaPorId(rs.getInt("idPelicula"));
                Sala sala = new SalaData().buscarSala(rs.getInt("nroSala"));

                Proyeccion p = new Proyeccion();
                p.setIdProyeccion(rs.getInt("idProyeccion"));
                p.setPelicula(pelicula);
                p.setSala(sala);
                p.setIdioma(rs.getString("idioma"));
                p.setEs3D(rs.getBoolean("es3D"));
                p.setSubtitulada(rs.getBoolean("subtitulada"));
                p.setHoraInicio(rs.getTimestamp("horaInicio").toLocalDateTime());
                p.setHoraFin(rs.getTimestamp("horaFin").toLocalDateTime());
                p.setPrecioLugar(rs.getDouble("precioLugar"));
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
        String sql = "UPDATE proyeccion SET idPelicula=?, nroSala=?, idioma=?, es3D=?, subtitulada=?, horaInicio=?, horaFin=?, precioLugar=?, activo=? WHERE idProyeccion=?";
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
        String sql = "UPDATE proyeccion SET activo=false WHERE idProyeccion=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Proyección eliminada (baja lógica) correctamente.");
        } catch (SQLException ex) {
            System.out.println("Error eliminando proyección: " + ex.getMessage());
        }
    }

    // BUSCAR PROYECCIONES POR PELÍCULA
    public List<Proyeccion> buscarPorPelicula(int idPelicula) {
        List<Proyeccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyeccion WHERE idPelicula = ? AND activo = true";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Proyeccion p = mapearProyeccion(rs);
                lista.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Error buscando proyecciones por película: " + ex.getMessage());
        }
        return lista;
    }

    // BUSCAR PROYECCIONES CON FILTROS AVANZADOS
    public List<Proyeccion> buscarConFiltros(Integer idPelicula, String idioma, Boolean es3D, Boolean subtitulada) {
        List<Proyeccion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM proyeccion WHERE activo = true");

        if (idPelicula != null) {
            sql.append(" AND idPelicula = ?");
        }
        if (idioma != null && !idioma.isEmpty()) {
            sql.append(" AND idioma = ?");
        }
        if (es3D != null) {
            sql.append(" AND es3D = ?");
        }
        if (subtitulada != null) {
            sql.append(" AND subtitulada = ?");
        }

        sql.append(" ORDER BY horaInicio");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (idPelicula != null) {
                ps.setInt(paramIndex++, idPelicula);
            }
            if (idioma != null && !idioma.isEmpty()) {
                ps.setString(paramIndex++, idioma);
            }
            if (es3D != null) {
                ps.setBoolean(paramIndex++, es3D);
            }
            if (subtitulada != null) {
                ps.setBoolean(paramIndex++, subtitulada);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Proyeccion p = mapearProyeccion(rs);
                lista.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Error buscando proyecciones con filtros: " + ex.getMessage());
        }
        return lista;
    }

    // LISTAR SOLO PROYECCIONES ACTIVAS
    public List<Proyeccion> listarProyeccionesActivas() {
        List<Proyeccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyeccion WHERE activo = true ORDER BY horaInicio";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Proyeccion p = mapearProyeccion(rs);
                lista.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Error listando proyecciones activas: " + ex.getMessage());
        }
        return lista;
    }

    // OBTENER IDIOMAS DISPONIBLES
    public List<String> obtenerIdiomasDisponibles() {
        List<String> idiomas = new ArrayList<>();
        String sql = "SELECT DISTINCT idioma FROM proyeccion WHERE activo = true ORDER BY idioma";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                idiomas.add(rs.getString("idioma"));
            }
        } catch (SQLException ex) {
            System.out.println("Error obteniendo idiomas: " + ex.getMessage());
        }
        return idiomas;
    }

    // VERIFICAR DISPONIBILIDAD DE SALA EN HORARIO
    public boolean verificarDisponibilidadSala(int nroSala, java.time.LocalDateTime horaInicio, java.time.LocalDateTime horaFin, Integer idProyeccionExcluir) {
        String sql = "SELECT COUNT(*) FROM proyeccion WHERE nroSala = ? AND activo = true " +
                     "AND ((horaInicio BETWEEN ? AND ?) OR (horaFin BETWEEN ? AND ?) " +
                     "OR (? BETWEEN horaInicio AND horaFin) OR (? BETWEEN horaInicio AND horaFin))";

        if (idProyeccionExcluir != null) {
            sql += " AND idProyeccion != ?";
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nroSala);
            ps.setTimestamp(2, Timestamp.valueOf(horaInicio));
            ps.setTimestamp(3, Timestamp.valueOf(horaFin));
            ps.setTimestamp(4, Timestamp.valueOf(horaInicio));
            ps.setTimestamp(5, Timestamp.valueOf(horaFin));
            ps.setTimestamp(6, Timestamp.valueOf(horaInicio));
            ps.setTimestamp(7, Timestamp.valueOf(horaFin));

            if (idProyeccionExcluir != null) {
                ps.setInt(8, idProyeccionExcluir);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // True si no hay conflictos
            }
        } catch (SQLException ex) {
            System.out.println("Error verificando disponibilidad de sala: " + ex.getMessage());
        }
        return false;
    }

    // MÉTODO AUXILIAR PARA MAPEAR RESULTSET A PROYECCIÓN
    private Proyeccion mapearProyeccion(ResultSet rs) throws SQLException {
        Pelicula pelicula = new PeliculaData().buscarPeliculaPorId(rs.getInt("idPelicula"));
        Sala sala = new SalaData().buscarSala(rs.getInt("nroSala"));

        Proyeccion p = new Proyeccion();
        p.setIdProyeccion(rs.getInt("idProyeccion"));
        p.setPelicula(pelicula);
        p.setSala(sala);
        p.setIdioma(rs.getString("idioma"));
        p.setEs3D(rs.getBoolean("es3D"));
        p.setSubtitulada(rs.getBoolean("subtitulada"));
        p.setHoraInicio(rs.getTimestamp("horaInicio").toLocalDateTime());
        p.setHoraFin(rs.getTimestamp("horaFin").toLocalDateTime());
        p.setPrecioLugar(rs.getDouble("precioLugar"));
        p.setActivo(rs.getBoolean("activo"));

        return p;
    }
}
