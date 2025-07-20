package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstablecimientoModel {
    private Integer idEstablecimiento;
    private String nombreEstablecimiento;
    private String direccionEstablecimiento;
    private String telefonoEstablecimiento;
}
