package vista;

import modelo.Pelicula;
import modelo.Proyeccion;
import modelo.Sala;
import persistencia.PeliculaData;
import persistencia.ProyeccionData;
import persistencia.SalaData;
import persistencia.LugarData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;

/**
 * Vista completa de gestión de funciones/proyecciones
 * @author Grupo 21
 */
public class Funcion extends javax.swing.JInternalFrame {

    private ProyeccionData proyeccionData;
    private PeliculaData peliculaData;
    private SalaData salaData;
    private LugarData lugarData;
    private DefaultTableModel modeloTabla;
    private Proyeccion proyeccionSeleccionada;

    public Funcion() {
        initComponents();
        inicializarDatos();
        cargarComboBoxes();
        configurarTabla();
        cargarTodasLasFunciones();
    }

    private void inicializarDatos() {
        proyeccionData = new ProyeccionData();
        peliculaData = new PeliculaData();
        salaData = new SalaData();
        lugarData = new LugarData();
        proyeccionSeleccionada = null;
    }

    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Object[][]{},
            new String[]{"ID", "Película", "Sala", "Idioma", "3D", "Subtitulada", "Inicio", "Fin", "Precio", "Lugares"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFunciones.setModel(modeloTabla);

        tablaFunciones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaFunciones.getSelectedRow() != -1) {
                seleccionarFuncion();
            }
        });
    }

    private void cargarComboBoxes() {
        // Cargar películas en cartelera
        comboPeli.removeAllItems();
        comboPeli.addItem("-- Todas las películas --");
        List<Pelicula> peliculas = peliculaData.listarPeliculasEnCartelera();
        for (Pelicula p : peliculas) {
            comboPeli.addItem(p);
        }

        // Cargar idiomas disponibles
        comboIdiom.removeAllItems();
        comboIdiom.addItem("-- Todos los idiomas --");
        List<String> idiomas = proyeccionData.obtenerIdiomasDisponibles();
        for (String idioma : idiomas) {
            comboIdiom.addItem(idioma);
        }

        // Cargar salas para el formulario
        comboSala.removeAllItems();
        List<Sala> salas = salaData.listarSalas();
        for (Sala s : salas) {
            if (s.isActivo()) {
                comboSala.addItem(s);
            }
        }

        // Cargar películas para el formulario
        comboPeliculaForm.removeAllItems();
        for (Pelicula p : peliculas) {
            comboPeliculaForm.addItem(p);
        }
    }

    private void cargarTodasLasFunciones() {
        limpiarTabla();
        List<Proyeccion> proyecciones = proyeccionData.listarProyeccionesActivas();
        cargarProyeccionesEnTabla(proyecciones);
    }

    private void cargarProyeccionesEnTabla(List<Proyeccion> proyecciones) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Proyeccion p : proyecciones) {
            int lugaresDisponibles = contarLugaresDisponibles(p.getIdProyeccion());

            modeloTabla.addRow(new Object[]{
                p.getIdProyeccion(),
                p.getPelicula().getTitulo(),
                "Sala " + p.getSala().getNroSala(),
                p.getIdioma(),
                p.isEs3D() ? "Sí" : "No",
                p.isSubtitulada() ? "Sí" : "No",
                p.getHoraInicio().format(formatter),
                p.getHoraFin().format(formatter),
                String.format("$%.2f", p.getPrecioLugar()),
                lugaresDisponibles
            });
        }
    }

    private int contarLugaresDisponibles(int idProyeccion) {
        try {
            return (int) lugarData.listarLugaresPorProyeccion(idProyeccion)
                    .stream()
                    .filter(l -> "Disponible".equalsIgnoreCase(l.getEstado()))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private void limpiarTabla() {
        while (modeloTabla.getRowCount() > 0) {
            modeloTabla.removeRow(0);
        }
    }

    private void buscarFunciones() {
        limpiarTabla();

        Integer idPelicula = null;
        String idioma = null;
        Boolean es3D = null;
        Boolean subtitulada = null;

        if (comboPeli.getSelectedIndex() > 0) {
            Pelicula p = (Pelicula) comboPeli.getSelectedItem();
            idPelicula = p.getIdPelicula();
        }

        if (comboIdiom.getSelectedIndex() > 0) {
            idioma = comboIdiom.getSelectedItem().toString();
        }

        if (check3D.isSelected()) {
            es3D = true;
        }

        if (checkSub.isSelected()) {
            subtitulada = true;
        }

        List<Proyeccion> proyecciones = proyeccionData.buscarConFiltros(idPelicula, idioma, es3D, subtitulada);
        cargarProyeccionesEnTabla(proyecciones);

        if (proyecciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron funciones con los filtros seleccionados.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void seleccionarFuncion() {
        int fila = tablaFunciones.getSelectedRow();
        if (fila != -1) {
            int idProyeccion = (int) modeloTabla.getValueAt(fila, 0);
            proyeccionSeleccionada = proyeccionData.buscarProyeccion(idProyeccion);
            cargarDatosEnFormulario();
        }
    }

    private void cargarDatosEnFormulario() {
        if (proyeccionSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            labelInfoPelicula.setText(proyeccionSeleccionada.getPelicula().getTitulo());
            labelInfoSala.setText("Sala " + proyeccionSeleccionada.getSala().getNroSala());
            labelInfoIdioma.setText(proyeccionSeleccionada.getIdioma());
            labelInfoInicio.setText(proyeccionSeleccionada.getHoraInicio().format(formatter));
            labelInfoFin.setText(proyeccionSeleccionada.getHoraFin().format(formatter));
            labelInfoPrecio.setText(String.format("$%.2f", proyeccionSeleccionada.getPrecioLugar()));
            labelInfo3D.setText(proyeccionSeleccionada.isEs3D() ? "Sí" : "No");
            labelInfoSub.setText(proyeccionSeleccionada.isSubtitulada() ? "Sí" : "No");

            // Cargar datos en formulario de edición
            for (int i = 0; i < comboPeliculaForm.getItemCount(); i++) {
                Pelicula p = (Pelicula) comboPeliculaForm.getItemAt(i);
                if (p.getIdPelicula() == proyeccionSeleccionada.getPelicula().getIdPelicula()) {
                    comboPeliculaForm.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < comboSala.getItemCount(); i++) {
                Sala s = (Sala) comboSala.getItemAt(i);
                if (s.getNroSala() == proyeccionSeleccionada.getSala().getNroSala()) {
                    comboSala.setSelectedIndex(i);
                    break;
                }
            }

            txtIdioma.setText(proyeccionSeleccionada.getIdioma());
            txtPrecio.setText(String.valueOf(proyeccionSeleccionada.getPrecioLugar()));
            check3DForm.setSelected(proyeccionSeleccionada.isEs3D());
            checkSubForm.setSelected(proyeccionSeleccionada.isSubtitulada());

            // Configurar fecha
            Date fecha = Date.from(proyeccionSeleccionada.getHoraInicio().atZone(ZoneId.systemDefault()).toInstant());
            dateChooserFecha.setDate(fecha);

            // Configurar hora de inicio
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(fecha);
            spinnerHoraInicio.setValue(calInicio.getTime());

            // Configurar hora de fin
            Date fechaFin = Date.from(proyeccionSeleccionada.getHoraFin().atZone(ZoneId.systemDefault()).toInstant());
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(fechaFin);
            spinnerHoraFin.setValue(calFin.getTime());
        }
    }

    private void limpiarFormulario() {
        if (comboPeliculaForm.getItemCount() > 0) comboPeliculaForm.setSelectedIndex(0);
        if (comboSala.getItemCount() > 0) comboSala.setSelectedIndex(0);
        txtIdioma.setText("");
        txtPrecio.setText("");
        dateChooserFecha.setDate(null);

        // Resetear spinners a hora actual
        Calendar cal = Calendar.getInstance();
        spinnerHoraInicio.setValue(cal.getTime());
        spinnerHoraFin.setValue(cal.getTime());

        check3DForm.setSelected(false);
        checkSubForm.setSelected(false);
        proyeccionSeleccionada = null;
        tablaFunciones.clearSelection();
        limpiarInfoPanel();
    }

    private void limpiarInfoPanel() {
        labelInfoPelicula.setText("---");
        labelInfoSala.setText("---");
        labelInfoIdioma.setText("---");
        labelInfoInicio.setText("---");
        labelInfoFin.setText("---");
        labelInfoPrecio.setText("---");
        labelInfo3D.setText("---");
        labelInfoSub.setText("---");
    }

    private void guardarFuncion() {
        try {
            if (!validarCampos()) {
                return;
            }

            Pelicula pelicula = (Pelicula) comboPeliculaForm.getSelectedItem();
            Sala sala = (Sala) comboSala.getSelectedItem();
            String idioma = txtIdioma.getText().trim();
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            boolean es3D = check3DForm.isSelected();
            boolean subtitulada = checkSubForm.isSelected();

            // Obtener fecha seleccionada
            Date fechaSeleccionada = dateChooserFecha.getDate();
            Calendar calFecha = Calendar.getInstance();
            calFecha.setTime(fechaSeleccionada);

            // Obtener hora de inicio
            Date horaInicioDate = (Date) spinnerHoraInicio.getValue();
            Calendar calInicio = Calendar.getInstance();
            calInicio.setTime(horaInicioDate);

            // Combinar fecha con hora de inicio
            calFecha.set(Calendar.HOUR_OF_DAY, calInicio.get(Calendar.HOUR_OF_DAY));
            calFecha.set(Calendar.MINUTE, calInicio.get(Calendar.MINUTE));
            calFecha.set(Calendar.SECOND, 0);
            LocalDateTime horaInicio = LocalDateTime.ofInstant(calFecha.toInstant(), ZoneId.systemDefault());

            // Obtener hora de fin
            Date horaFinDate = (Date) spinnerHoraFin.getValue();
            Calendar calFin = Calendar.getInstance();
            calFin.setTime(horaFinDate);

            // Combinar fecha con hora de fin
            Calendar calFechaFin = Calendar.getInstance();
            calFechaFin.setTime(fechaSeleccionada);
            calFechaFin.set(Calendar.HOUR_OF_DAY, calFin.get(Calendar.HOUR_OF_DAY));
            calFechaFin.set(Calendar.MINUTE, calFin.get(Calendar.MINUTE));
            calFechaFin.set(Calendar.SECOND, 0);
            LocalDateTime horaFin = LocalDateTime.ofInstant(calFechaFin.toInstant(), ZoneId.systemDefault());

            // Validar que la hora de fin sea posterior a la de inicio
            if (horaFin.isBefore(horaInicio) || horaFin.isEqual(horaInicio)) {
                JOptionPane.showMessageDialog(this, "La hora de finalización debe ser posterior a la hora de inicio.",
                        "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar que la sala no esté ocupada en ese horario
            Integer idExcluir = proyeccionSeleccionada != null ? proyeccionSeleccionada.getIdProyeccion() : null;
            if (!proyeccionData.verificarDisponibilidadSala(sala.getNroSala(), horaInicio, horaFin, idExcluir)) {
                JOptionPane.showMessageDialog(this, "La sala seleccionada ya está ocupada en ese horario.",
                        "Conflicto de horario", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar sala 3D
            if (es3D && !sala.isApta3D()) {
                JOptionPane.showMessageDialog(this, "La sala seleccionada no es apta para proyecciones 3D.",
                        "Error de validación", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (proyeccionSeleccionada == null) {
                // Crear nueva proyección
                Proyeccion nueva = new Proyeccion(pelicula, sala, idioma, es3D, subtitulada,
                        horaInicio, horaFin, precio, true);
                proyeccionData.guardarProyeccion(nueva);
                JOptionPane.showMessageDialog(this, "Función creada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Actualizar proyección existente
                proyeccionSeleccionada.setPelicula(pelicula);
                proyeccionSeleccionada.setSala(sala);
                proyeccionSeleccionada.setIdioma(idioma);
                proyeccionSeleccionada.setEs3D(es3D);
                proyeccionSeleccionada.setSubtitulada(subtitulada);
                proyeccionSeleccionada.setHoraInicio(horaInicio);
                proyeccionSeleccionada.setHoraFin(horaFin);
                proyeccionSeleccionada.setPrecioLugar(precio);
                proyeccionData.actualizarProyeccion(proyeccionSeleccionada);
                JOptionPane.showMessageDialog(this, "Función actualizada correctamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

            limpiarFormulario();
            cargarComboBoxes(); // Recargar filtros con nuevos idiomas
            cargarTodasLasFunciones();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la función: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (comboPeliculaForm.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una película.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (comboSala.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sala.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtIdioma.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el idioma.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtIdioma.requestFocus();
            return false;
        }
        if (txtPrecio.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el precio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtPrecio.requestFocus();
            return false;
        }
        if (dateChooserFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (spinnerHoraInicio.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar la hora de inicio.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (spinnerHoraFin.getValue() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar la hora de finalización.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void eliminarFuncion() {
        if (proyeccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una función de la tabla.",
                    "Ninguna función seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de dar de baja la función:\n" +
                proyeccionSeleccionada.getPelicula().getTitulo() +
                " - Sala " + proyeccionSeleccionada.getSala().getNroSala() + "?\n\n" +
                "Esta operación cambiará el estado a inactivo pero no eliminará los registros.",
                "Confirmar baja lógica",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            proyeccionData.eliminarProyeccion(proyeccionSeleccionada.getIdProyeccion());
            JOptionPane.showMessageDialog(this, "Función dada de baja correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarComboBoxes();
            cargarTodasLasFunciones();
        }
    }
    
    private void eliminarFuncionFisicamente() {
        if (proyeccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una función de la tabla.",
                    "Ninguna función seleccionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // VALIDACION
        if (proyeccionData.tieneTicketsVendidos(proyeccionSeleccionada.getIdProyeccion())) {
            JOptionPane.showMessageDialog(this,
                    "No se puede eliminar la función porque tiene tickets vendidos.\n\n" +
                    "Esta función tiene ventas registradas y no puede ser eliminada\n" +
                    "para mantener la integridad de los datos.\n\n" +
                    "Si desea desactivarla, utilice el botón 'Dar de baja'.",
                    "Eliminación no permitida",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿ESTÁ SEGURO de ELIMINAR PERMANENTEMENTE la función:\n" +
                proyeccionSeleccionada.getPelicula().getTitulo() +
                " - Sala " + proyeccionSeleccionada.getSala().getNroSala() + "?\n\n" +
                "ADVERTENCIA: Esta operación es IRREVERSIBLE.\n" +
                "Se eliminarán todos los lugares/asientos asociados a esta función.\n\n" +
                "Esta acción solo debe realizarse si:\n" +
                "• No hay tickets vendidos (ya verificado)\n" +
                "• La función fue creada por error\n",
                "CONFIRMAR ELIMINACIÓN PERMANENTE",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exitoso = proyeccionData.eliminarProyeccionFisicamente(proyeccionSeleccionada.getIdProyeccion());

            if (exitoso) {
                JOptionPane.showMessageDialog(this, 
                        "Función eliminada permanentemente de la base de datos.",
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);

                limpiarFormulario();
                cargarComboBoxes();
                cargarTodasLasFunciones();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar la función. Consulte los logs para más detalles.",
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    

    private void limpiarFiltros() {
        comboPeli.setSelectedIndex(0);
        if (comboIdiom.getItemCount() > 0) comboIdiom.setSelectedIndex(0);
        check3D.setSelected(false);
        checkSub.setSelected(false);
        cargarTodasLasFunciones();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelTitulo = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        panelPrincipal = new javax.swing.JPanel();
        panelBusqueda = new javax.swing.JPanel();
        labelBuscar = new javax.swing.JLabel();
        comboPeli = new javax.swing.JComboBox<>();
        comboIdiom = new javax.swing.JComboBox<>();
        checkSub = new javax.swing.JCheckBox();
        check3D = new javax.swing.JCheckBox();
        buttonBuscar = new javax.swing.JButton();
        buttonLimpiarFiltros = new javax.swing.JButton();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaFunciones = new javax.swing.JTable();
        panelInfo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        labelInfoPelicula = new javax.swing.JLabel();
        labelInfoSala = new javax.swing.JLabel();
        labelInfoIdioma = new javax.swing.JLabel();
        labelInfoInicio = new javax.swing.JLabel();
        labelInfoFin = new javax.swing.JLabel();
        labelInfoPrecio = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        labelInfo3D = new javax.swing.JLabel();
        labelInfoSub = new javax.swing.JLabel();
        panelFormulario = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        comboPeliculaForm = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        comboSala = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtIdioma = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtPrecio = new javax.swing.JTextField();
        check3DForm = new javax.swing.JCheckBox();
        checkSubForm = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        spinnerHoraInicio = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        spinnerHoraFin = new javax.swing.JSpinner();
        buttonGuardar = new javax.swing.JButton();
        buttonNuevo = new javax.swing.JButton();
        buttonDarBaja = new javax.swing.JButton();
        buttonEliminar = new javax.swing.JButton();
        dateChooserFecha = new com.toedter.calendar.JDateChooser();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión de Funciones");
        setPreferredSize(new java.awt.Dimension(1400, 700));

        panelTitulo.setBackground(new java.awt.Color(51, 90, 144));
        panelTitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        title.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setText("Gestión de Funciones");

        javax.swing.GroupLayout panelTituloLayout = new javax.swing.GroupLayout(panelTitulo);
        panelTitulo.setLayout(panelTituloLayout);
        panelTituloLayout.setHorizontalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloLayout.setVerticalGroup(
            panelTituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(title)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        panelBusqueda.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filtros de Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        labelBuscar.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        labelBuscar.setText("Buscar Funciones:");

        checkSub.setText("Subtitulada");

        check3D.setText("3D");

        buttonBuscar.setBackground(new java.awt.Color(51, 90, 144));
        buttonBuscar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonBuscar.setForeground(new java.awt.Color(255, 255, 255));
        buttonBuscar.setText("Buscar");
        buttonBuscar.setBorderPainted(false);
        buttonBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBuscarActionPerformed(evt);
            }
        });

        buttonLimpiarFiltros.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        buttonLimpiarFiltros.setText("Limpiar Filtros");
        buttonLimpiarFiltros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLimpiarFiltrosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBusquedaLayout = new javax.swing.GroupLayout(panelBusqueda);
        panelBusqueda.setLayout(panelBusquedaLayout);
        panelBusquedaLayout.setHorizontalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(comboPeli, 0, 188, Short.MAX_VALUE)
                    .addComponent(comboIdiom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonLimpiarFiltros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelBusquedaLayout.createSequentialGroup()
                        .addGroup(panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelBuscar)
                            .addComponent(checkSub)
                            .addComponent(check3D))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelBusquedaLayout.setVerticalGroup(
            panelBusquedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBusquedaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboPeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(comboIdiom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkSub)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(check3D)
                .addGap(18, 18, 18)
                .addComponent(buttonBuscar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonLimpiarFiltros)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelTabla.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Funciones Disponibles", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tablaFunciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Película", "Sala", "Idioma", "3D", "Subtitulada", "Inicio", "Fin", "Precio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaFunciones);

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelInfo.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Información de Función Seleccionada", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Película:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Sala:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Idioma:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Inicio:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Fin:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Precio:");

        labelInfoPelicula.setText("---");

        labelInfoSala.setText("---");

        labelInfoIdioma.setText("---");

        labelInfoInicio.setText("---");

        labelInfoFin.setText("---");

        labelInfoPrecio.setText("---");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("3D:");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setText("Subtitulada:");

        labelInfo3D.setText("---");

        labelInfoSub.setText("---");

        javax.swing.GroupLayout panelInfoLayout = new javax.swing.GroupLayout(panelInfo);
        panelInfo.setLayout(panelInfoLayout);
        panelInfoLayout.setHorizontalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoPelicula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoSala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoIdioma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoFin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoPrecio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfo3D, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelInfoLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelInfoSub, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelInfoLayout.setVerticalGroup(
            panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(labelInfoPelicula))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(labelInfoSala))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(labelInfoIdioma))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(labelInfoInicio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(labelInfoFin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(labelInfoPrecio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(labelInfo3D))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(labelInfoSub))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelFormulario.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Crear/Editar Función", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel7.setText("Película:");

        jLabel8.setText("Sala:");

        jLabel9.setText("Idioma:");

        jLabel10.setText("Precio:");

        check3DForm.setText("3D");

        checkSubForm.setText("Subtitulada");

        jLabel11.setText("Fecha:");

        jLabel12.setText("Hora Inicio:");

        spinnerHoraInicio.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1763500158243L), null, null, java.util.Calendar.HOUR_OF_DAY));

        jLabel15.setText("Hora Fin:");

        spinnerHoraFin.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1763500125997L), null, null, java.util.Calendar.HOUR_OF_DAY));

        buttonGuardar.setBackground(new java.awt.Color(46, 125, 50));
        buttonGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonGuardar.setForeground(new java.awt.Color(255, 255, 255));
        buttonGuardar.setText("Guardar");
        buttonGuardar.setBorderPainted(false);
        buttonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGuardarActionPerformed(evt);
            }
        });

        buttonNuevo.setBackground(new java.awt.Color(25, 118, 210));
        buttonNuevo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonNuevo.setForeground(new java.awt.Color(255, 255, 255));
        buttonNuevo.setText("Nuevo");
        buttonNuevo.setBorderPainted(false);
        buttonNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNuevoActionPerformed(evt);
            }
        });

        buttonDarBaja.setBackground(new java.awt.Color(211, 47, 47));
        buttonDarBaja.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonDarBaja.setForeground(new java.awt.Color(255, 255, 255));
        buttonDarBaja.setText("Dar de Baja");
        buttonDarBaja.setBorderPainted(false);
        buttonDarBaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDarBajaActionPerformed(evt);
            }
        });

        buttonEliminar.setBackground(new java.awt.Color(153, 0, 51));
        buttonEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        buttonEliminar.setForeground(new java.awt.Color(255, 255, 255));
        buttonEliminar.setText("Eliminar");

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dateChooserFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboPeliculaForm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(comboSala, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtIdioma)
                    .addComponent(txtPrecio)
                    .addComponent(spinnerHoraInicio)
                    .addComponent(spinnerHoraFin)
                    .addGroup(panelFormularioLayout.createSequentialGroup()
                        .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFormularioLayout.createSequentialGroup()
                                .addComponent(buttonGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(buttonNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(buttonDarBaja)
                                .addGap(18, 18, 18)
                                .addComponent(buttonEliminar))
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(check3DForm)
                            .addComponent(checkSubForm)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel15))
                        .addGap(0, 24, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFormularioLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboPeliculaForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(check3DForm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkSubForm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addGap(4, 4, 4)
                .addComponent(dateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinnerHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addGroup(panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGuardar)
                    .addComponent(buttonNuevo)
                    .addComponent(buttonDarBaja)
                    .addComponent(buttonEliminar))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelPrincipalLayout = new javax.swing.GroupLayout(panelPrincipal);
        panelPrincipal.setLayout(panelPrincipalLayout);
        panelPrincipalLayout.setHorizontalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelPrincipalLayout.setVerticalGroup(
            panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFormulario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelPrincipalLayout.createSequentialGroup()
                        .addComponent(panelBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBuscarActionPerformed
        buscarFunciones();
    }//GEN-LAST:event_buttonBuscarActionPerformed

    private void buttonLimpiarFiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLimpiarFiltrosActionPerformed
        limpiarFiltros();
    }//GEN-LAST:event_buttonLimpiarFiltrosActionPerformed

    private void buttonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGuardarActionPerformed
        guardarFuncion();
    }//GEN-LAST:event_buttonGuardarActionPerformed

    private void buttonNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNuevoActionPerformed
        limpiarFormulario();
    }//GEN-LAST:event_buttonNuevoActionPerformed

    private void buttonDarBajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDarBajaActionPerformed
        eliminarFuncion();
    }//GEN-LAST:event_buttonDarBajaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonBuscar;
    private javax.swing.JButton buttonDarBaja;
    private javax.swing.JButton buttonEliminar;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JButton buttonLimpiarFiltros;
    private javax.swing.JButton buttonNuevo;
    private javax.swing.JCheckBox check3D;
    private javax.swing.JCheckBox check3DForm;
    private javax.swing.JCheckBox checkSub;
    private javax.swing.JCheckBox checkSubForm;
    private javax.swing.JComboBox<String> comboIdiom;
    private javax.swing.JComboBox<Object> comboPeli;
    private javax.swing.JComboBox<Object> comboPeliculaForm;
    private javax.swing.JComboBox<Object> comboSala;
    private com.toedter.calendar.JDateChooser dateChooserFecha;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelBuscar;
    private javax.swing.JLabel labelInfo3D;
    private javax.swing.JLabel labelInfoFin;
    private javax.swing.JLabel labelInfoIdioma;
    private javax.swing.JLabel labelInfoInicio;
    private javax.swing.JLabel labelInfoPelicula;
    private javax.swing.JLabel labelInfoPrecio;
    private javax.swing.JLabel labelInfoSala;
    private javax.swing.JLabel labelInfoSub;
    private javax.swing.JPanel panelBusqueda;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelPrincipal;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JPanel panelTitulo;
    private javax.swing.JSpinner spinnerHoraFin;
    private javax.swing.JSpinner spinnerHoraInicio;
    private javax.swing.JTable tablaFunciones;
    private javax.swing.JLabel title;
    private javax.swing.JTextField txtIdioma;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
