package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutoriaModel {
    private Integer idTutoria;
    private Integer idTutor; // Clave foránea a Tutores
    private Integer idTopico; // Clave foránea a Topicos
    private String horaIncio;
    private String horaFin;
    private BigDecimal precio;
    private Integer idModalidad; // Clave foránea a Modalidades
    private Integer limiteEstudiantes;
}
