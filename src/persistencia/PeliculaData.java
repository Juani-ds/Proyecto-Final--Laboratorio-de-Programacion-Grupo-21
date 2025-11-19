package persistencia;
/*
 *@author  Alaina Reyes
    *@version 1.0
    *@editor Nahuel Guerra
 */

import conector.Conexion;
import modelo.Pelicula;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeliculaData {

    // INSERTAR PELICULA
    public void insertarPelicula(Pelicula pelicula) {
        String sql = "INSERT INTO pelicula(titulo, director, actores, origen, genero, estreno, enCartelera, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pelicula.getTitulo());
            ps.setString(2, pelicula.getDirector());
            ps.setString(3, pelicula.getActores());
            ps.setString(4, pelicula.getOrigen());
            ps.setString(5, pelicula.getGenero());
            ps.setDate(6, Date.valueOf(pelicula.getEstreno()));
            ps.setInt(7, pelicula.isEnCartelera() ? 1 : 0);
            ps.setInt(8, pelicula.isActivo() ? 1 : 0);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                pelicula.setIdPelicula(rs.getInt(1));
            }

            System.out.println("Película insertada correctamente.");

        } catch (SQLException ex) {
            System.out.println("Error insertando película: " + ex.getMessage());
        }
    }

    // LISTAR TODAS LAS PELICULAS
    public List<Pelicula> listarPeliculas() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM pelicula";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula pelicula = new Pelicula(
                    rs.getInt("idPelicula"),
                    rs.getString("titulo"),
                    rs.getString("director"),
                    rs.getString("actores"),
                    rs.getString("origen"),
                    rs.getString("genero"),
                    rs.getDate("estreno").toLocalDate(),
                    rs.getInt("enCartelera") == 1,
                    rs.getInt("activo") == 1
                );
                peliculas.add(pelicula);
            }

        } catch (SQLException ex) {
            System.out.println("Error listando películas: " + ex.getMessage());
        }

        return peliculas;
    }

    // BUSCAR POR ID
    public Pelicula buscarPeliculaPorId(int idPelicula) {
        String sql = "SELECT * FROM pelicula WHERE idPelicula = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Pelicula(
                    rs.getInt("idPelicula"),
                    rs.getString("titulo"),
                    rs.getString("director"),
                    rs.getString("actores"),
                    rs.getString("origen"),
                    rs.getString("genero"),
                    rs.getDate("estreno").toLocalDate(),
                    rs.getInt("enCartelera") == 1,
                    rs.getInt("activo") == 1
                );
            }

        } catch (SQLException ex) {
            System.out.println("Error buscando película: " + ex.getMessage());
        }

        return null;
    }

    // ACTUALIZAR PELICULA
    public void actualizarPelicula(Pelicula pelicula) {
        String sql = "UPDATE pelicula SET titulo=?, director=?, actores=?, origen=?, genero=?, estreno=?, enCartelera=?, activo=? WHERE idPelicula=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, pelicula.getTitulo());
            ps.setString(2, pelicula.getDirector());
            ps.setString(3, pelicula.getActores());
            ps.setString(4, pelicula.getOrigen());
            ps.setString(5, pelicula.getGenero());
            ps.setDate(6, Date.valueOf(pelicula.getEstreno()));
            ps.setInt(7, pelicula.isEnCartelera() ? 1 : 0);
            ps.setInt(8, pelicula.isActivo() ? 1 : 0);
            ps.setInt(9, pelicula.getIdPelicula());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Película actualizada correctamente.");
            } else {
                System.out.println("No se encontró película con ID: " + pelicula.getIdPelicula());
            }

        } catch (SQLException ex) {
            System.out.println("Error actualizando película: " + ex.getMessage());
        }
    }

    // CAMBIAR ESTADO
    public void cambiarEstadoPelicula(int idPelicula, boolean activo) {
        String sql = "UPDATE pelicula SET activo=? WHERE idPelicula=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setInt(2, idPelicula);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("Estado de la película actualizado correctamente.");
            } else {
                System.out.println("No se encontró película con ID: " + idPelicula);
            }

        } catch (SQLException ex) {
            System.out.println("Error cambiando estado de película: " + ex.getMessage());
        }
    }

    // ELIMINAR PELICULA
    public void eliminarPelicula(int idPelicula) {
        String sql = "DELETE FROM pelicula WHERE idPelicula=?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {

            ps.setInt(1, idPelicula);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Película eliminada correctamente.");
            } else {
                System.out.println("No se encontró película con ID: " + idPelicula);
            }

        } catch (SQLException ex) {
            System.out.println("Error eliminando película: " + ex.getMessage());
        }
    }

    // LISTAR PELICULAS EN CARTELERA Y ACTIVAS
    public List<Pelicula> listarPeliculasEnCartelera() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM pelicula WHERE enCartelera = 1 AND activo = 1 ORDER BY titulo";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula pelicula = new Pelicula(
                    rs.getInt("idPelicula"),
                    rs.getString("titulo"),
                    rs.getString("director"),
                    rs.getString("actores"),
                    rs.getString("origen"),
                    rs.getString("genero"),
                    rs.getDate("estreno").toLocalDate(),
                    rs.getInt("enCartelera") == 1,
                    rs.getInt("activo") == 1
                );
                peliculas.add(pelicula);
            }

        } catch (SQLException ex) {
            System.out.println("Error listando películas en cartelera: " + ex.getMessage());
        }

        return peliculas;
    }

    // VERIFICAR SI LA PELÍCULA TIENE RELACIONES (PROYECCIONES)
    public boolean tieneProyeccionesRelacionadas(int idPelicula) {
        String sql = "SELECT COUNT(*) FROM proyeccion WHERE idPelicula = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException ex) {
            System.out.println("Error verificando proyecciones: " + ex.getMessage());
        }

        return false;
    }

    // CONTAR PROYECCIONES RELACIONADAS
    public int contarProyeccionesRelacionadas(int idPelicula) {
        String sql = "SELECT COUNT(*) FROM proyeccion WHERE idPelicula = ?";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPelicula);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            System.out.println("Error contando proyecciones: " + ex.getMessage());
        }

        return 0;
    }

    // LISTAR PRÓXIMOS ESTRENOS (películas con fecha de estreno futura y activas)
    public List<Pelicula> listarProximosEstrenos() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM pelicula WHERE estreno > CURDATE() AND activo = 1 ORDER BY estreno";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pelicula pelicula = new Pelicula(
                    rs.getInt("idPelicula"),
                    rs.getString("titulo"),
                    rs.getString("director"),
                    rs.getString("actores"),
                    rs.getString("origen"),
                    rs.getString("genero"),
                    rs.getDate("estreno").toLocalDate(),
                    rs.getInt("enCartelera") == 1,
                    rs.getInt("activo") == 1
                );
                peliculas.add(pelicula);
            }

        } catch (SQLException ex) {
            System.out.println("Error listando próximos estrenos: " + ex.getMessage());
        }

        return peliculas;
    }
    
    public List<String> obtenerOrigenes() {
        List<String> origenes = new ArrayList<>();
        String sql = "SELECT DISTINCT origen FROM pelicula ORDER BY origen";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                origenes.add(rs.getString("origen"));
            }

        } catch (SQLException ex) {
            System.out.println("Error obteniendo orígenes: " + ex.getMessage());
        }

        return origenes;
    }
    
    public List<Object[]> obtenerRanking(String genero, String origen, LocalDate desde) {

        List<Object[]> lista = new ArrayList<>();

        String sql =
                "SELECT p.titulo AS nombre, " +
                "       SUM(d.cantidad) AS espectadores, " +
                "       SUM(d.subtotal) AS ingresos " +
                "FROM pelicula p " +
                "JOIN proyeccion pr ON p.idPelicula = pr.idPelicula " +
                "JOIN detalle_ticket d ON pr.idProyeccion = d.idProyeccion " +
                "JOIN ticket_compra t ON d.idTicket = t.idTicket " +
                "WHERE p.estreno <= CURDATE() " +
                "  AND p.activo = 1 " +
                "  AND pr.activo = 1 " +
                "  AND ( ? = 'Todos' OR p.genero = ? ) " +
                "  AND ( ? = 'Todos' OR p.origen = ? ) " +
                "  AND t.fechaCompra >= ? " +
                "GROUP BY p.idPelicula " +
                "ORDER BY espectadores DESC";

        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {

            ps.setString(1, genero);
            ps.setString(2, genero);

            ps.setString(3, origen);
            ps.setString(4, origen);

            ps.setDate(5, Date.valueOf(desde));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = new Object[]{
                        rs.getString("nombre"),
                        rs.getInt("espectadores"),
                        rs.getDouble("ingresos")
                };

                lista.add(fila);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
