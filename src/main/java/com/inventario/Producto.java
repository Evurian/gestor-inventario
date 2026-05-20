package com.inventario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un producto en el inventario.
 * Mantiene control del stock disponible y registra todos los movimientos
 * (entradas y salidas) con su fecha y hora correspondiente.
 */
public class Producto {

    private final String codigo;
    private String nombre;
    private double precio;
    private int cantidad;
    private final List<Movimiento> historialMovimientos;

    /**
     * Crea un nuevo producto con los atributos especificados.
     *
     * @param codigo   código único del producto (no puede ser nulo ni vacío)
     * @param nombre   nombre del producto (no puede ser nulo ni vacío)
     * @param precio   precio unitario del producto (debe ser positivo)
     * @param cantidad cantidad inicial en stock (no puede ser negativa)
     * @throws IllegalArgumentException si el código es nulo o vacío
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     * @throws IllegalArgumentException si el precio no es positivo
     * @throws IllegalArgumentException si la cantidad es negativa
     */
    public Producto(String codigo, String nombre, double precio, int cantidad) {
        validarCodigo(codigo);
        validarNombre(nombre);
        validarPrecio(precio);
        validarCantidadNoNegativa(cantidad);

        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.precio = precio;
        this.cantidad = cantidad;
        this.historialMovimientos = new ArrayList<>();
    }

    // ==================== VALIDACIONES ====================

    private void validarCodigo(String codigo) {
        if (codigo == null) {
            throw new IllegalArgumentException("El código del producto no puede ser nulo");
        }
        if (codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del producto no puede estar vacío");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre del producto no puede ser nulo");
        }
        if (nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
    }

    private void validarPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser positivo");
        }
        if (Double.isNaN(precio)) {
            throw new IllegalArgumentException("El precio del producto no puede ser NaN");
        }
        if (Double.isInfinite(precio)) {
            throw new IllegalArgumentException("El precio del producto no puede ser infinito");
        }
    }

    private void validarCantidadNoNegativa(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }

    // ==================== OPERACIONES DE STOCK ====================

    /**
     * Agrega stock al producto y registra el movimiento.
     *
     * @param cantidad la cantidad a agregar (debe ser positiva)
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public void agregarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a agregar debe ser positiva");
        }
        this.cantidad += cantidad;
        historialMovimientos.add(new Movimiento(TipoMovimiento.ENTRADA, cantidad));
    }

    /**
     * Extrae stock del producto y registra el movimiento.
     *
     * @param cantidad la cantidad a extraer (debe ser positiva y no exceder el stock disponible)
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     * @throws IllegalStateException    si no hay suficiente stock disponible
     */
    public void extraerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a extraer debe ser positiva");
        }
        if (cantidad > this.cantidad) {
            throw new IllegalStateException(
                    String.format("Stock insuficiente. Disponible: %d, solicitado: %d",
                            this.cantidad, cantidad));
        }
        this.cantidad -= cantidad;
        historialMovimientos.add(new Movimiento(TipoMovimiento.SALIDA, cantidad));
    }

    /**
     * Consulta el stock disponible del producto.
     *
     * @return la cantidad actual en stock
     */
    public int consultarStock() {
        return this.cantidad;
    }

    /**
     * Calcula el valor total del inventario para este producto.
     *
     * @return precio * cantidad
     */
    public double obtenerValorTotal() {
        return this.precio * this.cantidad;
    }

    // ==================== GETTERS ====================

    /**
     * @return el código del producto
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @return el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return el precio unitario del producto
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @return la cantidad actual en stock
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Retorna una lista no modificable del historial de movimientos.
     *
     * @return lista inmutable de movimientos registrados
     */
    public List<Movimiento> getHistorialMovimientos() {
        return Collections.unmodifiableList(historialMovimientos);
    }

    // ==================== SETTERS CON VALIDACIÓN ====================

    /**
     * Establece un nuevo nombre para el producto.
     *
     * @param nombre nuevo nombre (no puede ser nulo ni vacío)
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre.trim();
    }

    /**
     * Establece un nuevo precio para el producto.
     *
     * @param precio nuevo precio (debe ser positivo)
     * @throws IllegalArgumentException si el precio no es positivo
     */
    public void setPrecio(double precio) {
        validarPrecio(precio);
        this.precio = precio;
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return String.format("Producto{codigo='%s', nombre='%s', precio=%.2f, cantidad=%d, valorTotal=%.2f}",
                codigo, nombre, precio, cantidad, obtenerValorTotal());
    }
}
