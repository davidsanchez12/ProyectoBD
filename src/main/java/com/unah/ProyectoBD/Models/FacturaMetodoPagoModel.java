package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaMetodoPagoModel {
    private Integer idFacturaMetodoPago;
    private Integer idFactura; // Clave foránea a Facturas
    private Integer idMetodoPago; // Clave foránea a MetodoPago
}
