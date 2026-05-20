package com.inventario;

import java.util.Scanner;

/**
 * Clase de demostración rápida del gestor de inventario.
 * Permite crear un producto, añadir y extraer stock y visualizar el historial.
 * No es una UI completa, solo muestra cómo usar las clases del dominio.
 */
public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Gestor de Inventario (demo) ===");
        System.out.print("Código del producto: ");
        String codigo = scanner.nextLine().trim();
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Precio del producto: ");
        double precio = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Cantidad inicial: ");
        int cantidad = Integer.parseInt(scanner.nextLine().trim());

        Producto producto;
        try {
            producto = new Producto(codigo, nombre, precio, cantidad);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
            scanner.close();
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n--- Menú ---");
            System.out.println("1) Ver stock actual");
            System.out.println("2) Agregar stock");
            System.out.println("3) Extraer stock");
            System.out.println("4) Ver historial de movimientos");
            System.out.println("5) Salir");
            System.out.print("Seleccione una opción: ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1":
                    System.out.println("Stock disponible: " + producto.consultarStock());
                    break;
                case "2":
                    System.out.print("Cantidad a agregar: ");
                    int agregar = Integer.parseInt(scanner.nextLine().trim());
                    try {
                        producto.agregarStock(agregar);
                        System.out.println("Stock actualizado: " + producto.consultarStock());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                    break;
                case "3":
                    System.out.print("Cantidad a extraer: ");
                    int extraer = Integer.parseInt(scanner.nextLine().trim());
                    try {
                        producto.extraerStock(extraer);
                        System.out.println("Stock actualizado: " + producto.consultarStock());
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.println("Historial de movimientos:");
                    producto.getHistorialMovimientos().forEach(m -> {
                        System.out.println(m);
                    });
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        scanner.close();
        System.out.println("Fin del programa.");
    }
}
