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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de la clase Producto")
@Tag("unit")
class ProductoTest {

  private Producto producto;

  @BeforeEach
  void setUp() {
    producto = new Producto("PROD-001", "Laptop HP", 1500.00, 10);
  }

  @Nested
  @DisplayName("Pruebas de creación de productos")
  class CreacionProductoTests {

    @Test
    @DisplayName("Crear producto válido con todos los atributos")
    @Description("Verifica que un producto se cree correctamente con código, nombre, precio y cantidad")
    @Severity(SeverityLevel.CRITICAL)
    void crearProductoValido() {
      assertAll(
          () -> assertEquals("PROD-001", producto.getCodigo()),
          () -> assertEquals("Laptop HP", producto.getNombre()),
          () -> assertEquals(1500.00, producto.getPrecio(), 0.001),
          () -> assertEquals(10, producto.getCantidad()),
          () -> assertTrue(producto.getHistorialMovimientos().isEmpty()));
    }

    @Test
    @DisplayName("Crear producto con cantidad cero")
    @Description("Verifica que se pueda crear un producto sin stock inicial")
    @Severity(SeverityLevel.NORMAL)
    void crearProductoCantidadCero() {
      Producto p = new Producto("P2", "Mouse", 25.50, 0);
      assertAll(
          () -> assertEquals(0, p.consultarStock()),
          () -> assertEquals(0.0, p.obtenerValorTotal(), 0.001));
    }

    @Test
    @DisplayName("Recortar espacios del código y nombre")
    @Description("Verifica que el constructor elimine espacios en blanco al inicio y final")
    @Severity(SeverityLevel.MINOR)
    void recortarEspacios() {
      Producto p = new Producto("  P3  ", "  Teclado  ", 45.00, 5);
      assertAll(
          () -> assertEquals("P3", p.getCodigo()),
          () -> assertEquals("Teclado", p.getNombre()));
    }

    @ParameterizedTest(name = "Combinación: {0}, {1}, ${2}, {3} unidades")
    @CsvSource({
        "A, Pan, 0.50, 0",
        "PROD-999, Laptop Gaming, 5000.0, 1",
        "X-1, Item-Especial, 0.01, 999999"
    })
    @DisplayName("Crear productos con diversas combinaciones válidas")
    @Description("Verifica que el constructor funcione con diferentes valores válidos")
    @Severity(SeverityLevel.NORMAL)
    void diversasCombinaciones(String cod, String nom, double pre, int cant) {
      Producto p = new Producto(cod, nom, pre, cant);
      assertAll(
          () -> assertEquals(cod.trim(), p.getCodigo()),
          () -> assertEquals(nom.trim(), p.getNombre()),
          () -> assertEquals(pre, p.getPrecio(), 0.001),
          () -> assertEquals(cant, p.getCantidad()));
    }

    @Test
    @DisplayName("Precio muy pequeño cercano a cero es válido")
    @Description("Verifica que el precio mínimo positivo sea aceptado")
    @Severity(SeverityLevel.MINOR)
    void precioMuyPequeno() {
      Producto p = new Producto("MIN", "Mínimo", 0.001, 1);
      assertEquals(0.001, p.getPrecio(), 0.0001);
    }
  }

  @Nested
  @DisplayName("Pruebas de validaciones del constructor")
  class ValidacionesConstructorTests {

