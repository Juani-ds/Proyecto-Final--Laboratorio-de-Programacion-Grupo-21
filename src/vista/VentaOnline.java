/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import persistencia.PeliculaData;
import persistencia.SalaData;
import persistencia.ProyeccionData;
import persistencia.LugarData;
import persistencia.TicketCompraData;
import modelo.Pelicula;
import modelo.Sala;
import modelo.Proyeccion;
import modelo.Lugar;
import modelo.TicketCompra;
import modelo.DetalleTicket;
import modelo.Comprador;
import persistencia.CompradorData;
import java.util.ArrayList;

/**
 *
 * @author JuanmaPC
 */
public class VentaOnline extends javax.swing.JInternalFrame {

    /**
     * Creates new form VentaOnline
     */
    
    private PeliculaData peliculaData;
    private SalaData salaData;
    private ProyeccionData proyeccionData;
    private LugarData lugarData;
    private TicketCompraData ticketCompraData;
    private CompradorData compradorData;
    private Pelicula peliculaSeleccionada;
    private Sala salaSeleccionada;
    private Proyeccion proyeccionSeleccionada;
    private Lugar lugarSeleccionado;
    
    public VentaOnline() {
        initComponents();
        inicializarDatos();
        cargarPeliculas();
    }
    
    private void inicializarDatos() {
        peliculaData = new PeliculaData();
        salaData = new SalaData();
        proyeccionData = new ProyeccionData();
        lugarData = new LugarData();
        ticketCompraData = new TicketCompraData();
        compradorData = new CompradorData();
        
        peliculaSeleccionada = null;
        salaSeleccionada = null;
        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
    }
    
    private void limpiarFormulario() {
        comboPeli.setSelectedIndex(0);
        comboSala.removeAllItems();
        comboSala.addItem("-- Seleccione sala --");
        FuncionCombo.removeAllItems();
        FuncionCombo.addItem("-- Seleccione función --");
        FilaCombo.removeAllItems();
        FilaCombo.addItem("-- Seleccione fila --");
        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("-- Seleccione asiento --");

        dniInp.setText("");
        nroTarjetaInp.setText("");
        nombreTarjetaInp.setText("");
        cSeguridadInp.setText("");
        vencInp.setDate(null);

        labelPrecio.setText("TOTAL: $0.00");
        labelNum.setText("Numero de Ticket:");

        peliculaSeleccionada = null;
        salaSeleccionada = null;
        proyeccionSeleccionada = null;
        lugarSeleccionado = null;
    }
    
    private void cargarPeliculas() {
        comboPeli.removeAllItems();
        comboPeli.addItem("-- Seleccione película --");
        
        List<Pelicula> peliculas = peliculaData.listarPeliculasEnCartelera();
        for (Pelicula p : peliculas) {
            comboPeli.addItem(p.getTitulo());
        }
    }
    
    private void cargarSalasPorPelicula() {
        if (peliculaSeleccionada == null) {
            return;
        }
        
        comboSala.removeAllItems();
        comboSala.addItem("-- Seleccione sala --");
        
        List<Proyeccion> proyecciones = proyeccionData.listarProyeccionesFuturas();
        Set<Integer> salasUsadas = new HashSet<>();
        
        for (Proyeccion p : proyecciones) {
            if (p.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula() 
                && !salasUsadas.contains(p.getSala().getNroSala())) {
                comboSala.addItem("Sala " + p.getSala().getNroSala() + 
                                 " (" + p.getSala().getCapacidad() + " asientos)");
                salasUsadas.add(p.getSala().getNroSala());
            }
        }
    }
    
