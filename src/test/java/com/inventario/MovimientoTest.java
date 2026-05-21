package com.inventario;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la clase Movimiento")
@Tag("unit")
class MovimientoTest {

    private Movimiento movEntrada;
    private Movimiento movSalida;

    @BeforeEach
    void setUp() {

        movEntrada = new Movimiento(TipoMovimiento.ENTRADA, 10);
        movSalida = new Movimiento(TipoMovimiento.SALIDA, 5);

    }

    @Nested
    @DisplayName("Pruebas de creación de movimientos")
    class CreacionMovimientoTests {

        @Test
        @DisplayName("Crear movimiento de ENTRADA válido")
        @Description("Verifica que un movimiento de entrada se cree correctamente con tipo, cantidad y fecha")
        @Severity(SeverityLevel.CRITICAL)
        void crearEntradaValida() {

            assertAll(
                    () -> assertEquals(TipoMovimiento.ENTRADA, movEntrada.getTipo()),
                    () -> assertEquals(10, movEntrada.getCantidad()),
                    () -> assertNotNull(movEntrada.getFecha()));

        }

        @Test
        @DisplayName("Crear movimiento de SALIDA válido")
        @Description("Verifica que un movimiento de salida se cree correctamente")
        @Severity(SeverityLevel.CRITICAL)
        void crearSalidaValida() {

            assertAll(
                    () -> assertEquals(TipoMovimiento.SALIDA, movSalida.getTipo()),
                    () -> assertEquals(5, movSalida.getCantidad()),
                    () -> assertNotNull(movSalida.getFecha()));

        }

        @ParameterizedTest(name = "Tipo: {0}")
        @EnumSource(TipoMovimiento.class)
        @DisplayName("Crear movimiento con cada tipo del enum")
        @Description("Verifica que el constructor funcione correctamente con todos los tipos del enum")
        @Severity(SeverityLevel.NORMAL)
        void crearConCadaTipo(TipoMovimiento tipo) {

            Movimiento m = new Movimiento(tipo, 1);

            assertEquals(tipo, m.getTipo());

        }
    }

    @Nested
    @DisplayName("Pruebas de validaciones")
    class ValidacionesTests {

        @Test
        @DisplayName("Excepción si tipo es nulo")
        @Description("Debe lanzar IllegalArgumentException cuando el tipo de movimiento es nulo")
        @Severity(SeverityLevel.BLOCKER)
        void debeLanzarExcepcionSiTipoEsNulo() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(null, 10));

