package com.inventario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Nested
@DisplayName("Pruebas de la clase Producto")
class ProductoTest {

  private Producto producto;

  @BeforeEach
  void setUp() {
    producto = new Producto("PROD-001", "Laptop HP", 1500.00, 10);
  }

  // ===== CREACIÓN =====

  @Test
  @DisplayName("1. Debe crear un producto válido con todos los atributos")
  void debeCrearProductoValido() {
    assertAll(
        () -> assertEquals("PROD-001", producto.getCodigo()),
        () -> assertEquals("Laptop HP", producto.getNombre()),
        () -> assertEquals(1500.00, producto.getPrecio(), 0.001),
        () -> assertEquals(10, producto.getCantidad()),
        () -> assertTrue(producto.getHistorialMovimientos().isEmpty()));
  }

  @Test
  @DisplayName("2. Debe crear producto con cantidad cero")
  void debeCrearProductoCantidadCero() {
    Producto p = new Producto("P2", "Mouse", 25.50, 0);
    assertEquals(0, p.consultarStock());
    assertEquals(0.0, p.obtenerValorTotal(), 0.001);
  }

  @Test
  @DisplayName("3. Debe recortar espacios del código y nombre")
  void debeRecortarEspacios() {
    Producto p = new Producto("  P3  ", "  Teclado  ", 45.00, 5);
    assertEquals("P3", p.getCodigo());
    assertEquals("Teclado", p.getNombre());
  }

  // ===== VALIDACIONES CONSTRUCTOR =====

