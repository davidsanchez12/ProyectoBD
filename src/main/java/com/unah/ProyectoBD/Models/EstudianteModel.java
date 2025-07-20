package com.unah.ProyectoBD.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EstudianteModel {
    private Integer idEstudiante;
    private Integer idPersona; // Clave foránea a Personas
    private Integer idUsuario; // Clave foránea a Usuarios
    private String numeroCuenta;

}