            assertEquals(
                    "El tipo de movimiento no puede ser nulo",
                    ex.getMessage());

        }

        @Test
        @DisplayName("Excepción si cantidad es cero")
        @Description("Debe lanzar excepción cuando la cantidad es igual a cero")
        @Severity(SeverityLevel.CRITICAL)
        void debeLanzarExcepcionSiCantidadEsCero() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(TipoMovimiento.ENTRADA, 0));

            assertEquals(
                    "La cantidad del movimiento debe ser positiva",
                    ex.getMessage());

        }

        @Test
        @DisplayName("Excepción si cantidad es negativa")
        @Description("Debe lanzar excepción cuando la cantidad es negativa")
        @Severity(SeverityLevel.CRITICAL)
        void debeLanzarExcepcionSiCantidadEsNegativa() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(TipoMovimiento.SALIDA, -5));

            assertEquals(
                    "La cantidad del movimiento debe ser positiva",
                    ex.getMessage());

        }

        @Test
        @DisplayName("Excepción si fecha es nula")
        @Description("Debe lanzar excepción cuando la fecha del movimiento es nula")
        @Severity(SeverityLevel.CRITICAL)
        void debeLanzarExcepcionSiFechaEsNula() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(
                            TipoMovimiento.ENTRADA,
                            10,
                            null));

            assertEquals(
                    "La fecha del movimiento no puede ser nula",
                    ex.getMessage());

        }

        @Test
        @DisplayName("Excepción si tipo es nulo en constructor con fecha")
        @Description("Debe lanzar excepción cuando el tipo es nulo en constructor con fecha")
        @Severity(SeverityLevel.BLOCKER)
        void debeLanzarExcepcionSiTipoEsNuloConFecha() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(
                            null,
                            10,
                            LocalDateTime.now()));

            assertEquals(
                    "El tipo de movimiento no puede ser nulo",
                    ex.getMessage());

        }

        @Test
        @DisplayName("Excepción si cantidad es cero en constructor con fecha")
        @Description("Debe lanzar excepción cuando la cantidad es cero en constructor con fecha")
        @Severity(SeverityLevel.CRITICAL)
        void debeLanzarExcepcionSiCantidadEsCeroConFecha() {

            var ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> new Movimiento(
                            TipoMovimiento.SALIDA,
                            0,
                            LocalDateTime.now()));

            assertEquals(
                    "La cantidad del movimiento debe ser positiva",
                    ex.getMessage());

        }
    }

    @Nested
    @DisplayName("Pruebas de fecha y tipos")
    class FechaYTiposTests {

        @Test
        @DisplayName("Fecha se registra automáticamente cercana a ahora")
        @Description("Verifica que la fecha del movimiento se registre automáticamente")
        @Severity(SeverityLevel.MINOR)
        void fechaCercanaAhora() {

            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            Movimiento m = new Movimiento(
                    TipoMovimiento.ENTRADA,
                    1);

            LocalDateTime despues = LocalDateTime.now().plusSeconds(1);

            assertAll(
                    () -> assertTrue(
                            m.getFecha().isAfter(antes)
                                    || m.getFecha().isEqual(antes)),

                    () -> assertTrue(
                            m.getFecha().isBefore(despues)
                                    || m.getFecha().isEqual(despues)));

        }

        @Test
        @DisplayName("Tipo debe ser TipoMovimiento")
        @Description("Verifica que el tipo retornado pertenezca al enum TipoMovimiento")
        void tipoEsTipoMovimiento() {

            assertInstanceOf(
                    TipoMovimiento.class,
                    movEntrada.getTipo());

        }

        @Test
        @DisplayName("Cantidad debe ser Integer")
        @Description("Verifica que la cantidad sea de tipo Integer")
        void cantidadEsInteger() {

            assertInstanceOf(
                    Integer.class,
                    (Object) movEntrada.getCantidad());

        }

        @Test
        @DisplayName("Fecha debe ser LocalDateTime")
        @Description("Verifica que la fecha sea de tipo LocalDateTime")
        void fechaEsLocalDateTime() {

            assertInstanceOf(
                    LocalDateTime.class,
                    movEntrada.getFecha());

        }
    }

    @Nested
    @DisplayName("Pruebas de métodos adicionales")
    class MetodosAdicionalesTests {

        @Test
        @DisplayName("toString contiene información del movimiento")
        @Description("Verifica que el método toString incluya información importante del movimiento")
        @Severity(SeverityLevel.NORMAL)
        void toStringInfo() {

            String s = movEntrada.toString();

            assertAll(
                    () -> assertTrue(s.contains("ENTRADA")),
                    () -> assertTrue(s.contains("10")));

        }

        @Test
        @DisplayName("Constructor con fecha explícita funciona correctamente")
        @Description("Verifica que el constructor con fecha explícita almacene correctamente los datos")
        @Severity(SeverityLevel.NORMAL)
        void constructorConFecha() {

            LocalDateTime fecha = LocalDateTime.of(2026, 1, 15, 10, 30);

            Movimiento m = new Movimiento(
                    TipoMovimiento.ENTRADA,
                    50,
                    fecha);

            assertAll(
                    () -> assertEquals(fecha, m.getFecha()),
                    () -> assertEquals(50, m.getCantidad()));

        }
    }
}