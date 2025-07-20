package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicosModel {
    private Integer idTopicos;
    private Integer idAsignatura; // Clave foránea a Asignaturas
    private Integer idTutor; // Clave foránea a Tutores
    private String nombre;
}
