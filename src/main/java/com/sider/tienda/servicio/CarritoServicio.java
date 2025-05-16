package com.sider.tienda.servicio;

import com.sider.tienda.modelo.Producto;
import com.sider.tienda.modelo.carrito.CartItem; // Asegúrate que la ruta sea correcta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext; // Para WebApplicationContext.SCOPE_SESSION

import java.math.BigDecimal;

import java.util.ArrayList;     // Para new ArrayList<>()
import java.util.HashMap;       // Para new HashMap<>()
import java.util.List;          // Para el tipo de retorno de getItems()
import java.util.Map;           // Para el tipo de la colección 'items'
import java.util.Optional;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarritoServicio {

    private final ProductoServicio productoServicio; // Para obtener detalles del producto

    // Usaremos un Map para almacenar los ítems del carrito, donde la clave es el ID del producto.
    private Map<Integer, CartItem> items = new HashMap<>();

    @Autowired
    public CarritoServicio(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    public void agregarItem(Integer productoId, int cantidad) {
        Optional<Producto> productoOpt = productoServicio.obtenerProductoPorId(productoId);
        if (productoOpt.isEmpty()) {
            // Manejar el caso de producto no encontrado, tal vez lanzar una excepción
            System.err.println("Intento de agregar producto no existente al carrito: ID " + productoId);
            return;
        }
        Producto producto = productoOpt.get();

        CartItem cartItem = items.get(producto.getId());
        if (cartItem != null) {
            // Si el producto ya está en el carrito, actualizamos la cantidad
            // Podrías añadir lógica para no exceder el stock aquí
            cartItem.setCantidad(cartItem.getCantidad() + cantidad);
        } else {
            // Si el producto no está en el carrito, lo añadimos
            // Podrías añadir lógica para no exceder el stock aquí
            items.put(producto.getId(), new CartItem(producto, cantidad));
        }
    }

    public void removerItem(Integer productoId) {
        items.remove(productoId);
    }

    public void actualizarCantidad(Integer productoId, int cantidad) {
        CartItem cartItem = items.get(productoId);
        if (cartItem != null) {
            if (cantidad > 0) {
                // Podrías añadir lógica para no exceder el stock aquí
                cartItem.setCantidad(cantidad);
            } else {
                // Si la cantidad es 0 o menos, removemos el ítem
                removerItem(productoId);
            }
        }
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public BigDecimal getTotal() {
        return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getNumeroDeItems() {
        return items.values().stream()
                .mapToInt(CartItem::getCantidad)
                .sum();
    }

    public void limpiarCarrito() {
        items.clear();
    }
}