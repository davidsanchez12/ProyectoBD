package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoMetodoPagoModel {
    private Integer idPagosMetodoPago;
    private Integer idPagos; // Clave foránea a Pagos
    private Integer idMetodoPago; // Clave foránea a MetodoPago
}
