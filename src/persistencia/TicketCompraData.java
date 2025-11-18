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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Comprador;
import modelo.DetalleTicket;
import modelo.Lugar;
import modelo.TicketCompra;

/**
 *
 * @author Usuario
 */
public class TicketCompraData {
    private Connection con;
    private DetalleTicketData detalleTicketData;
    private CompradorData compradorData;
    
    public TicketCompraData() {
        this.con = Conexion.getConexion();
        this.detalleTicketData = new DetalleTicketData();
        this.compradorData = new CompradorData();
    }
    
    public void guardarTicket(TicketCompra ticket) {
        String sql = "INSERT INTO ticket_compra (dniComprador, fechaCompra, fechaFuncion, monto, tipoCompra, medioPago, codigoVenta, estadoTicket) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ticket.getComprador().getDni());
            ps.setTimestamp(2, Timestamp.valueOf(ticket.getFechaCompra()));
            ps.setTimestamp(3, Timestamp.valueOf(ticket.getFechaFuncion()));
            ps.setDouble(4, ticket.getMonto());
            ps.setString(5, ticket.getTipoCompra());
            ps.setString(6, ticket.getMedioPago());
            ps.setString(7, ticket.getCodigoVenta());
            ps.setString(8, ticket.getEstadoTicket());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                ticket.setIdTicket(rs.getInt(1));
                System.out.println("Ticket guardado con ID: " + ticket.getIdTicket());
            }


            for (DetalleTicket det : ticket.getDetalles()) {
                det.setTicket(ticket);
                detalleTicketData.generarDetalleTicket(det);
            }

        } catch (SQLException e) {
            System.out.println("Error al guardar ticket: " + e.getMessage());
        }
    }
    
    public void anularTicket(int idTicket) {
        String sql = "UPDATE ticket_compra SET estadoTicket = 'Anulado' WHERE idTicket = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTicket);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Ticket anulado correctamente");
            } else {
                System.out.println("No se encontró el ticket a anular");
            }

        } catch (SQLException e) {
            System.out.println("Error al anular ticket: " + e.getMessage());
        }
    }
    
    public TicketCompra buscarTicket(int idTicket) {
        String sql = "SELECT * FROM ticket_compra WHERE idTicket = ?";
        TicketCompra ticket = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idTicket);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ticket = new TicketCompra();
                ticket.setIdTicket(rs.getInt("idTicket"));

                String dni = rs.getString("dniComprador");
                Comprador comprador = compradorData.buscarComprador(dni);
                ticket.setComprador(comprador);

                Timestamp fCompra = rs.getTimestamp("fechaCompra");
                Timestamp fFuncion = rs.getTimestamp("fechaFuncion");
                if (fCompra != null) ticket.setFechaCompra(fCompra.toLocalDateTime());
                if (fFuncion != null) ticket.setFechaFuncion(fFuncion.toLocalDateTime());

                ticket.setMonto(rs.getDouble("monto"));
                ticket.setTipoCompra(rs.getString("tipoCompra"));
                ticket.setMedioPago(rs.getString("medioPago"));
                ticket.setCodigoVenta(rs.getString("codigoVenta"));
                ticket.setEstadoTicket(rs.getString("estadoTicket"));

                // Cargar los detalles
                List<DetalleTicket> detalles = detalleTicketData.obtenerDetallesPorTicket(idTicket);
                // Establecer la referencia del ticket en cada detalle
                for (DetalleTicket detalle : detalles) {
                    detalle.setTicket(ticket);
                }
                ticket.setDetalles(detalles);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar ticket: " + e.getMessage());
        }

        return ticket;
    }
    
    public List<TicketCompra> listarTickets() {
        List<TicketCompra> tickets = new ArrayList<>();
        String sql = "SELECT * FROM ticket_compra";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TicketCompra ticket = new TicketCompra();
                ticket.setIdTicket(rs.getInt("idTicket"));

                String dni = rs.getString("dniComprador");
                Comprador comprador = compradorData.buscarComprador(dni);
                ticket.setComprador(comprador);

                ticket.setFechaCompra(rs.getTimestamp("fechaCompra").toLocalDateTime());
                ticket.setFechaFuncion(rs.getTimestamp("fechaFuncion").toLocalDateTime());
                ticket.setMonto(rs.getDouble("monto"));
                ticket.setTipoCompra(rs.getString("tipoCompra"));
                ticket.setMedioPago(rs.getString("medioPago"));
                ticket.setCodigoVenta(rs.getString("codigoVenta"));
                ticket.setEstadoTicket(rs.getString("estadoTicket"));

                // Cargar los detalles del ticket
                List<DetalleTicket> detalles = detalleTicketData.obtenerDetallesPorTicket(ticket.getIdTicket());
                for (DetalleTicket detalle : detalles) {
                    detalle.setTicket(ticket);
                }
                ticket.setDetalles(detalles);

                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar tickets: " + e.getMessage());
        }

        return tickets;
    }
    
    public List<DetalleTicket> listarDetallesPorTicket(int idTicket) {
        return detalleTicketData.obtenerDetallesPorTicket(idTicket);
    }

    // Eliminar ticket completo (con detalles y liberación de asientos)
    public boolean eliminarTicket(int idTicket) {
        try {
            // 1. Obtener todos los detalles del ticket para liberar los asientos
            List<DetalleTicket> detalles = detalleTicketData.obtenerDetallesPorTicket(idTicket);

            // 2. Liberar todos los asientos (cambiar estado a "libre")
            LugarData lugarData = new LugarData();
            for (DetalleTicket detalle : detalles) {
                for (Lugar lugar : detalle.getLugares()) {
                    lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "libre");
                }
            }

            // 3. Eliminar los registros de detalle_lugar y detalle_ticket
            detalleTicketData.eliminarDetallePorTicket(idTicket);

            // 4. Eliminar el ticket principal
            String sql = "DELETE FROM ticket_compra WHERE idTicket = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idTicket);
            int resultado = ps.executeUpdate();
            ps.close();

            if (resultado > 0) {
                System.out.println("Ticket eliminado exitosamente con ID: " + idTicket);
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar ticket: " + e.getMessage());
        }

        return false;
    }

}
