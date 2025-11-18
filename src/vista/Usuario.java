/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import modelo.Comprador;
import persistencia.CompradorData;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author tizia
 */
public class Usuario extends javax.swing.JInternalFrame {

    /**
     * Creates new form Comprador
     */
    
    private CompradorData compradorData;
    private DefaultTableModel modeloTabla;
    private modelo.Comprador compradorSeleccionado;
    
    public Usuario() {
        initComponents();
        compradorData = new CompradorData();
        modeloTabla = (DefaultTableModel) tableDatos.getModel();
        compradorSeleccionado = null;
        configurarTabla();
        cargarTabla();
        configurarEventos();
    }
    
    private void configurarTabla() {
        modeloTabla.setRowCount(0);
        modeloTabla.setColumnCount(0);
        modeloTabla.addColumn("DNI");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Fecha Nac.");
        modeloTabla.addColumn("Medio de Pago");
        modeloTabla.addColumn("Activo");
    }
    
    private void cargarTabla() {
        modeloTabla.setRowCount(0);

        for (modelo.Comprador comprador : compradorData.listarCompradores()) {
            Object[] fila = {
                comprador.getDni(),
                comprador.getNombre(),
                comprador.getFechaNac(),
                comprador.getMedioPago(),
                comprador.isActivo() ? "Activo" : "Inactivo"
            };
            modeloTabla.addRow(fila);
        }
    }
    
