package com.unah.ProyectoBD.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unah.ProyectoBD.Dtos.RegistroTutorDto;
import com.unah.ProyectoBD.Models.PersonasModel;
import com.unah.ProyectoBD.Models.RolesModel;
import com.unah.ProyectoBD.Models.TutoresModel;
import com.unah.ProyectoBD.Models.UsuariosModel;
import com.unah.ProyectoBD.Repositories.PersonaRepository;
import com.unah.ProyectoBD.Repositories.RolRepository;
import com.unah.ProyectoBD.Repositories.TutorRepository;
import com.unah.ProyectoBD.Repositories.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private RolRepository rolRepository;

    /**
     * Endpoint para registrar un nuevo tutor.
     * Implica la creación de un Usuario, una Persona y un Tutor,
     * todo dentro de una transacción para asegurar la consistencia.
     * 
     * @param registroTutorDTO Datos del tutor a registrar.
     * @return ResponseEntity con el tutor registrado o un mensaje de error.
     */
    @PostMapping("/registrar")
    @Transactional // Asegura que todas las operaciones se realicen como una sola unidad atómica
    public ResponseEntity<?> registrarTutor(@Valid @RequestBody RegistroTutorDto registroTutorDTO) {
        // 1. Validar si el correo institucional ya existe en Usuarios o Personas
        if (usuarioRepository.findByCorreoInstitucional(registroTutorDTO.getCorreoInstitucional()).isPresent() ||
                personaRepository.findByCorreoInstitucional(registroTutorDTO.getCorreoInstitucional()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo institucional ya está registrado.");
        }

        // 2. Validar si el DNI ya existe
        if (personaRepository.findByDni(registroTutorDTO.getDni()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El DNI ya está registrado.");
        }

        // 3. Validar si el RTN ya existe
        if (personaRepository.findByRtn(registroTutorDTO.getRtn()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El RTN ya está registrado.");
        }

        try {
            // Crear Usuario
            UsuariosModel nuevoUsuario = new UsuariosModel();
            nuevoUsuario.setCorreoInstitucional(registroTutorDTO.getCorreoInstitucional());
            nuevoUsuario.setUsuarioPassword(registroTutorDTO.getUsuarioPassword()); // En un proyecto real, aquí se
                                                                                    // debería hashear la contraseña
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);

            // Crear Persona
            PersonasModel nuevaPersona = new PersonasModel();
            nuevaPersona.setPrimerNombre(registroTutorDTO.getPrimerNombre());
            nuevaPersona.setSegundoNombre(registroTutorDTO.getSegundoNombre());
            nuevaPersona.setPrimerApellido(registroTutorDTO.getPrimerApellido());
            nuevaPersona.setSegundoApellido(registroTutorDTO.getSegundoApellido());
            nuevaPersona.setDni(registroTutorDTO.getDni());
            nuevaPersona.setRtn(registroTutorDTO.getRtn());
            nuevaPersona.setTelefono(registroTutorDTO.getTelefono());
            nuevaPersona.setCorreoInstitucional(registroTutorDTO.getCorreoInstitucional());
            nuevaPersona = personaRepository.save(nuevaPersona);

            // Crear Tutor
            TutoresModel nuevoTutor = new TutoresModel();
            nuevoTutor.setIdPersona(nuevaPersona.getIdPersonas());
            nuevoTutor.setIdUsuario(nuevoUsuario.getIdUsuarios());
            nuevoTutor.setSalarioPorTutoria(registroTutorDTO.getSalarioPorTutoria());
            nuevoTutor = tutorRepository.save(nuevoTutor);

            // Asignar Rol de "TUTOR" al nuevo usuario
            RolesModel rolTutor = new RolesModel();
            rolTutor.setNombreRol("TUTOR");
            rolTutor.setIdUsuario(nuevoUsuario.getIdUsuarios());
            rolRepository.save(rolTutor);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTutor);

        } catch (Exception e) {
            // En caso de error, la transacción se revertirá automáticamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar tutor: " + e.getMessage());
        }
    }
}
