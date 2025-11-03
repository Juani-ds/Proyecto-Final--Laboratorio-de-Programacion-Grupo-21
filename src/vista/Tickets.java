/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;

import persistencia.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Neri
 */
public class Tickets extends javax.swing.JInternalFrame {

    /**
     * Creates new form Tickets
     */
    
    private PeliculaData peliculaData;
    private SalaData salaData;
    private ProyeccionData proyeccionData;
    private LugarData lugarData;
    private CompradorData compradorData;
    private TicketCompraData ticketData;
    private DefaultTableModel modeloTabla;
    private modelo.Pelicula peliculaSeleccionada;
    private modelo.Sala salaSeleccionada;
    private modelo.Proyeccion proyeccionSeleccionada;
    private modelo.Lugar lugarSeleccionado;
    private modelo.Comprador compradorSeleccionado;
    
    public Tickets() {
        initComponents();
        
        peliculaData = new PeliculaData();
        salaData = new SalaData();
        proyeccionData = new ProyeccionData();
        lugarData = new LugarData();
        compradorData = new CompradorData();
        ticketData = new TicketCompraData();
        modeloTabla = (DefaultTableModel) ID.getModel();
        peliculaSeleccionada = null;
        salaSeleccionada = null;
        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
        compradorSeleccionado = null;
        configurarTabla();
        configurarSpinner();
        cargarPeliculas();
        cargarCompradores();
        cargarFormasPago();
        cargarTablaTickets();
    }
    
