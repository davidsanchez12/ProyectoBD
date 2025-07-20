package com.unah.ProyectoBD.Models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutoresModel {

    private Integer idTutor;
    private Integer idPersona; // Clave foránea a Personas
    private Integer idUsuario; // Clave foránea a Usuarios
    private BigDecimal salarioPorTutoria;

}
