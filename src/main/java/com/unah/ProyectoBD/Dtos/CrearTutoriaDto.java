package com.unah.ProyectoBD.Dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class CrearTutoriaDto {
    @NotNull(message = "El ID del tutor es obligatorio")
    private Integer idTutor;

    @NotNull(message = "El ID del tópico es obligatorio")
    private Integer idTopico;

    @NotBlank(message = "La hora de inicio es obligatoria")
    @Size(min = 4, max = 4, message = "La hora de inicio debe tener 4 caracteres (ej. 0800)")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3])[0-5][0-9]$", message = "Formato de hora de inicio inválido (HHMM)")
    private String horaIncio;

    @NotBlank(message = "La hora de fin es obligatoria")
    @Size(min = 4, max = 4, message = "La hora de fin debe tener 4 caracteres (ej. 1000)")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3])[0-5][0-9]$", message = "Formato de hora de fin inválido (HHMM)")
    private String horaFin;

    @NotNull(message = "El ID de la modalidad es obligatorio")
    private Integer idModalidad;
}
