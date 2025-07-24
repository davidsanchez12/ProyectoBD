package com.unah.ProyectoBD.Controllers;

// En src/main/java/com/unah/ProyectoBD/Controllers/EstudianteController.java

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unah.ProyectoBD.Dtos.RegistroEstudianteDto;
import com.unah.ProyectoBD.Services.EstudianteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registro") // Agrupa todas las rutas de registro bajo /registro
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    /**
     * Muestra el formulario de registro para un nuevo estudiante.
     * * @param model El modelo para pasar datos a la vista.
     * 
     * @return El nombre de la plantilla Thymeleaf para el formulario.
     */
    @GetMapping("/estudiante")
    public String mostrarFormularioRegistro(Model model) {
        // Se envía un objeto DTO vacío para que el formulario de Thymeleaf
        // pueda enlazar los campos con th:object y th:field.
        model.addAttribute("estudianteDto", new RegistroEstudianteDto());
        return "registro"; // Asume que tu template se llama 'registro.html'
    }

    /**
     * Procesa la solicitud de registro de un nuevo estudiante.
     * * @param estudianteDto Los datos del formulario, validados.
     * 
     * @param bindingResult      Contiene los resultados de la validación.
     * @param redirectAttributes Para enviar mensajes a la vista después de una
     *                           redirección.
     * @return Una redirección a la página de login (si es exitoso) o de vuelta al
     *         formulario (si hay errores).
     */
    @PostMapping("/estudiante")
    public String procesarRegistro(
            @Valid @ModelAttribute("estudianteDto") RegistroEstudianteDto estudianteDto,
            BindingResult bindingResult, // ¡Importante! Debe ir justo después del DTO validado
            RedirectAttributes redirectAttributes) {

        // 1. Verificar si hay errores de validación del formulario (campos vacíos,
        // etc.)
        if (bindingResult.hasErrors()) {
            // Si hay errores, no se llama al servicio y se vuelve al formulario.
            // Thymeleaf mostrará los errores automáticamente si está configurado para ello.
            return "registro";
        }

        // 2. Llamar al servicio para intentar registrar al estudiante
        ResponseEntity<?> response = estudianteService.registrarEstudiante(estudianteDto);

        // 3. Manejar la respuesta del servicio
        if (response.getStatusCode() == HttpStatus.CREATED) {
            // Si el registro fue exitoso (código 201 CREATED)
            redirectAttributes.addFlashAttribute("registroExitoso", "¡Registro completado! Por favor, inicia sesión.");
            return "redirect:/login"; // Redirige al usuario a la página de login

        } else {
            // Si hubo un error (ej. correo duplicado, código 409 CONFLICT)
            String mensajeError = (String) response.getBody(); // Obtiene el mensaje ("El correo ya está registrado.")
            redirectAttributes.addFlashAttribute("errorRegistro", mensajeError);
            return "redirect:/registro/estudiante"; // Redirige de vuelta al formulario de registro para mostrar el
                                                    // error
        }
    }
}