    private void configurarEventos() {
        tableDatos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableDatos.getSelectedRow() != -1) {
                cargarDatosComprador();
            }
        });
    }
    
    private void cargarDatosComprador() {
        int filaSeleccionada = tableDatos.getSelectedRow();
        if (filaSeleccionada != -1) {
            String dni = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
            compradorSeleccionado = compradorData.buscarComprador(dni);

            if (compradorSeleccionado != null) {
                campo1.setText(compradorSeleccionado.getDni());
                campo2.setText(compradorSeleccionado.getNombre());
                java.time.LocalDate localDate = compradorSeleccionado.getFechaNac();
                java.util.Date date = java.sql.Date.valueOf(localDate);
                dateChooserFechaNac.setDate(date);
                campo4.setText(compradorSeleccionado.getPassword());
                comboMedioPago.setSelectedItem(compradorSeleccionado.getMedioPago());
                
                campo1.setEditable(false);
                campo1.setBackground(new java.awt.Color(240, 240, 240));
            }
        }
    }
    
    private void limpiarCampos() {
        campo1.setText("");
        campo2.setText("");
        dateChooserFechaNac.setDate(null);
        campo4.setText("");
        if (comboMedioPago.getItemCount() > 0) {
            comboMedioPago.setSelectedIndex(0);
        }
        campo1.setEditable(true);
        campo1.setBackground(java.awt.Color.WHITE);
        compradorSeleccionado = null;
        tableDatos.clearSelection();
    }
    
    private boolean validarCampos() {
        if (campo1.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El DNI es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (campo2.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"El nombre es obligatorio","Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (dateChooserFechaNac.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha de nacimiento", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        java.util.Date fechaSeleccionada = dateChooserFechaNac.getDate();
        if (fechaSeleccionada.after(new java.util.Date())) {
            JOptionPane.showMessageDialog(this, "La fecha de nacimiento no puede ser futura", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (campo4.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La contraseña es obligatoria","Error",JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (comboMedioPago.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un medio de pago","Error", JOptionPane.ERROR_MESSAGE);
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

        panelTitulo = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        panelDatos = new javax.swing.JPanel();
        dni = new javax.swing.JLabel();
        nombre = new javax.swing.JLabel();
        fechaNac = new javax.swing.JLabel();
        contrasenia = new javax.swing.JLabel();
        medioPago = new javax.swing.JLabel();
        campo1 = new javax.swing.JTextField();
        campo2 = new javax.swing.JTextField();
        campo4 = new javax.swing.JTextField();
        comboMedioPago = new javax.swing.JComboBox<>();
        dateChooserFechaNac = new com.toedter.calendar.JDateChooser();
        panelLabel = new javax.swing.JPanel();
        label = new javax.swing.JLabel();
        buttonAgg = new javax.swing.JButton();
        buttonEliminar = new javax.swing.JButton();
        panelTable = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        tableDatos = new javax.swing.JTable();
        buttonBuscar = new javax.swing.JButton();
        buttonEditar = new javax.swing.JButton();
        buttonDarBaja = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(904, 429));

        panelTitulo.setBackground(new java.awt.Color(51, 90, 144));
        panelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        title.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        title.setText("Datos de Usuario");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addGap(98, 98, 98)
                .addComponent(title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTituloLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(title)
                .addGap(402, 402, 402))
        );

        dni.setText("DNI");

        nombre.setText("Nombre");

        fechaNac.setText("Fecha de Nacimiento");

        contrasenia.setText("Contraseña");

        medioPago.setText("Medio de Pago");

        campo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campo1ActionPerformed(evt);
            }
        });

        campo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campo2ActionPerformed(evt);
            }
        });

        campo4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campo4ActionPerformed(evt);
            }
        });

        comboMedioPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Débito", "Crédito", "Mercado Pago", "Efectivo" }));
        comboMedioPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMedioPagoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelDatosLayout.createSequentialGroup()
                        .addComponent(contrasenia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(campo4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelDatosLayout.createSequentialGroup()
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dni)
                            .addComponent(nombre)
                            .addComponent(fechaNac)
                            .addComponent(medioPago))
                        .addGap(9, 9, 9)
                        .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(campo1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .addComponent(campo2)
                            .addComponent(comboMedioPago, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateChooserFechaNac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dni)
                    .addComponent(campo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombre)
                    .addComponent(campo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechaNac)
                    .addComponent(dateChooserFechaNac, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contrasenia)
                    .addComponent(campo4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(medioPago)
                    .addComponent(comboMedioPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        label.setText("Ingrese los datos:");

        javax.swing.GroupLayout panelLabelLayout = new javax.swing.GroupLayout(panelLabel);
        panelLabel.setLayout(panelLabelLayout);
        panelLabelLayout.setHorizontalGroup(
            panelLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label)
                .addContainerGap(13, Short.MAX_VALUE))
        );
        panelLabelLayout.setVerticalGroup(
            panelLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLabelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label)
                .addContainerGap())
        );

        buttonAgg.setText("Registrar");
        buttonAgg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAggActionPerformed(evt);
            }
        });

        buttonEliminar.setText("Eliminar");
        buttonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEliminarActionPerformed(evt);
            }
        });

        panelTable.setPreferredSize(new java.awt.Dimension(517, 234));

        scroll.setPreferredSize(new java.awt.Dimension(653, 403));

        tableDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scroll.setViewportView(tableDatos);

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonBuscar.setText("Buscar");
        buttonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBuscarActionPerformed(evt);
            }
        });

        buttonEditar.setText("Editar");
        buttonEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditarActionPerformed(evt);
            }
        });

        buttonDarBaja.setText("Dar de baja");
        buttonDarBaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDarBajaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(buttonAgg)
                                        .addGap(18, 18, 18)
                                        .addComponent(buttonBuscar)
                                        .addGap(18, 18, 18)
                                        .addComponent(buttonDarBaja)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(111, 111, 111)
                        .addComponent(buttonEditar)
                        .addGap(43, 43, 43)
                        .addComponent(buttonEliminar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonAgg)
                            .addComponent(buttonBuscar)
                            .addComponent(buttonDarBaja)))
                    .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEditar)
                    .addComponent(buttonEliminar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void campo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campo1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campo1ActionPerformed

    private void campo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campo2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campo2ActionPerformed

    private void campo4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campo4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campo4ActionPerformed

    private void buttonAggActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAggActionPerformed
        if (!validarCampos()) {
            return;
        }

        modelo.Comprador existe = compradorData.buscarComprador(campo1.getText().trim());
        if (existe != null) {
            JOptionPane.showMessageDialog(this,"Ya existe un usuario con ese DNI", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        java.util.Date fechaUtil = dateChooserFechaNac.getDate();
        java.time.LocalDate fechaNac = fechaUtil.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        modelo.Comprador nuevoComprador = new modelo.Comprador();
        nuevoComprador.setDni(campo1.getText().trim());
        nuevoComprador.setNombre(campo2.getText().trim());
        nuevoComprador.setFechaNac(fechaNac);
        nuevoComprador.setPassword(campo4.getText().trim());
        nuevoComprador.setMedioPago(comboMedioPago.getSelectedItem().toString());
        nuevoComprador.setActivo(true);
        compradorData.guardarComprador(nuevoComprador);
        
        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        limpiarCampos();
        cargarTabla();
    }//GEN-LAST:event_buttonAggActionPerformed

    private void buttonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEliminarActionPerformed
        if (compradorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla",
                    "Ningún usuario seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // VALIDACION:
        if (compradorData.tieneTicketsAsociados(compradorSeleccionado.getDni())) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar el usuario porque tiene tickets de compra asociados.\n\n" +
                    "Este usuario tiene compras registradas y no puede ser eliminado\n" +
                    "para mantener la integridad de los datos.\n\n" +
                    "Si desea desactivarlo, utilice el botón 'Dar de baja'.",
                    "Eliminación no permitida",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿ESTÁ SEGURO de ELIMINAR al usuario:\n" +
                compradorSeleccionado.getNombre() + " (DNI: " + compradorSeleccionado.getDni() + ")?\n\n" +
                "ADVERTENCIA: Esta operación es IRREVERSIBLE.\n\n" +
                "Esta acción solo debe realizarse si:\n" +
                "• No hay tickets de compra asociados (ya verificado)\n" +
                "• El usuario fue creado por error\n",
                "CONFIRMAR ELIMINACIÓN",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            compradorData.borrarComprador(compradorSeleccionado.getDni());
            JOptionPane.showMessageDialog(this, 
                    "Usuario eliminado permanentemente de la base de datos.",
                    "Eliminación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTabla();
        }
    }//GEN-LAST:event_buttonEliminarActionPerformed

    private void buttonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBuscarActionPerformed
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del usuario a buscar:", "Buscar Usuario", JOptionPane.QUESTION_MESSAGE);
        if (dni != null && !dni.trim().isEmpty()) {
            modelo.Comprador comprador = compradorData.buscarComprador(dni.trim());
            if (comprador != null) {
                compradorSeleccionado = comprador;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                campo1.setText(comprador.getDni());
                campo2.setText(comprador.getNombre());
                java.time.LocalDate localDate = comprador.getFechaNac();
                java.util.Date date = java.sql.Date.valueOf(localDate);
                dateChooserFechaNac.setDate(date);
                campo4.setText(comprador.getPassword());
                comboMedioPago.setSelectedItem(comprador.getMedioPago());
                
                campo1.setEditable(false);
                campo1.setBackground(new java.awt.Color(240, 240, 240));    
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    if (modeloTabla.getValueAt(i, 0).equals(dni.trim())) {
                        tableDatos.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,"No se encontró usuario con DNI: "+ dni, "No encontrado",JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_buttonBuscarActionPerformed

    private void comboMedioPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMedioPagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_comboMedioPagoActionPerformed

    private void buttonDarBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDarBajaActionPerformed
        if (compradorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla",
                    "Ningún usuario seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de dar de baja al usuario:\n" +
                compradorSeleccionado.getNombre() + " (DNI: " + compradorSeleccionado.getDni() + ")?\n\n" +
                "Esta operación cambiará el estado a inactivo.",
                "Confirmar baja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            compradorData.bajaComprador(compradorSeleccionado.getDni());
            JOptionPane.showMessageDialog(this, "Usuario dado de baja correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            cargarTabla();
        }
    }//GEN-LAST:event_buttonDarBajaActionPerformed

    private void buttonEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditarActionPerformed
        if (compradorSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario de la tabla",
                    "Ningún usuario seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) {
            return;
        }
        
        if (!campo1.getText().trim().equals(compradorSeleccionado.getDni())) {
            JOptionPane.showMessageDialog(this, 
                    "No se puede modificar el DNI del usuario.\n\n" +
                    "Si necesita cambiar el DNI, debe crear un nuevo usuario.",
                    "DNI no modificable",
                    JOptionPane.ERROR_MESSAGE);
            campo1.setText(compradorSeleccionado.getDni());
            return;
        }

        try {
            java.util.Date fechaUtil = dateChooserFechaNac.getDate();
            java.time.LocalDate fechaNac = fechaUtil.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            compradorSeleccionado.setNombre(campo2.getText().trim());
            compradorSeleccionado.setFechaNac(fechaNac);
            compradorSeleccionado.setPassword(campo4.getText().trim());
            compradorSeleccionado.setMedioPago(comboMedioPago.getSelectedItem().toString());

            compradorData.actualizarComprador(compradorSeleccionado);

            limpiarCampos();
            cargarTabla();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_buttonEditarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAgg;
    private javax.swing.JButton buttonBuscar;
    private javax.swing.JButton buttonDarBaja;
    private javax.swing.JButton buttonEditar;
    private javax.swing.JButton buttonEliminar;
    private javax.swing.JTextField campo1;
    private javax.swing.JTextField campo2;
    private javax.swing.JTextField campo4;
    private javax.swing.JComboBox<String> comboMedioPago;
    private javax.swing.JLabel contrasenia;
    private com.toedter.calendar.JDateChooser dateChooserFechaNac;
    private javax.swing.JLabel dni;
    private javax.swing.JLabel fechaNac;
    private javax.swing.JLabel label;
    private javax.swing.JLabel medioPago;
    private javax.swing.JLabel nombre;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelLabel;
    private javax.swing.JPanel panelTable;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tableDatos;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
