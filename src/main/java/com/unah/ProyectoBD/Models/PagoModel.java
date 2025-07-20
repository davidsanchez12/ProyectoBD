package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoModel {
    private Integer idPagos;
    private String ordenPago;
    private String reciboDocente;
    private String comprobantePago;
    private BigDecimal monto;
    private Integer idTutor; // Clave foránea a Tutores
}
