package com.sider.tienda.repositorio;

import com.sider.tienda.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Es buena práctica, aunque opcional si extiendes JpaRepository

import java.util.List; // Si quieres añadir métodos personalizados que devuelvan listas

@Repository // Indica que es un componente de repositorio de Spring
public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

    // JpaRepository ya nos da:
    // - save(Producto producto) -> para guardar o actualizar
    // - findById(Integer id) -> para buscar por ID
    // - findAll() -> para obtener todos los productos
    // - deleteById(Integer id) -> para eliminar por ID
    // - count() -> para contar productos
    // - existsById(Integer id) -> para verificar si existe
    // ¡Y muchos más!

    // Si en el futuro necesitas métodos de búsqueda más específicos,
    // como buscar productos por nombre, los puedes definir aquí. Por ejemplo:
    // List<Producto> findByNombreContainingIgnoreCase(String nombre);
}