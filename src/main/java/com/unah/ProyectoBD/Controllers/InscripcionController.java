package com.unah.ProyectoBD.Controllers;

import com.unah.ProyectoBD.Dtos.InscripcionRequestDto;
import com.unah.ProyectoBD.Models.EstudianteModel;
import com.unah.ProyectoBD.Repositories.EstudianteRepository; // Importa tu repositorio
import com.unah.ProyectoBD.Services.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Para obtener el usuario logueado
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/inscripcion")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private EstudianteRepository estudianteRepository; // Para buscar al estudiante por su correo

    @PostMapping("/matricular/{idTutoria}")
    public String matricularEnTutoria(
            @PathVariable("idTutoria") Integer idTutoria,
            Authentication authentication, // Objeto de Spring Security con la info del usuario
            RedirectAttributes redirectAttributes) {

        // 1. Obtener el correo del usuario que ha iniciado sesión
        String correoUsuario = authentication.getName();

        // 2. Buscar el ID del estudiante correspondiente a ese correo
        Optional<EstudianteModel> estudianteOptional = estudianteRepository.findByCorreo(correoUsuario);

        if (estudianteOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorInscripcion", "No se pudo encontrar tu perfil de estudiante.");
            return "redirect:/tutorias/disponibles"; // O a donde corresponda
        }

        Integer idEstudiante = estudianteOptional.get().getIdEstudiante();

        // 3. Preparar el DTO para el servicio
        InscripcionRequestDto inscripcionDto = new InscripcionRequestDto();
        inscripcionDto.setIdEstudiante(idEstudiante);
        inscripcionDto.setIdTutoria(idTutoria);

        // 4. Llamar al servicio y manejar la respuesta
        ResponseEntity<?> response = inscripcionService.inscribirEstudiante(inscripcionDto);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            redirectAttributes.addFlashAttribute("exitoInscripcion", "¡Inscripción exitosa!");
        } else {
            // El servicio ya genera un mensaje de error específico
            String mensajeError = (String) response.getBody();
            redirectAttributes.addFlashAttribute("errorInscripcion", mensajeError);
        }

        // 5. Redirigir de vuelta a la lista de tutorías
        return "redirect:/tutorias/disponibles";
    }

    /**
     * NOTA IMPORTANTE:
     * Para que esto funcione, necesitas un método en tu `EstudianteRepository` que
     * pueda
     * buscar un estudiante a partir de su correo electrónico (que es el username en
     * Spring Security).
     *
     * public Optional<EstudianteModel> findByCorreo(String correo);
     * * La implementación dependerá de cómo estén relacionadas tus tablas.
     */
}