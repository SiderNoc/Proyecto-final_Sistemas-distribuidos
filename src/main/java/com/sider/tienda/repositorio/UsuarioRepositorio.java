// Corrección para UsuarioRepositorio.java
package com.sider.tienda.repositorio;

import com.sider.tienda.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> { // <-- Cambiado a Integer
    Usuario findByUsername(String username);
}
