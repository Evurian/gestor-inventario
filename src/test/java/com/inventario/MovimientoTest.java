package com.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la clase Movimiento")
class MovimientoTest {

    private Movimiento movEntrada;
    private Movimiento movSalida;

    @BeforeEach
    void setUp() {
        movEntrada = new Movimiento(TipoMovimiento.ENTRADA, 10);
        movSalida = new Movimiento(TipoMovimiento.SALIDA, 5);
    }

    @Test
    @DisplayName("Crear movimiento de ENTRADA válido")
    void crearEntradaValida() {
        assertEquals(TipoMovimiento.ENTRADA, movEntrada.getTipo());
        assertEquals(10, movEntrada.getCantidad());
        assertNotNull(movEntrada.getFecha());
    }

    @Test
    @DisplayName("Crear movimiento de SALIDA válido")
    void crearSalidaValida() {
        assertEquals(TipoMovimiento.SALIDA, movSalida.getTipo());
        assertEquals(5, movSalida.getCantidad());
        assertNotNull(movSalida.getFecha());
    }

    @Test
    @DisplayName("Excepción si tipo es nulo")
    void excepcionTipoNulo() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(null, 10));
        assertEquals("El tipo de movimiento no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si cantidad es cero")
    void excepcionCantidadCero() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(TipoMovimiento.ENTRADA, 0));
        assertEquals("La cantidad del movimiento debe ser positiva", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si cantidad es negativa")
    void excepcionCantidadNeg() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(TipoMovimiento.SALIDA, -5));
        assertEquals("La cantidad del movimiento debe ser positiva", ex.getMessage());
    }

    @Test
    @DisplayName("Fecha se registra automáticamente cercana a ahora")
    void fechaCercanaAhora() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Movimiento m = new Movimiento(TipoMovimiento.ENTRADA, 1);
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);
        assertTrue(m.getFecha().isAfter(antes) || m.getFecha().isEqual(antes));
        assertTrue(m.getFecha().isBefore(despues) || m.getFecha().isEqual(despues));
    }

    @ParameterizedTest(name = "Tipo: {0}")
    @DisplayName("Crear movimiento con cada tipo del enum")
    @EnumSource(TipoMovimiento.class)
    void crearConCadaTipo(TipoMovimiento tipo) {
        Movimiento m = new Movimiento(tipo, 1);
        assertEquals(tipo, m.getTipo());
    }

    @Test
    @DisplayName("Tipo debe ser TipoMovimiento")
    void tipoEsTipoMovimiento() {
        assertInstanceOf(TipoMovimiento.class, movEntrada.getTipo());
    }

    @Test
    @DisplayName("Cantidad debe ser Integer")
    void cantidadEsInteger() {
        assertInstanceOf(Integer.class, (Object) movEntrada.getCantidad());
    }

    @Test
    @DisplayName("Fecha debe ser LocalDateTime")
    void fechaEsLocalDateTime() {
        assertInstanceOf(LocalDateTime.class, movEntrada.getFecha());
    }

    @Test
    @DisplayName("toString contiene información del movimiento")
    void toStringInfo() {
        String s = movEntrada.toString();
        assertTrue(s.contains("ENTRADA"));
        assertTrue(s.contains("10"));
    }

    // Constructor con fecha explícita

    @Test
    @DisplayName("Constructor con fecha explícita funciona correctamente")
    void constructorConFecha() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 10, 30);
        Movimiento m = new Movimiento(TipoMovimiento.ENTRADA, 50, fecha);
        assertEquals(fecha, m.getFecha());
        assertEquals(50, m.getCantidad());
    }

    @Test
    @DisplayName("Excepción si fecha es nula en constructor con fecha")
    void excepcionFechaNula() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(TipoMovimiento.ENTRADA, 10, null));
        assertEquals("La fecha del movimiento no puede ser nula", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si tipo nulo en constructor con fecha")
    void excepcionTipoNuloConFecha() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(null, 10, LocalDateTime.now()));
        assertEquals("El tipo de movimiento no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si cantidad cero en constructor con fecha")
    void excepcionCantidadCeroConFecha() {
        var ex = assertThrows(IllegalArgumentException.class,
            () -> new Movimiento(TipoMovimiento.SALIDA, 0, LocalDateTime.now()));
        assertEquals("La cantidad del movimiento debe ser positiva", ex.getMessage());
    }
}
