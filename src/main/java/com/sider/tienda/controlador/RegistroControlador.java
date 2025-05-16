package com.sider.tienda.controlador;

import com.sider.tienda.dto.UsuarioRegistroDTO;
import com.sider.tienda.servicio.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class RegistroControlador {

    private final UsuarioServicio usuarioServicio;

    @Autowired
    public RegistroControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // Para que el objeto DTO esté disponible en el modelo si volvemos al form con errores
    @ModelAttribute("usuarioDto")
    public UsuarioRegistroDTO usuarioRegistroDTO() {
        return new UsuarioRegistroDTO();
    }

    // Mostrar el formulario de registro
    @GetMapping
    public String mostrarFormularioDeRegistro() {
        // El @ModelAttribute("usuarioDto") ya provee el DTO al modelo
        return "registro"; // Nombre de la vista que crearemos: templates/registro.html
    }

    // Procesar el formulario de registro
    @PostMapping
    public String procesarRegistro(@Valid @ModelAttribute("usuarioDto") UsuarioRegistroDTO registroDto,
                                   BindingResult result, // Importante: BindingResult DEBE ir INMEDIATAMENTE después del objeto validado
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // 1. Validaciones de anotaciones del DTO (@NotBlank, @Size)
        if (result.hasErrors()) {
            // model.addAttribute("usuarioDto", registroDto); // No es necesario si usamos @ModelAttribute a nivel de método
            return "registro"; // Volver al formulario si hay errores de las anotaciones
        }

        // 2. Validaciones personalizadas del servicio (username existe, passwords coinciden)
        try {
            // Intenta registrar al nuevo usuario
            usuarioServicio.registrarNuevoUsuario(registroDto);

            redirectAttributes.addFlashAttribute("mensajeExito", "¡Registro exitoso! Por favor, inicia sesión.");
            return "redirect:/login"; // Redirigir a la página de login tras un registro exitoso

        } catch (Exception e) {
            // Si el servicio lanza una excepción (ej. username ya existe, passwords no coinciden)
            // model.addAttribute("usuarioDto", registroDto); // No es necesario
            // Añadimos el error al BindingResult o directamente al Model para mostrarlo en la vista
            // Para errores que no son de un campo específico, podemos usar un error global
            // o si es de un campo específico, result.rejectValue("campo", "codigoError", mensaje);
            model.addAttribute("errorGlobal", e.getMessage());
            return "registro"; // Volver al formulario
        }
    }
}