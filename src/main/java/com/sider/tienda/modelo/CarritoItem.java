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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarritoItem that = (CarritoItem) o;
        // Compara por ID solo si ambos IDs no son nulos
        // Si los IDs son nulos, solo son iguales si son la misma instancia (cubierto por this == o)
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        // Si el ID es nulo, usa el hashCode del objeto base.
        // Si el ID no es nulo, usa su hashCode.
        // Es una práctica común para entidades JPA.
        return id != null ? id.hashCode() : super.hashCode();
        // Alternativamente, si esperas usar entidades sin ID en un Set/Map antes de persistir:
        // return getClass().hashCode(); // O una constante como 31 si el ID es nulo.
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