package com.sider.tienda.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Importamos las anotaciones de validación
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre no puede estar vacío.") // No puede ser nulo ni solo espacios en blanco
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Lob
    @Column
    private String descripcion; // Descripción puede ser opcional o tener sus propias validaciones si es necesario

    @NotNull(message = "El precio no puede ser nulo.")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero.")
    @Column(precision = 10, scale = 2, nullable = false) // Hacemos precio no nulo también en DB
    private BigDecimal precio;

    @NotNull(message = "El stock no puede ser nulo.")
    @Min(value = 0, message = "El stock no puede ser negativo.")
    @Column(nullable = false) // Hacemos stock no nulo también en DB
    private Integer stock;

    // Constructores (sin cambios)
    public Producto() {
    }

    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    // Getters y Setters (sin cambios)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    @Override
    public String toString() { // Sin cambios
        return "Producto{" + "id=" + id + ", nombre='" + nombre + '\'' + ", descripcion='" + descripcion + '\'' + ", precio=" + precio + ", stock=" + stock + '}';
    }
}