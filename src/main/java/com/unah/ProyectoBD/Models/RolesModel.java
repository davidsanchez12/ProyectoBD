package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolesModel {
    private Integer idRol;
    private String nombreRol;
    private Integer idUsuario; // Clave foránea a Usuarios

}
