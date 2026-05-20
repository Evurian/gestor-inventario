package com.inventario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del enum TipoMovimiento")
class TipoMovimientoTest {

    @Test
    @DisplayName("Enum tiene exactamente 2 valores")
    void enumTieneDosValores() {
        assertEquals(2, TipoMovimiento.values().length);
    }

    @Test
    @DisplayName("ENTRADA tiene descripción correcta")
    void entradaDescripcion() {
        assertEquals("Entrada de stock", TipoMovimiento.ENTRADA.getDescripcion());
    }

    @Test
    @DisplayName("SALIDA tiene descripción correcta")
    void salidaDescripcion() {
        assertEquals("Salida de stock", TipoMovimiento.SALIDA.getDescripcion());
    }

    @ParameterizedTest(name = "valueOf(\"{0}\") debe funcionar")
    @EnumSource(TipoMovimiento.class)
    @DisplayName("valueOf funciona para todos los tipos")
    void valueOfFunciona(TipoMovimiento tipo) {
        assertEquals(tipo, TipoMovimiento.valueOf(tipo.name()));
    }

    @Test
    @DisplayName("valueOf con valor inválido lanza excepción")
    void valueOfInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> TipoMovimiento.valueOf("INVALIDO"));
    }

    @Test
    @DisplayName("Descripción debe ser String")
    void descripcionEsString() {
        assertInstanceOf(String.class, TipoMovimiento.ENTRADA.getDescripcion());
    }
}
