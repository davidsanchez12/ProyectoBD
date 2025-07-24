package com.unah.ProyectoBD.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import com.unah.ProyectoBD.Dtos.InscripcionRequestDto;
import com.unah.ProyectoBD.Models.EstudianteModel;
import com.unah.ProyectoBD.Models.InscripcionModel;
import com.unah.ProyectoBD.Models.TutoriaModel;
import com.unah.ProyectoBD.Repositories.EstudianteRepository;
import com.unah.ProyectoBD.Repositories.InscripcionRepository;
import com.unah.ProyectoBD.Repositories.TutoriaRepository;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;

@Service

public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private TutoriaRepository tutoriaRepository;

    /**
     * Endpoint para inscribir a un estudiante en una tutoría.
     * Realiza validaciones para asegurar que el estudiante y la tutoría existan,
     * que el estudiante no esté ya inscrito en esa tutoría y que no se haya
     * alcanzado el límite de estudiantes para la tutoría.
     * 
     * @param inscripcionRequestDTO Objeto DTO con los IDs del estudiante y la
     * @param correoUsuario         Correo del usuario logueado, utilizado para
     *                              buscar el ID del estudiante.
     *                              tutoría.
     * @return ResponseEntity con la inscripción creada o un mensaje de error.
     */
    @Transactional
    public ResponseEntity<?> inscribirEstudiante(InscripcionRequestDto inscripcionRequestDTO) {
        // 1. Validar que el estudiante exista
        Optional<EstudianteModel> estudianteOptional = estudianteRepository
                .findById(inscripcionRequestDTO.getIdEstudiante());
        if (estudianteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Estudiante con ID " + inscripcionRequestDTO.getIdEstudiante() + " no encontrado.");
        }

        // 2. Validar que la tutoría exista
        Optional<TutoriaModel> tutoriaOptional = tutoriaRepository.findById(inscripcionRequestDTO.getIdTutoria());
        if (tutoriaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tutoría con ID " + inscripcionRequestDTO.getIdTutoria() + " no encontrada.");
        }

        TutoriaModel tutoria = tutoriaOptional.get();

        // 3. Validar si el estudiante ya está inscrito en esta tutoría
        if (inscripcionRepository.existsByEstudianteIdAndTutoriaId(inscripcionRequestDTO.getIdEstudiante(),
                inscripcionRequestDTO.getIdTutoria())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El estudiante con ID " + inscripcionRequestDTO.getIdEstudiante()
                            + " ya está inscrito en la tutoría con ID " + inscripcionRequestDTO.getIdTutoria() + ".");
        }

        // 4. Validar el límite de estudiantes
        int inscripcionesActuales = inscripcionRepository.countInscripcionesByTutoriaId(tutoria.getIdTutoria());
        if (inscripcionesActuales >= tutoria.getLimiteEstudiantes()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La tutoría con ID " + tutoria.getIdTutoria()
                    + " ha alcanzado su límite de " + tutoria.getLimiteEstudiantes() + " estudiantes.");
        }

        try {
            InscripcionModel nuevaInscripcion = new InscripcionModel();
            nuevaInscripcion.setIdEstudiante(inscripcionRequestDTO.getIdEstudiante());
            nuevaInscripcion.setIdTutoria(inscripcionRequestDTO.getIdTutoria());
            nuevaInscripcion.setFechaInscripcion(LocalDate.now()); // Fecha de inscripción actual

            nuevaInscripcion = inscripcionRepository.save(nuevaInscripcion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaInscripcion);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al inscribir al estudiante: " + e.getMessage());
        }
    }
}
