package com.sider.tienda.servicio;

import com.sider.tienda.modelo.Producto;
import com.sider.tienda.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Opcional para CRUD simple, pero buena práctica

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServicio {

    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public ProductoServicio(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    @Transactional(readOnly = true) // Indica que es una transacción de solo lectura, optimiza la consulta
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepositorio.findById(id);
    }

    @Transactional // Indica que es una transacción que puede modificar datos
    public Producto guardarProducto(Producto producto) {
        // Aquí podrías añadir validaciones o lógica de negocio antes de guardar
        return productoRepositorio.save(producto);
    }

    @Transactional
    public void eliminarProducto(Integer id) {
        // Aquí podrías añadir verificaciones antes de eliminar,
        // por ejemplo, si el producto está en algún pedido pendiente, etc.
        productoRepositorio.deleteById(id);
    }

    // Si quieres añadir más lógica de negocio, este es el lugar.
    // Por ejemplo, verificar stock, aplicar descuentos, etc.
}