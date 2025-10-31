
package persistencia;

import conector.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.DetalleTicket;
import modelo.Lugar;
import modelo.Proyeccion;

/**
 *
 * @author Usuario
 */
public class DetalleTicketData {
    private Connection con;
    private TicketCompraData ticketCompraBD = new TicketCompraData();
    
    public DetalleTicketData(){
        this.con = Conexion.getConexion();
    }
    
    public void generarDetalleTicket(DetalleTicket detalleTicket){
        String sql = "INSERT INTO detalle_ticket (cantidad, idProyeccion, idTicket, subtotal) " + "VALUES (?,?,?,?)";
        
        try{
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, detalleTicket.getCantidad());
            ps.setInt(2, detalleTicket.getProyeccion().getIdProyeccion());
            ps.setInt(3, detalleTicket.getTicket().getIdTicket());
            ps.setDouble(4, detalleTicket.getSubtotal());
            
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                int codDetalleGenerado = rs.getInt(1);
                detalleTicket.setCodDetalle(codDetalleGenerado);
            }
            
            ps.close();
            
            if(detalleTicket.getLugares() != null && !detalleTicket.getLugares().isEmpty()){
                String sqlDetalleLugar = "INSERT INTO detalle_lugar(codDetalle, codLugar)" + "VALUES(?, ?)";
                PreparedStatement psLugar = con.prepareStatement(sqlDetalleLugar);
                
                for (Lugar lugar : detalleTicket.getLugares()) {
                psLugar.setInt(1, detalleTicket.getCodDetalle());
                psLugar.setInt(2, lugar.getCodLugar());
                psLugar.addBatch();
                }
                
                psLugar.executeBatch();
                ps.close();
            }
                        
//            JOptionPane.showMessageDialog(null, "Detalle de ticket generado Correctamente");
            System.out.println("Detalle de ticket generado");
                        
        }catch(SQLException e){
//            JOptionPane.showMessageDialog(null, "Error al generar ticket" + e.getMessage());
            System.out.println("Error al generar código de ticket " + e.getMessage());
        }
    }
    
    public List<DetalleTicket> obtenerDetallesPorTicket(int idTicket) {
        List<DetalleTicket> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_ticket WHERE idTicket = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idTicket);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DetalleTicket dt = new DetalleTicket();
                dt.setCodDetalle(rs.getInt("codDetalle"));
                dt.setCantidad(rs.getInt("cantidad"));
                dt.setSubtotal(rs.getDouble("subtotal"));


                ProyeccionData proyeccionData = new ProyeccionData();
                dt.setProyeccion(proyeccionData.buscarProyeccion(rs.getInt("idProyeccion")));


                TicketCompraData ticketData = new TicketCompraData();
                dt.setTicket(ticketData.buscarTicket(idTicket));


                dt.setLugares(obtenerLugaresPorDetalle(dt.getCodDetalle()));

                detalles.add(dt);
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener detalles del ticket: " + e.getMessage());
        }

        return detalles;
    }

    public List<Lugar> obtenerLugaresPorDetalle(int codDetalle) {
        List<Lugar> lugares = new ArrayList<>();
        String sql = "SELECT l.* FROM lugar l JOIN detalle_lugar dl ON l.codLugar = dl.codLugar WHERE dl.codDetalle = ?";

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, codDetalle);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Lugar lugar = new Lugar();
                lugar.setCodLugar(rs.getInt("codLugar"));
                lugar.setFila(rs.getString("fila").charAt(0));
                lugar.setNumero(rs.getInt("numero"));
                lugar.setEstado(rs.getString("estado"));


                lugares.add(lugar);
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error al obtener lugares del detalle: " + e.getMessage());
        }

        return lugares;
    }
    
    public void eliminarDetallePorTicket(int idTicket) {
        String sqlDL = "DELETE FROM detalleLugar WHERE codDetalle IN (SELECT codDetalle FROM detalle_ticket WHERE idTicket = ?)";
        String sqlD = "DELETE FROM detalle_ticket WHERE idTicket = ?";

        try {
            PreparedStatement ps1 = con.prepareStatement(sqlDL);
            ps1.setInt(1, idTicket);
            ps1.executeUpdate();
            ps1.close();

            PreparedStatement ps2 = con.prepareStatement(sqlD);
            ps2.setInt(1, idTicket);
            ps2.executeUpdate();
            ps2.close();

            System.out.println("Detalles del ticket eliminados correctamente.");

        } catch (SQLException e) {
            System.out.println("Error al eliminar detalle de ticket: " + e.getMessage());
        }
    }
    
}
