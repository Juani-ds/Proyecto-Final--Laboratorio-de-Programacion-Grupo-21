package vista;

import persistencia.SalaData;
import modelo.Sala;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Neri
 */
public class Salas extends javax.swing.JInternalFrame {

    private SalaData salaData;
    private DefaultTableModel modeloTabla;
    private int salaSeleccionadaId = -1;
    private boolean modoEdicion = false;

    public Salas() {
        initComponents();
        salaData = new SalaData();
        configurarTabla();
        configurarEventos();
        cargarTabla();
        habilitarCampos(false);
    }

    private void configurarTabla() {
        String[] columnas = {"Nro Sala", "Tipo", "Capacidad", "Estado", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ID.setModel(modeloTabla);

        ID.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && ID.getSelectedRow() != -1) {
                mostrarDetallesSala();
            }
        });
    }

    private void configurarEventos() {
        // Los eventos de los botones ya están conectados en el .form
        // Solo agregamos eventos programáticos adicionales aquí

        // Búsqueda en tiempo real
        textBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarSalas();
            }
        });

        // Filtro de tipo
        comboTipo.addActionListener(e -> buscarSalas());

        // Filtro de estado
        comboEstado.addActionListener(e -> buscarSalas());
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Sala> salas = salaData.listarSalas();

        for (Sala sala : salas) {
            Object[] fila = {
                sala.getNroSala(),
                sala.isApta3D() ? "3D" : "2D",
                sala.getCapacidad(),
                sala.getEstado(),
                sala.isActivo() ? "Activo" : "Inactivo"
            };
            modeloTabla.addRow(fila);
        }

        labelResultados.setText("Mostrando " + salas.size() + " sala(s)");
    }

    private void buscarSalas() {
        modeloTabla.setRowCount(0);
        List<Sala> salas = salaData.listarSalas();

        // Aplicar filtros
        String textoBusqueda = textBuscar.getText().toLowerCase().trim();
        String tipoSeleccionado = (String) comboTipo.getSelectedItem();
        String estadoSeleccionado = (String) comboEstado.getSelectedItem();

        // Filtrar por texto de búsqueda (busca en número de sala)
        if (!textoBusqueda.isEmpty()) {
            salas = salas.stream()
                .filter(s -> String.valueOf(s.getNroSala()).contains(textoBusqueda))
                .collect(Collectors.toList());
        }

        // Filtrar por tipo
        if (!tipoSeleccionado.equals("Todos")) {
            boolean es3D = tipoSeleccionado.equals("3D");
            salas = salas.stream()
                .filter(s -> s.isApta3D() == es3D)
                .collect(Collectors.toList());
        }

        // Filtrar por estado
        if (!estadoSeleccionado.equals("Todos")) {
            salas = salas.stream()
                .filter(s -> s.getEstado().equalsIgnoreCase(estadoSeleccionado))
                .collect(Collectors.toList());
        }

        // Llenar tabla con resultados filtrados
        for (Sala sala : salas) {
            Object[] fila = {
                sala.getNroSala(),
                sala.isApta3D() ? "3D" : "2D",
                sala.getCapacidad(),
                sala.getEstado(),
                sala.isActivo() ? "Activo" : "Inactivo"
            };
            modeloTabla.addRow(fila);
        }

        labelResultados.setText("Mostrando " + salas.size() + " sala(s)");
    }

    private void limpiarFiltros() {
        textBuscar.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        cargarTabla();
    }

    private void mostrarDetallesSala() {
        int fila = ID.getSelectedRow();
        if (fila != -1) {
            salaSeleccionadaId = (int) modeloTabla.getValueAt(fila, 0);
            Sala sala = salaData.buscarSala(salaSeleccionadaId);

            if (sala != null) {
                campo1.setText(String.valueOf(sala.getNroSala()));
                campo2.setText(String.valueOf(sala.getCapacidad()));
                comboTipoForm.setSelectedItem(sala.isApta3D() ? "3D" : "2D");
                comboEstadoForm.setSelectedItem(sala.getEstado());
                checkActivo.setSelected(sala.isActivo());
            }
        }
    }

    private void nuevaSala() {
        limpiarCampos();
        habilitarCampos(true);
        modoEdicion = false;
        salaSeleccionadaId = -1;
        campo1.setEnabled(false); // El nro de sala es autoincremental
        buttonGuardar.setEnabled(true);
    }

    private void editarSala() {
        if (salaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una sala para editar");
            return;
        }

        habilitarCampos(true);
        campo1.setEnabled(false); // No se puede editar el nro de sala
        modoEdicion = true;
        buttonGuardar.setEnabled(true);
    }

    private void guardarSala() {
        try {
            // Validaciones
            if (campo2.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese la capacidad");
                return;
            }

            int capacidad = Integer.parseInt(campo2.getText().trim());

            if (capacidad < 170 || capacidad > 230) {
                JOptionPane.showMessageDialog(this,
                    "La capacidad debe estar entre 170 y 230 butacas",
                    "Capacidad inválida",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String tipoTexto = (String) comboTipoForm.getSelectedItem();
            boolean apta3D = tipoTexto.equals("3D");
            String estado = (String) comboEstadoForm.getSelectedItem();
            boolean activo = checkActivo.isSelected();

            if (modoEdicion) {
                // Actualizar sala existente
                Sala sala = new Sala(salaSeleccionadaId, apta3D, capacidad, estado, activo);
                salaData.actualizarSala(sala);
                JOptionPane.showMessageDialog(this, "Sala actualizada correctamente");
            } else {
                // Insertar nueva sala
                Sala sala = new Sala(0, apta3D, capacidad, estado, activo);
                salaData.insertarSala(sala);
                JOptionPane.showMessageDialog(this, "Sala creada correctamente");
            }

            cargarTabla();
            limpiarCampos();
            habilitarCampos(false);
            buttonGuardar.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser un número válido");
        }
    }

    private void eliminarSala() {
        if (salaSeleccionadaId == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una sala para eliminar");
            return;
        }

        // Verificar si la sala tiene proyecciones relacionadas
        if (salaData.tieneProyeccionesRelacionadas(salaSeleccionadaId)) {
            int cantidadProyecciones = salaData.contarProyeccionesRelacionadas(salaSeleccionadaId);

            JOptionPane.showMessageDialog(this,
                "No se puede eliminar la sala " + salaSeleccionadaId + ".\n\n" +
                "Motivo: La sala tiene " + cantidadProyecciones + " proyección(es) relacionada(s).\n" +
                "Debe eliminar primero todas las proyecciones asociadas a esta sala.",
                "No se puede eliminar",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si no tiene relaciones, confirmar eliminación física
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de que desea eliminar permanentemente la sala " + salaSeleccionadaId + "?\n\n" +
            "Esta acción NO se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                salaData.eliminarSala(salaSeleccionadaId);
                JOptionPane.showMessageDialog(this,
                    "Sala eliminada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

                cargarTabla();
                limpiarCampos();
                salaSeleccionadaId = -1;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar la sala: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarCampos() {
        campo1.setText("");
        campo2.setText("");
        comboTipoForm.setSelectedIndex(0);
        comboEstadoForm.setSelectedIndex(0);
        checkActivo.setSelected(true);
        salaSeleccionadaId = -1;
        modoEdicion = false;
        habilitarCampos(false);
        buttonGuardar.setEnabled(false);
    }

    private void habilitarCampos(boolean habilitar) {
        campo1.setEnabled(habilitar);
        campo2.setEnabled(habilitar);
        comboTipoForm.setEnabled(habilitar);
        comboEstadoForm.setEnabled(habilitar);
        checkActivo.setEnabled(habilitar);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneltitulo = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        panelFiltros = new javax.swing.JPanel();
        labelBuscar = new javax.swing.JLabel();
        textBuscar = new javax.swing.JTextField();
        labelTipo = new javax.swing.JLabel();
        comboTipo = new javax.swing.JComboBox<>();
        labelEstado = new javax.swing.JLabel();
        comboEstado = new javax.swing.JComboBox<>();
        buttonLimpiarFiltros = new javax.swing.JButton();
        labelResultados = new javax.swing.JLabel();
        tablebutton = new javax.swing.JPanel();
        buttonEdit = new javax.swing.JButton();
        buttonEliminar = new javax.swing.JButton();
        buttonAgg = new javax.swing.JButton();
        scroll = new javax.swing.JScrollPane();
        ID = new javax.swing.JTable();
        panelbutton = new javax.swing.JPanel();
        buttonGuardar = new javax.swing.JButton();
        buttonlimpiar = new javax.swing.JButton();
        panelInfo = new javax.swing.JPanel();
        titleDetalles = new javax.swing.JLabel();
        labelNombre = new javax.swing.JLabel();
        labelCapacidad = new javax.swing.JLabel();
        labelTipoForm = new javax.swing.JLabel();
        labelEstadoForm = new javax.swing.JLabel();
        campo1 = new javax.swing.JTextField();
        campo2 = new javax.swing.JTextField();
        comboTipoForm = new javax.swing.JComboBox<>();
        comboEstadoForm = new javax.swing.JComboBox<>();
        checkActivo = new javax.swing.JCheckBox();

        paneltitulo.setBackground(new java.awt.Color(51, 90, 144));
        paneltitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        title.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Gestion de Salas");

        javax.swing.GroupLayout paneltituloLayout = new javax.swing.GroupLayout(paneltitulo);
        paneltitulo.setLayout(paneltituloLayout);
        paneltituloLayout.setHorizontalGroup(
            paneltituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltituloLayout.createSequentialGroup()
                .addGap(300, 300, 300)
                .addComponent(title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        paneltituloLayout.setVerticalGroup(
            paneltituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltituloLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(title)
                .addContainerGap())
        );

        panelFiltros.setBackground(new java.awt.Color(240, 240, 240));
        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtros de Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        labelBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelBuscar.setText("Buscar Nro Sala:");

        labelTipo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelTipo.setText("Tipo:");

        comboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "2D", "3D" }));

        labelEstado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelEstado.setText("Estado:");

        comboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Disponible", "En uso", "Mantenimiento" }));

        buttonLimpiarFiltros.setText("Limpiar");

        labelResultados.setFont(new java.awt.Font("Segoe UI", 2, 11)); // NOI18N
        labelResultados.setForeground(new java.awt.Color(102, 102, 102));
        labelResultados.setText("Mostrando 0 sala(s)");

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelEstado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(buttonLimpiarFiltros)
                .addGap(18, 18, 18)
                .addComponent(labelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(66, 66, 66))
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBuscar)
                    .addComponent(textBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTipo)
                    .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelEstado)
                    .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonLimpiarFiltros)
                    .addComponent(labelResultados))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablebutton.setBackground(java.awt.SystemColor.controlHighlight);

        buttonEdit.setBackground(new java.awt.Color(0, 51, 255));
        buttonEdit.setForeground(new java.awt.Color(255, 255, 255));
        buttonEdit.setText("Editar");
        buttonEdit.setBorderPainted(false);
        buttonEdit.setOpaque(true);
        buttonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditActionPerformed(evt);
            }
        });

        buttonEliminar.setBackground(new java.awt.Color(255, 51, 51));
        buttonEliminar.setForeground(new java.awt.Color(255, 255, 255));
        buttonEliminar.setText("Eliminar");
        buttonEliminar.setBorderPainted(false);
        buttonEliminar.setOpaque(true);
        buttonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEliminarActionPerformed(evt);
            }
        });

        buttonAgg.setBackground(new java.awt.Color(51, 204, 0));
        buttonAgg.setForeground(new java.awt.Color(255, 255, 255));
        buttonAgg.setText("Agregar Sala");
        buttonAgg.setToolTipText("");
        buttonAgg.setBorderPainted(false);
        buttonAgg.setOpaque(true);
        buttonAgg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAggActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tablebuttonLayout = new javax.swing.GroupLayout(tablebutton);
        tablebutton.setLayout(tablebuttonLayout);
        tablebuttonLayout.setHorizontalGroup(
            tablebuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablebuttonLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(buttonAgg)
                .addGap(18, 18, 18)
                .addComponent(buttonEdit)
                .addGap(18, 18, 18)
                .addComponent(buttonEliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tablebuttonLayout.setVerticalGroup(
            tablebuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablebuttonLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(tablebuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonEdit)
                    .addComponent(buttonEliminar)
                    .addComponent(buttonAgg))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        ID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nro Sala", "Tipo", "Capacidad", "Estado", "Activo"
            }
        ));
        scroll.setViewportView(ID);

        buttonGuardar.setBackground(new java.awt.Color(51, 102, 255));
        buttonGuardar.setForeground(new java.awt.Color(255, 255, 255));
        buttonGuardar.setText("Guardar");
        buttonGuardar.setBorderPainted(false);
        buttonGuardar.setOpaque(true);
        buttonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGuardarActionPerformed(evt);
            }
        });

        buttonlimpiar.setBackground(new java.awt.Color(102, 102, 102));
        buttonlimpiar.setForeground(new java.awt.Color(255, 255, 255));
        buttonlimpiar.setText("Limpiar");
        buttonlimpiar.setBorderPainted(false);
        buttonlimpiar.setOpaque(true);
        buttonlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonlimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelbuttonLayout = new javax.swing.GroupLayout(panelbutton);
        panelbutton.setLayout(panelbuttonLayout);
        panelbuttonLayout.setHorizontalGroup(
            panelbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelbuttonLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(buttonGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonlimpiar)
                .addContainerGap())
        );
        panelbuttonLayout.setVerticalGroup(
            panelbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelbuttonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelbuttonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGuardar)
                    .addComponent(buttonlimpiar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelInfo.setBackground(new java.awt.Color(255, 255, 255));
        panelInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Detalles de la Sala", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        titleDetalles.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        titleDetalles.setText("Formulario");

        labelNombre.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNombre.setForeground(new java.awt.Color(102, 102, 102));
        labelNombre.setText("Nro Sala:");

        labelCapacidad.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelCapacidad.setForeground(new java.awt.Color(102, 102, 102));
        labelCapacidad.setText("Capacidad:");

        labelTipoForm.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelTipoForm.setForeground(new java.awt.Color(102, 102, 102));
        labelTipoForm.setText("Tipo:");

        labelEstadoForm.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelEstadoForm.setForeground(new java.awt.Color(102, 102, 102));
        labelEstadoForm.setText("Estado:");

        comboTipoForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2D", "3D" }));

        comboEstadoForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Disponible", "En uso", "Mantenimiento" }));

        checkActivo.setSelected(true);
        checkActivo.setText("Activo");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleDetalles)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(checkActivo)
                            .addGroup(panelInfoLayout.createSequentialGroup()
                                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelNombre)
                                    .addComponent(labelCapacidad)
                                    .addComponent(labelTipoForm)
                                    .addComponent(labelEstadoForm))
                                .addGap(18, 18, 18)
                                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(campo1)
                                    .addComponent(campo2)
                                    .addComponent(comboTipoForm, 0, 150, Short.MAX_VALUE)
                                    .addComponent(comboEstadoForm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleDetalles)
                .addGap(18, 18, 18)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelNombre)
                    .addComponent(campo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCapacidad)
                    .addComponent(campo2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTipoForm)
                    .addComponent(comboTipoForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelEstadoForm)
                    .addComponent(comboEstadoForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(checkActivo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tablebutton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelbutton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(paneltitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablebutton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelbutton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditActionPerformed
        editarSala();
    }//GEN-LAST:event_buttonEditActionPerformed

    private void buttonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEliminarActionPerformed
        eliminarSala();
    }//GEN-LAST:event_buttonEliminarActionPerformed

    private void buttonAggActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAggActionPerformed
        nuevaSala();
    }//GEN-LAST:event_buttonAggActionPerformed

    private void buttonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGuardarActionPerformed
        guardarSala();
    }//GEN-LAST:event_buttonGuardarActionPerformed

    private void buttonlimpiarActionPerformed(java.awt.event.ActionEvent evt) {                                             
        limpiarCampos();
    }                                             


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ID;
    private javax.swing.JButton buttonAgg;
    private javax.swing.JButton buttonEdit;
    private javax.swing.JButton buttonEliminar;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JButton buttonLimpiarFiltros;
    private javax.swing.JButton buttonlimpiar;
    private javax.swing.JTextField campo1;
    private javax.swing.JTextField campo2;
    private javax.swing.JCheckBox checkActivo;
    private javax.swing.JComboBox<String> comboEstado;
    private javax.swing.JComboBox<String> comboEstadoForm;
    private javax.swing.JComboBox<String> comboTipo;
    private javax.swing.JComboBox<String> comboTipoForm;
    private javax.swing.JLabel labelBuscar;
    private javax.swing.JLabel labelCapacidad;
    private javax.swing.JLabel labelEstado;
    private javax.swing.JLabel labelEstadoForm;
    private javax.swing.JLabel labelNombre;
    private javax.swing.JLabel labelResultados;
    private javax.swing.JLabel labelTipo;
    private javax.swing.JLabel labelTipoForm;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelbutton;
    private javax.swing.JPanel paneltitulo;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JPanel tablebutton;
    private javax.swing.JTextField textBuscar;
    private javax.swing.JLabel title;
    private javax.swing.JLabel titleDetalles;
    // End of variables declaration//GEN-END:variables
}
