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

        // Maximizar la ventana al abrirse
//        try {
//            this.setMaximum(true);
//        } catch (java.beans.PropertyVetoException e) {
//            System.out.println("No se pudo maximizar la ventana: " + e.getMessage());
//        }
    }

    private void agregarSelectoresProyeccion() {
        // Configurar layout del panelButton para que muestre componentes horizontalmente
        panelButton.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));
        panelButton.setBackground(new Color(240, 240, 240));

        // Panel para los selectores (izquierda)
        JPanel panelSelectores = new JPanel();
        panelSelectores.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelSelectores.setBackground(new Color(240, 240, 240));

        // ComboBox de Películas
        JLabel labelPeliSelector = new JLabel("Película:");
        comboPelicula = new JComboBox<>();
        comboPelicula.setPreferredSize(new Dimension(140, 25));
        comboPelicula.addItem("Seleccione película");
        cargarPeliculas();

        // ComboBox de Salas
        JLabel labelSalaSelector = new JLabel("Sala:");
        comboSala = new JComboBox<>();
        comboSala.setPreferredSize(new Dimension(100, 25));
        comboSala.addItem("Seleccione sala");

        // ComboBox de Funciones
        JLabel labelFuncionSelector = new JLabel("Función:");
        comboFuncion = new JComboBox<>();
        comboFuncion.setPreferredSize(new Dimension(160, 25));
        comboFuncion.addItem("Seleccione función");

        panelSelectores.add(labelPeliSelector);
        panelSelectores.add(comboPelicula);
        panelSelectores.add(labelSalaSelector);
        panelSelectores.add(comboSala);
        panelSelectores.add(labelFuncionSelector);
        panelSelectores.add(comboFuncion);

        // Agregar selectores primero (izquierda)
        panelButton.add(panelSelectores);

        // Separador visual
        JLabel separador = new JLabel("  |  ");
        separador.setFont(new Font("Arial", Font.BOLD, 16));
        panelButton.add(separador);

        // Botones (derecha)
        panelButton.add(buttonAct);
        panelButton.add(buttonLimpiar);
        //panelButton.add(buttonImprimir);
        panelButton.add(buttonVerOcupacion);

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

        // Agregar pantalla y leyenda en la misma fila
        JPanel panelPantallaYLeyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        panelPantallaYLeyenda.setBackground(new Color(204, 204, 255));

        JLabel pantalla = new JLabel("=== PANTALLA ===");
        pantalla.setFont(new Font("Arial", Font.BOLD, 18));
        pantalla.setForeground(Color.DARK_GRAY);
        panelPantallaYLeyenda.add(pantalla);

        // Agregar leyenda al lado de la pantalla
        panelPantallaYLeyenda.add(crearItemLeyenda("Disponible", new Color(76, 175, 80)));
        panelPantallaYLeyenda.add(crearItemLeyenda("Ocupado", new Color(244, 67, 54)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 20;
        gbc.insets = new Insets(10, 0, 20, 0);
        panelAsientosDinamico.add(panelPantallaYLeyenda, gbc);

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

        // La leyenda ya está arriba junto a la pantalla, no agregar otra aquí

        // Establecer un tamaño máximo para forzar el scroll cuando hay muchos asientos
        int maxAsientosPorFila = 0;
        for (List<Lugar> asientosFila : lugaresporFila.values()) {
            if (asientosFila.size() > maxAsientosPorFila) {
                maxAsientosPorFila = asientosFila.size();
            }
        }

        // Calcular tamaño real basado en contenido
        int anchoReal = (maxAsientosPorFila * 54) + 100;
        int altoReal = (filasOrdenadas.size() * 44) + 150;

        // Establecer SOLO el tamaño máximo, no el preferido
        panelAsientosDinamico.setMaximumSize(new Dimension(anchoReal, altoReal));

        // Crear JScrollPane con el panel de asientos
        JScrollPane scrollPane = new JScrollPane(panelAsientosDinamico);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // CLAVE: Establecer un tamaño preferido dinámico en el SCROLLPANE
        // Usar casi todo el espacio disponible en panelMapa
        int anchoPanelMapa = panelMapa.getWidth() > 0 ? panelMapa.getWidth() : 1600;
        int altoPanelMapa = panelMapa.getHeight() > 0 ? panelMapa.getHeight() : 600;

        // Usar el 98% del ancho y 75% del alto
        int anchoScrollPane = (int) (anchoPanelMapa * 0.98);
        int altoScrollPane = (int) (altoPanelMapa * 0.75);

        // Asegurar un tamaño mínimo razonable
        anchoScrollPane = Math.max(anchoScrollPane, 1400);
        altoScrollPane = Math.max(Math.min(altoScrollPane, 450), 380);

        scrollPane.setPreferredSize(new Dimension(anchoScrollPane, altoScrollPane));

        // Configurar el layout del panelMapa para que use BorderLayout
        panelMapa.setLayout(new BorderLayout());
        panelMapa.add(scrollPane, BorderLayout.CENTER);

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
            String estado = lugar.getEstado().toLowerCase();

            if (estado.equals("libre") || estado.equals("disponible")) {
                // Asiento desocupado: permitir ocuparlo o reservarlo
                manejarAsientoDesocupado(lugar, btn);
            } else if (estado.equals("reservado")) {
                // Asiento reservado: verificar si tiene ticket
                manejarAsientoReservado(lugar, btn);
            } else if (estado.equals("ocupado")) {
                // Asiento ocupado: verificar si tiene ticket y permitir eliminarlo
                manejarAsientoOcupado(lugar, btn);
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
            btn.setEnabled(true);
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
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setBackground(new Color(204, 204, 255));

        JPanel cuadro = new JPanel();
        cuadro.setPreferredSize(new Dimension(45, 35));
        cuadro.setBackground(color);
        cuadro.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.BLACK);

        item.add(cuadro);
        item.add(label);

        return item;
    }

    private void generarAsientosParaProyeccion() {
        if (proyeccionSeleccionada == null) return;

        int capacidadSala = proyeccionSeleccionada.getSala().getCapacidad();
        if (capacidadSala == 0) capacidadSala = 50; // Valor por defecto

        // Calcular distribución: 6 filas
        int asientosPorFila = (int) Math.ceil(capacidadSala / 6.0);
        char[] filas = {'A', 'B', 'C', 'D', 'E', 'F'};

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
        //buttonVer.addActionListener(e -> cargarAsientosDinamicos());

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

        //buttonVolver.addActionListener(e -> {
        //    this.setVisible(false);
        //});
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

    // Manejar clic en asiento desocupado (libre/disponible)
    private void manejarAsientoDesocupado(Lugar lugar, JButton btn) {
        String[] opciones = {"Ocupar", "Reservar", "Cancelar"};
        int seleccion = JOptionPane.showOptionDialog(this,
            "Fila " + lugar.getFila() + " - Asiento " + lugar.getNumero() + "\n\n¿Qué desea hacer?",
            "Asiento Disponible",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]);

        if (seleccion == 0) { // Ocupar
            lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "ocupado");
            lugar.setEstado("ocupado");
            actualizarColorBoton(btn, "ocupado");
            actualizarDetalles();
        } else if (seleccion == 1) { // Reservar
            lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "reservado");
            lugar.setEstado("reservado");
            actualizarColorBoton(btn, "reservado");
            actualizarDetalles();
        }
    }

    // Manejar clic en asiento reservado
    private void manejarAsientoReservado(Lugar lugar, JButton btn) {
        persistencia.DetalleTicketData detalleTicketData = new persistencia.DetalleTicketData();
        Integer idTicket = detalleTicketData.buscarTicketPorLugar(lugar.getCodLugar());

        if (idTicket != null) {
            // Tiene ticket asociado: mostrar información y opción de eliminar
            mostrarTicketConOpcionEliminar(idTicket, lugar, btn);
        } else {
            // No tiene ticket: ofrecer cancelar la reserva
            int opcion = JOptionPane.showConfirmDialog(this,
                "Fila " + lugar.getFila() + " - Asiento " + lugar.getNumero() +
                "\n\nEste asiento está reservado pero no tiene ticket asociado.\n¿Desea cancelar la reserva?",
                "Cancelar Reserva",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (opcion == JOptionPane.YES_OPTION) {
                lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "libre");
                lugar.setEstado("libre");
                actualizarColorBoton(btn, "libre");
                actualizarDetalles();
                JOptionPane.showMessageDialog(this,
                    "Reserva cancelada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Manejar clic en asiento ocupado
    private void manejarAsientoOcupado(Lugar lugar, JButton btn) {
        persistencia.DetalleTicketData detalleTicketData = new persistencia.DetalleTicketData();
        Integer idTicket = detalleTicketData.buscarTicketPorLugar(lugar.getCodLugar());

        if (idTicket != null) {
            // Tiene ticket asociado: mostrar información y opción de eliminar
            mostrarTicketConOpcionEliminar(idTicket, lugar, btn);
        } else {
            // No tiene ticket: ofrecer liberar el asiento
            int opcion = JOptionPane.showConfirmDialog(this,
                "Fila " + lugar.getFila() + " - Asiento " + lugar.getNumero() +
                "\n\nEste asiento está ocupado pero no tiene ticket asociado.\n¿Desea liberar el asiento?",
                "Liberar Asiento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (opcion == JOptionPane.YES_OPTION) {
                lugarData.cambiarEstadoLugar(lugar.getCodLugar(), "libre");
                lugar.setEstado("libre");
                actualizarColorBoton(btn, "libre");
                actualizarDetalles();
                JOptionPane.showMessageDialog(this,
                    "Asiento liberado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Mostrar información del ticket con opción de eliminar
    private void mostrarTicketConOpcionEliminar(int idTicket, Lugar lugar, JButton btn) {
        persistencia.TicketCompraData ticketData = new persistencia.TicketCompraData();
        modelo.TicketCompra ticket = ticketData.buscarTicket(idTicket);

        if (ticket == null) {
            JOptionPane.showMessageDialog(this,
                "No se pudo cargar la información del ticket",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir información del ticket
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("INFORMACIÓN DEL TICKET\n\n");
        mensaje.append("ID Ticket: ").append(ticket.getIdTicket()).append("\n");
        mensaje.append("Comprador: ").append(ticket.getComprador() != null ? ticket.getComprador().getNombre() : "N/A").append("\n");
        mensaje.append("DNI: ").append(ticket.getComprador() != null ? ticket.getComprador().getDni() : "N/A").append("\n");
        mensaje.append("Fecha Compra: ").append(ticket.getFechaCompra() != null ? ticket.getFechaCompra().format(formatter) : "N/A").append("\n");
        mensaje.append("Función: ").append(ticket.getFechaFuncion() != null ? ticket.getFechaFuncion().format(formatter) : "N/A").append("\n");
        mensaje.append("Monto: $").append(String.format("%.2f", ticket.getMonto())).append("\n");
        mensaje.append("Estado: ").append(ticket.getEstadoTicket()).append("\n\n");
        mensaje.append("¿Desea eliminar este ticket?\n");
        mensaje.append("(Esto liberará todos los asientos asociados)");

        String[] opciones = {"Eliminar Ticket", "Cancelar"};
        int seleccion = JOptionPane.showOptionDialog(this,
            mensaje.toString(),
            "Ticket - Fila " + lugar.getFila() + " Asiento " + lugar.getNumero(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[1]);

        if (seleccion == 0) { // Eliminar
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar el ticket #" + idTicket + "?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                boolean eliminado = ticketData.eliminarTicket(idTicket);

                if (eliminado) {
                    JOptionPane.showMessageDialog(this,
                        "Ticket eliminado exitosamente.\nTodos los asientos han sido liberados.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

                    // Recargar la vista de asientos
                    cargarAsientosDinamicos();
                    actualizarDetalles();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar el ticket.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: DO NOT modify this code. The content of this method is always
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
        buttonVerOcupacion = new javax.swing.JButton();
        panelMapa = new javax.swing.JPanel();
        labelMapa = new javax.swing.JLabel();
        panelButton1 = new javax.swing.JPanel();
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
        buttonGuardar = new javax.swing.JButton();

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
                .addComponent(buttonVerOcupacion)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelButtonLayout.setVerticalGroup(
            panelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelButtonLayout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(panelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonAct)
                    .addComponent(buttonLimpiar)
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
                .addComponent(labelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 489, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMapaLayout.setVerticalGroup(
            panelMapaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMapaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 254, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelButton1Layout = new javax.swing.GroupLayout(panelButton1);
        panelButton1.setLayout(panelButton1Layout);
        panelButton1Layout.setHorizontalGroup(
            panelButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 706, Short.MAX_VALUE)
        );
        panelButton1Layout.setVerticalGroup(
            panelButton1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
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
                .addGap(0, 12, Short.MAX_VALUE))
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

        buttonGuardar.setText("Guardar cambios");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paneltitulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(titleDetalles)
                            .addComponent(buttonGuardar))
                        .addGap(25, 25, 25))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleDetalles)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGuardar))
                    .addComponent(panelMapa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAct;
    private javax.swing.JButton buttonGuardar;
    private javax.swing.JButton buttonLimpiar;
    private javax.swing.JButton buttonVerOcupacion;
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
