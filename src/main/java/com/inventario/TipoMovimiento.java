package com.inventario;

/**
 * Enumeración que define los tipos de movimiento de inventario.
 */
public enum TipoMovimiento {
    ENTRADA("Entrada de stock"),
    SALIDA("Salida de stock");

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible del tipo de movimiento.
     * @return descripción del tipo de movimiento
     */
    public String getDescripcion() {
        return descripcion;
    }
}
