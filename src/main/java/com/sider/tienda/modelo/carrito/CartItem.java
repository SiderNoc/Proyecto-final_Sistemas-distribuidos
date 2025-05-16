package com.sider.tienda.modelo.carrito;

import com.sider.tienda.modelo.Producto;
import java.math.BigDecimal;

public class CartItem {
    private Producto producto;
    private int cantidad;

    public CartItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
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

    public BigDecimal getSubtotal() {
        if (producto != null && producto.getPrecio() != null) {
            return producto.getPrecio().multiply(new BigDecimal(cantidad));
        }
        return BigDecimal.ZERO;
    }
}
