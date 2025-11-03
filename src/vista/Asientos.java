/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package vista;

import modelo.Pelicula;
import modelo.Proyeccion;
import modelo.Sala;
import modelo.Lugar;
import persistencia.PeliculaData;
import persistencia.ProyeccionData;
import persistencia.SalaData;
import persistencia.LugarData;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Neri
 */
public class Asientos extends javax.swing.JInternalFrame {

    private PeliculaData peliculaData;
    private SalaData salaData;
    private ProyeccionData proyeccionData;
    private LugarData lugarData;

    private Proyeccion proyeccionSeleccionada;
    private Map<String, JButton> botonesAsientos;
    private JPanel panelAsientosDinamico;

    private JComboBox<String> comboPelicula;
    private JComboBox<String> comboSala;
    private JComboBox<String> comboFuncion;

    /**
     * Creates new form Asientos
     */
    public Asientos() {
        initComponents();

        // Configurar ventana para que sea maximizable y redimensionable
        this.setMaximizable(true);
        this.setResizable(true);
        this.setClosable(true);

        peliculaData = new PeliculaData();
        salaData = new SalaData();
        proyeccionData = new ProyeccionData();
        lugarData = new LugarData();

        proyeccionSeleccionada = null;
        botonesAsientos = new HashMap<>();

        agregarSelectoresProyeccion();
        configurarEventosBotones();

        // Ocultar la imagen del mapa estático
        labelMapa.setVisible(false);
    }

    private void agregarSelectoresProyeccion() {
        // Configurar layout del panelButton para que muestre componentes verticalmente
        panelButton.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.anchor = java.awt.GridBagConstraints.WEST;

        // Fila 0: Botones originales
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panelButton.add(buttonAct, gbc);

        gbc.gridx = 1;
        panelButton.add(buttonLimpiar, gbc);

        gbc.gridx = 2;
        panelButton.add(buttonImprimir, gbc);

        gbc.gridx = 3;
        panelButton.add(buttonVerOcupacion, gbc);

        // Fila 1: Selectores
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        JPanel panelSelectores = new JPanel();
        panelSelectores.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSelectores.setBackground(new Color(240, 240, 240));

        // ComboBox de Películas
        JLabel labelPeliSelector = new JLabel("Película:");
        comboPelicula = new JComboBox<>();
        comboPelicula.setPreferredSize(new Dimension(150, 25));
        comboPelicula.addItem("Seleccione película");
        cargarPeliculas();

        // ComboBox de Salas
        JLabel labelSalaSelector = new JLabel("Sala:");
        comboSala = new JComboBox<>();
        comboSala.setPreferredSize(new Dimension(120, 25));
        comboSala.addItem("Seleccione sala");

        // ComboBox de Funciones
        JLabel labelFuncionSelector = new JLabel("Función:");
        comboFuncion = new JComboBox<>();
        comboFuncion.setPreferredSize(new Dimension(180, 25));
        comboFuncion.addItem("Seleccione función");

        panelSelectores.add(labelPeliSelector);
        panelSelectores.add(comboPelicula);
        panelSelectores.add(labelSalaSelector);
        panelSelectores.add(comboSala);
        panelSelectores.add(labelFuncionSelector);
        panelSelectores.add(comboFuncion);

        panelButton.add(panelSelectores, gbc);

        // Configurar eventos
        comboPelicula.addActionListener(e -> onPeliculaSeleccionada());
        comboSala.addActionListener(e -> onSalaSeleccionada());
        comboFuncion.addActionListener(e -> onFuncionSeleccionada());

        // Actualizar el panel
        panelButton.revalidate();
        panelButton.repaint();
    }

    private void cargarPeliculas() {
        comboPelicula.removeAllItems();
        comboPelicula.addItem("Seleccione película");

        List<Pelicula> peliculas = peliculaData.listarPeliculas();
        for (Pelicula peli : peliculas) {
            if (peli.isActivo() && peli.isEnCartelera()) {
                comboPelicula.addItem(peli.getTitulo());
            }
        }
    }

