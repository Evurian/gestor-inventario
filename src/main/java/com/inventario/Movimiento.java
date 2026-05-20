package com.inventario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un movimiento de inventario (entrada o salida de stock).
 * Registra el tipo de movimiento, la cantidad y la fecha/hora en que ocurrió.
 */
public class Movimiento {

    private final TipoMovimiento tipo;
    private final int cantidad;
    private final LocalDateTime fecha;

    /**
     * Crea un nuevo movimiento de inventario.
     *
     * @param tipo     el tipo de movimiento (ENTRADA o SALIDA)
     * @param cantidad la cantidad del movimiento (debe ser positiva)
     * @throws IllegalArgumentException si el tipo es nulo
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public Movimiento(TipoMovimiento tipo, int cantidad) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser positiva");
        }
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDateTime.now();
    }

    /**
     * Constructor interno para pruebas que permite especificar la fecha.
     *
     * @param tipo     el tipo de movimiento
     * @param cantidad la cantidad del movimiento
     * @param fecha    la fecha y hora del movimiento
     */
    Movimiento(TipoMovimiento tipo, int cantidad, LocalDateTime fecha) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser positiva");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del movimiento no puede ser nula");
        }
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    /**
     * @return el tipo de movimiento (ENTRADA o SALIDA)
     */
    public TipoMovimiento getTipo() {
        return tipo;
    }

    /**
     * @return la cantidad del movimiento
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * @return la fecha y hora en que se registró el movimiento
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s: %d unidades - %s",
                fecha.format(formatter),
                tipo.getDescripcion(),
                cantidad,
                tipo.name());
    }
}
