package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class TicketCompra {
    
    private int idTicket;
    private Comprador comprador;
    private LocalDateTime fechaCompra;
    private LocalDateTime fechaFuncion;
    private double monto;
    private String tipoCompra;
    private String codigoVenta;
    private String estadoTicket;
    private List<DetalleTicket> detalles;
    private String medioPago;

    public TicketCompra() {
        this.fechaCompra = LocalDateTime.now();
        this.estadoTicket = "Pendiente";
        this.detalles = new ArrayList<>();
    }

    public TicketCompra(int idTicket, Comprador comprador, LocalDateTime fechaCompra, LocalDateTime fechaFuncion, double monto, String tipoCompra, String codigoVenta, String estadoTicket, List<DetalleTicket> detalles, String medioPago) {
        this.idTicket = idTicket;
        this.comprador = comprador;
        this.fechaCompra = fechaCompra;
        this.fechaFuncion = fechaFuncion;
        this.monto = monto;
        this.tipoCompra = tipoCompra;
        this.codigoVenta = codigoVenta;
        this.estadoTicket = estadoTicket;
        this.detalles = detalles;
        this.medioPago = medioPago;
    }

    public TicketCompra(Comprador comprador, LocalDateTime fechaCompra, LocalDateTime fechaFuncion, double monto, String tipoCompra, String codigoVenta, String estadoTicket, List<DetalleTicket> detalles, String medioPago) {
        this.comprador = comprador;
        this.fechaCompra = fechaCompra;
        this.fechaFuncion = fechaFuncion;
        this.monto = monto;
        this.tipoCompra = tipoCompra;
        this.codigoVenta = codigoVenta;
        this.estadoTicket = estadoTicket;
        this.detalles = detalles;
        this.medioPago = medioPago;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public void setComprador(Comprador comprador) {
        this.comprador = comprador;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public LocalDateTime getFechaFuncion() {
        return fechaFuncion;
    }

    public void setFechaFuncion(LocalDateTime fechaFuncion) {
        this.fechaFuncion = fechaFuncion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getTipoCompra() {
        return tipoCompra;
    }

    public void setTipoCompra(String tipoCompra) {
        this.tipoCompra = tipoCompra;
    }

    public String getCodigoVenta() {
        return codigoVenta;
    }

    public void setCodigoVenta(String codigoVenta) {
        this.codigoVenta = codigoVenta;
    }

    public String getEstadoTicket() {
        return estadoTicket;
    }

    public void setEstadoTicket(String estadoTicket) {
        this.estadoTicket = estadoTicket;
    }

    public List<DetalleTicket> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleTicket> detalles) {
        this.detalles = detalles;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    @Override
    public String toString() {
        return "TicketCompra{" + "idTicket=" + idTicket + ", comprador=" + comprador + ", fechaCompra=" + fechaCompra + ", fechaFuncion=" + fechaFuncion + ", monto=" + monto + ", tipoCompra=" + tipoCompra + ", codigoVenta=" + codigoVenta + ", estadoTicket=" + estadoTicket + ", detalles=" + detalles + ", medioPago=" + medioPago + '}';
    }
    
}
