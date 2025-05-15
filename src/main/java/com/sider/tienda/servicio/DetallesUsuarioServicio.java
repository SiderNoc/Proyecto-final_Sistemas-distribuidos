package com.sider.tienda.servicio;

import com.sider.tienda.modelo.Usuario;
import com.sider.tienda.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DetallesUsuarioServicio implements UserDetailsService {


    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Buscando usuario: " + username);
        Usuario usuario = usuarioRepositorio.findByUsername(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }
}
