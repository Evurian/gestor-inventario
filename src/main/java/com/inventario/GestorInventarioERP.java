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

    private static final Color COLOR_SUCCESS = new Color(16, 185, 129); // Entradas (Verde)
    private static final Color COLOR_DANGER = new Color(239, 68, 68); // Salidas (Rojo)

    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42); // Texto Principal
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Texto Deshabilitado

    // --- CONSTANTES DE TIPOGRAFÍA ---
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);

    // --- ESTADO DE LA APLICACIÓN ---
    private Producto productoActual;

    // --- COMPONENTES DE LA INTERFAZ ---
    private JTextField txtCodigo, txtNombre, txtPrecio, txtCantidadInicial;
    private JButton btnCrearProducto;

    private JTextField txtCantidadOperacion;
    private JButton btnAgregarStock, btnExtraerStock;

    private JLabel lblValorStock, lblValorInventario;
    private JTable tablaHistorial;
    private DefaultTableModel modeloTabla;

    public GestorInventarioERP() {
        setTitle("ERP - Sistema de Gestión de Inventario");
        setSize(950, 650);
        setMinimumSize(new Dimension(800, 600));
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

        panelRegistro.add(new JLabel("Código Único:"));
        txtCodigo = new JTextField();
        panelRegistro.add(txtCodigo);

        panelRegistro.add(new JLabel("Nombre del Producto:"));
        txtNombre = new JTextField();
        panelRegistro.add(txtNombre);

        panelRegistro.add(new JLabel("Precio Unitario (S/.):"));
        txtPrecio = new JTextField();
        panelRegistro.add(txtPrecio);

        panelRegistro.add(new JLabel("Stock Inicial:"));
        txtCantidadInicial = new JTextField();
        panelRegistro.add(txtCantidadInicial);

        panelRegistro.add(new JLabel("")); // Espacio
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
        panelOperaciones.add(new JLabel("Cantidad a operar:"), gbcOp);

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
    }

    private void configurarEventos() {
        btnCrearProducto.addActionListener(e -> {
            try {
                String codigo = txtCodigo.getText().trim();
                String nombre = txtNombre.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int cantidad = Integer.parseInt(txtCantidadInicial.getText().trim());

                productoActual = new Producto(codigo, nombre, precio, cantidad);

                txtCodigo.setEditable(false);
                txtNombre.setEditable(false);
                txtPrecio.setEditable(false);
                txtCantidadInicial.setEditable(false);
                btnCrearProducto.setEnabled(false);

                actualizarVista();
                JOptionPane.showMessageDialog(this, "Producto registrado correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                mostrarErrorDialog("Los valores numéricos de precio o cantidad contienen formatos inválidos.");
            } catch (IllegalArgumentException ex) {
                mostrarErrorDialog(ex.getMessage());
            }
        });

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
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        campo.setBackground(Color.WHITE);
    }

    private void configurarEstilosTabla() {
        tablaHistorial.setFont(FONT_BODY);
        tablaHistorial.setRowHeight(30);
        tablaHistorial.setShowVerticalLines(false);
        tablaHistorial.setGridColor(COLOR_BORDER);
        tablaHistorial.setSelectionBackground(new Color(239, 246, 255));
        tablaHistorial.setSelectionForeground(COLOR_TEXT_MAIN);

        JTableHeader header = tablaHistorial.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

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

    private void mostrarErrorDialog(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error Operacional", JOptionPane.ERROR_MESSAGE);
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
                g2.setColor(isHovered ? fondoBase.brighter() : fondoBase);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fallback silencioso
            }
            new GestorInventarioERP().setVisible(true);
        });
    }
}