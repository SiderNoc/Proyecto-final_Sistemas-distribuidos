package com.sider.tienda.controlador;

import com.sider.tienda.modelo.Producto;
import com.sider.tienda.servicio.ProductoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//Nuevos Imports para el carrito
import com.sider.tienda.servicio.CarritoServicio;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.security.Principal;

import java.util.List;

@Controller
public class PrincipalControlador {

    private final ProductoServicio productoServicio;
    private final CarritoServicio carritoServicio;

    @Autowired
    public PrincipalControlador(ProductoServicio productoServicio, CarritoServicio carritoServicio) {
        this.productoServicio = productoServicio;
        this.carritoServicio = carritoServicio;
    }

    // Método para añadir el contador de ítems al modelo
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(Principal principal) {
        if (principal != null) {
            return carritoServicio.getNumeroDeItems(principal.getName());
        }
        return 0;
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
    public String mostrarPaginaDeTienda(Model model) { // Principal no es necesario aquí si no lo usas directamente
        List<Producto> listaProductos = productoServicio.obtenerTodosLosProductos();
        model.addAttribute("productos", listaProductos);
        // cartItemCount ya está disponible gracias al @ModelAttribute
        return "tienda";
    }
}