    private void onPeliculaSeleccionada() {
        if (comboPelicula.getSelectedIndex() <= 0) {
            comboSala.removeAllItems();
            comboSala.addItem("Seleccione sala");
            comboFuncion.removeAllItems();
            comboFuncion.addItem("Seleccione función");
            return;
        }

        String tituloSeleccionado = comboPelicula.getSelectedItem().toString();
        List<Pelicula> peliculas = peliculaData.listarPeliculas();
        Pelicula peliculaSeleccionada = null;

        for (Pelicula peli : peliculas) {
            if (peli.getTitulo().equals(tituloSeleccionado)) {
                peliculaSeleccionada = peli;
                break;
            }
        }

        if (peliculaSeleccionada != null) {
            cargarSalasPorPelicula(peliculaSeleccionada.getIdPelicula());
        }

        comboFuncion.removeAllItems();
        comboFuncion.addItem("Seleccione función");
    }

    private void cargarSalasPorPelicula(int idPelicula) {
        comboSala.removeAllItems();
        comboSala.addItem("Seleccione sala");

        List<Proyeccion> proyecciones = proyeccionData.listarProyecciones();
        java.util.Set<Integer> salasIds = new java.util.HashSet<>();

        for (Proyeccion proy : proyecciones) {
            if (proy.getPelicula().getIdPelicula() == idPelicula && proy.isActivo()) {
                salasIds.add(proy.getSala().getNroSala());
            }
        }

        for (Integer salaId : salasIds) {
            Sala sala = salaData.buscarSala(salaId);
            if (sala != null && sala.isActivo()) {
                comboSala.addItem("Sala " + sala.getNroSala());
            }
        }
    }

    private void onSalaSeleccionada() {
        if (comboSala.getSelectedIndex() <= 0 || comboPelicula.getSelectedIndex() <= 0) {
            comboFuncion.removeAllItems();
            comboFuncion.addItem("Seleccione función");
            return;
        }

        String salaTexto = comboSala.getSelectedItem().toString();
        int nroSala = Integer.parseInt(salaTexto.replaceAll("[^0-9]", "").trim());

        String tituloSeleccionado = comboPelicula.getSelectedItem().toString();
        List<Pelicula> peliculas = peliculaData.listarPeliculas();
        Pelicula peliculaSeleccionada = null;

        for (Pelicula peli : peliculas) {
            if (peli.getTitulo().equals(tituloSeleccionado)) {
                peliculaSeleccionada = peli;
                break;
            }
        }

        if (peliculaSeleccionada != null) {
            cargarFuncionesPorSalaYPelicula(peliculaSeleccionada.getIdPelicula(), nroSala);
        }
    }

