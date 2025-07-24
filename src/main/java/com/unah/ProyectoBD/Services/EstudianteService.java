package com.unah.ProyectoBD.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unah.ProyectoBD.Dtos.RegistroEstudianteDto;
import com.unah.ProyectoBD.Models.EstudianteModel;
import com.unah.ProyectoBD.Models.PersonasModel;
import com.unah.ProyectoBD.Models.RolesModel;
import com.unah.ProyectoBD.Models.UsuariosModel;
import com.unah.ProyectoBD.Repositories.EstudianteRepository;
import com.unah.ProyectoBD.Repositories.PersonaRepository;
import com.unah.ProyectoBD.Repositories.RolRepository;
import com.unah.ProyectoBD.Repositories.UsuarioRepository;

// ... otros imports ...

@Service
public class EstudianteService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private RolRepository rolRepository;

    private RegistroEstudianteDto registroEstudianteDTO; // Asegúrate de que este DTO esté bien definido

    // Inyectar el codificador de contraseñas
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    // SOLUCIÓN #1: Quitar @RequestBody y @Valid. El controller se encarga de eso.
    public ResponseEntity<?> registrarEstudiante(RegistroEstudianteDto RegistroEstudianteDto) {

        // ... (tus validaciones de DNI, correo, etc. están bien) ...

        try {
            // Crear Usuario
            UsuariosModel usuarioParaGuardar = new UsuariosModel();
            usuarioParaGuardar.setCorreoInstitucional(registroEstudianteDTO.getCorreoInstitucional());
            // SOLUCIÓN #2: Hashear la contraseña
            usuarioParaGuardar.setUsuarioPassword(passwordEncoder.encode(registroEstudianteDTO.getUsuarioPassword()));

            UsuariosModel nuevoUsuarioConId = usuarioRepository.save(usuarioParaGuardar);

            // Crear Persona
            PersonasModel personaParaGuardar = new PersonasModel();
            // ... (asignar todos los campos de la persona) ...
            PersonasModel nuevaPersonaConId = personaRepository.save(personaParaGuardar);

            // Crear Estudiante usando los IDs recién creados
            EstudianteModel estudianteParaGuardar = new EstudianteModel();
            estudianteParaGuardar.setIdPersona(nuevaPersonaConId.getIdPersonas());
            estudianteParaGuardar.setIdUsuario(nuevoUsuarioConId.getIdUsuarios());
            estudianteParaGuardar.setNumeroCuenta(registroEstudianteDTO.getNumeroCuenta());
            EstudianteModel nuevoEstudiante = estudianteRepository.save(estudianteParaGuardar);

            // Asignar Rol
            RolesModel rolEstudiante = new RolesModel();
            rolEstudiante.setNombreRol("ESTUDIANTE");
            rolEstudiante.setIdUsuario(nuevoUsuarioConId.getIdUsuarios());
            rolRepository.save(rolEstudiante);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstudiante);

        } catch (Exception e) {
            // SOLUCIÓN #3: Imprimir el error completo para facilitar la depuración
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al registrar el estudiante.");
        }
    }
}