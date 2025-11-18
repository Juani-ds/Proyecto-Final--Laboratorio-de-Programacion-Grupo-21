package vista;

import persistencia.PeliculaData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Vista para gestionar películas del sistema
 * @author Neri
 */
public class Pelicula extends javax.swing.JInternalFrame {

    private PeliculaData peliculaData;
    private DefaultTableModel modeloTabla;
    private modelo.Pelicula peliculaSeleccionada = null;
    private boolean modoEdicion = false;

    public Pelicula() {
        initComponents();
        peliculaData = new PeliculaData();
        configurarTabla();
        cargarPeliculas();
        configurarEventos();
        deshabilitarFormulario();
    }

    private void configurarTabla() {
        String[] columnas = {"ID", "Título", "Director", "Género", "Origen", "Estreno", "En Cartelera", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePeliculas.setModel(modeloTabla);
        tablePeliculas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajustar ancho de columnas
        tablePeliculas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablePeliculas.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablePeliculas.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablePeliculas.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablePeliculas.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablePeliculas.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablePeliculas.getColumnModel().getColumn(6).setPreferredWidth(100);
        tablePeliculas.getColumnModel().getColumn(7).setPreferredWidth(80);
    }

    private void configurarEventos() {
        // Evento de selección en la tabla
        tablePeliculas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablePeliculas.getSelectedRow() != -1) {
                cargarPeliculaSeleccionada();
            }
        });

        // Evento para búsqueda en tiempo real
        textBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarPeliculas();
            }
        });

        // Evento para filtro de género
        comboGenero.addActionListener(e -> buscarPeliculas());

        // Eventos de botones
        buttonNuevo.addActionListener(e -> nuevaPelicula());
        buttonEditar.addActionListener(e -> editarPelicula());
        buttonGuardar.addActionListener(e -> guardarPelicula());
        buttonEliminar.addActionListener(e -> eliminarPelicula());
        buttonCancelar.addActionListener(e -> cancelarOperacion());
        buttonLimpiar.addActionListener(e -> limpiarBusqueda());
    }

    private void cargarPeliculas() {
        modeloTabla.setRowCount(0);
        List<modelo.Pelicula> peliculas = peliculaData.listarPeliculas();

        for (modelo.Pelicula p : peliculas) {
            Object[] fila = {
                p.getIdPelicula(),
                p.getTitulo(),
                p.getDirector(),
                p.getGenero(),
                p.getOrigen(),
                p.getEstreno().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                p.isEnCartelera() ? "Sí" : "No",
                p.isActivo() ? "Activa" : "Inactiva"
            };
            modeloTabla.addRow(fila);
        }
    }

    private void buscarPeliculas() {
        String textoBusqueda = textBuscar.getText().trim().toLowerCase();
        String generoSeleccionado = (String) comboGenero.getSelectedItem();

        modeloTabla.setRowCount(0);
        List<modelo.Pelicula> peliculas = peliculaData.listarPeliculas();

        for (modelo.Pelicula p : peliculas) {
            boolean coincideTexto = textoBusqueda.isEmpty() ||
                    p.getTitulo().toLowerCase().contains(textoBusqueda) ||
                    p.getDirector().toLowerCase().contains(textoBusqueda) ||
                    p.getActores().toLowerCase().contains(textoBusqueda);

            boolean coincideGenero = generoSeleccionado.equals("Todos") ||
                    p.getGenero().equalsIgnoreCase(generoSeleccionado);

            if (coincideTexto && coincideGenero) {
                Object[] fila = {
                    p.getIdPelicula(),
                    p.getTitulo(),
                    p.getDirector(),
                    p.getGenero(),
                    p.getOrigen(),
                    p.getEstreno().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    p.isEnCartelera() ? "Sí" : "No",
                    p.isActivo() ? "Activa" : "Inactiva"
                };
                modeloTabla.addRow(fila);
            }
        }

        labelResultados.setText("Resultados: " + modeloTabla.getRowCount() + " película(s)");
    }

    private void cargarPeliculaSeleccionada() {
        int filaSeleccionada = tablePeliculas.getSelectedRow();
        if (filaSeleccionada == -1) return;

        int idPelicula = (int) tablePeliculas.getValueAt(filaSeleccionada, 0);
        peliculaSeleccionada = peliculaData.buscarPeliculaPorId(idPelicula);

        if (peliculaSeleccionada != null) {
            mostrarInfoPelicula();
        }
    }

    private void mostrarInfoPelicula() {
        if (peliculaSeleccionada == null) {
            labelInfoTitulo.setText("Título: -");
            labelInfoDirector.setText("Director: -");
            labelInfoActores.setText("Actores: -");
            labelInfoGenero.setText("Género: -");
            labelInfoOrigen.setText("Origen: -");
            labelInfoEstreno.setText("Estreno: -");
            labelInfoCartelera.setText("En cartelera: -");
            labelInfoEstado.setText("Estado: -");
        } else {
            labelInfoTitulo.setText("Título: " + peliculaSeleccionada.getTitulo());
            labelInfoDirector.setText("Director: " + peliculaSeleccionada.getDirector());
            labelInfoActores.setText("Actores: " + peliculaSeleccionada.getActores());
            labelInfoGenero.setText("Género: " + peliculaSeleccionada.getGenero());
            labelInfoOrigen.setText("Origen: " + peliculaSeleccionada.getOrigen());
            labelInfoEstreno.setText("Estreno: " + peliculaSeleccionada.getEstreno().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            labelInfoCartelera.setText("En cartelera: " + (peliculaSeleccionada.isEnCartelera() ? "Sí" : "No"));
            labelInfoEstado.setText("Estado: " + (peliculaSeleccionada.isActivo() ? "Activa" : "Inactiva"));
        }
    }

    private void nuevaPelicula() {
        modoEdicion = false;
        peliculaSeleccionada = null;
        limpiarFormulario();
        habilitarFormulario();
        textTitulo.requestFocus();
    }

    private void editarPelicula() {
        if (peliculaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una película de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        modoEdicion = true;
        cargarDatosFormulario();
        habilitarFormulario();
        textTitulo.requestFocus();
    }

    private void cargarDatosFormulario() {
        if (peliculaSeleccionada == null) return;

        textTitulo.setText(peliculaSeleccionada.getTitulo());
        textDirector.setText(peliculaSeleccionada.getDirector());
        textAreaActores.setText(peliculaSeleccionada.getActores());
        textOrigen.setText(peliculaSeleccionada.getOrigen());
        comboGeneroForm.setSelectedItem(peliculaSeleccionada.getGenero());
        textEstreno.setText(peliculaSeleccionada.getEstreno().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        checkCartelera.setSelected(peliculaSeleccionada.isEnCartelera());
        checkActivo.setSelected(peliculaSeleccionada.isActivo());
    }

    private void guardarPelicula() {
        if (!validarCampos()) {
            return;
        }

        try {
            String titulo = textTitulo.getText().trim();
            String director = textDirector.getText().trim();
            String actores = textAreaActores.getText().trim();
            String origen = textOrigen.getText().trim();
            String genero = (String) comboGeneroForm.getSelectedItem();
            LocalDate estreno = LocalDate.parse(textEstreno.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            boolean enCartelera = checkCartelera.isSelected();
            boolean activo = checkActivo.isSelected();

            if (modoEdicion && peliculaSeleccionada != null) {
                // Actualizar película existente
                peliculaSeleccionada.setTitulo(titulo);
                peliculaSeleccionada.setDirector(director);
                peliculaSeleccionada.setActores(actores);
                peliculaSeleccionada.setOrigen(origen);
                peliculaSeleccionada.setGenero(genero);
                peliculaSeleccionada.setEstreno(estreno);
                peliculaSeleccionada.setEnCartelera(enCartelera);
                peliculaSeleccionada.setActivo(activo);

                peliculaData.actualizarPelicula(peliculaSeleccionada);
                JOptionPane.showMessageDialog(this,
                    "Película actualizada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Crear nueva película
                modelo.Pelicula nuevaPeli = new modelo.Pelicula();
                nuevaPeli.setTitulo(titulo);
                nuevaPeli.setDirector(director);
                nuevaPeli.setActores(actores);
                nuevaPeli.setOrigen(origen);
                nuevaPeli.setGenero(genero);
                nuevaPeli.setEstreno(estreno);
                nuevaPeli.setEnCartelera(enCartelera);
                nuevaPeli.setActivo(activo);

                peliculaData.insertarPelicula(nuevaPeli);
                JOptionPane.showMessageDialog(this,
                    "Película guardada correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            cargarPeliculas();
            deshabilitarFormulario();
            limpiarFormulario();
            peliculaSeleccionada = null;
            modoEdicion = false;

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Formato de fecha inválido. Use dd/MM/yyyy",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al guardar película: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPelicula() {
        if (peliculaSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor, seleccione una película de la tabla",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea eliminar la película '" + peliculaSeleccionada.getTitulo() + "'?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            peliculaData.cambiarEstadoPelicula(peliculaSeleccionada.getIdPelicula(), false);
            JOptionPane.showMessageDialog(this,
                "Película eliminada correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            cargarPeliculas();
            limpiarFormulario();
            peliculaSeleccionada = null;
            mostrarInfoPelicula();
        }
    }

    private void cancelarOperacion() {
        limpiarFormulario();
        deshabilitarFormulario();
        peliculaSeleccionada = null;
        modoEdicion = false;
        tablePeliculas.clearSelection();
        mostrarInfoPelicula();
    }

    private void limpiarBusqueda() {
        textBuscar.setText("");
        comboGenero.setSelectedIndex(0);
        cargarPeliculas();
        labelResultados.setText("");
    }

    private boolean validarCampos() {
        if (textTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            textTitulo.requestFocus();
            return false;
        }

        if (textDirector.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El director es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            textDirector.requestFocus();
            return false;
        }

        if (textAreaActores.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Los actores son obligatorios", "Validación", JOptionPane.WARNING_MESSAGE);
            textAreaActores.requestFocus();
            return false;
        }

        if (textOrigen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El origen es obligatorio", "Validación", JOptionPane.WARNING_MESSAGE);
            textOrigen.requestFocus();
            return false;
        }

        if (textEstreno.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de estreno es obligatoria", "Validación", JOptionPane.WARNING_MESSAGE);
            textEstreno.requestFocus();
            return false;
        }

        // Validar formato de fecha
        try {
            LocalDate.parse(textEstreno.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Formato de fecha inválido. Use dd/MM/yyyy (ejemplo: 25/12/2024)",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            textEstreno.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        textTitulo.setText("");
        textDirector.setText("");
        textAreaActores.setText("");
        textOrigen.setText("");
        comboGeneroForm.setSelectedIndex(0);
        textEstreno.setText("");
        checkCartelera.setSelected(true);
        checkActivo.setSelected(true);
    }

    private void habilitarFormulario() {
        textTitulo.setEnabled(true);
        textDirector.setEnabled(true);
        textAreaActores.setEnabled(true);
        textOrigen.setEnabled(true);
        comboGeneroForm.setEnabled(true);
        textEstreno.setEnabled(true);
        checkCartelera.setEnabled(true);
        checkActivo.setEnabled(true);
        buttonGuardar.setEnabled(true);
        buttonCancelar.setEnabled(true);
        buttonNuevo.setEnabled(false);
        buttonEditar.setEnabled(false);
        buttonEliminar.setEnabled(false);
    }

    private void deshabilitarFormulario() {
        textTitulo.setEnabled(false);
        textDirector.setEnabled(false);
        textAreaActores.setEnabled(false);
        textOrigen.setEnabled(false);
        comboGeneroForm.setEnabled(false);
        textEstreno.setEnabled(false);
        checkCartelera.setEnabled(false);
        checkActivo.setEnabled(false);
        buttonGuardar.setEnabled(false);
        buttonCancelar.setEnabled(false);
        buttonNuevo.setEnabled(true);
        buttonEditar.setEnabled(true);
        buttonEliminar.setEnabled(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitulo = new javax.swing.JPanel();
        labelTitulo = new javax.swing.JLabel();
        panelBusqueda = new javax.swing.JPanel();
        labelBuscar = new javax.swing.JLabel();
        textBuscar = new javax.swing.JTextField();
        labelGenero = new javax.swing.JLabel();
        comboGenero = new javax.swing.JComboBox<>();
        buttonLimpiar = new javax.swing.JButton();
        labelResultados = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        scrollTabla = new javax.swing.JScrollPane();
        tablePeliculas = new javax.swing.JTable();
        panelInfo = new javax.swing.JPanel();
        labelInfoTitulo = new javax.swing.JLabel();
        labelInfoDirector = new javax.swing.JLabel();
        labelInfoActores = new javax.swing.JLabel();
        labelInfoGenero = new javax.swing.JLabel();
        labelInfoOrigen = new javax.swing.JLabel();
        labelInfoEstreno = new javax.swing.JLabel();
        labelInfoCartelera = new javax.swing.JLabel();
        labelInfoEstado = new javax.swing.JLabel();
        panelFormulario = new javax.swing.JPanel();
        labelFormTitulo = new javax.swing.JLabel();
        textTitulo = new javax.swing.JTextField();
        labelFormDirector = new javax.swing.JLabel();
        textDirector = new javax.swing.JTextField();
        labelFormActores = new javax.swing.JLabel();
        scrollActores = new javax.swing.JScrollPane();
        textAreaActores = new javax.swing.JTextArea();
        labelFormOrigen = new javax.swing.JLabel();
        textOrigen = new javax.swing.JTextField();
        labelFormGenero = new javax.swing.JLabel();
        comboGeneroForm = new javax.swing.JComboBox<>();
        labelFormEstreno = new javax.swing.JLabel();
        textEstreno = new javax.swing.JTextField();
        checkCartelera = new javax.swing.JCheckBox();
        checkActivo = new javax.swing.JCheckBox();
        panelBotones = new javax.swing.JPanel();
        buttonNuevo = new javax.swing.JButton();
        buttonEditar = new javax.swing.JButton();
        buttonGuardar = new javax.swing.JButton();
        buttonEliminar = new javax.swing.JButton();
        buttonCancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión de Películas");

        panelTitulo.setBackground(new java.awt.Color(51, 90, 144));
        panelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        labelTitulo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        labelTitulo.setForeground(new java.awt.Color(255, 255, 255));
        labelTitulo.setText("Gestor de Películas");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addGap(320, 320, 320)
                .addComponent(labelTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTitulo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder("Buscar Películas"));

        labelBuscar.setText("Buscar:");

        labelGenero.setText("Género:");

        comboGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Acción", "Comedia", "Terror", "Romántica", "Drama", "Ciencia Ficción", "Aventura", "Suspenso", "Animación", "Documental" }));
        comboGenero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboGeneroActionPerformed(evt);
            }
        });

        buttonLimpiar.setBackground(new java.awt.Color(102, 102, 102));
        buttonLimpiar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        buttonLimpiar.setText("Limpiar");
        buttonLimpiar.setOpaque(true);
        buttonLimpiar.setBorderPainted(false);

        labelResultados.setFont(new java.awt.Font("Segoe UI", 2, 11)); // NOI18N
        labelResultados.setForeground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelGenero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonLimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(labelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelBusquedaLayout.setVerticalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelBuscar)
                    .addComponent(textBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelGenero)
                    .addComponent(comboGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonLimpiar)
                    .addComponent(labelResultados))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablePeliculas.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        tablePeliculas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Título", "Director", "Género", "Origen", "Estreno", "En Cartelera", "Estado"
            }
        ));
        scrollTabla.setViewportView(tablePeliculas);

        tabbedPane.addTab("Lista de Películas", scrollTabla);

        panelInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Información de la Película"));

        labelInfoTitulo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelInfoTitulo.setText("Título: -");

        labelInfoDirector.setText("Director: -");

        labelInfoActores.setText("Actores: -");

        labelInfoGenero.setText("Género: -");

        labelInfoOrigen.setText("Origen: -");

        labelInfoEstreno.setText("Estreno: -");

        labelInfoCartelera.setText("En cartelera: -");

        labelInfoEstado.setText("Estado: -");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfoTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelInfoDirector, javax.swing.GroupLayout.DEFAULT_SIZE, 854, Short.MAX_VALUE)
                    .addComponent(labelInfoActores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelInfoGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelInfoOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelInfoEstreno, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelInfoCartelera, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(labelInfoEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDirector)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoActores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoGenero)
                    .addComponent(labelInfoEstreno)
                    .addComponent(labelInfoEstado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelInfoOrigen)
                    .addComponent(labelInfoCartelera))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Información", panelInfo);

        panelFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder("Formulario de Película"));

        labelFormTitulo.setText("Título:");

        labelFormDirector.setText("Director:");

        labelFormActores.setText("Actores:");

        textAreaActores.setColumns(20);
        textAreaActores.setRows(3);
        scrollActores.setViewportView(textAreaActores);

        labelFormOrigen.setText("Origen:");

        labelFormGenero.setText("Género:");

        comboGeneroForm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Acción", "Comedia", "Terror", "Romántica", "Drama", "Ciencia Ficción", "Aventura", "Suspenso", "Animación", "Documental" }));

        labelFormEstreno.setText("Estreno (dd/MM/yyyy):");

        checkCartelera.setText("En cartelera");
        checkCartelera.setSelected(true);

        checkActivo.setText("Activo");
        checkActivo.setSelected(true);

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelFormTitulo)
                            .addComponent(textTitulo)
                            .addComponent(labelFormDirector)
                            .addComponent(textDirector)
                            .addComponent(labelFormActores)
                            .addComponent(scrollActores, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labelFormOrigen)
                            .addComponent(textOrigen)
                            .addComponent(labelFormGenero)
                            .addComponent(comboGeneroForm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelFormEstreno)
                            .addComponent(textEstreno, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)))
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addComponent(checkCartelera)
                        .addGap(18, 18, 18)
                        .addComponent(checkActivo)))
                .addContainerGap(164, Short.MAX_VALUE))
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFormTitulo)
                    .addComponent(labelFormOrigen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFormDirector)
                    .addComponent(labelFormGenero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDirector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(comboGeneroForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelFormActores)
                    .addComponent(labelFormEstreno))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollActores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textEstreno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkCartelera)
                    .addComponent(checkActivo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.addTab("Formulario", panelFormulario);

        panelBotones.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonNuevo.setBackground(new java.awt.Color(46, 125, 50));
        buttonNuevo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonNuevo.setForeground(new java.awt.Color(255, 255, 255));
        buttonNuevo.setText("Nuevo");
        buttonNuevo.setOpaque(true);
        buttonNuevo.setBorderPainted(false);

        buttonEditar.setBackground(new java.awt.Color(21, 101, 192));
        buttonEditar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonEditar.setForeground(new java.awt.Color(255, 255, 255));
        buttonEditar.setText("Editar");
        buttonEditar.setOpaque(true);
        buttonEditar.setBorderPainted(false);

        buttonGuardar.setBackground(new java.awt.Color(51, 90, 144));
        buttonGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonGuardar.setForeground(new java.awt.Color(255, 255, 255));
        buttonGuardar.setText("Guardar");
        buttonGuardar.setOpaque(true);
        buttonGuardar.setBorderPainted(false);

        buttonEliminar.setBackground(new java.awt.Color(211, 47, 47));
        buttonEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonEliminar.setForeground(new java.awt.Color(255, 255, 255));
        buttonEliminar.setText("Eliminar");
        buttonEliminar.setOpaque(true);
        buttonEliminar.setBorderPainted(false);

        buttonCancelar.setBackground(new java.awt.Color(117, 117, 117));
        buttonCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonCancelar.setForeground(new java.awt.Color(255, 255, 255));
        buttonCancelar.setText("Cancelar");
        buttonCancelar.setOpaque(true);
        buttonCancelar.setBorderPainted(false);

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(buttonNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(buttonEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(buttonGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(buttonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(buttonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tabbedPane)
                    .addComponent(panelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comboGeneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboGeneroActionPerformed
        buscarPeliculas();
    }//GEN-LAST:event_comboGeneroActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelar;
    private javax.swing.JButton buttonEditar;
    private javax.swing.JButton buttonEliminar;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JButton buttonLimpiar;
    private javax.swing.JButton buttonNuevo;
    private javax.swing.JCheckBox checkActivo;
    private javax.swing.JCheckBox checkCartelera;
    private javax.swing.JComboBox<String> comboGenero;
    private javax.swing.JComboBox<String> comboGeneroForm;
    private javax.swing.JLabel labelBuscar;
    private javax.swing.JLabel labelFormActores;
    private javax.swing.JLabel labelFormDirector;
    private javax.swing.JLabel labelFormEstreno;
    private javax.swing.JLabel labelFormGenero;
    private javax.swing.JLabel labelFormOrigen;
    private javax.swing.JLabel labelFormTitulo;
    private javax.swing.JLabel labelGenero;
    private javax.swing.JLabel labelInfoActores;
    private javax.swing.JLabel labelInfoCartelera;
    private javax.swing.JLabel labelInfoDirector;
    private javax.swing.JLabel labelInfoEstado;
    private javax.swing.JLabel labelInfoEstreno;
    private javax.swing.JLabel labelInfoGenero;
    private javax.swing.JLabel labelInfoOrigen;
    private javax.swing.JLabel labelInfoTitulo;
    private javax.swing.JLabel labelResultados;
    private javax.swing.JLabel labelTitulo;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JScrollPane scrollActores;
    private javax.swing.JScrollPane scrollTabla;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTable tablePeliculas;
    private javax.swing.JTextArea textAreaActores;
    private javax.swing.JTextField textBuscar;
    private javax.swing.JTextField textDirector;
    private javax.swing.JTextField textEstreno;
    private javax.swing.JTextField textOrigen;
    private javax.swing.JTextField textTitulo;
    // End of variables declaration//GEN-END:variables
}
