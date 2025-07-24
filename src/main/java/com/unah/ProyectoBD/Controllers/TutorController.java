package com.unah.ProyectoBD.Controllers;

// En src/main/java/com/unah/ProyectoBD/Controllers/TutorController.java

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

import com.unah.ProyectoBD.Dtos.RegistroTutorDto;
import com.unah.ProyectoBD.Services.TutorService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registro") // Las rutas estarán bajo /registro
public class TutorController {

    @Autowired
    private TutorService tutorService;

    /**
     * Muestra el formulario para registrar un nuevo tutor.
     */
    @GetMapping("/tutor")
    public String mostrarFormularioRegistroTutor(Model model) {
        // Se envía un DTO vacío para que el formulario pueda enlazar los campos.
        model.addAttribute("tutorDto", new RegistroTutorDto());
        // Asume que tu template se llama 'registro_tutor.html'
        return "registro_tutor";
    }

    /**
     * Procesa la solicitud de registro de un nuevo tutor.
     */
    @PostMapping("/tutor")
    public String procesarRegistroTutor(
            @Valid @ModelAttribute("tutorDto") RegistroTutorDto tutorDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 1. Verifica errores de validación (ej. campos vacíos).
        if (bindingResult.hasErrors()) {
            return "registro_tutor"; // Vuelve al formulario si hay errores.
        }

        // 2. Llama al servicio para intentar el registro.
        ResponseEntity<?> response = tutorService.registrarTutor(tutorDto);

        // 3. Evalúa la respuesta del servicio.
        if (response.getStatusCode() == HttpStatus.CREATED) {
            // ÉXITO: Redirige al login con un mensaje de éxito.
            redirectAttributes.addFlashAttribute("registroExitoso",
                    "¡Registro de tutor completado! Ya puede iniciar sesión.");
            return "redirect:/login";

        } else {
            // ERROR: Redirige de vuelta al formulario con el mensaje de error del servicio.
            String mensajeError = (String) response.getBody();
            redirectAttributes.addFlashAttribute("errorRegistro", mensajeError);
            return "redirect:/registro/tutor";
        }
    }
}