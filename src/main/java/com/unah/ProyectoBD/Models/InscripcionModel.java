package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // Para el tipo DATE en SQL

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionModel {
    private Integer idInscripcion;
    private Integer idEstudiante; // Clave foránea a Estudiantes
    private Integer idTutoria; // Clave foránea a Tutorias
    private LocalDate fechaInscripcion;
}
