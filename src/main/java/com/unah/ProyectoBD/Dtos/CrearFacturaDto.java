package com.unah.ProyectoBD.Dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class CrearFacturaDto {
    @NotNull(message = "El ID de la tutoría es obligatorio")
    private Integer idTutoria;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 250, message = "El nombre del cliente no puede exceder los 250 caracteres")
    private String nombreCliente;

    @NotBlank(message = "El RTN del cliente es obligatorio")
    @Size(min = 10, max = 14, message = "El RTN del cliente debe tener entre 10 y 14 caracteres")
    @Pattern(regexp = "^[0-9]{10,14}$", message = "El RTN del cliente solo debe contener números")
    private String rtnCliente;

    @NotNull(message = "Debe especificar al menos un método de pago")
    @Size(min = 1, message = "Debe especificar al menos un método de pago")
    private List<Integer> idMetodosPago;
}
