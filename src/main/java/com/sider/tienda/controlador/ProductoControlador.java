package com.sider.tienda.controlador;

import com.sider.tienda.modelo.Producto;
import com.sider.tienda.servicio.ProductoServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/productos") // Ruta base para todas las acciones de este controlador
@PreAuthorize("hasRole('ADMIN')")   // Solo usuarios con ROLE_ADMIN pueden acceder
public class ProductoControlador {

    private final ProductoServicio productoServicio;

    @Autowired
    public ProductoControlador(ProductoServicio productoServicio) {
        this.productoServicio = productoServicio;
    }

    // Listar todos los productos
    @GetMapping
    public String listarProductos(Model model) {
        List<Producto> productos = productoServicio.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        // Crearemos esta vista más adelante: templates/admin/productos-lista.html
        return "admin/productos-lista";
    }

    // Mostrar formulario para agregar un nuevo producto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("pageTitle", "Nuevo Producto");
        // Crearemos esta vista más adelante: templates/admin/producto-form.html
        return "admin/producto-form";
    }

    // Mostrar formulario para editar un producto existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarProducto(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Producto> productoOptional = productoServicio.obtenerProductoPorId(id);
        if (productoOptional.isPresent()) {
            model.addAttribute("producto", productoOptional.get());
            model.addAttribute("pageTitle", "Editar Producto (ID: " + id + ")");
            return "admin/producto-form";
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Producto no encontrado con ID: " + id);
            return "redirect:/admin/productos";
        }
    }

    // Procesar el guardado (creación o actualización) de un producto
    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("producto") Producto producto,
                                  BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // Si hay errores de validación, volvemos al formulario
            // El título de la página dependerá de si es una creación o edición
            model.addAttribute("pageTitle", producto.getId() == null ? "Nuevo Producto" : "Editar Producto (ID: " + producto.getId() + ")");
            return "admin/producto-form";
        }

        productoServicio.guardarProducto(producto);
        redirectAttributes.addFlashAttribute("mensajeExito", "Producto guardado exitosamente.");
        return "redirect:/admin/productos";
    }

    // Eliminar un producto
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoServicio.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto eliminado exitosamente.");
        } catch (Exception e) {
            // Podrías ser más específico con el manejo de excepciones si, por ejemplo,
            // un producto no se puede eliminar debido a restricciones de clave foránea.
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el producto. Es posible que esté asociado a otros datos.");
        }
        return "redirect:/admin/productos";
    }
}