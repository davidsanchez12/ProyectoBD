package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFacturaModel {
    private Integer idDetalleFactura;
    private Integer idFactura; // Clave foránea a Facturas
    private Integer idTutoria; // Clave foránea a Tutorias
    private String descripcion;
    private Integer cantidad;
    private BigDecimal subtotal;
    private BigDecimal total;
}
