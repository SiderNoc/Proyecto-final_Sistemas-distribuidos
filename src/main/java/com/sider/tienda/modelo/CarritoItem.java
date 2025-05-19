package com.sider.tienda.modelo;

import jakarta.persistence.*;
import java.math.BigDecimal; // Para el subtotal

@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos ítems pertenecen a un carrito
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY) // Un ítem del carrito referencia a un producto
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private int cantidad;

    // Constructor vacío requerido por JPA
    public CarritoItem() {}

    public CarritoItem(Carrito carrito, Producto producto, int cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
    }


    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Método para calcular el subtotal (no se persiste, se calcula al vuelo)
    @Transient // Indica a JPA que no mapee este campo a la base de datos
    public BigDecimal getSubtotal() {
        if (producto != null && producto.getPrecio() != null) {
            return producto.getPrecio().multiply(new BigDecimal(cantidad));
        }
        return BigDecimal.ZERO;
    }
}