    private void cargarFuncionesPorSalaYPelicula(int idPelicula, int nroSala) {
        comboFuncion.removeAllItems();
        comboFuncion.addItem("Seleccione función");

        List<Proyeccion> proyecciones = proyeccionData.listarProyecciones();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Proyeccion proy : proyecciones) {
            if (proy.getPelicula().getIdPelicula() == idPelicula &&
                proy.getSala().getNroSala() == nroSala && proy.isActivo()) {
                String funcion = proy.getHoraInicio().format(formatter) +
                               (proy.isEs3D() ? " (3D)" : " (2D)");
                comboFuncion.addItem(funcion);
            }
        }
    }

    private void onFuncionSeleccionada() {
        if (comboFuncion.getSelectedIndex() <= 0) {
            proyeccionSeleccionada = null;
            return;
        }

        String funcionSeleccionada = comboFuncion.getSelectedItem().toString();
        String funcionFecha = funcionSeleccionada.split(" \\(")[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String tituloSeleccionado = comboPelicula.getSelectedItem().toString();
        String salaTexto = comboSala.getSelectedItem().toString();
        int nroSala = Integer.parseInt(salaTexto.replaceAll("[^0-9]", "").trim());

        List<Proyeccion> proyecciones = proyeccionData.listarProyecciones();
        for (Proyeccion proy : proyecciones) {
            if (proy.getPelicula().getTitulo().equals(tituloSeleccionado) &&
                proy.getSala().getNroSala() == nroSala &&
                proy.getHoraInicio().format(formatter).equals(funcionFecha)) {
                proyeccionSeleccionada = proy;
                break;
            }
        }

        if (proyeccionSeleccionada != null) {
            actualizarDetalles();
            cargarAsientosDinamicos();
        }
    }

    private void actualizarDetalles() {
        if (proyeccionSeleccionada != null) {
            DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

            labelPeli.setText("Película: " + proyeccionSeleccionada.getPelicula().getTitulo());
            labelSala.setText("Sala: " + proyeccionSeleccionada.getSala().getNroSala());
            labelFecha.setText("Fecha: " + proyeccionSeleccionada.getHoraInicio().format(formatterFecha));
            labelHora.setText("Hora: " + proyeccionSeleccionada.getHoraInicio().format(formatterHora));

            List<Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
            int disponibles = 0;
            for (Lugar lugar : lugares) {
                if (lugar.getEstado().equalsIgnoreCase("libre") ||
                    lugar.getEstado().equalsIgnoreCase("disponible")) {
                    disponibles++;
                }
            }
            labelTotal.setText("Total disponibles: " + disponibles + "/" + lugares.size());
        }
    }

    private void cargarAsientosDinamicos() {
        if (proyeccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una proyección primero",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiar panel de mapa
        panelMapa.removeAll();
        botonesAsientos.clear();

        // Crear panel dinámico para asientos
        panelAsientosDinamico = new JPanel();
        panelAsientosDinamico.setLayout(new GridBagLayout());
        panelAsientosDinamico.setBackground(new Color(204, 204, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Obtener lugares de la proyección
        List<Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());

        if (lugares.isEmpty()) {
            // Si no hay asientos, generarlos automáticamente
            System.out.println("Generando asientos para proyección: " + proyeccionSeleccionada.getIdProyeccion());
            generarAsientosParaProyeccion();
            lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
            System.out.println("Asientos generados: " + lugares.size());
        }

        if (lugares.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se pudieron generar los asientos. Verifique que la sala tenga capacidad configurada.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Agrupar por filas
        Map<Character, List<Lugar>> lugaresporFila = new HashMap<>();
        for (Lugar lugar : lugares) {
            lugaresporFila.computeIfAbsent(lugar.getFila(), k -> new ArrayList<>()).add(lugar);
        }

        // Agregar pantalla
        JLabel pantalla = new JLabel("=== PANTALLA ===");
        pantalla.setFont(new Font("Arial", Font.BOLD, 18));
        pantalla.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 20;
        gbc.insets = new Insets(10, 0, 20, 0);
        panelAsientosDinamico.add(pantalla, gbc);

        // Ordenar filas
        List<Character> filasOrdenadas = new ArrayList<>(lugaresporFila.keySet());
        filasOrdenadas.sort(Character::compareTo);

        int fila = 1;
        for (Character letraFila : filasOrdenadas) {
            List<Lugar> asientosFila = lugaresporFila.get(letraFila);
            asientosFila.sort((a, b) -> Integer.compare(a.getNumero(), b.getNumero()));

            // Label de fila
            gbc.gridx = 0;
            gbc.gridy = fila;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(2, 5, 2, 10);
            JLabel labelFila = new JLabel(String.valueOf(letraFila));
            labelFila.setFont(new Font("Arial", Font.BOLD, 16));
            labelFila.setForeground(Color.BLACK);
            panelAsientosDinamico.add(labelFila, gbc);

            // Botones de asientos
            int col = 1;
            for (Lugar lugar : asientosFila) {
                JButton btnAsiento = crearBotonAsiento(lugar);
                String key = letraFila + "-" + lugar.getNumero();
                botonesAsientos.put(key, btnAsiento);

                gbc.gridx = col;
                gbc.gridy = fila;
                panelAsientosDinamico.add(btnAsiento, gbc);
                col++;
            }

            fila++;
        }

        // Agregar leyenda
        JPanel panelLeyenda = crearPanelLeyenda();
        gbc.gridx = 0;
        gbc.gridy = fila + 1;
        gbc.gridwidth = 15;
        gbc.insets = new Insets(20, 0, 10, 0);
        panelAsientosDinamico.add(panelLeyenda, gbc);

        // Configurar el layout del panelMapa para que use BorderLayout
        panelMapa.setLayout(new BorderLayout());
        panelMapa.add(panelAsientosDinamico, BorderLayout.CENTER);

        // Forzar actualización visual
        panelMapa.revalidate();
        panelMapa.repaint();
        this.revalidate();
        this.repaint();

        System.out.println("Mapa de asientos cargado con " + lugares.size() + " asientos");
    }

    private JButton crearBotonAsiento(Lugar lugar) {
        JButton btn = new JButton(String.valueOf(lugar.getNumero()));
        btn.setPreferredSize(new Dimension(50, 40));
        btn.setFont(new Font("Arial", Font.BOLD, 10));

        actualizarColorBoton(btn, lugar.getEstado());

        btn.addActionListener(e -> {
            if (lugar.getEstado().equalsIgnoreCase("ocupado")) {
                JOptionPane.showMessageDialog(this,
                    "Este asiento ya está ocupado",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                int opcion = JOptionPane.showConfirmDialog(this,
                    "¿Desea cambiar el estado de Fila " + lugar.getFila() +
                    " - Asiento " + lugar.getNumero() + "?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);

                if (opcion == JOptionPane.YES_OPTION) {
                    String nuevoEstado = lugar.getEstado().equalsIgnoreCase("libre") ?
                                       "ocupado" : "libre";
                    lugarData.cambiarEstadoLugar(lugar.getCodLugar(), nuevoEstado);
                    lugar.setEstado(nuevoEstado);
                    actualizarColorBoton(btn, nuevoEstado);
                    actualizarDetalles();
                }
            }
        });

        return btn;
    }

    private void actualizarColorBoton(JButton btn, String estado) {
        if (estado.equalsIgnoreCase("libre") || estado.equalsIgnoreCase("disponible")) {
            btn.setBackground(new Color(76, 175, 80)); // Verde
            btn.setEnabled(true);
        } else if (estado.equalsIgnoreCase("ocupado")) {
            btn.setBackground(new Color(244, 67, 54)); // Rojo
            btn.setEnabled(false);
        } else {
            btn.setBackground(new Color(255, 193, 7)); // Amarillo (reservado)
            btn.setEnabled(true);
        }
    }

    private JPanel crearPanelLeyenda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setBackground(new Color(204, 204, 255));

        panel.add(crearItemLeyenda("Disponible", new Color(76, 175, 80)));
        panel.add(crearItemLeyenda("Ocupado", new Color(244, 67, 54)));

        return panel;
    }

    private JPanel crearItemLeyenda(String texto, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(new Color(204, 204, 255));

        JPanel cuadro = new JPanel();
        cuadro.setPreferredSize(new Dimension(20, 20));
        cuadro.setBackground(color);
        cuadro.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);

        item.add(cuadro);
        item.add(label);

        return item;
    }

    private void generarAsientosParaProyeccion() {
        if (proyeccionSeleccionada == null) return;

        int capacidadSala = proyeccionSeleccionada.getSala().getCapacidad();
        if (capacidadSala == 0) capacidadSala = 50; // Valor por defecto

        // Calcular distribución: 5 filas
        int asientosPorFila = (int) Math.ceil(capacidadSala / 5.0);
        char[] filas = {'A', 'B', 'C', 'D', 'E'};

        for (char fila : filas) {
            for (int num = 1; num <= asientosPorFila; num++) {
                Lugar lugar = new Lugar();
                lugar.setProyeccion(proyeccionSeleccionada);
                lugar.setFila(fila);
                lugar.setNumero(num);
                lugar.setEstado("libre");

                lugarData.guardarLugar(lugar);
            }
        }
    }

    private void configurarEventosBotones() {
        buttonVer.addActionListener(e -> cargarAsientosDinamicos());

        buttonAct.addActionListener(e -> {
            if (proyeccionSeleccionada != null) {
                cargarAsientosDinamicos();
                actualizarDetalles();
                JOptionPane.showMessageDialog(this,
                    "Vista actualizada correctamente",
                    "Actualizar",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar una proyección primero",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonLimpiar.addActionListener(e -> limpiarVista());

        buttonVerOcupacion.addActionListener(e -> mostrarReporteOcupacion());

        buttonGuardar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Los cambios se guardan automáticamente",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        });

        buttonVolver.addActionListener(e -> {
            this.setVisible(false);
        });
    }

    private void limpiarVista() {
        comboPelicula.setSelectedIndex(0);
        comboSala.removeAllItems();
        comboSala.addItem("Seleccione sala");
        comboFuncion.removeAllItems();
        comboFuncion.addItem("Seleccione función");

        proyeccionSeleccionada = null;

        labelPeli.setText("Película:");
        labelSala.setText("Sala:");
        labelFecha.setText("Fecha:");
        labelHora.setText("Hora:");
        labelTotal.setText("Total disponibles:");

        panelMapa.removeAll();
        panelMapa.revalidate();
        panelMapa.repaint();

        botonesAsientos.clear();
    }

    private void mostrarReporteOcupacion() {
        if (proyeccionSeleccionada == null) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una proyección primero",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Lugar> lugares = lugarData.listarLugaresPorProyeccion(proyeccionSeleccionada.getIdProyeccion());
        int ocupados = 0;
        int libres = 0;

        for (Lugar lugar : lugares) {
            if (lugar.getEstado().equalsIgnoreCase("ocupado")) {
                ocupados++;
            } else {
                libres++;
            }
        }

        int total = lugares.size();
        double porcentajeOcupacion = total > 0 ? (ocupados * 100.0 / total) : 0;

        String mensaje = String.format(
            "Reporte de Ocupación\n\n" +
            "Película: %s\n" +
            "Sala: %d\n" +
            "Fecha/Hora: %s\n\n" +
            "Total asientos: %d\n" +
            "Ocupados: %d\n" +
            "Libres: %d\n" +
            "Porcentaje ocupación: %.1f%%",
            proyeccionSeleccionada.getPelicula().getTitulo(),
            proyeccionSeleccionada.getSala().getNroSala(),
            proyeccionSeleccionada.getHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
            total, ocupados, libres, porcentajeOcupacion
        );

        JOptionPane.showMessageDialog(this, mensaje,
            "Reporte de Ocupación",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paneltitulo = new javax.swing.JPanel();
        titleAsientos = new javax.swing.JLabel();
        panelButton = new javax.swing.JPanel();
        buttonAct = new javax.swing.JButton();
        buttonLimpiar = new javax.swing.JButton();
        buttonImprimir = new javax.swing.JButton();
        buttonVerOcupacion = new javax.swing.JButton();
        panelMapa = new javax.swing.JPanel();
        labelMapa = new javax.swing.JLabel();
        panelButton1 = new javax.swing.JPanel();
        buttonGuardar = new javax.swing.JButton();
        buttonVolver = new javax.swing.JButton();
        buttonVer = new javax.swing.JButton();
        panelDetalles = new javax.swing.JPanel();
        panelPeli = new javax.swing.JPanel();
        labelPeli = new javax.swing.JLabel();
        panelSala = new javax.swing.JPanel();
        labelSala = new javax.swing.JLabel();
        panelFecha = new javax.swing.JPanel();
        labelFecha = new javax.swing.JLabel();
        panelHora = new javax.swing.JPanel();
        labelHora = new javax.swing.JLabel();
        panelTotal = new javax.swing.JPanel();
        labelTotal = new javax.swing.JLabel();
        titleDetalles = new javax.swing.JLabel();

        paneltitulo.setBackground(new java.awt.Color(51, 90, 144));
        paneltitulo.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        titleAsientos.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleAsientos.setForeground(new java.awt.Color(255, 255, 255));
        titleAsientos.setText("Gestion de Asientos");

        javax.swing.GroupLayout paneltituloLayout = new javax.swing.GroupLayout(paneltitulo);
        paneltitulo.setLayout(paneltituloLayout);
        paneltituloLayout.setHorizontalGroup(
            paneltituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paneltituloLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(titleAsientos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        paneltituloLayout.setVerticalGroup(
            paneltituloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneltituloLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(titleAsientos)
                .addContainerGap())
        );

        buttonAct.setText("Actualizar");

        buttonLimpiar.setText("Limpiar");

        buttonImprimir.setText("Imprimir");

        buttonVerOcupacion.setText("Ver ocupacion");

        javax.swing.GroupLayout panelButtonLayout = new javax.swing.GroupLayout(panelButton);
        panelButton.setLayout(panelButtonLayout);
        panelButtonLayout.setHorizontalGroup(
            panelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonAct)
                .addGap(18, 18, 18)
                .addComponent(buttonLimpiar)
                .addGap(18, 18, 18)
                .addComponent(buttonImprimir)
                .addGap(18, 18, 18)
                .addComponent(buttonVerOcupacion)
                .addContainerGap(656, Short.MAX_VALUE))
        );
        panelButtonLayout.setVerticalGroup(
            panelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(panelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAct)
                    .addComponent(buttonLimpiar)
                    .addComponent(buttonImprimir)
                    .addComponent(buttonVerOcupacion))
                .addContainerGap())
        );

        panelMapa.setBackground(new java.awt.Color(204, 204, 255));

        labelMapa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imag/mapa de asientos.png"))); // NOI18N
        labelMapa.setText("jLabel1");

        javax.swing.GroupLayout panelMapaLayout = new javax.swing.GroupLayout(panelMapa);
        panelMapa.setLayout(panelMapaLayout);
        panelMapaLayout.setHorizontalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 226, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonGuardar.setText("Guardar cambios");

        buttonVolver.setText("Volver a funciones");

        buttonVer.setText("Ver");

        javax.swing.GroupLayout panelButton1Layout = new javax.swing.GroupLayout(panelButton1);
        panelButton1.setLayout(panelButton1Layout);
        panelButton1Layout.setHorizontalGroup(
            panelButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButton1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonGuardar)
                .addGap(18, 18, 18)
                .addComponent(buttonVolver)
                .addGap(18, 18, 18)
                .addComponent(buttonVer)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelButton1Layout.setVerticalGroup(
            panelButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButton1Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(panelButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGuardar)
                    .addComponent(buttonVolver)
                    .addComponent(buttonVer))
                .addContainerGap())
        );

        panelPeli.setBackground(new java.awt.Color(255, 255, 255));
        panelPeli.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelPeli.setText("Pelicula:");

        javax.swing.GroupLayout panelPeliLayout = new javax.swing.GroupLayout(panelPeli);
        panelPeli.setLayout(panelPeliLayout);
        panelPeliLayout.setHorizontalGroup(
            panelPeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeliLayout.createSequentialGroup()
                .addComponent(labelPeli)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelPeliLayout.setVerticalGroup(
            panelPeliLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPeliLayout.createSequentialGroup()
                .addComponent(labelPeli)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        panelSala.setBackground(new java.awt.Color(255, 255, 255));
        panelSala.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelSala.setText("Sala:");

        javax.swing.GroupLayout panelSalaLayout = new javax.swing.GroupLayout(panelSala);
        panelSala.setLayout(panelSalaLayout);
        panelSalaLayout.setHorizontalGroup(
            panelSalaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalaLayout.createSequentialGroup()
                .addComponent(labelSala)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelSalaLayout.setVerticalGroup(
            panelSalaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalaLayout.createSequentialGroup()
                .addComponent(labelSala)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        panelFecha.setBackground(new java.awt.Color(255, 255, 255));
        panelFecha.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelFecha.setText("Fecha:");

        javax.swing.GroupLayout panelFechaLayout = new javax.swing.GroupLayout(panelFecha);
        panelFecha.setLayout(panelFechaLayout);
        panelFechaLayout.setHorizontalGroup(
            panelFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFechaLayout.createSequentialGroup()
                .addComponent(labelFecha)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelFechaLayout.setVerticalGroup(
            panelFechaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFechaLayout.createSequentialGroup()
                .addComponent(labelFecha)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        panelHora.setBackground(new java.awt.Color(255, 255, 255));
        panelHora.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelHora.setText("Hora:");

        javax.swing.GroupLayout panelHoraLayout = new javax.swing.GroupLayout(panelHora);
        panelHora.setLayout(panelHoraLayout);
        panelHoraLayout.setHorizontalGroup(
            panelHoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHoraLayout.createSequentialGroup()
                .addComponent(labelHora)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelHoraLayout.setVerticalGroup(
            panelHoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHoraLayout.createSequentialGroup()
                .addComponent(labelHora)
                .addGap(0, 7, Short.MAX_VALUE))
        );

        panelTotal.setBackground(new java.awt.Color(255, 255, 255));
        panelTotal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labelTotal.setText("Total disponibles:");

        javax.swing.GroupLayout panelTotalLayout = new javax.swing.GroupLayout(panelTotal);
        panelTotal.setLayout(panelTotalLayout);
        panelTotalLayout.setHorizontalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalLayout.createSequentialGroup()
                .addComponent(labelTotal)
                .addGap(0, 34, Short.MAX_VALUE))
        );
        panelTotalLayout.setVerticalGroup(
            panelTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalLayout.createSequentialGroup()
                .addComponent(labelTotal)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelDetallesLayout = new javax.swing.GroupLayout(panelDetalles);
        panelDetalles.setLayout(panelDetallesLayout);
        panelDetallesLayout.setHorizontalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelPeli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelSala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelHora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 86, Short.MAX_VALUE))
        );
        panelDetallesLayout.setVerticalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPeli, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(panelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        titleDetalles.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        titleDetalles.setText("Detalles");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(titleDetalles))
                        .addGap(55, 55, 55))
                    .addComponent(panelButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paneltitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleDetalles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(panelButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAct;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JButton buttonImprimir;
    private javax.swing.JButton buttonLimpiar;
    private javax.swing.JButton buttonVer;
    private javax.swing.JButton buttonVerOcupacion;
    private javax.swing.JButton buttonVolver;
    private javax.swing.JLabel labelFecha;
    private javax.swing.JLabel labelHora;
    private javax.swing.JLabel labelMapa;
    private javax.swing.JLabel labelPeli;
    private javax.swing.JLabel labelSala;
    private javax.swing.JLabel labelTotal;
    private javax.swing.JPanel panelButton;
    private javax.swing.JPanel panelButton1;
    private javax.swing.JPanel panelDetalles;
    private javax.swing.JPanel panelFecha;
    private javax.swing.JPanel panelHora;
    private javax.swing.JPanel panelMapa;
    private javax.swing.JPanel panelPeli;
    private javax.swing.JPanel panelSala;
    private javax.swing.JPanel panelTotal;
    private javax.swing.JPanel paneltitulo;
    private javax.swing.JLabel titleAsientos;
    private javax.swing.JLabel titleDetalles;
    // End of variables declaration//GEN-END:variables
}