    private void cargarFuncionesPorSala() {
        if (peliculaSeleccionada == null || salaSeleccionada == null) {
            return;
        }
        
        FuncionCombo.removeAllItems();
        FuncionCombo.addItem("-- Seleccione función --");
        
        List<Proyeccion> proyecciones = proyeccionData.listarProyeccionesFuturas();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Proyeccion p : proyecciones) {
            if (p.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula() 
                && p.getSala().getNroSala() == salaSeleccionada.getNroSala()) {
                String textoFuncion = p.getHoraInicio().format(formatter);
                if (p.isEs3D()) {
                    textoFuncion += " - 3D";
                }
                if (p.isSubtitulada()) {
                    textoFuncion += " - SUB";
                }
                textoFuncion += " - " + p.getIdioma();
                FuncionCombo.addItem(textoFuncion);
            }
        }
    }
    
    private void cargarFilas() {
        if (proyeccionSeleccionada == null) {
            return;
        }
        
        FilaCombo.removeAllItems();
        FilaCombo.addItem("-- Seleccione fila --");
        
        List<Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
        Set<Character> filasUsadas = new HashSet<>();
        
        for (Lugar l : lugares) {
            if (!filasUsadas.contains(l.getFila())) {
                FilaCombo.addItem(String.valueOf(l.getFila()));
                filasUsadas.add(l.getFila());
            }
        }
    }
    
    private void cargarAsientosPorFila() {
        if (proyeccionSeleccionada == null || FilaCombo.getSelectedIndex() <= 0) {
            return;
        }

        ComboAsiento.removeAllItems();
        ComboAsiento.addItem("-- Seleccione asiento --");

        String filaSeleccionadaStr = FilaCombo.getSelectedItem().toString();
        char filaSeleccionadaChar = filaSeleccionadaStr.charAt(0);

        List<Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());

        for (Lugar l : lugares) {
            if (l.getFila() == filaSeleccionadaChar && "libre".equalsIgnoreCase(l.getEstado())) {
                ComboAsiento.addItem("Asiento " + l.getNumero());
            }
        }
    }
    
    private void actualizarPrecios() {
        if (proyeccionSeleccionada == null) {
            labelPrecio.setText("TOTAL: $0.00");
            return;
        }
        
        double precio = proyeccionSeleccionada.getPrecioLugar();
        labelPrecio.setText(String.format("TOTAL: $%.2f", precio));
    }
    
    private boolean validarDatosCompra() {
        String dni = dniInp.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar su DNI", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            dniInp.requestFocus();
            return false;
        }

        if (peliculaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una película", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (salaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sala", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (proyeccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una función", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (lugarSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un asiento", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String nroTarjeta = nroTarjetaInp.getText().trim();
        if (nroTarjeta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el número de tarjeta", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String nombreTarjeta = nombreTarjetaInp.getText().trim();
        if (nombreTarjeta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del titular", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String codigoSeguridad = cSeguridadInp.getText().trim();
        if (codigoSeguridad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el código de seguridad", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (vencInp.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar la fecha de vencimiento", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelbox = new javax.swing.JPanel();
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
        paneltickets = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        panelNum = new javax.swing.JPanel();
        labelNum = new javax.swing.JLabel();
        panelEntrada3 = new javax.swing.JPanel();
        labelEntrada3 = new javax.swing.JLabel();
        panelPrecio = new javax.swing.JPanel();
        labelPrecio = new javax.swing.JLabel();
        DatosTarjetaLabel = new javax.swing.JLabel();
        nroTarjetaLbl = new javax.swing.JLabel();
        nombreLbl = new javax.swing.JLabel();
        codigoLbl = new javax.swing.JLabel();
        vencimientoLbl = new javax.swing.JLabel();
        nroTarjetaInp = new javax.swing.JTextField();
        nombreTarjetaInp = new javax.swing.JTextField();
        cSeguridadInp = new javax.swing.JTextField();
        vencInp = new com.toedter.calendar.JDateChooser();
        comprarBtn = new javax.swing.JButton();
        limpiarBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        dniInp = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(1028, 400));

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

        paneltickets.setBackground(new java.awt.Color(51, 90, 144));
        paneltickets.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        title.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Venta Online");

        javax.swing.GroupLayout panelticketsLayout = new javax.swing.GroupLayout(paneltickets);
        paneltickets.setLayout(panelticketsLayout);
        panelticketsLayout.setHorizontalGroup(
            panelticketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelticketsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title)
                .addContainerGap(846, Short.MAX_VALUE))
        );
        panelticketsLayout.setVerticalGroup(
            panelticketsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelticketsLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(title)
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addContainerGap(188, Short.MAX_VALUE))
        );
        panelNumLayout.setVerticalGroup(
            panelNumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNumLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelNum)
                .addContainerGap())
        );

        panelEntrada3.setBackground(new java.awt.Color(255, 255, 255));
        panelEntrada3.setForeground(new java.awt.Color(102, 102, 102));
        panelEntrada3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        labelEntrada3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelEntrada3.setForeground(new java.awt.Color(102, 102, 102));
        labelEntrada3.setText("ENTRADA");

        javax.swing.GroupLayout panelEntrada3Layout = new javax.swing.GroupLayout(panelEntrada3);
        panelEntrada3.setLayout(panelEntrada3Layout);
        panelEntrada3Layout.setHorizontalGroup(
            panelEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntrada3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEntrada3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelEntrada3Layout.setVerticalGroup(
            panelEntrada3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEntrada3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelEntrada3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        DatosTarjetaLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        DatosTarjetaLabel.setText("Datos de la tarjeta:");

        nroTarjetaLbl.setText("Numero de tarjeta");

        nombreLbl.setText("Nombre");

        codigoLbl.setText("CDM");

        vencimientoLbl.setText("Vencimiento");

        comprarBtn.setBackground(new java.awt.Color(0, 153, 0));
        comprarBtn.setFont(new java.awt.Font("Dialog", 1, 36)); // NOI18N
        comprarBtn.setForeground(new java.awt.Color(255, 255, 255));
        comprarBtn.setText("Comprar");
        comprarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comprarBtnActionPerformed(evt);
            }
        });

        limpiarBtn.setText("Limpiar");
        limpiarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("DNI");

        dniInp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dniInpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelboxLayout = new javax.swing.GroupLayout(panelbox);
        panelbox.setLayout(panelboxLayout);
        panelboxLayout.setHorizontalGroup(
            panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addComponent(paneltickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(PeliLabel)
                            .addComponent(SalaLabel)
                            .addComponent(LabelFuncion)
                            .addComponent(LabelFila)
                            .addComponent(LabelAsiento)
                            .addComponent(comboPeli, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboSala, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(FuncionCombo, 0, 181, Short.MAX_VALUE)
                            .addComponent(FilaCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ComboAsiento, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelboxLayout.createSequentialGroup()
                                    .addComponent(cSeguridadInp, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(dniInp, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(panelboxLayout.createSequentialGroup()
                                    .addComponent(codigoLbl)
                                    .addGap(59, 59, 59)
                                    .addComponent(jLabel1))
                                .addComponent(nroTarjetaLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(DatosTarjetaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nombreLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(vencimientoLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(vencInp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                    .addComponent(nombreTarjetaInp, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nroTarjetaInp, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(panelboxLayout.createSequentialGroup()
                                .addComponent(limpiarBtn)
                                .addGap(202, 202, 202)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelPrecio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelNum, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelEntrada3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comprarBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panelboxLayout.setVerticalGroup(
            panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelboxLayout.createSequentialGroup()
                .addComponent(paneltickets, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PeliLabel)
                            .addComponent(DatosTarjetaLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelboxLayout.createSequentialGroup()
                                .addComponent(comboPeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SalaLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(LabelFuncion)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FuncionCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(LabelFila)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FilaCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LabelAsiento)
                                    .addGroup(panelboxLayout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(ComboAsiento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 22, 22))
                            .addGroup(panelboxLayout.createSequentialGroup()
                                .addComponent(nroTarjetaLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nroTarjetaInp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nombreLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nombreTarjetaInp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vencimientoLbl)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(vencInp, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(codigoLbl)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelboxLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cSeguridadInp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dniInp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addComponent(limpiarBtn)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(panelboxLayout.createSequentialGroup()
                        .addComponent(panelEntrada3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(comprarBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(43, 43, 43))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelbox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void comboSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboSalaActionPerformed
        if (comboSala.getSelectedIndex() <= 0) {
            salaSeleccionada = null;
            FuncionCombo.removeAllItems();
            FuncionCombo.addItem("Seleccione función");
            return;
        }

        String salaTexto = comboSala.getSelectedItem().toString();
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

    private void FilaComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FilaComboActionPerformed
        if (FilaCombo.getSelectedIndex() <= 0) {
            ComboAsiento.removeAllItems();
            ComboAsiento.addItem("Seleccione asiento");
            return;
        }

        cargarAsientosPorFila();
    }//GEN-LAST:event_FilaComboActionPerformed

    private void FuncionComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FuncionComboActionPerformed
        if (FuncionCombo.getSelectedIndex() <= 0) {
            proyeccionSeleccionada = null;
            FilaCombo.removeAllItems();
            FilaCombo.addItem("Seleccione fila");
            labelPrecio.setText("TOTAL: $0.00");
            return;
        }

        String funcionSeleccionada = FuncionCombo.getSelectedItem().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<modelo.Proyeccion> proyecciones = proyeccionData.listarProyeccionesFuturas();
        for (modelo.Proyeccion proy : proyecciones) {
            if (proy.getPelicula().getIdPelicula() == peliculaSeleccionada.getIdPelicula() &&
                proy.getSala().getNroSala() == salaSeleccionada.getNroSala()) {
                String textoFuncion = proy.getHoraInicio().format(formatter);
                if (proy.isEs3D()) {
                    textoFuncion += " - 3D";
                }
                if (proy.isSubtitulada()) {
                    textoFuncion += " - SUB";
                }
                textoFuncion += " - " + proy.getIdioma();

                if (textoFuncion.equals(funcionSeleccionada)) {
                    proyeccionSeleccionada = proy;
                    break;
                }
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

    private void comprarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comprarBtnActionPerformed
         if (!validarDatosCompra()) {
            return;
        }

        String dniComprador = dniInp.getText().trim();

        try {
            Comprador compradorSeleccionado = compradorData.buscarComprador(dniComprador);

            if (compradorSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                                            "No existe un usuario registrado con el DNI ingresado.\n" +
                                            "Por favor, registre su cuenta en 'Usuarios > Clientes' primero.", 
                                            "Usuario no encontrado", 
                                            JOptionPane.ERROR_MESSAGE);
                dniInp.requestFocus();
                return;
            }

            TicketCompra nuevoTicket = new TicketCompra();
            nuevoTicket.setComprador(compradorSeleccionado);
            nuevoTicket.setFechaCompra(java.time.LocalDateTime.now());
            nuevoTicket.setFechaFuncion(proyeccionSeleccionada.getHoraInicio());
            nuevoTicket.setMonto(proyeccionSeleccionada.getPrecioLugar());
            nuevoTicket.setTipoCompra("Online");
            nuevoTicket.setMedioPago("Crédito");

            String codigoVenta = "ONLINE-" + System.currentTimeMillis();
            nuevoTicket.setCodigoVenta(codigoVenta);
            nuevoTicket.setEstadoTicket("Activo");

            DetalleTicket detalle = new DetalleTicket();
            detalle.setProyeccion(proyeccionSeleccionada);
            detalle.setCantidad(1); // Siempre es 1 asiento en venta online
            detalle.setSubtotal(proyeccionSeleccionada.getPrecioLugar());

            List<Lugar> lugaresTicket = new ArrayList<>();
            lugaresTicket.add(lugarSeleccionado);
            detalle.setLugares(lugaresTicket);

            List<DetalleTicket> detalles = new ArrayList<>();
            detalles.add(detalle);
            nuevoTicket.setDetalles(detalles);

            ticketCompraData.guardarTicket(nuevoTicket);

            lugarData.cambiarEstadoLugar(lugarSeleccionado.getCodLugar(), "ocupado");

            labelNum.setText("Numero de Ticket: " + nuevoTicket.getIdTicket());

            JOptionPane.showMessageDialog(this, 
                                        "¡Compra realizada exitosamente!\n" + 
                                        "Número de ticket: " + nuevoTicket.getIdTicket() + "\n" +
                                        "Código de venta: " + codigoVenta + "\n" +
                                        "Cliente: " + compradorSeleccionado.getNombre() + "\n" +
                                        "Total: $" + proyeccionSeleccionada.getPrecioLugar(), 
                                        "Venta exitosa", 
                                        JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                                        "Error al procesar la compra: " + e.getMessage(), 
                                        "Error", 
                                        JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }//GEN-LAST:event_comprarBtnActionPerformed

    private void limpiarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarBtnActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_limpiarBtnActionPerformed

    private void dniInpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dniInpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dniInpActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboAsiento;
    private javax.swing.JLabel DatosTarjetaLabel;
    private javax.swing.JComboBox<String> FilaCombo;
    private javax.swing.JComboBox<String> FuncionCombo;
    private javax.swing.JLabel LabelAsiento;
    private javax.swing.JLabel LabelFila;
    private javax.swing.JLabel LabelFuncion;
    private javax.swing.JLabel PeliLabel;
    private javax.swing.JLabel SalaLabel;
    private javax.swing.JTextField cSeguridadInp;
    private javax.swing.JLabel codigoLbl;
    private javax.swing.JComboBox<String> comboPeli;
    private javax.swing.JComboBox<String> comboSala;
    private javax.swing.JButton comprarBtn;
    private javax.swing.JTextField dniInp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel labelEntrada;
    private javax.swing.JLabel labelEntrada1;
    private javax.swing.JLabel labelEntrada2;
    private javax.swing.JLabel labelEntrada3;
    private javax.swing.JLabel labelNum;
    private javax.swing.JLabel labelPrecio;
    private javax.swing.JButton limpiarBtn;
    private javax.swing.JLabel nombreLbl;
    private javax.swing.JTextField nombreTarjetaInp;
    private javax.swing.JTextField nroTarjetaInp;
    private javax.swing.JLabel nroTarjetaLbl;
    private javax.swing.JPanel panelEntrada;
    private javax.swing.JPanel panelEntrada1;
    private javax.swing.JPanel panelEntrada2;
    private javax.swing.JPanel panelEntrada3;
    private javax.swing.JPanel panelNum;
    private javax.swing.JPanel panelPrecio;
    private javax.swing.JPanel panelbox;
    private javax.swing.JPanel paneltickets;
    private javax.swing.JLabel title;
    private com.toedter.calendar.JDateChooser vencInp;
    private javax.swing.JLabel vencimientoLbl;
    // End of variables declaration//GEN-END:variables
}
