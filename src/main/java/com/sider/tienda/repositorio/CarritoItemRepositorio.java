package com.sider.tienda.repositorio;

import com.sider.tienda.modelo.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoItemRepositorio extends JpaRepository<CarritoItem, Integer> {
    // Podrías añadir métodos específicos si los necesitas, por ejemplo:
    // Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
}