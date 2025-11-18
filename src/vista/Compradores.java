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
        String[] columnas = {"ID Ticket", "DNI", "Nombre Comprador", "Película", "Sala", "Fecha Función", "Monto", "Estado"};
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
            // Obtener película y sala del primer detalle del ticket
            String pelicula = "N/A";
            String sala = "N/A";

            if (ticket.getDetalles() != null && !ticket.getDetalles().isEmpty()
                && ticket.getDetalles().get(0).getProyeccion() != null) {
                if (ticket.getDetalles().get(0).getProyeccion().getPelicula() != null) {
                    pelicula = ticket.getDetalles().get(0).getProyeccion().getPelicula().getTitulo();
                }
                if (ticket.getDetalles().get(0).getProyeccion().getSala() != null) {
                    sala = String.valueOf(ticket.getDetalles().get(0).getProyeccion().getSala().getNroSala());
                }
            }

            Object[] fila = {
                ticket.getIdTicket(),
                ticket.getComprador() != null ? ticket.getComprador().getDni() : "N/A",
                ticket.getComprador() != null ? ticket.getComprador().getNombre() : "N/A",
                pelicula,
                sala,
                ticket.getFechaFuncion() != null ? ticket.getFechaFuncion().format(formatter) : "N/A",
                String.format("$%.2f", ticket.getMonto()),
                ticket.getEstadoTicket()
            };
            modeloTabla.addRow(fila);
        }

        labelResultados.setText("Mostrando " + tickets.size() + " compra(s)");
    }

    private void buscarPorFechas() {
        modeloTabla.setRowCount(0);
        List<TicketCompra> tickets = ticketData.listarTickets();

        // Obtener el texto del filtro de comprador
        String textoComprador = txtComprador.getText().trim().toLowerCase();

        // Verificar si hay filtros aplicados
        boolean hayFiltroFechas = dateInicio.getDate() != null && dateFin.getDate() != null;
        boolean hayFiltroComprador = !textoComprador.isEmpty();

        if (!hayFiltroFechas && !hayFiltroComprador) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese al menos un filtro (DNI/Nombre o Fechas)",
                "Filtros requeridos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;

        if (hayFiltroFechas) {
            // Convertir Date a LocalDate
            fechaInicio = dateInicio.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            fechaFin = dateFin.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            // Validar que fecha inicio no sea mayor a fecha fin
            if (fechaInicio.isAfter(fechaFin)) {
                JOptionPane.showMessageDialog(this,
                    "La fecha de inicio no puede ser mayor a la fecha de fin",
                    "Error en fechas",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Variables finales para uso en lambda
        final LocalDate fechaInicioFinal = fechaInicio;
        final LocalDate fechaFinFinal = fechaFin;
        final boolean aplicarFiltroFechas = hayFiltroFechas;
        final boolean aplicarFiltroComprador = hayFiltroComprador;

        // Filtrar tickets por rango de fechas y/o comprador
        List<TicketCompra> ticketsFiltrados = tickets.stream()
            .filter(t -> {
                boolean cumpleFechas = true;
                boolean cumpleComprador = true;

                // Filtro por fechas
                if (aplicarFiltroFechas) {
                    if (t.getFechaFuncion() == null) {
                        cumpleFechas = false;
                    } else {
                        LocalDate fechaTicket = t.getFechaFuncion().toLocalDate();
                        cumpleFechas = !fechaTicket.isBefore(fechaInicioFinal) && !fechaTicket.isAfter(fechaFinFinal);
                    }
                }

                // Filtro por comprador (DNI o nombre)
                if (aplicarFiltroComprador) {
                    if (t.getComprador() == null) {
                        cumpleComprador = false;
                    } else {
                        String dni = t.getComprador().getDni() != null ? t.getComprador().getDni().toLowerCase() : "";
                        String nombre = t.getComprador().getNombre() != null ? t.getComprador().getNombre().toLowerCase() : "";
                        cumpleComprador = dni.contains(textoComprador) || nombre.contains(textoComprador);
                    }
                }

                return cumpleFechas && cumpleComprador;
            })
            .collect(Collectors.toList());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TicketCompra ticket : ticketsFiltrados) {
            // Obtener película y sala del primer detalle del ticket
            String pelicula = "N/A";
            String sala = "N/A";

            if (ticket.getDetalles() != null && !ticket.getDetalles().isEmpty()
                && ticket.getDetalles().get(0).getProyeccion() != null) {
                if (ticket.getDetalles().get(0).getProyeccion().getPelicula() != null) {
                    pelicula = ticket.getDetalles().get(0).getProyeccion().getPelicula().getTitulo();
                }
                if (ticket.getDetalles().get(0).getProyeccion().getSala() != null) {
                    sala = String.valueOf(ticket.getDetalles().get(0).getProyeccion().getSala().getNroSala());
                }
            }

            Object[] fila = {
                ticket.getIdTicket(),
                ticket.getComprador() != null ? ticket.getComprador().getDni() : "N/A",
                ticket.getComprador() != null ? ticket.getComprador().getNombre() : "N/A",
                pelicula,
                sala,
                ticket.getFechaFuncion() != null ? ticket.getFechaFuncion().format(formatter) : "N/A",
                String.format("$%.2f", ticket.getMonto()),
                ticket.getEstadoTicket()
            };
            modeloTabla.addRow(fila);
        }

        // Construir mensaje de resultados
        StringBuilder mensaje = new StringBuilder("Mostrando " + ticketsFiltrados.size() + " compra(s)");
        if (aplicarFiltroComprador) {
            mensaje.append(" para '").append(textoComprador).append("'");
        }
        if (aplicarFiltroFechas) {
            mensaje.append(" entre ")
                   .append(fechaInicioFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                   .append(" y ")
                   .append(fechaFinFinal.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        labelResultados.setText(mensaje.toString());
    }

    private void limpiarFiltros() {
        txtComprador.setText("");
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
        labelComprador = new javax.swing.JLabel();
        txtComprador = new javax.swing.JTextField();
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

        labelComprador.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelComprador.setText("DNI/Nombre:");

        txtComprador.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

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
                .addComponent(labelComprador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtComprador, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelFechaInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelFechaFin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateFin, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(labelComprador, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtComprador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                "ID Ticket", "DNI", "Nombre Comprador", "Película", "Sala", "Fecha Función", "Monto", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
    private javax.swing.JLabel labelComprador;
    private javax.swing.JLabel labelFechaFin;
    private javax.swing.JLabel labelFechaInicio;
    private javax.swing.JLabel labelResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JTable tablaCompradores;
    private javax.swing.JLabel titleCompradores;
    private javax.swing.JTextField txtComprador;
    // End of variables declaration//GEN-END:variables
}
