package vista;

import persistencia.TicketCompraData;
import modelo.TicketCompra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Juan Manuel, Nerina, Nahuel, Alaina, Juan Ignacio, Tiziana
 */
public class Compradores extends javax.swing.JInternalFrame {

    private TicketCompraData ticketData;
    private DefaultTableModel modeloTabla;

    public Compradores() {
        initComponents();
        ticketData = new TicketCompraData();
        configurarTabla();
        configurarEventos();
        cargarTabla();
    }

    private void configurarTabla() {
        String[] columnas = {"ID Ticket", "DNI", "Nombre Comprador", "Fecha Compra", "Fecha Función", "Monto", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCompradores.setModel(modeloTabla);
    }

    private void configurarEventos() {
        // Evento del botón buscar ya está conectado en el .form
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<TicketCompra> tickets = ticketData.listarTickets();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TicketCompra ticket : tickets) {
            Object[] fila = {
                ticket.getIdTicket(),
                ticket.getComprador() != null ? ticket.getComprador().getDni() : "N/A",
                ticket.getComprador() != null ? ticket.getComprador().getNombre() : "N/A",
                ticket.getFechaCompra() != null ? ticket.getFechaCompra().format(formatter) : "N/A",
                ticket.getFechaFuncion() != null ? ticket.getFechaFuncion().format(formatter) : "N/A",
                String.format("$%.2f", ticket.getMonto()),
                ticket.getEstadoTicket()
            };
            modeloTabla.addRow(fila);
        }

        labelResultados.setText("Mostrando " + tickets.size() + " compra(s)");
    }

    private void buscarPorFechas() {
        if (dateInicio.getDate() == null || dateFin.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione ambas fechas (Inicio y Fin)",
                "Fechas requeridas",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convertir Date a LocalDate
        LocalDate fechaInicio = dateInicio.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate fechaFin = dateFin.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        // Validar que fecha inicio no sea mayor a fecha fin
        if (fechaInicio.isAfter(fechaFin)) {
            JOptionPane.showMessageDialog(this,
                "La fecha de inicio no puede ser mayor a la fecha de fin",
                "Error en fechas",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloTabla.setRowCount(0);
        List<TicketCompra> tickets = ticketData.listarTickets();

        // Filtrar tickets por rango de fechas (basado en fechaFuncion)
        List<TicketCompra> ticketsFiltrados = tickets.stream()
            .filter(t -> {
                if (t.getFechaFuncion() == null) return false;
                LocalDate fechaTicket = t.getFechaFuncion().toLocalDate();
                return !fechaTicket.isBefore(fechaInicio) && !fechaTicket.isAfter(fechaFin);
            })
            .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TicketCompra ticket : ticketsFiltrados) {
            Object[] fila = {
                ticket.getIdTicket(),
                ticket.getComprador() != null ? ticket.getComprador().getDni() : "N/A",
                ticket.getComprador() != null ? ticket.getComprador().getNombre() : "N/A",
                ticket.getFechaCompra() != null ? ticket.getFechaCompra().format(formatter) : "N/A",
                ticket.getFechaFuncion() != null ? ticket.getFechaFuncion().format(formatter) : "N/A",
                String.format("$%.2f", ticket.getMonto()),
                ticket.getEstadoTicket()
            };
            modeloTabla.addRow(fila);
        }

        labelResultados.setText("Mostrando " + ticketsFiltrados.size() + " compra(s) entre " +
            fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " y " +
            fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void limpiarFiltros() {
        dateInicio.setDate(null);
        dateFin.setDate(null);
        cargarTabla();
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
        titleCompradores = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        labelFechaInicio = new javax.swing.JLabel();
        dateInicio = new com.toedter.calendar.JDateChooser();
        labelFechaFin = new javax.swing.JLabel();
        dateFin = new com.toedter.calendar.JDateChooser();
        buttonBuscar = new javax.swing.JButton();
        buttonLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCompradores = new javax.swing.JTable();
        labelResultados = new javax.swing.JLabel();

        panelTitulo.setBackground(new java.awt.Color(51, 90, 144));
        panelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        titleCompradores.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleCompradores.setForeground(new java.awt.Color(255, 255, 255));
        titleCompradores.setText("Reporte de Compradores");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleCompradores)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleCompradores)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFiltros.setBackground(new java.awt.Color(240, 240, 240));
        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtros de Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        labelFechaInicio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelFechaInicio.setText("Fecha Inicio:");

        dateInicio.setDateFormatString("dd/MM/yyyy");

        labelFechaFin.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelFechaFin.setText("Fecha Fin:");

        dateFin.setDateFormatString("dd/MM/yyyy");

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
                .addComponent(labelFechaInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelFechaFin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateFin, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelFechaInicio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelFechaFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dateFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonBuscar)
                            .addComponent(buttonLimpiar))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tablaCompradores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Ticket", "DNI", "Nombre Comprador", "Fecha Compra", "Fecha Función", "Monto", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaCompradores);

        labelResultados.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        labelResultados.setText("Mostrando 0 compra(s)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelResultados)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelResultados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBuscarActionPerformed
        buscarPorFechas();
    }//GEN-LAST:event_buttonBuscarActionPerformed

    private void buttonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLimpiarActionPerformed
        limpiarFiltros();
    }//GEN-LAST:event_buttonLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBuscar;
    private javax.swing.JButton buttonLimpiar;
    private com.toedter.calendar.JDateChooser dateFin;
    private com.toedter.calendar.JDateChooser dateInicio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelFechaFin;
    private javax.swing.JLabel labelFechaInicio;
    private javax.swing.JLabel labelResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JTable tablaCompradores;
    private javax.swing.JLabel titleCompradores;
    // End of variables declaration//GEN-END:variables
}
