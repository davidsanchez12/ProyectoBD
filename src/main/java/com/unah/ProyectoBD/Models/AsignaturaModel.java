package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignaturaModel {
    private Integer idAsignatura;
    private String nombreAsignatura;
    private String codigoAsignatura;
    private Integer idTutor; // Clave foránea a Tutores
}
