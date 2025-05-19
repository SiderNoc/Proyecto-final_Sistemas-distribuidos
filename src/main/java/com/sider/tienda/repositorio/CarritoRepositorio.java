package com.sider.tienda.repositorio;

import com.sider.tienda.modelo.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Integer> {
    Optional<Carrito> findByUsuarioId(Integer usuarioId);
    Optional<Carrito> findByUsuarioUsername(String username); // Más útil
}

