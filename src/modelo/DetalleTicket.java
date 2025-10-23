package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class DetalleTicket {
    
    private int codDetalle;
    private TicketCompra ticket;
    private Proyeccion proyeccion;
    private int cantidad;
    private double subtotal;
    private List<Lugar> lugares;
    
    public DetalleTicket() {
        this.lugares = new ArrayList<>();
    }

    public DetalleTicket(int codDetalle, TicketCompra ticket, Proyeccion proyeccion, int cantidad, double subtotal, List<Lugar> lugares) {
        this.codDetalle = codDetalle;
        this.ticket = ticket;
        this.proyeccion = proyeccion;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.lugares = lugares;
    }

    public int getCodDetalle() {
        return codDetalle;
    }

    public void setCodDetalle(int codDetalle) {
        this.codDetalle = codDetalle;
    }

    public TicketCompra getTicket() {
        return ticket;
    }

    public void setTicket(TicketCompra ticket) {
        this.ticket = ticket;
    }

    public Proyeccion getProyeccion() {
        return proyeccion;
    }

    public void setProyeccion(Proyeccion proyeccion) {
        this.proyeccion = proyeccion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public List<Lugar> getLugares() {
        return lugares;
    }

    public void setLugares(List<Lugar> lugares) {
        this.lugares = lugares;
    }

    @Override
    public String toString() {
        return "DetalleTicket{" + "codDetalle=" + codDetalle + ", ticket=" + ticket + ", proyeccion=" + proyeccion + ", cantidad=" + cantidad + ", subtotal=" + subtotal + ", lugares=" + lugares + '}';
    }
    
    
}
