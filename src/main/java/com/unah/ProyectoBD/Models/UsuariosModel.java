package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuariosModel {
    private Integer idUsuarios;
    private String correoInstitucional;
    private String usuarioPassword;

}
