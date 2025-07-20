package com.unah.ProyectoBD.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unah.ProyectoBD.Dtos.RegistroEstudianteDto;
import com.unah.ProyectoBD.Models.EstudianteModel;
import com.unah.ProyectoBD.Models.PersonasModel;
import com.unah.ProyectoBD.Models.RolesModel;
import com.unah.ProyectoBD.Models.UsuariosModel;
import com.unah.ProyectoBD.Repositories.EstudianteRepository;
import com.unah.ProyectoBD.Repositories.PersonaRepository;
import com.unah.ProyectoBD.Repositories.RolRepository;
import com.unah.ProyectoBD.Repositories.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private RolRepository rolRepository;

    /**
     * Endpoint para registrar un nuevo estudiante.
     * Implica la creación de un Usuario, una Persona y un Estudiante,
     * todo dentro de una transacción para asegurar la consistencia.
     * 
     * @param registroEstudianteDTO Datos del estudiante a registrar.
     * @return ResponseEntity con el estudiante registrado o un mensaje de error.
     */
    @PostMapping("/registrar")
    @Transactional // Asegura que todas las operaciones se realicen como una sola unidad atómica
    public ResponseEntity<?> registrarEstudiante(@Valid @RequestBody RegistroEstudianteDto registroEstudianteDTO) {
        // 1. Validar si el correo institucional ya existe en Usuarios o Personas
        if (usuarioRepository.findByCorreoInstitucional(registroEstudianteDTO.getCorreoInstitucional()).isPresent() ||
                personaRepository.findByCorreoInstitucional(registroEstudianteDTO.getCorreoInstitucional())
                        .isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo institucional ya está registrado.");
        }

        // 2. Validar si el DNI ya existe
        if (personaRepository.findByDni(registroEstudianteDTO.getDni()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El DNI ya está registrado.");
        }

        // 3. Validar si el RTN ya existe
        if (personaRepository.findByRtn(registroEstudianteDTO.getRtn()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El RTN ya está registrado.");
        }

        // 4. Validar si el número de cuenta ya existe
        if (estudianteRepository.findByNumeroCuenta(registroEstudianteDTO.getNumeroCuenta()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El número de cuenta ya está registrado.");
        }

        try {
            // Crear Usuario
            UsuariosModel nuevoUsuario = new UsuariosModel();
            nuevoUsuario.setCorreoInstitucional(registroEstudianteDTO.getCorreoInstitucional());
            nuevoUsuario.setUsuarioPassword(registroEstudianteDTO.getUsuarioPassword()); // En un proyecto real, aquí se
                                                                                         // debería hashear la
                                                                                         // contraseña
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);

            // Crear Persona
            PersonasModel nuevaPersona = new PersonasModel();
            nuevaPersona.setPrimerNombre(registroEstudianteDTO.getPrimerNombre());
            nuevaPersona.setSegundoNombre(registroEstudianteDTO.getSegundoNombre());
            nuevaPersona.setPrimerApellido(registroEstudianteDTO.getPrimerApellido());
            nuevaPersona.setSegundoApellido(registroEstudianteDTO.getSegundoApellido());
            nuevaPersona.setDni(registroEstudianteDTO.getDni());
            nuevaPersona.setRtn(registroEstudianteDTO.getRtn());
            nuevaPersona.setTelefono(registroEstudianteDTO.getTelefono());
            nuevaPersona.setCorreoInstitucional(registroEstudianteDTO.getCorreoInstitucional());
            nuevaPersona = personaRepository.save(nuevaPersona);

            // Crear Estudiante
            EstudianteModel nuevoEstudiante = new EstudianteModel();
            nuevoEstudiante.setIdPersona(nuevaPersona.getIdPersonas());
            nuevoEstudiante.setIdUsuario(nuevoUsuario.getIdUsuarios());
            nuevoEstudiante.setNumeroCuenta(registroEstudianteDTO.getNumeroCuenta());
            nuevoEstudiante = estudianteRepository.save(nuevoEstudiante);

            // Asignar Rol de "ESTUDIANTE" al nuevo usuario
            RolesModel rolEstudiante = new RolesModel();
            rolEstudiante.setNombreRol("ESTUDIANTE");
            rolEstudiante.setIdUsuario(nuevoUsuario.getIdUsuarios());
            rolRepository.save(rolEstudiante);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstudiante);

        } catch (Exception e) {
            // En caso de error, la transacción se revertirá automáticamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar estudiante: " + e.getMessage());
        }
    }
}
