package com.unah.ProyectoBD.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.unah.ProyectoBD.Dtos.CrearTutoriaDto;
import com.unah.ProyectoBD.Models.ModalidadModel;
import com.unah.ProyectoBD.Models.TopicosModel;
import com.unah.ProyectoBD.Models.TutoresModel;
import com.unah.ProyectoBD.Models.TutoriaModel;
import com.unah.ProyectoBD.Repositories.ModalidadRepository;
import com.unah.ProyectoBD.Repositories.TopicoRepository;
import com.unah.ProyectoBD.Repositories.TutorRepository;
import com.unah.ProyectoBD.Repositories.TutoriaRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TutoriaService {

    @Autowired
    private TutoriaRepository tutoriaRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private ModalidadRepository modalidadRepository;

    // Valores fijos para nuevas tutorías
    private static final BigDecimal PRECIO_FIJO_TUTORIA = new BigDecimal("3500.00");
    private static final Integer LIMITE_ESTUDIANTES_FIJO = 20;

    /**
     * Endpoint para crear una nueva tutoría.
     * Realiza validaciones para asegurar que el tutor, tópico y modalidad existan,
     * y que el tutor de la tutoría coincida con el tutor asociado al tópico.
     * El precio y el límite de estudiantes se establecen con valores fijos.
     * 
     * @param crearTutoriaDTO Objeto DTO con los datos para crear la tutoría.
     * @return ResponseEntity con la tutoría creada o un mensaje de error.
     */
    @Transactional
    public ResponseEntity<?> crearTutoria(CrearTutoriaDto crearTutoriaDTO) {
        // 1. Validar que el tutor exista
        Optional<TutoresModel> tutorOptional = tutorRepository.findById(crearTutoriaDTO.getIdTutor());
        if (tutorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tutor con ID " + crearTutoriaDTO.getIdTutor() + " no encontrado.");
        }

        // 2. Validar que el tópico exista
        Optional<TopicosModel> topicoOptional = topicoRepository.findById(crearTutoriaDTO.getIdTopico());
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tópico con ID " + crearTutoriaDTO.getIdTopico() + " no encontrado.");
        }

        // 3. Validar que la modalidad exista
        Optional<ModalidadModel> modalidadOptional = modalidadRepository.findById(crearTutoriaDTO.getIdModalidad());
        if (modalidadOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Modalidad con ID " + crearTutoriaDTO.getIdModalidad() + " no encontrada.");
        }

        // 4. Validar que el tutor de la tutoría coincida con el tutor asignado al
        // tópico
        // Esto asegura la consistencia: una tutoría sobre un tópico X debe ser
        // impartida por el tutor de X.
        if (!topicoOptional.get().getIdTutor().equals(crearTutoriaDTO.getIdTutor())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("El tutor especificado (" + crearTutoriaDTO.getIdTutor()
                            + ") no es el tutor asignado al tópico (" + topicoOptional.get().getIdTutor() + ").");
        }

        try {
            TutoriaModel nuevaTutoria = new TutoriaModel();
            nuevaTutoria.setIdTutor(crearTutoriaDTO.getIdTutor());
            nuevaTutoria.setIdTopico(crearTutoriaDTO.getIdTopico());
            nuevaTutoria.setHoraIncio(crearTutoriaDTO.getHoraIncio());
            nuevaTutoria.setHoraFin(crearTutoriaDTO.getHoraFin());
            nuevaTutoria.setPrecio(PRECIO_FIJO_TUTORIA); // Precio fijo
            nuevaTutoria.setIdModalidad(crearTutoriaDTO.getIdModalidad());
            nuevaTutoria.setLimiteEstudiantes(LIMITE_ESTUDIANTES_FIJO); // Límite fijo

            nuevaTutoria = tutoriaRepository.save(nuevaTutoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTutoria);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la tutoría: " + e.getMessage());
        }
    }
}
