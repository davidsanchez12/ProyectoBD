package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaModel {
    private Integer idFacturas;
    private Integer idRangosCAI; // Clave foránea a RangosCAI
    private String empresaEmisora;
    private String rtnEmpresaEmisora;
    private String numeroFactura;
    private Integer establecimiento; // Clave foránea a Establecimiento
    private String nombreCliente;
    private String rtnCliente;
}