    private void configurarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        modeloTabla.addColumn("Numero");
        modeloTabla.addColumn("Pelicula");
        modeloTabla.addColumn("Sala");
        modeloTabla.addColumn("Asiento");
        modeloTabla.addColumn("Precio");
    }
    
    private void configurarSpinner() {
        SpinnerCant.setValue(1);
        ((javax.swing.SpinnerNumberModel) SpinnerCant.getModel()).setMinimum(1);
        ((javax.swing.SpinnerNumberModel) SpinnerCant.getModel()).setMaximum(10);
    }
    
    private void cargarPeliculas() {
        comboPeli.removeAllItems();
        comboPeli.addItem("Seleccione película");

        List<modelo.Pelicula> peliculas = peliculaData.listarPeliculas();
        for (modelo.Pelicula peli : peliculas) {
            if (peli.isActivo()) {
                comboPeli.addItem(peli.getTitulo());
            }
        }
    }
    
    private void cargarSalasPorPelicula() {
        comboSala.removeAllItems();
        comboSala.addItem("Seleccione sala");

        if (peliculaSeleccionada == null) {
            return;
        }

        List<modelo.Proyeccion> todasProyecciones = proyeccionData.listarProyecciones();
        java.util.Set<Integer> salasIds = new java.util.HashSet<>();

        for (modelo.Proyeccion proy : todasProyecciones) {
            if (proy.getPelicula() != null && proy.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula()) {
                salasIds.add(proy.getSala().getNroSala());
            }
        }

        for (Integer salaId : salasIds) {
            modelo.Sala sala = salaData.buscarSala(salaId);
            if (sala != null && sala.isActivo()) {
                String nombreSala = "Sala " + sala.getNroSala();
                comboSala.addItem(nombreSala);
            }
        }
    }
    
    private void cargarFuncionesPorSala() {
        FuncionCombo.removeAllItems();
        FuncionCombo.addItem("Seleccione función");

        if (peliculaSeleccionada == null || salaSeleccionada == null) {
            return;
        }

        List<modelo.Proyeccion> todasProyecciones = proyeccionData.listarProyecciones();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (modelo.Proyeccion proy : todasProyecciones) {
            if (proy.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula() &&
                proy.getSala().getNroSala() == salaSeleccionada.getNroSala()) {
                String funcion = proy.getHoraInicio().format(formatter);
                FuncionCombo.addItem(funcion);
            }
        }
    }
    
    private void cargarFilas() {
        FilaCombo.removeAllItems();
        FilaCombo.addItem("Seleccione fila");

        if (proyeccionSeleccionada == null) {
            System.out.println("DEBUG: No se pueden cargar filas - proyección null");
            return;
        }

        System.out.println("DEBUG: Cargando filas para proyección ID: " + proyeccionSeleccionada.getIdProyeccion());
        List<modelo.Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
        System.out.println("DEBUG: Total lugares encontrados: " + lugares.size());

        java.util.Set<String> filasSet = new java.util.TreeSet<>();

        for (modelo.Lugar lugar : lugares) {
            filasSet.add(String.valueOf(lugar.getFila()));
        }

        System.out.println("DEBUG: Filas únicas encontradas: " + filasSet.size());
        for (String fila : filasSet) {
            System.out.println("DEBUG: Agregando fila: " + fila);
            FilaCombo.addItem(fila);
        }
    }
    
    private void cargarAsientosPorFila() {
        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("Seleccione asiento");

        if (proyeccionSeleccionada == null || FilaCombo.getSelectedIndex() <= 0) {
            System.out.println("DEBUG: No se pueden cargar asientos - proyección: " + (proyeccionSeleccionada != null) + ", fila seleccionada: " + (FilaCombo.getSelectedIndex() > 0));
            return;
        }

        String filaSeleccionadaStr = FilaCombo.getSelectedItem().toString();
        char filaSeleccionadaChar = filaSeleccionadaStr.charAt(0);
        System.out.println("DEBUG: Cargando asientos para fila: " + filaSeleccionadaChar);

        List<modelo.Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
        System.out.println("DEBUG: Total lugares: " + lugares.size());

        int asientosEncontrados = 0;
        for (modelo.Lugar lugar : lugares) {
            if (lugar.getFila() == filaSeleccionadaChar) {
                String estado = lugar.getEstado().equalsIgnoreCase("libre") ? "✓" : "✗";
                String asiento = estado + " Asiento " + lugar.getNumero();
                System.out.println("DEBUG: Agregando asiento: " + asiento);
                ComboAsiento.addItem(asiento);
                asientosEncontrados++;
            }
        }
        System.out.println("DEBUG: Total asientos agregados: " + asientosEncontrados);
    }
    
    private void cargarCompradores() {
        ComboComprador.removeAllItems();
        ComboComprador.addItem("Seleccione comprador");
        
        List<modelo.Comprador> compradores = compradorData.listarCompradores();
        for (modelo.Comprador comp : compradores) {
            if (comp.isActivo()) {
                ComboComprador.addItem(comp.getDni() + " - " + comp.getNombre());
            }
        }
    }
    
    private void cargarFormasPago() {
        ComboFormaPago.removeAllItems();
        ComboFormaPago.addItem("Seleccione forma de pago");
        ComboFormaPago.addItem("Débito");
        ComboFormaPago.addItem("Crédito");
        ComboFormaPago.addItem("Mercado Pago");
    }
    
    private void cargarTablaTickets() {
        modeloTabla.setRowCount(0);
        List<modelo.TicketCompra> tickets = ticketData.listarTickets();
        for (modelo.TicketCompra ticket : tickets) {
            List<modelo.DetalleTicket> detalles = ticketData.listarDetallesPorTicket(ticket.getIdTicket());
            if (!detalles.isEmpty()) {
                modelo.DetalleTicket detalle = detalles.get(0);
                modelo.Proyeccion proy = detalle.getProyeccion();

                StringBuilder asientos = new StringBuilder();
                for (modelo.Lugar lugar : detalle.getLugares()) {
                    if (asientos.length() > 0) asientos.append(", ");
                    asientos.append("Fila ").append(lugar.getFila()).append(" - Num ").append(lugar.getNumero());
                }

                Object[] fila = {
                    ticket.getIdTicket(),
                    proy.getPelicula().getTitulo(),
                    "Sala " + proy.getSala().getNroSala(),
                    asientos.toString(),
                    "$" + ticket.getMonto()
                };
                modeloTabla.addRow(fila);
            }
        }
    }
    
    private double calcularTotalTicket(modelo.TicketCompra ticket) {
        double total = 0;
        List<modelo.DetalleTicket> detalles = ticketData.listarDetallesPorTicket(ticket.getIdTicket());
        for (modelo.DetalleTicket detalle : detalles) {
            total += detalle.getSubtotal();
        }
        return total;
    }
    
    private void actualizarPrecios() {
        if (proyeccionSeleccionada != null) {
            double precio = proyeccionSeleccionada.getPrecioLugar();
            String tipo = proyeccionSeleccionada.isEs3D() ? "3D" : "2D";

            PrecioProy.setText("Precio " + tipo + ": $" + precio);

            int cantidad = (Integer) SpinnerCant.getValue();
            double total = precio * cantidad;

            labelPrecio.setText("TOTAL: $" + total);
        }
    }
    
    private boolean validarCampos() {
        if (comboPeli.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una película", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (comboSala.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sala", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (FuncionCombo.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una función", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (FilaCombo.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fila", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (ComboAsiento.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un asiento", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (ComboComprador.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un comprador", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (ComboFormaPago.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una forma de pago", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (lugarSeleccionado != null && lugarSeleccionado.getEstado().equals("ocupado")) {
            JOptionPane.showMessageDialog(this, "El asiento seleccionado ya está ocupado", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void limpiarCampos() {
        comboPeli.setSelectedIndex(0);
        comboSala.removeAllItems();
        comboSala.addItem("Seleccione sala");
        FuncionCombo.removeAllItems();
        FuncionCombo.addItem("Seleccione función");
        FilaCombo.removeAllItems();
        FilaCombo.addItem("Seleccione fila");
        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("Seleccione asiento");
        ComboComprador.setSelectedIndex(0);
        ComboFormaPago.setSelectedIndex(0);
        SpinnerCant.setValue(1);
        
        PrecioProy.setText("Precio:");
        labelPrecio.setText("TOTAL: $0");
        labelNum.setText("Numero de Ticket:");
        labelNombre.setText("Nombre del cliente:");
        
        peliculaSeleccionada = null;
        salaSeleccionada = null;
        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
        compradorSeleccionado = null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneltickets = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        panelbox = new javax.swing.JPanel();
        labelSelec = new javax.swing.JLabel();
        comboPeli = new javax.swing.JComboBox<>();
        comboSala = new javax.swing.JComboBox<>();
        FilaCombo = new javax.swing.JComboBox<>();
        FuncionCombo = new javax.swing.JComboBox<>();
        ComboAsiento = new javax.swing.JComboBox<>();
        PeliLabel = new javax.swing.JLabel();
        SalaLabel = new javax.swing.JLabel();
        LabelFuncion = new javax.swing.JLabel();
        LabelFila = new javax.swing.JLabel();
        LabelAsiento = new javax.swing.JLabel();
        panelButtons = new javax.swing.JPanel();
        buttonVender = new javax.swing.JButton();
        buttonCancelar = new javax.swing.JButton();
        buttonLimpiar = new javax.swing.JButton();
        panelEntrada = new javax.swing.JPanel();
        labelEntrada = new javax.swing.JLabel();
        panelNum = new javax.swing.JPanel();
        labelNum = new javax.swing.JLabel();
        panelNombre = new javax.swing.JPanel();
        labelNombre = new javax.swing.JLabel();
        panelPrecio = new javax.swing.JPanel();
        labelPrecio = new javax.swing.JLabel();
        panelID = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        ID = new javax.swing.JTable();
        panelVendidos = new javax.swing.JPanel();
        labelVendidos = new javax.swing.JLabel();
        SpinnerCant = new javax.swing.JSpinner();
        LabelCant = new javax.swing.JLabel();
        LabelComprador = new javax.swing.JLabel();
        ComboComprador = new javax.swing.JComboBox<>();
        LabelFormaPago = new javax.swing.JLabel();
        ComboFormaPago = new javax.swing.JComboBox<>();
        PrecioProy = new javax.swing.JLabel();

        paneltickets.setBackground(new java.awt.Color(51, 90, 144));
        paneltickets.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        title.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Tickets");

        javax.swing.GroupLayout panelticketsLayout = new javax.swing.GroupLayout(paneltickets);
        paneltickets.setLayout(panelticketsLayout);
        panelticketsLayout.setHorizontalGroup(
            panelticketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelticketsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelticketsLayout.setVerticalGroup(
            panelticketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelticketsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(title)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        labelSelec.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        labelSelec.setText("Seleccionar:");

        comboPeli.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pelicula" }));
        comboPeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboPeliActionPerformed(evt);
            }
        });

        comboSala.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sala" }));
        comboSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboSalaActionPerformed(evt);
            }
        });

        FilaCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione fila" }));
        FilaCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FilaComboActionPerformed(evt);
            }
        });

        FuncionCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione función" }));
        FuncionCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FuncionComboActionPerformed(evt);
            }
        });

        ComboAsiento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione asiento" }));
        ComboAsiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboAsientoActionPerformed(evt);
            }
        });

        PeliLabel.setText("Pelicula:");

        SalaLabel.setText("Sala:");

        LabelFuncion.setText("Funcion:");

        LabelFila.setText("Fila:");

        LabelAsiento.setText("Asiento:");

        javax.swing.GroupLayout panelboxLayout = new javax.swing.GroupLayout(panelbox);
        panelbox.setLayout(panelboxLayout);
        panelboxLayout.setHorizontalGroup(
            panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxLayout.createSequentialGroup()
                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboSala, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FuncionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboPeli, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FilaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelAsiento)
                            .addComponent(ComboAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LabelFila)
                            .addComponent(LabelFuncion)
                            .addComponent(SalaLabel)
                            .addComponent(PeliLabel)))
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelSelec)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelboxLayout.setVerticalGroup(
            panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSelec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PeliLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboPeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SalaLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelFuncion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FuncionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelFila)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FilaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LabelAsiento)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        buttonVender.setText("Vender Ticket");
        buttonVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonVenderActionPerformed(evt);
            }
        });

        buttonCancelar.setText("Cancelar Ticket");
        buttonCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelarActionPerformed(evt);
            }
        });

        buttonLimpiar.setText("Limpiar");
        buttonLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonVender)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(buttonCancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonLimpiar)
                .addContainerGap())
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonVender)
                    .addComponent(buttonCancelar)
                    .addComponent(buttonLimpiar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelEntrada.setBackground(new java.awt.Color(255, 255, 255));
        panelEntrada.setForeground(new java.awt.Color(102, 102, 102));
        panelEntrada.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        labelEntrada.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelEntrada.setForeground(new java.awt.Color(102, 102, 102));
        labelEntrada.setText("ENTRADA");

        javax.swing.GroupLayout panelEntradaLayout = new javax.swing.GroupLayout(panelEntrada);
        panelEntrada.setLayout(panelEntradaLayout);
        panelEntradaLayout.setHorizontalGroup(
            panelEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntradaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEntrada)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelEntradaLayout.setVerticalGroup(
            panelEntradaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntradaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEntrada)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelNum.setBackground(new java.awt.Color(255, 255, 255));
        panelNum.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelNum.setText("Numero de Ticket:");

        javax.swing.GroupLayout panelNumLayout = new javax.swing.GroupLayout(panelNum);
        panelNum.setLayout(panelNumLayout);
        panelNumLayout.setHorizontalGroup(
            panelNumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNumLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelNum)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelNumLayout.setVerticalGroup(
            panelNumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNumLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelNum)
                .addContainerGap())
        );

        panelNombre.setBackground(new java.awt.Color(255, 255, 255));
        panelNombre.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelNombre.setText("Nombre del cliente:");

        javax.swing.GroupLayout panelNombreLayout = new javax.swing.GroupLayout(panelNombre);
        panelNombre.setLayout(panelNombreLayout);
        panelNombreLayout.setHorizontalGroup(
            panelNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNombreLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelNombre)
                .addContainerGap(182, Short.MAX_VALUE))
        );
        panelNombreLayout.setVerticalGroup(
            panelNombreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNombreLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelNombre)
                .addContainerGap())
        );

        panelPrecio.setBackground(new java.awt.Color(255, 255, 255));
        panelPrecio.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelPrecio.setText("Precio:");

        javax.swing.GroupLayout panelPrecioLayout = new javax.swing.GroupLayout(panelPrecio);
        panelPrecio.setLayout(panelPrecioLayout);
        panelPrecioLayout.setHorizontalGroup(
            panelPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrecioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelPrecio)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelPrecioLayout.setVerticalGroup(
            panelPrecioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelPrecioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelPrecio)
                .addContainerGap())
        );

        panelID.setBackground(new java.awt.Color(255, 255, 255));

        ID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Numero", "Pelicula", "Sala", "Asiento", "Precio"
            }
        ));
        scroll.setViewportView(ID);

        javax.swing.GroupLayout panelIDLayout = new javax.swing.GroupLayout(panelID);
        panelID.setLayout(panelIDLayout);
        panelIDLayout.setHorizontalGroup(
            panelIDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIDLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelIDLayout.setVerticalGroup(
            panelIDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIDLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelVendidos.setBackground(new java.awt.Color(255, 255, 255));
        panelVendidos.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelVendidos.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelVendidos.setForeground(new java.awt.Color(102, 102, 102));
        labelVendidos.setText("TICKETS VENDIDOS:");

        javax.swing.GroupLayout panelVendidosLayout = new javax.swing.GroupLayout(panelVendidos);
        panelVendidos.setLayout(panelVendidosLayout);
        panelVendidosLayout.setHorizontalGroup(
            panelVendidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVendidosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelVendidos)
                .addContainerGap(289, Short.MAX_VALUE))
        );
        panelVendidosLayout.setVerticalGroup(
            panelVendidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVendidosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelVendidos)
                .addContainerGap())
        );

        SpinnerCant.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SpinnerCantStateChanged(evt);
            }
        });

        LabelCant.setText("Cant. entradas:");

        LabelComprador.setText("Comprador:");

        ComboComprador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboComprador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboCompradorActionPerformed(evt);
            }
        });

        LabelFormaPago.setText("Forma de pago:");

        ComboFormaPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        ComboFormaPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboFormaPagoActionPerformed(evt);
            }
        });

        PrecioProy.setText("Precio 2D:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paneltickets, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(LabelCant)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(SpinnerCant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(LabelComprador)
                                    .addComponent(ComboComprador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ComboFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(PrecioProy, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(LabelFormaPago)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(panelID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(15, 15, 15))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelEntrada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(29, 29, 29))
                            .addComponent(panelVendidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(panelPrecio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(panelNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(panelNum, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneltickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(panelEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelVendidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(SpinnerCant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(LabelCant))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(LabelComprador)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboComprador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(LabelFormaPago)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboFormaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47)
                                .addComponent(PrecioProy, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboPeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboPeliActionPerformed
        if (comboPeli.getSelectedIndex() <= 0) {
            peliculaSeleccionada = null;
            comboSala.removeAllItems();
            comboSala.addItem("Seleccione sala");
            return;
        }

        String tituloSeleccionado = comboPeli.getSelectedItem().toString();
        List<modelo.Pelicula> peliculas = peliculaData.listarPeliculas();

        for (modelo.Pelicula peli : peliculas) {
            if (peli.getTitulo().equals(tituloSeleccionado)) {
                peliculaSeleccionada = peli;
                break;
            }
        }

        cargarSalasPorPelicula();

        FuncionCombo.removeAllItems();
        FuncionCombo.addItem("Seleccione función");
        FilaCombo.removeAllItems();
        FilaCombo.addItem("Seleccione fila");
        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("Seleccione asiento");

        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
    }//GEN-LAST:event_comboPeliActionPerformed

    private void FuncionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FuncionComboActionPerformed
        if (FuncionCombo.getSelectedIndex() <= 0) {
            proyeccionSeleccionada = null;
            FilaCombo.removeAllItems();
            FilaCombo.addItem("Seleccione fila");
            return;
        }

        String funcionSeleccionada = FuncionCombo.getSelectedItem().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<modelo.Proyeccion> proyecciones = proyeccionData.listarProyecciones();
        for (modelo.Proyeccion proy : proyecciones) {
            if (proy.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula() &&
                proy.getSala().getNroSala() == salaSeleccionada.getNroSala() &&
                proy.getHoraInicio().format(formatter).equals(funcionSeleccionada)) {
                proyeccionSeleccionada = proy;
                break;
            }
        }

        if (proyeccionSeleccionada != null) {
            cargarFilas();
            actualizarPrecios();
        }

        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("Seleccione asiento");
        lugarSeleccionado = null;
    }//GEN-LAST:event_FuncionComboActionPerformed

    private void comboSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSalaActionPerformed
        if (comboSala.getSelectedIndex() <= 0) {
            salaSeleccionada = null;
            FuncionCombo.removeAllItems();
            FuncionCombo.addItem("Seleccione función");
            return;
        }

        String salaTexto = comboSala.getSelectedItem().toString();
        // Extraer el número de sala usando regex para encontrar el primer número después de "Sala "
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("Sala\\s+(\\d+)");
        java.util.regex.Matcher matcher = pattern.matcher(salaTexto);

        if (!matcher.find()) {
            salaSeleccionada = null;
            return;
        }

        int nroSala = Integer.parseInt(matcher.group(1));
        salaSeleccionada = salaData.buscarSala(nroSala);

        cargarFuncionesPorSala();

        FilaCombo.removeAllItems();
        FilaCombo.addItem("Seleccione fila");
        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("Seleccione asiento");
        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
    }//GEN-LAST:event_comboSalaActionPerformed

    private void ComboFormaPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboFormaPagoActionPerformed

    }//GEN-LAST:event_ComboFormaPagoActionPerformed

    private void FilaComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilaComboActionPerformed
         if (FilaCombo.getSelectedIndex() <= 0) {
            ComboAsiento.removeAllItems();
            ComboAsiento.addItem("Seleccione asiento");
            return;
        }

        cargarAsientosPorFila();
    }//GEN-LAST:event_FilaComboActionPerformed

    private void ComboAsientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboAsientoActionPerformed
        if (ComboAsiento.getSelectedIndex() <= 0) {
            lugarSeleccionado = null;
            return;
        }

        String asientoTexto = ComboAsiento.getSelectedItem().toString();
        int numeroAsiento = Integer.parseInt(asientoTexto.replaceAll("[^0-9]", "").trim());

        String filaSeleccionadaStr = FilaCombo.getSelectedItem().toString();
        char filaSeleccionadaChar = filaSeleccionadaStr.charAt(0);

        List<modelo.Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());

        for (modelo.Lugar lugar : lugares) {
            if (lugar.getFila() == filaSeleccionadaChar && lugar.getNumero() == numeroAsiento) {
                lugarSeleccionado = lugar;
                break;
            }
        }
    }//GEN-LAST:event_ComboAsientoActionPerformed

    private void ComboCompradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboCompradorActionPerformed
        if (ComboComprador.getSelectedIndex() <= 0) {
            compradorSeleccionado = null;
            labelNombre.setText("Nombre del cliente:");
            return;
        }

        String compradorTexto = ComboComprador.getSelectedItem().toString();
        String dni = compradorTexto.split(" - ")[0].trim();
        compradorSeleccionado = compradorData.buscarComprador(dni);
        if (compradorSeleccionado != null) {
            labelNombre.setText("Nombre del cliente: " + compradorSeleccionado.getNombre());
        }
    }//GEN-LAST:event_ComboCompradorActionPerformed

    private void SpinnerCantStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SpinnerCantStateChanged
        actualizarPrecios();
    }//GEN-LAST:event_SpinnerCantStateChanged

    private void buttonVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonVenderActionPerformed
        if (!validarCampos()) {
            return;
        }
        if (lugarSeleccionado.getEstado().equalsIgnoreCase("ocupado")) {
            JOptionPane.showMessageDialog(this, "El asiento seleccionado ya está ocupado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {

            int cantidad = (Integer) SpinnerCant.getValue();
            double precioUnitario = proyeccionSeleccionada.getPrecioLugar();
            double total = precioUnitario * cantidad;

            modelo.TicketCompra nuevoTicket = new modelo.TicketCompra();
            nuevoTicket.setComprador(compradorSeleccionado);
            nuevoTicket.setFechaCompra(java.time.LocalDateTime.now());
            nuevoTicket.setFechaFuncion(proyeccionSeleccionada.getHoraInicio());
            nuevoTicket.setMonto(total);
            nuevoTicket.setTipoCompra(ComboFormaPago.getSelectedItem().toString());
            String codigoVenta = "TICKET-" + System.currentTimeMillis();
            nuevoTicket.setCodigoVenta(codigoVenta);
            nuevoTicket.setEstadoTicket("Activo");
            modelo.DetalleTicket detalle = new modelo.DetalleTicket();
            detalle.setProyeccion(proyeccionSeleccionada);
            detalle.setCantidad(cantidad);
            detalle.setSubtotal(total);
            List<modelo.Lugar> lugaresTicket = new ArrayList<>();
            lugaresTicket.add(lugarSeleccionado);
            detalle.setLugares(lugaresTicket);
            List<modelo.DetalleTicket> detalles = new ArrayList<>();
            detalles.add(detalle);
            nuevoTicket.setDetalles(detalles);
            ticketData.guardarTicket(nuevoTicket);
            lugarData.cambiarEstadoLugar(lugarSeleccionado.getCodLugar(), "ocupado");
            labelNum.setText("Numero de Ticket: " + nuevoTicket.getIdTicket());
            labelNombre.setText("Nombre del cliente: " + compradorSeleccionado.getNombre());
            labelPrecio.setText("TOTAL: $" + total);
            
            JOptionPane.showMessageDialog(this, 
                "Ticket vendido exitosamente!\n" + "Número de ticket: " + nuevoTicket.getIdTicket() + "\n" +"Código de venta: " + codigoVenta + "\n" +"Total: $" + total, "Venta exitosa", 
                JOptionPane.INFORMATION_MESSAGE);

            cargarTablaTickets();
            limpiarCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Error al guardar el ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_buttonVenderActionPerformed

    private void buttonCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelarActionPerformed
        int filaSeleccionada = ID.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ticket de la tabla para cancelarlo","Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTicket = (int) modeloTabla.getValueAt(filaSeleccionada, 0);

        int respuesta = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro que desea anular el ticket N° " + idTicket + "?","Confirmar cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                modelo.TicketCompra ticket = ticketData.buscarTicket(idTicket);

                if (ticket != null) {
                    List<modelo.DetalleTicket> detalles = ticketData.listarDetallesPorTicket(idTicket);
                    for (modelo.DetalleTicket detalle : detalles) {
                        for (modelo.Lugar lugar : detalle.getLugares()) {
                            lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "libre");
                        }
                    }
                    ticketData.anularTicket(idTicket);

                    JOptionPane.showMessageDialog(this, "Ticket anulado correctamente. Los asientos han sido liberados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

                    cargarTablaTickets();
                    ID.clearSelection();

                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el ticket", "Error",  JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cancelar el ticket: " + e.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_buttonCancelarActionPerformed

    private void buttonLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLimpiarActionPerformed
        limpiarCampos();
        JOptionPane.showMessageDialog(this,"Campos limpiados", "Información",  JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_buttonLimpiarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboAsiento;
    private javax.swing.JComboBox<String> ComboComprador;
    private javax.swing.JComboBox<String> ComboFormaPago;
    private javax.swing.JComboBox<String> FilaCombo;
    private javax.swing.JComboBox<String> FuncionCombo;
    private javax.swing.JTable ID;
    private javax.swing.JLabel LabelAsiento;
    private javax.swing.JLabel LabelCant;
    private javax.swing.JLabel LabelComprador;
    private javax.swing.JLabel LabelFila;
    private javax.swing.JLabel LabelFormaPago;
    private javax.swing.JLabel LabelFuncion;
    private javax.swing.JLabel PeliLabel;
    private javax.swing.JLabel PrecioProy;
    private javax.swing.JLabel SalaLabel;
    private javax.swing.JSpinner SpinnerCant;
    private javax.swing.JButton buttonCancelar;
    private javax.swing.JButton buttonLimpiar;
    private javax.swing.JButton buttonVender;
    private javax.swing.JComboBox<String> comboPeli;
    private javax.swing.JComboBox<String> comboSala;
    private javax.swing.JLabel labelEntrada;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel labelNum;
    private javax.swing.JLabel labelPrecio;
    private javax.swing.JLabel labelSelec;
    private javax.swing.JLabel labelVendidos;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelEntrada;
    private javax.swing.JPanel panelID;
    private javax.swing.JPanel panelNombre;
    private javax.swing.JPanel panelNum;
    private javax.swing.JPanel panelPrecio;
    private javax.swing.JPanel panelVendidos;
    private javax.swing.JPanel panelbox;
    private javax.swing.JPanel paneltickets;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
