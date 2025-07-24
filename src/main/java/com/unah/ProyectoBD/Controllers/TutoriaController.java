
package com.unah.ProyectoBD.Controllers;

import com.unah.ProyectoBD.Dtos.CrearTutoriaDto;
import com.unah.ProyectoBD.Models.TutoresModel;
import com.unah.ProyectoBD.Repositories.ModalidadRepository;
import com.unah.ProyectoBD.Repositories.TopicoRepository;
import com.unah.ProyectoBD.Repositories.TutorRepository;
import com.unah.ProyectoBD.Services.TutoriaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/tutorias") // Agrupamos las rutas relacionadas a tutorías
public class TutoriaController {

    @Autowired
    private TutoriaService tutoriaService;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private ModalidadRepository modalidadRepository;

    /**
     * Muestra el formulario para crear una nueva tutoría.
     */
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model, Authentication authentication) {
        String correoTutor = authentication.getName();
        // Buscamos al tutor que ha iniciado sesión para obtener su ID
        Optional<TutoresModel> tutorOpt = tutorRepository.findByCorreo(correoTutor);

        if (tutorOpt.isPresent()) {
            Integer tutorId = tutorOpt.get().getIdTutor();
            // Pasamos a la vista la lista de tópicos de ESE tutor y todas las modalidades
            model.addAttribute("listaTopicos", topicoRepository.findById(tutorId));
            model.addAttribute("listaModalidades", modalidadRepository.findAll());
        } else {
            // Si no se encuentra el tutor, se pasa una lista vacía para evitar errores
            model.addAttribute("listaTopicos", Collections.emptyList());
            model.addAttribute("listaModalidades", Collections.emptyList());
            model.addAttribute("error", "No se pudo encontrar tu perfil de tutor.");
        }

        // Se envía un DTO vacío para que el formulario pueda enlazar los campos
        model.addAttribute("tutoriaDto", new CrearTutoriaDto());
        return "crear_tutoria"; // Nombre del archivo HTML
    }

    /**
     * Procesa los datos del formulario de creación de tutoría.
     */
    @PostMapping("/crear")
    public String procesarCreacion(@ModelAttribute("tutoriaDto") CrearTutoriaDto tutoriaDto,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        String correoTutor = authentication.getName();
        Optional<TutoresModel> tutorOpt = tutorRepository.findByCorreo(correoTutor);

        if (tutorOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se pudo verificar tu identidad de tutor.");
            return "redirect:/"; // Redirigir a la página de inicio o de login
        }

        // Asignamos el ID del tutor logueado al DTO antes de enviarlo al servicio
        tutoriaDto.setIdTutor(tutorOpt.get().getIdTutor());

        // Llamamos al servicio para crear la tutoría
        ResponseEntity<?> response = tutoriaService.crearTutoria(tutoriaDto);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            redirectAttributes.addFlashAttribute("exito", "¡Tutoría creada exitosamente!");
            return "redirect:/tutorias/mis-tutorias"; // Redirige al panel del tutor
        } else {
            // Si hay un error, volvemos a mostrar el formulario con el mensaje de error
            model.addAttribute("error", response.getBody());
            // Recargamos las listas para que los dropdowns no queden vacíos
            model.addAttribute("listaTopicos", topicoRepository.findById(tutorOpt.get().getIdTutor()));
            model.addAttribute("listaModalidades", modalidadRepository.findAll());
            return "crear_tutoria";
        }
    }

}