/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.DetalleTicket;
import modelo.TicketCompra;
import persistencia.PeliculaData;
import persistencia.TicketCompraData;
/**
 *
 * @author tizia
 */
public class Ventas extends javax.swing.JInternalFrame {

    /**
     * Creates new form Ventas
     */
    
    private TicketCompraData ticketCompraData;
    private javax.swing.table.DefaultTableModel modeloTabla;
    
    public Ventas() {
        initComponents();
        inicializarDatos();
        configurarTabla();
        cargarVentas();
        calcularTotales();
    }
    
    private void inicializarDatos() {
        ticketCompraData = new TicketCompraData();
    }
    
    private void configurarTabla() {
        String[] columnas = {"Película", "Total Entradas", "Total Recaudado", "Promedio x Entrada"};
        modeloTabla = new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableVentas.setModel(modeloTabla);

        tableVentas.getColumnModel().getColumn(0).setPreferredWidth(250); 
        tableVentas.getColumnModel().getColumn(1).setPreferredWidth(120); 
        tableVentas.getColumnModel().getColumn(2).setPreferredWidth(150); 
        tableVentas.getColumnModel().getColumn(3).setPreferredWidth(150); 
    }

    private void cargarVentas() {
        modeloTabla.setRowCount(0);

        List<TicketCompra> tickets = ticketCompraData.listarTickets();

        java.util.Map<String, double[]> ventasPorPelicula = new java.util.HashMap<>();

        for (TicketCompra ticket : tickets) {
            if (!"Activo".equalsIgnoreCase(ticket.getEstadoTicket())) {
                continue;
            }

            List<DetalleTicket> detalles = ticketCompraData.listarDetallesPorTicket(ticket.getIdTicket());

            for (DetalleTicket detalle : detalles) {
                if (detalle.getProyeccion() != null && 
                    detalle.getProyeccion().getPelicula() != null) {

                    String nombrePelicula = detalle.getProyeccion().getPelicula().getTitulo();
                    int cantidad = detalle.getCantidad();
                    double monto = detalle.getSubtotal();

                    // Si ya existe, acumular
                    if (ventasPorPelicula.containsKey(nombrePelicula)) {
                        double[] datos = ventasPorPelicula.get(nombrePelicula);
                        datos[0] += cantidad;  
                        datos[1] += monto;     
                    } else {
                        ventasPorPelicula.put(nombrePelicula, new double[]{cantidad, monto});
                    }
                }
            }
        }

        // Agregar filas a la tabla
        for (java.util.Map.Entry<String, double[]> entry : ventasPorPelicula.entrySet()) {
            String pelicula = entry.getKey();
            double[] datos = entry.getValue();
            int totalEntradas = (int) datos[0];
            double totalRecaudado = datos[1];
            double promedio = totalEntradas > 0 ? totalRecaudado / totalEntradas : 0;

            Object[] fila = {
                pelicula,
                totalEntradas,
                String.format("$%.2f", totalRecaudado),
                String.format("$%.2f", promedio)
            };

            modeloTabla.addRow(fila);
        }

        calcularTotales();
    }

    private void buscarVentas() {
        modeloTabla.setRowCount(0);

        java.util.Date fechaInicio = dateChooserInicio.getDate();
        java.util.Date fechaFin = dateChooserFin.getDate();

        List<TicketCompra> tickets = ticketCompraData.listarTickets();

        java.util.Map<String, double[]> ventasPorPelicula = new java.util.HashMap<>();

        for (TicketCompra ticket : tickets) {
            if (!"Activo".equalsIgnoreCase(ticket.getEstadoTicket())) {
                continue;
            }

            // Filtrar por fecha inicio
            if (fechaInicio != null) {
                java.time.LocalDateTime inicioLD = fechaInicio.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(0).withMinute(0).withSecond(0);

                if (ticket.getFechaCompra().isBefore(inicioLD)) {
                    continue;
                }
            }

            // Filtrar por fecha fin
            if (fechaFin != null) {
                java.time.LocalDateTime finLD = fechaFin.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(23).withMinute(59).withSecond(59);

                if (ticket.getFechaCompra().isAfter(finLD)) {
                    continue;
                }
            }

            List<DetalleTicket> detalles = ticketCompraData.listarDetallesPorTicket(ticket.getIdTicket());

            for (DetalleTicket detalle : detalles) {
                if (detalle.getProyeccion() != null && 
                    detalle.getProyeccion().getPelicula() != null) {

                    String nombrePelicula = detalle.getProyeccion().getPelicula().getTitulo();
                    int cantidad = detalle.getCantidad();
                    double monto = detalle.getSubtotal();

                    if (ventasPorPelicula.containsKey(nombrePelicula)) {
                        double[] datos = ventasPorPelicula.get(nombrePelicula);
                        datos[0] += cantidad;
                        datos[1] += monto;
                    } else {
                        ventasPorPelicula.put(nombrePelicula, new double[]{cantidad, monto});
                    }
                }
            }
        }

        // Agregar filas a la tabla
        for (java.util.Map.Entry<String, double[]> entry : ventasPorPelicula.entrySet()) {
            String pelicula = entry.getKey();
            double[] datos = entry.getValue();
            int totalEntradas = (int) datos[0];
            double totalRecaudado = datos[1];
            double promedio = totalEntradas > 0 ? totalRecaudado / totalEntradas : 0;

            Object[] fila = {
                pelicula,
                totalEntradas,
                String.format("$%.2f", totalRecaudado),
                String.format("$%.2f", promedio)
            };

            modeloTabla.addRow(fila);
        }

        calcularTotales();
    }

