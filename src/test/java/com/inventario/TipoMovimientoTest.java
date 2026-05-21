package com.inventario;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas del enum TipoMovimiento")
@Tag("unit")
class TipoMovimientoTest {

    @Nested
    @DisplayName("Pruebas de estructura del enum")
    class EstructuraEnumTests {

        @Test
        @DisplayName("Enum tiene exactamente 2 valores")
        @Description("Verifica que el enum TipoMovimiento contenga exactamente dos constantes")
        @Severity(SeverityLevel.NORMAL)
        void enumTieneDosValores() {

            assertEquals(
                    2,
                    TipoMovimiento.values().length);

        }

        @ParameterizedTest(name = "valueOf(\"{0}\") debe funcionar")
        @EnumSource(TipoMovimiento.class)
        @DisplayName("valueOf funciona para todos los tipos")
        @Description("Verifica que el método valueOf funcione correctamente para todos los valores del enum")
        @Severity(SeverityLevel.NORMAL)
        void valueOfFunciona(TipoMovimiento tipo) {

            assertEquals(
                    tipo,
                    TipoMovimiento.valueOf(tipo.name()));

        }

        @Test
        @DisplayName("valueOf con valor inválido lanza excepción")
        @Description("Debe lanzar IllegalArgumentException cuando se intenta obtener un valor inexistente del enum")
        @Severity(SeverityLevel.CRITICAL)
        void valueOfInvalido() {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> TipoMovimiento.valueOf("INVALIDO"));

        }
    }

    @Nested
    @DisplayName("Pruebas de descripciones")
    class DescripcionTests {

        @Test
        @DisplayName("ENTRADA tiene descripción correcta")
        @Description("Verifica que el tipo ENTRADA tenga la descripción esperada")
        @Severity(SeverityLevel.NORMAL)
        void entradaDescripcion() {

            assertEquals(
                    "Entrada de stock",
                    TipoMovimiento.ENTRADA.getDescripcion());

        }

        @Test
        @DisplayName("SALIDA tiene descripción correcta")
        @Description("Verifica que el tipo SALIDA tenga la descripción esperada")
        @Severity(SeverityLevel.NORMAL)
        void salidaDescripcion() {

            assertEquals(
                    "Salida de stock",
                    TipoMovimiento.SALIDA.getDescripcion());

        }

        @Test
        @DisplayName("Descripción debe ser String")
        @Description("Verifica que la descripción retornada sea de tipo String")
        @Severity(SeverityLevel.MINOR)
        void descripcionEsString() {

            assertInstanceOf(
                    String.class,
                    TipoMovimiento.ENTRADA.getDescripcion());

        }
    }
}