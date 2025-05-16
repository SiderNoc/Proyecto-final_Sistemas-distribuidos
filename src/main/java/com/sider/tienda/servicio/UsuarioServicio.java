package com.sider.tienda.servicio;

import com.sider.tienda.dto.UsuarioRegistroDTO;
import com.sider.tienda.modelo.Usuario;
import com.sider.tienda.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Usar la interfaz genérica
import org.springframework.stereotype.Service;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder; // Inyectar el BCryptPasswordEncoder a través de su interfaz

    @Autowired
    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existeUsername(String username) {
        return usuarioRepositorio.findByUsername(username) != null;
    }

    public Usuario registrarNuevoUsuario(UsuarioRegistroDTO registroDTO) throws Exception {
        if (existeUsername(registroDTO.getUsername())) {
            throw new Exception("Ya existe una cuenta con el nombre de usuario: " + registroDTO.getUsername());
        }
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            throw new Exception("Las contraseñas no coinciden.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(registroDTO.getUsername());
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword())); // Hashear la contraseña
        nuevoUsuario.setRol("USER"); // Asignar un rol por defecto a los nuevos usuarios

        return usuarioRepositorio.save(nuevoUsuario);
    }
}