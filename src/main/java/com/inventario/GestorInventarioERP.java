package com.inventario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaz Gráfica de Usuario (GUI) estilo ERP Moderno para la gestión de
 * inventario.
 * Versión limpia: Enfocada estrictamente en el código base y funcionalidad
 * real.
 */
public class GestorInventarioERP extends JFrame {

    // --- CONSTANTES ESTÉTICAS (PALETA DE COLORES CORPORATIVA) ---
    private static final Color COLOR_PRIMARY = new Color(15, 23, 42); // Azul Oscuro Profundo
    private static final Color COLOR_ACCENT = new Color(37, 99, 235); // Azul de Acciones
    private static final Color COLOR_BG = new Color(241, 245, 249); // Fondo General
    private static final Color COLOR_CARD = Color.WHITE; // Blanco Puro
    private static final Color COLOR_BORDER = new Color(226, 232, 240); // Líneas divisorias

    private static final Color COLOR_SUCCESS = new Color(5, 150, 105); // Entradas (Verde - tonalidad moderada)
    private static final Color COLOR_DANGER = new Color(239, 68, 68); // Salidas (Rojo)

    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42); // Texto Principal
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Texto Deshabilitado

    // --- CONSTANTES DE TIPOGRAFÍA ---
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);

    // --- ESTADO DE LA APLICACIÓN ---
    private final java.util.List<Producto> listaProductos = new ArrayList<>();
    private Producto productoActual;

    // --- COMPONENTES DE LA INTERFAZ ---
    private JTextField txtCodigo, txtNombre, txtPrecio, txtCantidadInicial;
    private JButton btnCrearProducto;

    private JTextField txtCantidadOperacion;
    private JButton btnAgregarStock, btnExtraerStock;

    private JLabel lblValorStock, lblValorInventario;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    // --- SIDEBAR: LISTA DE PRODUCTOS ---
    private DefaultListModel<String> modeloListaProductos;
    private JList<String> listaProductosUI;
    private JButton btnNuevoProducto;

    public GestorInventarioERP() {
        setTitle("ERP - Sistema de Gestión de Inventario");
        setSize(1150, 680);
        setMinimumSize(new Dimension(1000, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        configurarEventos();
        aplicarEstilos();
        actualizarVista();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // 1. BARRA SUPERIOR (NORTH)
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setBackground(COLOR_PRIMARY);
        barraSuperior.setPreferredSize(new Dimension(getWidth(), 60));
        barraSuperior.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN DE INVENTARIO");
        lblTitulo.setFont(FONT_HEADING);
        lblTitulo.setForeground(Color.WHITE);

        barraSuperior.add(lblTitulo, BorderLayout.WEST);
        add(barraSuperior, BorderLayout.NORTH);

        // 2. ÁREA PRINCIPAL DE TRABAJO (CENTER)
        JPanel areaPrincipal = new JPanel(new GridBagLayout());
        areaPrincipal.setBackground(COLOR_BG);
        areaPrincipal.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- TARJETA 1: REGISTRO/INFORMACIÓN DE PRODUCTO ---
        TarjetasRounded panelRegistro = new TarjetasRounded("1. Información del Producto");
        panelRegistro.setLayout(new GridLayout(5, 2, 10, 15));

        panelRegistro.add(crearEtiqueta("Código Único:"));
        txtCodigo = new JTextField();
        panelRegistro.add(txtCodigo);

        panelRegistro.add(crearEtiqueta("Nombre del Producto:"));
        txtNombre = new JTextField();
        panelRegistro.add(txtNombre);

        panelRegistro.add(crearEtiqueta("Precio Unitario (S/.):"));
        txtPrecio = new JTextField();
        panelRegistro.add(txtPrecio);

        panelRegistro.add(crearEtiqueta("Stock Inicial:"));
        txtCantidadInicial = new JTextField();
        panelRegistro.add(txtCantidadInicial);

        panelRegistro.add(crearEtiqueta("")); // Espacio
        btnCrearProducto = new BotonEstilizado("Crear y Registrar", COLOR_ACCENT);
        panelRegistro.add(btnCrearProducto);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        areaPrincipal.add(panelRegistro, gbc);

        // --- TARJETA 2: OPERACIONES DE STOCK ---
        TarjetasRounded panelOperaciones = new TarjetasRounded("2. Operaciones de Almacén");
        panelOperaciones.setLayout(new GridBagLayout());
        GridBagConstraints gbcOp = new GridBagConstraints();
        gbcOp.fill = GridBagConstraints.HORIZONTAL;
        gbcOp.insets = new Insets(8, 8, 8, 8);

        // Indicadores de KPI
        JPanel panelKPIs = new JPanel(new GridLayout(1, 2, 10, 0));
        panelKPIs.setOpaque(false);

        JPanel kpiStock = new JPanel(new BorderLayout());
        kpiStock.setBackground(new Color(239, 246, 255));
        kpiStock.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254), 1));
        JLabel titleS = new JLabel(" STOCK DISPONIBLE", JLabel.CENTER);
        titleS.setFont(FONT_BODY_BOLD);
        titleS.setForeground(COLOR_ACCENT);
        lblValorStock = new JLabel("0", JLabel.CENTER);
        lblValorStock.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValorStock.setForeground(COLOR_TEXT_MAIN);
        kpiStock.add(titleS, BorderLayout.NORTH);
        kpiStock.add(lblValorStock, BorderLayout.CENTER);

        JPanel kpiValor = new JPanel(new BorderLayout());
        kpiValor.setBackground(new Color(240, 253, 244));
        kpiValor.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208), 1));
        JLabel titleV = new JLabel(" VALOR TOTAL INVENTARIO", JLabel.CENTER);
        titleV.setFont(FONT_BODY_BOLD);
        titleV.setForeground(COLOR_SUCCESS);
        lblValorInventario = new JLabel("S/. 0.00", JLabel.CENTER);
        lblValorInventario.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValorInventario.setForeground(COLOR_TEXT_MAIN);
        kpiValor.add(titleV, BorderLayout.NORTH);
        kpiValor.add(lblValorInventario, BorderLayout.CENTER);

        panelKPIs.add(kpiStock);
        panelKPIs.add(kpiValor);

        gbcOp.gridx = 0;
        gbcOp.gridy = 0;
        gbcOp.gridwidth = 2;
        gbcOp.weightx = 1.0;
        gbcOp.ipady = 15;
        panelOperaciones.add(panelKPIs, gbcOp);

        // Formulario de entrada/salida
        gbcOp.gridx = 0;
        gbcOp.gridy = 1;
        gbcOp.gridwidth = 1;
        gbcOp.weightx = 0.3;
        gbcOp.ipady = 0;
        panelOperaciones.add(crearEtiqueta("Cantidad a operar:"), gbcOp);

        gbcOp.gridx = 1;
        gbcOp.gridy = 1;
        gbcOp.weightx = 0.7;
        txtCantidadOperacion = new JTextField();
        panelOperaciones.add(txtCantidadOperacion, gbcOp);

        JPanel panelAccionesBtn = new JPanel(new GridLayout(1, 2, 12, 0));
        panelAccionesBtn.setOpaque(false);
        btnAgregarStock = new BotonEstilizado("Agregar Stock (+)", COLOR_SUCCESS);
        btnExtraerStock = new BotonEstilizado("Extraer Stock (-)", COLOR_DANGER);
        panelAccionesBtn.add(btnAgregarStock);
        panelAccionesBtn.add(btnExtraerStock);

        gbcOp.gridx = 0;
        gbcOp.gridy = 2;
        gbcOp.gridwidth = 2;
        gbcOp.weightx = 1.0;
        panelOperaciones.add(panelAccionesBtn, gbcOp);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.4;
        areaPrincipal.add(panelOperaciones, gbc);

        // --- TARJETA 3: TABLA DE HISTORIAL DE MOVIMIENTOS ---
        TarjetasRounded panelHistorial = new TarjetasRounded("3. Historial de Auditoría de Movimientos");
        panelHistorial.setLayout(new BorderLayout(0, 10));

        String[] columnas = { "Fecha y Hora", "Tipo de Acción", "Cantidad (Uds)", "Detalle Técnico" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaHistorial = new JTable(modeloTabla);
        configurarEstilosTabla();

        JScrollPane scrollPane = new JScrollPane(tablaHistorial);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panelHistorial.add(scrollPane, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        areaPrincipal.add(panelHistorial, gbc);

        add(areaPrincipal, BorderLayout.CENTER);

        // 3. SIDEBAR DERECHO: LISTA DE PRODUCTOS (EAST)
        JPanel sidebar = new JPanel(new BorderLayout(0, 0));
        sidebar.setPreferredSize(new Dimension(240, getHeight()));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BORDER));

        // Encabezado del sidebar
        JPanel sidebarHeader = new JPanel(new BorderLayout());
        sidebarHeader.setBackground(COLOR_PRIMARY);
        sidebarHeader.setPreferredSize(new Dimension(240, 45));
        sidebarHeader.setBorder(new EmptyBorder(8, 15, 8, 15));
        JLabel lblSidebarTitulo = new JLabel("·  PRODUCTOS", JLabel.LEFT);
        lblSidebarTitulo.setFont(FONT_SUBTITLE);
        lblSidebarTitulo.setForeground(Color.WHITE);
        sidebarHeader.add(lblSidebarTitulo, BorderLayout.CENTER);
        sidebar.add(sidebarHeader, BorderLayout.NORTH);

        // Botón Agregar Producto + Lista
        JPanel sidebarBody = new JPanel(new BorderLayout(0, 0));
        sidebarBody.setBackground(Color.WHITE);
        sidebarBody.setBorder(new EmptyBorder(12, 12, 12, 12));

        btnNuevoProducto = new BotonEstilizado("+ Agregar Producto", COLOR_ACCENT);
        btnNuevoProducto.setPreferredSize(new Dimension(210, 38));
        JPanel wrapBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapBtn.setBackground(Color.WHITE);
        wrapBtn.add(btnNuevoProducto);
        sidebarBody.add(wrapBtn, BorderLayout.NORTH);

        // Lista de productos
        modeloListaProductos = new DefaultListModel<>();
        listaProductosUI = new JList<>(modeloListaProductos);
        listaProductosUI.setFont(FONT_BODY);
        listaProductosUI.setFixedCellHeight(44);
        listaProductosUI.setBackground(Color.WHITE);
        listaProductosUI.setForeground(COLOR_TEXT_MAIN);
        listaProductosUI.setSelectionBackground(new Color(219, 234, 254));
        listaProductosUI.setSelectionForeground(COLOR_TEXT_MAIN);
        listaProductosUI.setBorder(new EmptyBorder(6, 0, 0, 0));

        // Renderer personalizado para cada item de la lista
        listaProductosUI.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(FONT_BODY);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                        new EmptyBorder(8, 12, 8, 12)));
                lbl.setOpaque(true);
                if (isSelected) {
                    lbl.setBackground(new Color(219, 234, 254));
                    lbl.setForeground(COLOR_ACCENT);
                    lbl.setFont(FONT_BODY_BOLD);
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(COLOR_TEXT_MAIN);
                }
                return lbl;
            }
        });

        JScrollPane scrollProductos = new JScrollPane(listaProductosUI);
        scrollProductos.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollProductos.getViewport().setBackground(Color.WHITE);
        sidebarBody.add(scrollProductos, BorderLayout.CENTER);

        sidebar.add(sidebarBody, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);
    }

    private void configurarEventos() {
        // --- CREAR PRODUCTO: agrega a la lista global y sidebar ---
        btnCrearProducto.addActionListener(e -> {
            try {
                String codigo = txtCodigo.getText().trim();
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int cantidad = Integer.parseInt(txtCantidadInicial.getText().trim());

                // Verificar código duplicado
                for (Producto p : listaProductos) {
                    if (p.getCodigo().equalsIgnoreCase(codigo)) {
                        mostrarErrorDialog("Ya existe un producto con el código: " + codigo);
                        return;
                    }
                }

                Producto nuevo = new Producto(codigo, nombre, precio, cantidad);
                listaProductos.add(nuevo);
                productoActual = nuevo;

                // Agregar a la lista visual del sidebar
                modeloListaProductos.addElement(codigo + " - " + nombre);

                bloquearFormularioProducto();
                actualizarVista();

                // Seleccionar el nuevo producto en la lista
                listaProductosUI.setSelectedIndex(listaProductos.size() - 1);

                mostrarExitoDialog("Producto registrado correctamente.");

            } catch (NumberFormatException ex) {
                mostrarErrorDialog("Los valores numéricos de precio o cantidad contienen formatos inválidos.");
            } catch (IllegalArgumentException ex) {
                mostrarErrorDialog(ex.getMessage());
            }
        });

        // --- AGREGAR STOCK ---
        btnAgregarStock.addActionListener(e -> {
            try {
                int cant = Integer.parseInt(txtCantidadOperacion.getText().trim());
                productoActual.agregarStock(cant);
                txtCantidadOperacion.setText("");
                actualizarVista();
            } catch (NumberFormatException ex) {
                mostrarErrorDialog("Inserte un valor entero válido para la operación de entrada.");
            } catch (IllegalArgumentException ex) {
                mostrarErrorDialog(ex.getMessage());
            }
        });

        // --- EXTRAER STOCK ---
        btnExtraerStock.addActionListener(e -> {
            try {
                int cant = Integer.parseInt(txtCantidadOperacion.getText().trim());
                productoActual.extraerStock(cant);
                txtCantidadOperacion.setText("");
                actualizarVista();
            } catch (NumberFormatException ex) {
                mostrarErrorDialog("Inserte un valor entero válido para la operación de salida.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErrorDialog(ex.getMessage());
            }
        });

        // --- SIDEBAR: Seleccionar producto de la lista ---
        listaProductosUI.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int index = listaProductosUI.getSelectedIndex();
            if (index >= 0 && index < listaProductos.size()) {
                productoActual = listaProductos.get(index);
                cargarProductoEnFormulario(productoActual);
                bloquearFormularioProducto();
                actualizarVista();
            }
        });

        // --- SIDEBAR: Botón Agregar Producto (resetea formulario para uno nuevo) ---
        btnNuevoProducto.addActionListener(e -> {
            productoActual = null;
            listaProductosUI.clearSelection();
            desbloquearFormularioProducto();
            limpiarFormularioProducto();
            actualizarVista();
        });
    }

    /** Carga los datos de un producto existente en los campos del formulario */
    private void cargarProductoEnFormulario(Producto p) {
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtCantidadInicial.setText(String.valueOf(p.getCantidad()));
    }

    /** Bloquea los campos del formulario de creación */
    private void bloquearFormularioProducto() {
        txtCodigo.setEditable(false);
        txtNombre.setEditable(false);
        txtPrecio.setEditable(false);
        txtCantidadInicial.setEditable(false);
        btnCrearProducto.setEnabled(false);
    }

    /** Desbloquea los campos del formulario para crear un nuevo producto */
    private void desbloquearFormularioProducto() {
        txtCodigo.setEditable(true);
        txtNombre.setEditable(true);
        txtPrecio.setEditable(true);
        txtCantidadInicial.setEditable(true);
        btnCrearProducto.setEnabled(true);
    }

    /** Limpia todos los campos del formulario de creación */
    private void limpiarFormularioProducto() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidadInicial.setText("");
        txtCantidadOperacion.setText("");
    }

    private void aplicarEstilos() {
        estilizarCampoTexto(txtCodigo);
        estilizarCampoTexto(txtNombre);
        estilizarCampoTexto(txtPrecio);
        estilizarCampoTexto(txtCantidadInicial);
        estilizarCampoTexto(txtCantidadOperacion);
    }

    private void actualizarVista() {
        if (productoActual == null) {
            txtCantidadOperacion.setEnabled(false);
            btnAgregarStock.setEnabled(false);
            btnExtraerStock.setEnabled(false);
            lblValorStock.setText("-");
            lblValorInventario.setText("S/. --");
        } else {
            txtCantidadOperacion.setEnabled(true);
            btnAgregarStock.setEnabled(true);
            btnExtraerStock.setEnabled(true);

            lblValorStock.setText(String.valueOf(productoActual.consultarStock()));
            lblValorInventario.setText(String.format("S/. %.2f", productoActual.obtenerValorTotal()));

            modeloTabla.setRowCount(0);
            List<Movimiento> historial = productoActual.getHistorialMovimientos();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            for (Movimiento mov : historial) {
                modeloTabla.addRow(new Object[] {
                        mov.getFecha().format(formatter),
                        mov.getTipo().getDescripcion(),
                        mov.getCantidad(),
                        mov.getTipo().name()
                });
            }
        }
    }

    private void estilizarCampoTexto(JTextField campo) {
        campo.setFont(FONT_BODY);
        campo.setForeground(COLOR_TEXT_MAIN);
        campo.setCaretColor(COLOR_TEXT_MAIN);
        campo.setDisabledTextColor(COLOR_TEXT_MUTED);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        campo.setBackground(Color.WHITE);
        campo.setOpaque(true);
        // Forzar colores para evitar que el tema del sistema (GTK) los sobreescriba
        campo.putClientProperty("JTextField.background", Color.WHITE);
        campo.putClientProperty("JTextField.foreground", COLOR_TEXT_MAIN);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FONT_BODY);
        etiqueta.setForeground(COLOR_TEXT_MAIN);
        return etiqueta;
    }

    private void configurarEstilosTabla() {
        tablaHistorial.setFont(FONT_BODY);
        tablaHistorial.setRowHeight(30);
        tablaHistorial.setShowVerticalLines(false);
        tablaHistorial.setGridColor(COLOR_BORDER);
        tablaHistorial.setBackground(Color.WHITE);
        tablaHistorial.setForeground(COLOR_TEXT_MAIN);
        tablaHistorial.setSelectionBackground(new Color(37, 99, 235, 40));
        tablaHistorial.setSelectionForeground(COLOR_TEXT_MAIN);
        tablaHistorial.setOpaque(true);

        JTableHeader header = tablaHistorial.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Renderer personalizado con header oscuro propio
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(COLOR_PRIMARY);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(FONT_BODY_BOLD);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACCENT),
                        BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                // Fondo alternado para mejor legibilidad
                if (isSelected) {
                    c.setBackground(new Color(219, 234, 254)); // Azul claro selección
                } else if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(248, 250, 252)); // Gris muy suave
                }

                if (column == 1) {
                    String tipo = value.toString();
                    if (tipo.contains("Entrada")) {
                        c.setForeground(COLOR_SUCCESS);
                        c.setFont(FONT_BODY_BOLD);
                    } else {
                        c.setForeground(COLOR_DANGER);
                        c.setFont(FONT_BODY_BOLD);
                    }
                } else {
                    c.setForeground(COLOR_TEXT_MAIN);
                }
                return c;
            }
        };

        for (int i = 0; i < tablaHistorial.getColumnCount(); i++) {
            tablaHistorial.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    /** Muestra un diálogo de error con diseño personalizado acorde al tema */
    private void mostrarErrorDialog(String mensaje) {
        mostrarDialogoPersonalizado(mensaje, "Error Operacional", COLOR_DANGER, "✖");
    }

    /** Muestra un diálogo de éxito con diseño personalizado acorde al tema */
    private void mostrarExitoDialog(String mensaje) {
        mostrarDialogoPersonalizado(mensaje, "Operación Exitosa", COLOR_SUCCESS, "✔");
    }

    /** Crea y muestra un diálogo modal personalizado con diseño moderno */
    private void mostrarDialogoPersonalizado(String mensaje, String titulo, Color colorTema, String icono) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 200);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Barra de color superior
        JPanel barraColor = new JPanel();
        barraColor.setBackground(colorTema);
        barraColor.setPreferredSize(new Dimension(dialog.getWidth(), 6));
        mainPanel.add(barraColor, BorderLayout.NORTH);

        // Contenido central
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(new EmptyBorder(20, 30, 10, 30));

        // Icono grande
        JLabel lblIcono = new JLabel(icono, JLabel.CENTER);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        lblIcono.setForeground(colorTema);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(lblIcono);
        contenido.add(Box.createVerticalStrut(8));

        // Título
        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setFont(FONT_SUBTITLE);
        lblTitulo.setForeground(COLOR_TEXT_MAIN);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(lblTitulo);
        contenido.add(Box.createVerticalStrut(8));

        // Mensaje
        JLabel lblMensaje = new JLabel("<html><div style='text-align:center;width:300px;'>"
                + mensaje + "</div></html>", JLabel.CENTER);
        lblMensaje.setFont(FONT_BODY);
        lblMensaje.setForeground(COLOR_TEXT_MUTED);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(lblMensaje);

        mainPanel.add(contenido, BorderLayout.CENTER);

        // Botón de cerrar
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setBorder(new EmptyBorder(5, 0, 15, 0));
        BotonEstilizado btnCerrar = new BotonEstilizado("  Aceptar  ", colorTema);
        btnCerrar.setPreferredSize(new Dimension(140, 36));
        btnCerrar.addActionListener(ev -> dialog.dispose());
        panelBoton.add(btnCerrar);
        mainPanel.add(panelBoton, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setVisible(true);
    }

    /** Componente personalizado: Tarjeta contenedora con bordes redondeados */
    private static class TarjetasRounded extends JPanel {
        private final String tituloSeccion;

        public TarjetasRounded(String titulo) {
            this.tituloSeccion = titulo;
            setOpaque(false);
            setBorder(new EmptyBorder(40, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(COLOR_CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

            g2.setColor(COLOR_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);

            g2.setColor(COLOR_PRIMARY);
            g2.setFont(FONT_SUBTITLE);
            g2.drawString(tituloSeccion, 20, 25);

            g2.dispose();
        }
    }

    /** Componente personalizado: Botón moderno con hover nativo */
    private static class BotonEstilizado extends JButton {
        private final Color fondoBase;
        private boolean isHovered = false;

        public BotonEstilizado(String texto, Color fondo) {
            super(texto);
            this.fondoBase = fondo;
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(FONT_BODY_BOLD);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(COLOR_TEXT_MUTED);
            } else {
                g2.setColor(isHovered ? fondoBase.darker() : fondoBase);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Usar CrossPlatform LAF para garantizar colores consistentes
                // El System LAF (GTK en Linux) sobreescribe fondos/textos con
                // colores oscuros del tema del sistema, haciendo ilegibles los campos.
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                // Forzar colores claros para componentes de texto
                UIManager.put("TextField.background", Color.WHITE);
                UIManager.put("TextField.foreground", new Color(15, 23, 42));
                UIManager.put("TextField.caretForeground", new Color(15, 23, 42));
                UIManager.put("TextArea.background", Color.WHITE);
                UIManager.put("TextArea.foreground", new Color(15, 23, 42));
                UIManager.put("Table.background", Color.WHITE);
                UIManager.put("Table.foreground", new Color(15, 23, 42));
                UIManager.put("Table.selectionBackground", new Color(219, 234, 254));
                UIManager.put("Table.selectionForeground", new Color(15, 23, 42));
                UIManager.put("OptionPane.background", Color.WHITE);
                UIManager.put("Panel.background", new Color(241, 245, 249));
            } catch (Exception e) {
                // Fallback silencioso
            }
            new GestorInventarioERP().setVisible(true);
        });
    }
}