  @Test
  @DisplayName("4. Excepción si código es nulo")
  void excepcionCodigoNulo() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto(null, "X", 10.0, 1));
    assertEquals("El código del producto no puede ser nulo", ex.getMessage());
  }

  @Test
  @DisplayName("5. Excepción si código está vacío")
  void excepcionCodigoVacio() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("", "X", 10.0, 1));
    assertEquals("El código del producto no puede estar vacío", ex.getMessage());
  }

  @Test
  @DisplayName("6. Excepción si código es solo espacios")
  void excepcionCodigoEspacios() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("   ", "X", 10.0, 1));
    assertEquals("El código del producto no puede estar vacío", ex.getMessage());
  }

  @Test
  @DisplayName("7. Excepción si nombre es nulo")
  void excepcionNombreNulo() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", null, 10.0, 1));
    assertEquals("El nombre del producto no puede ser nulo", ex.getMessage());
  }

  @Test
  @DisplayName("8. Excepción si nombre está vacío")
  void excepcionNombreVacio() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "", 10.0, 1));
    assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
  }

  @ParameterizedTest(name = "Precio inválido: {0}")
  @DisplayName("9. Excepción para precios no positivos")
  @ValueSource(doubles = { 0.0, -1.0, -100.50 })
  void excepcionPrecioNoPositivo(double precio) {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "X", precio, 5));
    assertEquals("El precio del producto debe ser positivo", ex.getMessage());
  }

  @Test
  @DisplayName("10. Excepción si cantidad inicial es negativa")
  void excepcionCantidadNegativa() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "X", 10.0, -1));
    assertEquals("La cantidad no puede ser negativa", ex.getMessage());
  }

  @Test
  @DisplayName("11. Excepción si precio es NaN")
  void excepcionPrecioNaN() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "X", Double.NaN, 5));
    assertEquals("El precio del producto no puede ser NaN", ex.getMessage());
  }

  @Test
  @DisplayName("12. Excepción si precio es infinito positivo")
  void excepcionPrecioInfinito() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "X", Double.POSITIVE_INFINITY, 5));
    assertEquals("El precio del producto no puede ser infinito", ex.getMessage());
  }

  @Test
  @DisplayName("Excepción si precio es infinito negativo")
  void excepcionPrecioInfNeg() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> new Producto("P1", "X", Double.NEGATIVE_INFINITY, 5));
    assertEquals("El precio del producto debe ser positivo", ex.getMessage());
  }

  // ===== AGREGAR STOCK =====

  @Test
  @DisplayName("13. Agregar stock correctamente")
  void agregarStockCorrecto() {
    producto.agregarStock(5);
    assertEquals(15, producto.consultarStock());
  }

  @Test
  @DisplayName("14. Registrar movimiento ENTRADA al agregar")
  void registrarMovimientoEntrada() {
    producto.agregarStock(20);
    var movs = producto.getHistorialMovimientos();
    assertEquals(1, movs.size());
    assertEquals(TipoMovimiento.ENTRADA, movs.get(0).getTipo());
    assertEquals(20, movs.get(0).getCantidad());
    assertNotNull(movs.get(0).getFecha());
  }

  @Test
  @DisplayName("15. Excepción al agregar stock cero")
  void excepcionAgregarCero() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.agregarStock(0));
    assertEquals("La cantidad a agregar debe ser positiva", ex.getMessage());
  }

  @Test
  @DisplayName("16. Excepción al agregar stock negativo")
  void excepcionAgregarNegativo() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.agregarStock(-5));
    assertEquals("La cantidad a agregar debe ser positiva", ex.getMessage());
  }

  @ParameterizedTest(name = "Agregar {0} unidades")
  @DisplayName("Agregar diversas cantidades válidas")
  @ValueSource(ints = { 1, 50, 100, 999 })
  void agregarDiversasCantidades(int cant) {
    producto.agregarStock(cant);
    assertEquals(10 + cant, producto.consultarStock());
  }

  // ===== EXTRAER STOCK =====

  @Test
  @DisplayName("17. Extraer stock correctamente")
  void extraerStockCorrecto() {
    producto.extraerStock(3);
    assertEquals(7, producto.consultarStock());
  }

  @Test
  @DisplayName("18. Registrar movimiento SALIDA al extraer")
  void registrarMovimientoSalida() {
    producto.extraerStock(5);
    var movs = producto.getHistorialMovimientos();
    assertEquals(1, movs.size());
    assertEquals(TipoMovimiento.SALIDA, movs.get(0).getTipo());
    assertEquals(5, movs.get(0).getCantidad());
  }

  @Test
  @DisplayName("19. Excepción al extraer más del disponible")
  void excepcionStockInsuficiente() {
    var ex = assertThrows(IllegalStateException.class,
        () -> producto.extraerStock(15));
    assertTrue(ex.getMessage().contains("Stock insuficiente"));
    assertTrue(ex.getMessage().contains("Disponible: 10"));
  }

  @Test
  @DisplayName("20. Excepción al extraer cantidad cero")
  void excepcionExtraerCero() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.extraerStock(0));
    assertEquals("La cantidad a extraer debe ser positiva", ex.getMessage());
  }

  @Test
  @DisplayName("21. Excepción al extraer cantidad negativa")
  void excepcionExtraerNegativo() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.extraerStock(-5));
    assertEquals("La cantidad a extraer debe ser positiva", ex.getMessage());
  }

  @Test
  @DisplayName("Extraer todo el stock dejando en cero")
  void extraerTodoStock() {
    producto.extraerStock(10);
    assertEquals(0, producto.consultarStock());
  }

  @Test
  @DisplayName("Excepción al extraer de producto sin stock")
  void excepcionProductoSinStock() {
    var p = new Producto("PX", "Vacío", 10.0, 0);
    assertThrows(IllegalStateException.class, () -> p.extraerStock(1));
  }

  // ===== CONSULTAS =====

  @Test
  @DisplayName("22. consultarStock() retorna cantidad actual")
  void consultarStockRetorna() {
    assertEquals(10, producto.consultarStock());
  }

  @Test
  @DisplayName("23. obtenerValorTotal() retorna precio * cantidad")
  void obtenerValorTotal() {
    assertEquals(15000.00, producto.obtenerValorTotal(), 0.001);
  }

  @Test
  @DisplayName("24. Valor total cero cuando sin stock")
  void valorTotalCero() {
    var p = new Producto("PV", "Vacío", 100.0, 0);
    assertEquals(0.0, p.obtenerValorTotal(), 0.001);
  }

  @Test
  @DisplayName("Valor total se actualiza tras agregar")
  void valorTotalTrasAgregar() {
    producto.agregarStock(5);
    assertEquals(1500.00 * 15, producto.obtenerValorTotal(), 0.001);
  }

  @Test
  @DisplayName("Valor total se actualiza tras extraer")
  void valorTotalTrasExtraer() {
    producto.extraerStock(4);
    assertEquals(1500.00 * 6, producto.obtenerValorTotal(), 0.001);
  }

  // ===== SETTERS =====

  @Test
  @DisplayName("Actualizar nombre correctamente")
  void actualizarNombre() {
    producto.setNombre("Laptop Dell");
    assertEquals("Laptop Dell", producto.getNombre());
  }

  @Test
  @DisplayName("Excepción setNombre nulo")
  void excepcionSetNombreNulo() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.setNombre(null));
    assertEquals("El nombre del producto no puede ser nulo", ex.getMessage());
  }

  @Test
  @DisplayName("Excepción setNombre vacío")
  void excepcionSetNombreVacio() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.setNombre(""));
    assertEquals("El nombre del producto no puede estar vacío", ex.getMessage());
  }

  @Test
  @DisplayName("Actualizar precio correctamente")
  void actualizarPrecio() {
    producto.setPrecio(2000.00);
    assertEquals(2000.00, producto.getPrecio(), 0.001);
  }

  @Test
  @DisplayName("Excepción setPrecio cero")
  void excepcionSetPrecioCero() {
    assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(0));
  }

  @Test
  @DisplayName("Excepción setPrecio negativo")
  void excepcionSetPrecioNeg() {
    assertThrows(IllegalArgumentException.class, () -> producto.setPrecio(-50.0));
  }

  @Test
  @DisplayName("Excepción setPrecio NaN")
  void excepcionSetPrecioNaN() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.setPrecio(Double.NaN));
    assertEquals("El precio del producto no puede ser NaN", ex.getMessage());
  }

  @Test
  @DisplayName("Excepción setPrecio infinito")
  void excepcionSetPrecioInf() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> producto.setPrecio(Double.POSITIVE_INFINITY));
    assertEquals("El precio del producto no puede ser infinito", ex.getMessage());
  }

  // ===== HISTORIAL =====

  @Test
  @DisplayName("Historial registra múltiples movimientos en orden")
  void historialMultiple() {
    producto.agregarStock(5);
    producto.extraerStock(3);
    producto.agregarStock(10);
    var movs = producto.getHistorialMovimientos();
    assertEquals(3, movs.size());
    assertEquals(TipoMovimiento.ENTRADA, movs.get(0).getTipo());
    assertEquals(TipoMovimiento.SALIDA, movs.get(1).getTipo());
    assertEquals(TipoMovimiento.ENTRADA, movs.get(2).getTipo());
  }

  @Test
  @DisplayName("Historial no es modificable externamente")
  void historialInmutable() {
    producto.agregarStock(5);
    var movs = producto.getHistorialMovimientos();
    assertThrows(UnsupportedOperationException.class, () -> movs.clear());
  }

  @Test
  @DisplayName("Operaciones fallidas no registran movimientos")
  void fallidasNoRegistran() {
    assertThrows(IllegalStateException.class, () -> producto.extraerStock(100));
    assertThrows(IllegalArgumentException.class, () -> producto.agregarStock(-1));
    assertTrue(producto.getHistorialMovimientos().isEmpty());
  }

  // ===== TIPOS DE DATO =====

  @Test
  @DisplayName("Código debe ser String")
  void codigoEsString() {
    assertInstanceOf(String.class, producto.getCodigo());
  }

  @Test
  @DisplayName("Nombre debe ser String")
  void nombreEsString() {
    assertInstanceOf(String.class, producto.getNombre());
  }

  @Test
  @DisplayName("Precio debe ser Double")
  void precioEsDouble() {
    assertInstanceOf(Double.class, (Object) producto.getPrecio());
  }

  @Test
  @DisplayName("Cantidad debe ser Integer")
  void cantidadEsInteger() {
    assertInstanceOf(Integer.class, (Object) producto.getCantidad());
  }

  @Test
  @DisplayName("ValorTotal debe ser Double")
  void valorTotalEsDouble() {
    assertInstanceOf(Double.class, (Object) producto.obtenerValorTotal());
  }

  @Test
  @DisplayName("Historial debe ser List de Movimiento")
  void historialEsListMovimiento() {
    producto.agregarStock(1);
    var h = producto.getHistorialMovimientos();
    assertInstanceOf(List.class, h);
    assertInstanceOf(Movimiento.class, h.get(0));
  }

  @Test
  @DisplayName("consultarStock == getCantidad siempre")
  void consultarStockIgualGetCantidad() {
    assertEquals(producto.getCantidad(), producto.consultarStock());
    producto.agregarStock(7);
    assertEquals(producto.getCantidad(), producto.consultarStock());
  }

  // ===== PARAMETRIZADA COMBINACIONES =====

  @ParameterizedTest(name = "Producto({0}, {1}, {2}, {3})")
  @DisplayName("Crear productos con diversas combinaciones válidas")
  @CsvSource({
      "A, Pan, 0.50, 0",
      "PROD-999, Laptop Gaming, 5000.0, 1",
      "X-1, Item-Especial, 0.01, 999999"
  })
  void diversasCombinaciones(String cod, String nom, double pre, int cant) {
    var p = new Producto(cod, nom, pre, cant);
    assertEquals(cod.trim(), p.getCodigo());
    assertEquals(nom.trim(), p.getNombre());
    assertEquals(pre, p.getPrecio(), 0.001);
    assertEquals(cant, p.getCantidad());
  }

  @Test
  @DisplayName("Precio muy pequeño cercano a cero es válido")
  void precioMuyPequeno() {
    var p = new Producto("MIN", "Mínimo", 0.001, 1);
    assertEquals(0.001, p.getPrecio(), 0.0001);
  }

  @Test
  @DisplayName("Consistencia tras múltiples operaciones")
  void consistenciaMultiple() {
    producto.agregarStock(100);
    producto.extraerStock(50);
    producto.agregarStock(30);
    producto.extraerStock(90);
    assertEquals(0, producto.consultarStock());
    assertEquals(4, producto.getHistorialMovimientos().size());
  }

  // ===== toString =====

  @Test
  @DisplayName("toString contiene información del producto")
  void toStringInfo() {
    String s = producto.toString();
    assertTrue(s.contains("PROD-001"));
    assertTrue(s.contains("Laptop HP"));
  }
}
