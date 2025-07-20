package com.unah.ProyectoBD.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangosCAIModel {
    private Integer idRangosCai;
    private String cai;
    private String rangoInicial;
    private String rangoFinal;
    private LocalDate fechaLimiteEmision;
}
