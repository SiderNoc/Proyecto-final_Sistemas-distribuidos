package com.sider.tienda.controlador;

import com.sider.tienda.modelo.carrito.CartItem; // Asegúrate de la ruta correcta
import com.sider.tienda.servicio.CarritoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/carrito") // Ruta base para todas las acciones del carrito
public class CarritoControlador {

    private final CarritoServicio carritoServicio;

    @Autowired
    public CarritoControlador(CarritoServicio carritoServicio) {
        this.carritoServicio = carritoServicio;
    }

    // Mostrar el carrito de compras
    @GetMapping
    public String verCarrito(Model model) {
        List<CartItem> items = carritoServicio.getItems();
        model.addAttribute("cartItems", items);
        model.addAttribute("totalCarrito", carritoServicio.getTotal());
        // Crearemos esta vista más adelante: templates/carrito.html
        return "carrito";
    }

    // Añadir un producto al carrito
    @PostMapping("/agregar/{productoId}")
    public String agregarAlCarrito(@PathVariable("productoId") Integer productoId,
                                   @RequestParam(value = "cantidad", defaultValue = "1") int cantidad,
                                   RedirectAttributes redirectAttributes) {
        try {
            carritoServicio.agregarItem(productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "¡Producto añadido al carrito!");
        } catch (Exception e) {
            // Aquí podrías manejar excepciones específicas, como producto no encontrado o stock insuficiente
            redirectAttributes.addFlashAttribute("mensajeErrorCarrito", "No se pudo añadir el producto: " + e.getMessage());
        }
        // Redirigir a la tienda o al carrito, según preferencia.
        // Por ahora, redirigimos a la tienda.
        return "redirect:/tienda";
    }

    // Actualizar la cantidad de un producto en el carrito
    @PostMapping("/actualizar")
    public String actualizarCantidadCarrito(@RequestParam("productoId") Integer productoId,
                                            @RequestParam("cantidad") int cantidad,
                                            RedirectAttributes redirectAttributes) {
        if (cantidad < 0) {
            redirectAttributes.addFlashAttribute("mensajeErrorCarrito", "La cantidad no puede ser negativa.");
        } else {
            carritoServicio.actualizarCantidad(productoId, cantidad);
            redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "Cantidad actualizada.");
        }
        return "redirect:/carrito";
    }

    // Remover un producto del carrito
    @PostMapping("/remover/{productoId}")
    public String removerDelCarrito(@PathVariable("productoId") Integer productoId,
                                    RedirectAttributes redirectAttributes) {
        carritoServicio.removerItem(productoId);
        redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "Producto eliminado del carrito.");
        return "redirect:/carrito";
    }
    @PostMapping("/limpiar")
    public String limpiarCarrito(RedirectAttributes redirectAttributes) {
        carritoServicio.limpiarCarrito();
        redirectAttributes.addFlashAttribute("mensajeExitoCarrito", "El carrito ha sido vaciado.");
        return "redirect:/carrito";
    }
}