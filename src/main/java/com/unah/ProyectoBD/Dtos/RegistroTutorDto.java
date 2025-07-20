package com.unah.ProyectoBD.Dtos;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class RegistroTutorDto {
    // Datos de Persona
    @NotBlank(message = "El primer nombre es obligatorio")
    private String primerNombre;
    private String segundoNombre;
    @NotBlank(message = "El primer apellido es obligatorio")
    private String primerApellido;
    private String segundoApellido;
    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 13, max = 13, message = "El DNI debe tener 13 caracteres")
    @Pattern(regexp = "^[0-9]{13}$", message = "El DNI solo debe contener números")
    private String dni;
    @NotBlank(message = "El RTN es obligatorio")
    @Size(min = 14, max = 14, message = "El RTN debe tener 14 caracteres")
    @Pattern(regexp = "^[0-9]{14}$", message = "El RTN solo debe contener números")
    private String rtn;
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 8, max = 8, message = "El teléfono debe tener 8 caracteres")
    @Pattern(regexp = "^[0-9]{8}$", message = "El teléfono solo debe contener números")
    private String telefono;
    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo institucional debe ser válido")
    private String correoInstitucional;

    // Datos de Usuario
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String usuarioPassword;

    // Datos de Tutor
    @NotNull(message = "El salario por tutoría es obligatorio")
    @DecimalMin(value = "0.01", message = "El salario por tutoría debe ser mayor que cero")
    private BigDecimal salarioPorTutoria;
}