    @Test
    @DisplayName("Excepción si código es nulo")
    @Description("Debe lanzar IllegalArgumentException cuando el código es nulo")
    @Severity(SeverityLevel.BLOCKER)
    void excepcionCodigoNulo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto(null, "X", 10.0, 1));
      assertEquals("El código del producto no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si código está vacío o solo espacios")
    @Description("Debe lanzar excepción cuando el código es vacío o solo espacios")
    @Severity(SeverityLevel.BLOCKER)
    void excepcionCodigoVacio() {
      assertAll(
          () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Producto("", "X", 10.0, 1));
            assertEquals("El código del producto no puede estar vacío", ex.getMessage());
          },
          () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Producto("   ", "X", 10.0, 1));
            assertEquals("El código del producto no puede estar vacío", ex.getMessage());
          });
    }

    @Test
    @DisplayName("Excepción si nombre es nulo")
    @Description("Debe lanzar IllegalArgumentException cuando el nombre es nulo")
    @Severity(SeverityLevel.BLOCKER)
    void excepcionNombreNulo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto("P1", null, 10.0, 1));
      assertEquals("El nombre del producto no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si nombre está vacío o solo espacios")
    @Description("Debe lanzar excepción cuando el nombre es vacío o solo espacios")
    @Severity(SeverityLevel.BLOCKER)
    void excepcionNombreVacio() {
      assertAll(
          () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Producto("P1", "", 10.0, 1));
            assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
          },
          () -> {
            var ex = assertThrows(IllegalArgumentException.class,
                () -> new Producto("P1", "   ", 10.0, 1));
            assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
          });
    }

    @ParameterizedTest(name = "Precio inválido: {0}")
    @ValueSource(doubles = { 0.0, -1.0, -100.50, Double.NEGATIVE_INFINITY })
    @DisplayName("Excepción para precios no positivos")
    @Description("Debe lanzar excepción cuando el precio es cero, negativo o infinito negativo")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionPrecioNoPositivo(double precio) {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto("P1", "X", precio, 5));
      assertEquals("El precio del producto debe ser positivo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si cantidad inicial es negativa")
    @Description("Debe lanzar excepción cuando la cantidad inicial es negativa")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionCantidadNegativa() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto("P1", "X", 10.0, -1));
      assertEquals("La cantidad no puede ser negativa", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si precio es NaN")
    @Description("Debe lanzar excepción cuando el precio es NaN")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionPrecioNaN() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto("P1", "X", Double.NaN, 5));
      assertEquals("El precio del producto no puede ser NaN", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción si precio es infinito positivo")
    @Description("Debe lanzar excepción cuando el precio es infinito positivo")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionPrecioInfinitoPositivo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> new Producto("P1", "X", Double.POSITIVE_INFINITY, 5));
      assertEquals("El precio del producto no puede ser infinito", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("Pruebas de agregar stock")
  class AgregarStockTests {

    @Test
    @DisplayName("Agregar stock correctamente")
    @Description("Verifica que agregar stock incremente la cantidad correctamente")
    @Severity(SeverityLevel.CRITICAL)
    void agregarStockCorrecto() {
      producto.agregarStock(5);
      assertEquals(15, producto.consultarStock());
    }

    @ParameterizedTest(name = "Agregar {0} unidades")
    @ValueSource(ints = { 1, 50, 100, 999 })
    @DisplayName("Agregar diversas cantidades válidas")
    @Description("Verifica que agregar diferentes cantidades funcione correctamente")
    @Severity(SeverityLevel.NORMAL)
    void agregarDiversasCantidades(int cantidad) {
      producto.agregarStock(cantidad);
      assertEquals(10 + cantidad, producto.consultarStock());
    }

    @Test
    @DisplayName("Registrar movimiento ENTRADA al agregar")
    @Description("Verifica que al agregar stock se cree un movimiento de tipo ENTRADA")
    @Severity(SeverityLevel.CRITICAL)
    void registrarMovimientoEntrada() {
      producto.agregarStock(20);
      var movimientos = producto.getHistorialMovimientos();

      assertAll(
          () -> assertEquals(1, movimientos.size()),
          () -> assertEquals(TipoMovimiento.ENTRADA, movimientos.get(0).getTipo()),
          () -> assertEquals(20, movimientos.get(0).getCantidad()),
          () -> assertNotNull(movimientos.get(0).getFecha()));
    }

    @Test
    @DisplayName("Excepción al agregar stock cero")
    @Description("Debe lanzar excepción cuando la cantidad a agregar es cero")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionAgregarCero() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.agregarStock(0));
      assertEquals("La cantidad a agregar debe ser positiva", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción al agregar stock negativo")
    @Description("Debe lanzar excepción cuando la cantidad a agregar es negativa")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionAgregarNegativo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.agregarStock(-5));
      assertEquals("La cantidad a agregar debe ser positiva", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("Pruebas de extraer stock")
  class ExtraerStockTests {

    @Test
    @DisplayName("Extraer stock correctamente")
    @Description("Verifica que extraer stock decremente la cantidad correctamente")
    @Severity(SeverityLevel.CRITICAL)
    void extraerStockCorrecto() {
      producto.extraerStock(3);
      assertEquals(7, producto.consultarStock());
    }

    @Test
    @DisplayName("Extraer todo el stock dejando en cero")
    @Description("Verifica que se pueda extraer todo el stock disponible")
    @Severity(SeverityLevel.NORMAL)
    void extraerTodoStock() {
      producto.extraerStock(10);
      assertEquals(0, producto.consultarStock());
    }

    @Test
    @DisplayName("Registrar movimiento SALIDA al extraer")
    @Description("Verifica que al extraer stock se cree un movimiento de tipo SALIDA")
    @Severity(SeverityLevel.CRITICAL)
    void registrarMovimientoSalida() {
      producto.extraerStock(5);
      var movimientos = producto.getHistorialMovimientos();

      assertAll(
          () -> assertEquals(1, movimientos.size()),
          () -> assertEquals(TipoMovimiento.SALIDA, movimientos.get(0).getTipo()),
          () -> assertEquals(5, movimientos.get(0).getCantidad()));
    }

    @Test
    @DisplayName("Excepción al extraer más del disponible")
    @Description("Debe lanzar IllegalStateException cuando el stock es insuficiente")
    @Severity(SeverityLevel.BLOCKER)
    void excepcionStockInsuficiente() {
      var ex = assertThrows(IllegalStateException.class,
          () -> producto.extraerStock(15));
      assertTrue(ex.getMessage().contains("Stock insuficiente"));
      assertTrue(ex.getMessage().contains("Disponible: 10"));
    }

    @Test
    @DisplayName("Excepción al extraer de producto sin stock")
    @Description("Debe lanzar excepción cuando el producto tiene stock cero")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionProductoSinStock() {
      Producto p = new Producto("PX", "Vacío", 10.0, 0);
      var ex = assertThrows(IllegalStateException.class, () -> p.extraerStock(1));
      assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }

    @Test
    @DisplayName("Excepción al extraer cantidad cero")
    @Description("Debe lanzar excepción cuando la cantidad a extraer es cero")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionExtraerCero() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.extraerStock(0));
      assertEquals("La cantidad a extraer debe ser positiva", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción al extraer cantidad negativa")
    @Description("Debe lanzar excepción cuando la cantidad a extraer es negativa")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionExtraerNegativo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.extraerStock(-5));
      assertEquals("La cantidad a extraer debe ser positiva", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("Pruebas de consultas y cálculos")
  class ConsultasTests {

    @Test
    @DisplayName("consultarStock retorna cantidad actual")
    @Description("Verifica que consultarStock devuelva el stock actual")
    @Severity(SeverityLevel.NORMAL)
    void consultarStockRetorna() {
      assertEquals(10, producto.consultarStock());
    }

    @Test
    @DisplayName("consultarStock es igual a getCantidad")
    @Description("Verifica que consultarStock y getCantidad devuelvan el mismo valor")
    @Severity(SeverityLevel.MINOR)
    void consultarStockIgualGetCantidad() {
      assertEquals(producto.getCantidad(), producto.consultarStock());
      producto.agregarStock(7);
      assertEquals(producto.getCantidad(), producto.consultarStock());
    }

    @Test
    @DisplayName("obtenerValorTotal retorna precio por cantidad")
    @Description("Verifica que el valor total sea precio multiplicado por cantidad")
    @Severity(SeverityLevel.NORMAL)
    void obtenerValorTotal() {
      assertEquals(15000.00, producto.obtenerValorTotal(), 0.001);
    }

    @Test
    @DisplayName("Valor total es cero cuando sin stock")
    @Description("Verifica que el valor total sea cero cuando el stock es cero")
    @Severity(SeverityLevel.NORMAL)
    void valorTotalCero() {
      Producto p = new Producto("PV", "Vacío", 100.0, 0);
      assertEquals(0.0, p.obtenerValorTotal(), 0.001);
    }

    @Test
    @DisplayName("Valor total se actualiza tras agregar stock")
    @Description("Verifica que el valor total se actualice correctamente después de agregar stock")
    @Severity(SeverityLevel.NORMAL)
    void valorTotalTrasAgregar() {
      producto.agregarStock(5);
      assertEquals(1500.00 * 15, producto.obtenerValorTotal(), 0.001);
    }

    @Test
    @DisplayName("Valor total se actualiza tras extraer stock")
    @Description("Verifica que el valor total se actualice correctamente después de extraer stock")
    @Severity(SeverityLevel.NORMAL)
    void valorTotalTrasExtraer() {
      producto.extraerStock(4);
      assertEquals(1500.00 * 6, producto.obtenerValorTotal(), 0.001);
    }
  }

  @Nested
  @DisplayName("Pruebas de setters y actualizaciones")
  class SettersTests {

    @Test
    @DisplayName("Actualizar nombre correctamente")
    @Description("Verifica que setNombre actualice el nombre correctamente")
    @Severity(SeverityLevel.NORMAL)
    void actualizarNombre() {
      producto.setNombre("Laptop Dell");
      assertEquals("Laptop Dell", producto.getNombre());
    }

    @Test
    @DisplayName("Excepción al setear nombre nulo")
    @Description("Debe lanzar excepción cuando el nombre es nulo")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionSetNombreNulo() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.setNombre(null));
      assertEquals("El nombre del producto no puede ser nulo", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción al setear nombre vacío")
    @Description("Debe lanzar excepción cuando el nombre está vacío")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionSetNombreVacio() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.setNombre(""));
      assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
    }

    @Test
    @DisplayName("Actualizar precio correctamente")
    @Description("Verifica que setPrecio actualice el precio correctamente")
    @Severity(SeverityLevel.NORMAL)
    void actualizarPrecio() {
      producto.setPrecio(2000.00);
      assertEquals(2000.00, producto.getPrecio(), 0.001);
    }

    @Test
    @DisplayName("Excepción al setear precio cero o negativo")
    @Description("Debe lanzar excepción cuando el precio es cero o negativo")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionSetPrecioNoPositivo() {
      assertAll(
          () -> assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(0)),
          () -> assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(-50.0)));
    }

    @Test
    @DisplayName("Excepción al setear precio NaN")
    @Description("Debe lanzar excepción cuando el precio es NaN")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionSetPrecioNaN() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.setPrecio(Double.NaN));
      assertEquals("El precio del producto no puede ser NaN", ex.getMessage());
    }

    @Test
    @DisplayName("Excepción al setear precio infinito")
    @Description("Debe lanzar excepción cuando el precio es infinito")
    @Severity(SeverityLevel.CRITICAL)
    void excepcionSetPrecioInfinito() {
      var ex = assertThrows(IllegalArgumentException.class,
          () -> producto.setPrecio(Double.POSITIVE_INFINITY));
      assertEquals("El precio del producto no puede ser infinito", ex.getMessage());
    }
  }

  @Nested
  @DisplayName("Pruebas del historial de movimientos")
  class HistorialTests {

    @Test
    @DisplayName("Historial registra múltiples movimientos en orden")
    @Description("Verifica que el historial mantenga el orden cronológico de los movimientos")
    @Severity(SeverityLevel.CRITICAL)
    void historialMultiple() {
      producto.agregarStock(5);
      producto.extraerStock(3);
      producto.agregarStock(10);

      var movimientos = producto.getHistorialMovimientos();

      assertAll(
          () -> assertEquals(3, movimientos.size()),
          () -> assertEquals(TipoMovimiento.ENTRADA, movimientos.get(0).getTipo()),
          () -> assertEquals(TipoMovimiento.SALIDA, movimientos.get(1).getTipo()),
          () -> assertEquals(TipoMovimiento.ENTRADA, movimientos.get(2).getTipo()));
    }

    @Test
    @DisplayName("Historial inmutable - no modificable externamente")
    @Description("Verifica que la lista de historial no pueda ser modificada desde fuera")
    @Severity(SeverityLevel.NORMAL)
    void historialInmutable() {
      producto.agregarStock(5);
      var movimientos = producto.getHistorialMovimientos();
      assertThrows(UnsupportedOperationException.class, () -> movimientos.clear());
    }

    @Test
    @DisplayName("Operaciones fallidas no registran movimientos")
    @Description("Verifica que los movimientos fallidos no queden registrados")
    @Severity(SeverityLevel.NORMAL)
    void fallidasNoRegistran() {
      assertAll(
          () -> assertThrows(IllegalStateException.class, () -> producto.extraerStock(100)),
          () -> assertThrows(IllegalArgumentException.class, () -> producto.agregarStock(-1)));
      assertTrue(producto.getHistorialMovimientos().isEmpty());
    }

    @Test
    @DisplayName("Consistencia tras múltiples operaciones")
    @Description("Verifica el comportamiento correcto tras una secuencia compleja de operaciones")
    @Severity(SeverityLevel.NORMAL)
    void consistenciaMultiple() {
      producto.agregarStock(100);
      producto.extraerStock(50);
      producto.agregarStock(30);
      producto.extraerStock(90);

      assertAll(
          () -> assertEquals(0, producto.consultarStock()),
          () -> assertEquals(4, producto.getHistorialMovimientos().size()));
    }
  }

  @Nested
  @DisplayName("Pruebas de tipos de datos")
  class TiposDatoTests {

    @Test
    @DisplayName("Código debe ser String")
    @Description("Verifica que getCodigo retorne String")
    @Severity(SeverityLevel.MINOR)
    void codigoEsString() {
      assertInstanceOf(String.class, producto.getCodigo());
    }

    @Test
    @DisplayName("Nombre debe ser String")
    @Description("Verifica que getNombre retorne String")
    @Severity(SeverityLevel.MINOR)
    void nombreEsString() {
      assertInstanceOf(String.class, producto.getNombre());
    }

    @Test
    @DisplayName("Precio debe ser Double")
    @Description("Verifica que getPrecio retorne Double")
    @Severity(SeverityLevel.MINOR)
    void precioEsDouble() {
      assertInstanceOf(Double.class, (Object) producto.getPrecio());
    }

    @Test
    @DisplayName("Cantidad debe ser Integer")
    @Description("Verifica que getCantidad retorne Integer")
    @Severity(SeverityLevel.MINOR)
    void cantidadEsInteger() {
      assertInstanceOf(Integer.class, (Object) producto.getCantidad());
    }

    @Test
    @DisplayName("ValorTotal debe ser Double")
    @Description("Verifica que obtenerValorTotal retorne Double")
    @Severity(SeverityLevel.MINOR)
    void valorTotalEsDouble() {
      assertInstanceOf(Double.class, (Object) producto.obtenerValorTotal());
    }

    @Test
    @DisplayName("Historial debe ser List de Movimiento")
    @Description("Verifica que getHistorialMovimientos retorne List<Movimiento>")
    @Severity(SeverityLevel.MINOR)
    void historialEsListMovimiento() {
      producto.agregarStock(1);
      var historial = producto.getHistorialMovimientos();

      assertAll(
          () -> assertInstanceOf(List.class, historial),
          () -> assertInstanceOf(Movimiento.class, historial.get(0)));
    }
  }

  @Nested
  @DisplayName("Pruebas del método toString")
  class ToStringTests {

    @Test
    @DisplayName("toString contiene información del producto")
    @Description("Verifica que el método toString incluya información importante del producto")
    @Severity(SeverityLevel.MINOR)
    void toStringInfo() {
      String resultado = producto.toString();

      assertAll(
          () -> assertTrue(resultado.contains("PROD-001")),
          () -> assertTrue(resultado.contains("Laptop HP")));
    }
  }
}