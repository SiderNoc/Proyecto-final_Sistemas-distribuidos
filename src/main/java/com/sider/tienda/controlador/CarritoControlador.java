package com.sider.tienda.controlador;

import com.sider.tienda.modelo.CarritoItem; // Ahora usamos la entidad JPA
import com.sider.tienda.servicio.CarritoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal; // Para obtener el usuario autenticado
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoControlador {

    private final CarritoServicio carritoServicio;

    @Autowired
    public CarritoControlador(CarritoServicio carritoServicio) {
        this.carritoServicio = carritoServicio;
    }

    // Método para añadir el contador de ítems al modelo para la vista del carrito
    @ModelAttribute("cartItemCount")
    public int getCartItemCount(Principal principal) {
        if (principal != null) {
            return carritoServicio.getNumeroDeItems(principal.getName());
        }
        return 0;
    }

    @GetMapping
    public String verCarrito(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            // Esto no debería ocurrir si la ruta está protegida por Spring Security,
            // pero es una salvaguarda.
            redirectAttributes.addFlashAttribute("mensajeError", "Por favor, inicia sesión para ver tu carrito.");
            return "redirect:/login";
        }
        String username = principal.getName();
        List<CarritoItem> items = carritoServicio.getItems(username); // La entidad CarritoItem
        model.addAttribute("cartItems", items);
        model.addAttribute("totalCarrito", carritoServicio.getTotal(username));
        // model.addAttribute("cartItemCount", carritoServicio.getNumeroDeItems(username)); // Cubierto por @ModelAttribute
        return "carrito";
    }

    @PostMapping("/agregar/{productoId}")
    public String agregarAlCarrito(@PathVariable("productoId") Integer productoId,
                                   @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
                                   Principal principal, // Obtener el usuario autenticado
                                   RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login"; // Debe iniciar sesión para agregar al carrito
        }
        String username = principal.getName();
        try {
            carritoServicio.agregarItem(username, productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "¡Producto añadido al carrito!");
        } catch (RuntimeException e) { // Captura excepciones como stock insuficiente, producto no encontrado
            redirectAttributes.addFlashAttribute("mensajeErrorCarrito", e.getMessage());
        }
        return "redirect:/tienda";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidadCarrito(@RequestParam("productoId") Integer productoId,
                                            @RequestParam("cantidad") int cantidad,
                                            Principal principal,
                                            RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        try {
            if (cantidad < 0) {
                redirectAttributes.addFlashAttribute("mensajeErrorCarrito", "La cantidad no puede ser negativa.");
            } else {
                carritoServicio.actualizarCantidad(username, productoId, cantidad);
                redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "Cantidad actualizada.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("mensajeErrorCarrito", "No se pudo actualizar la cantidad: " + e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/remover/{productoId}")
    public String removerDelCarrito(@PathVariable("productoId") Integer productoId,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        carritoServicio.removerItem(username, productoId);
        redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "Producto eliminado del carrito.");
        return "redirect:/carrito";
    }

    @PostMapping("/limpiar")
    public String limpiarCarrito(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        carritoServicio.limpiarCarrito(username);
        redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "El carrito ha sido vaciado.");
        return "redirect:/carrito";
    }
}