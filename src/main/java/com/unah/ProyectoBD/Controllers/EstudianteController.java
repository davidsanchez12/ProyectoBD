package com.unah.ProyectoBD.Controllers;

// Todos tus imports están bien...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // <-- Asegúrate de que este import esté
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unah.ProyectoBD.Dtos.RegistroEstudianteDto;
import com.unah.ProyectoBD.Repositories.EstudianteRepository;
import com.unah.ProyectoBD.Services.EstudianteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registro")
public class EstudianteController {
    @Autowired
    EstudianteRepository estudianteRepository;

    @Autowired
    private EstudianteService estudianteService;

    // --- El método GET para mostrar el formulario está perfecto. No necesita
    // cambios. ---
    @GetMapping("/estudiante")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("estudianteDto", new RegistroEstudianteDto());
        return "registro";
    }

    // --- El método POST para procesar el formulario con la mejora ---
    @PostMapping("/estudiante")
    public String procesarRegistro(
            @Valid @ModelAttribute("estudianteDto") RegistroEstudianteDto estudianteDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) { // <-- AÑADIR EL PARÁMETRO 'Model'

        // La validación de campos vacíos está perfecta.
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        ResponseEntity<?> response = estudianteService.registrarEstudiante(estudianteDto);

        // El manejo del caso de ÉXITO está perfecto.
        if (response.getStatusCode() == HttpStatus.CREATED) {
            redirectAttributes.addFlashAttribute("registroExitoso", "¡Registro completado! Por favor, inicia sesión.");
            return "redirect:/login";

        } else {
            // ====================== MEJORA EN EL MANEJO DE ERROR ======================
            // ANTES: Redirigías, lo que borraba los datos que el usuario había escrito.
            // String mensajeError = (String) response.getBody();
            // redirectAttributes.addFlashAttribute("errorRegistro", mensajeError);
            // return "redirect:/registro/estudiante";

            // AHORA: Devolvemos la vista directamente.
            // Esto conserva los datos que el usuario ya había ingresado en el formulario.
            model.addAttribute("errorRegistro", response.getBody());
            return "registro"; // <-- Se devuelve el nombre de la plantilla, no una redirección.
        }
    }
}