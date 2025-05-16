// En tu archivo PrincipalControlador.java
package com.sider.tienda.controlador;

import com.sider.tienda.modelo.Producto;
import com.sider.tienda.servicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PrincipalControlador {

    private final ProductoServicio productoServicio;

    @Autowired
    public PrincipalControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    @GetMapping("/")
    public String mostrarInicio() {
        // Redirigimos directamente a la página de la tienda
        return "redirect:/tienda";
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/tienda")
    public String mostrarPaginaDeTienda(Model model) {
        List<Producto> listaProductos = productoServicio.obtenerTodosLosProductos();
        model.addAttribute("productos", listaProductos);
        return "tienda";
    }
}