    private void limpiarFiltros() {
        dateChooserInicio.setDate(null);
        dateChooserFin.setDate(null);
        cargarVentas();
    }

    private void calcularTotales() {
        int totalTickets = 0;
        double totalDinero = 0.0;

        // Sumar todas las filas de la tabla
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            totalTickets += (int) modeloTabla.getValueAt(i, 1); // Columna Total Entradas

            String montoStr = modeloTabla.getValueAt(i, 2).toString(); // Columna Total Recaudado
            montoStr = montoStr.replace("$", "").replace(",", "").trim();
            totalDinero += Double.parseDouble(montoStr);
        }

        labelInfoDinero.setText(String.format("$%.2f", totalDinero));
        labelInfoTickets.setText(String.valueOf(totalTickets));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitulo = new javax.swing.JPanel();
        titleVentas = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        labelInicio = new javax.swing.JLabel();
        labelFin = new javax.swing.JLabel();
        dateChooserInicio = new com.toedter.calendar.JDateChooser();
        dateChooserFin = new com.toedter.calendar.JDateChooser();
        buttonBuscar = new javax.swing.JButton();
        buttonLimpiar = new javax.swing.JButton();
        scrollPane1 = new javax.swing.JScrollPane();
        tableVentas = new javax.swing.JTable();
        panelTotales = new javax.swing.JPanel();
        labelDinero = new javax.swing.JLabel();
        labelTotales = new javax.swing.JLabel();
        labelInfoDinero = new javax.swing.JLabel();
        labelTickets = new javax.swing.JLabel();
        labelInfoTickets = new javax.swing.JLabel();

        panelTitulo.setBackground(new java.awt.Color(51, 90, 144));
        panelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        titleVentas.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleVentas.setText("Reporte de Ventas");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleVentas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleVentas)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltros.setBackground(new java.awt.Color(240, 240, 240));
        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros de búsqueda"));

        labelInicio.setText("Fecha inicio");

        labelFin.setText("Fecha fin");

        buttonBuscar.setBackground(new java.awt.Color(76, 175, 80));
        buttonBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonBuscar.setForeground(new java.awt.Color(255, 255, 255));
        buttonBuscar.setText("Buscar");
        buttonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBuscarActionPerformed(evt);
            }
        });

        buttonLimpiar.setBackground(new java.awt.Color(158, 158, 158));
        buttonLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        buttonLimpiar.setText("Limpiar");
        buttonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateChooserInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelFin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateChooserFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98)
                .addComponent(buttonBuscar)
                .addGap(18, 18, 18)
                .addComponent(buttonLimpiar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(labelInicio)
                        .addComponent(labelFin)
                        .addComponent(buttonBuscar)
                        .addComponent(buttonLimpiar))
                    .addComponent(dateChooserFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateChooserInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        tableVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Numero", "Pelicula", "Sala", "Asiento", "Monto"
            }
        ));
        scrollPane1.setViewportView(tableVentas);

        labelDinero.setText("Dinero recaudado:");

        labelTotales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelTotales.setText("Totales");

        labelInfoDinero.setText("---");

        labelTickets.setText("Tickets vendidos:");

        labelInfoTickets.setText("---");

        javax.swing.GroupLayout panelTotalesLayout = new javax.swing.GroupLayout(panelTotales);
        panelTotales.setLayout(panelTotalesLayout);
        panelTotalesLayout.setHorizontalGroup(
            panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalesLayout.createSequentialGroup()
                .addGroup(panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTotalesLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelDinero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelInfoDinero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelTotalesLayout.createSequentialGroup()
                        .addGroup(panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelTotalesLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(labelTotales))
                            .addGroup(panelTotalesLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(labelTickets)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelInfoTickets, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelTotalesLayout.setVerticalGroup(
            panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalesLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(labelTotales)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelDinero)
                    .addComponent(labelInfoDinero))
                .addGap(18, 18, 18)
                .addGroup(panelTotalesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfoTickets)
                    .addComponent(labelTickets))
                .addContainerGap(215, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTotales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(panelTotales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBuscarActionPerformed
        buscarVentas();
    }//GEN-LAST:event_buttonBuscarActionPerformed

    private void buttonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLimpiarActionPerformed
        limpiarFiltros();
    }//GEN-LAST:event_buttonLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBuscar;
    private javax.swing.JButton buttonLimpiar;
    private com.toedter.calendar.JDateChooser dateChooserFin;
    private com.toedter.calendar.JDateChooser dateChooserInicio;
    private javax.swing.JLabel labelDinero;
    private javax.swing.JLabel labelFin;
    private javax.swing.JLabel labelInfoDinero;
    private javax.swing.JLabel labelInfoTickets;
    private javax.swing.JLabel labelInicio;
    private javax.swing.JLabel labelTickets;
    private javax.swing.JLabel labelTotales;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JPanel panelTotales;
    private javax.swing.JScrollPane scrollPane1;
    private javax.swing.JTable tableVentas;
    private javax.swing.JLabel titleVentas;
    // End of variables declaration//GEN-END:variables
}
