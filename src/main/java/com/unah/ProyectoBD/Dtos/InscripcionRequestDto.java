package com.unah.ProyectoBD.Dtos;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class InscripcionRequestDto {
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Integer idEstudiante;

    @NotNull(message = "El ID de la tutoría es obligatorio")
    private Integer idTutoria;
}
