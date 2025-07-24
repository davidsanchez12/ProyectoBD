package com.unah.ProyectoBD.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unah.ProyectoBD.Dtos.CrearFacturaDto;
import com.unah.ProyectoBD.Models.DetalleFacturaModel;
import com.unah.ProyectoBD.Models.EstablecimientoModel;
import com.unah.ProyectoBD.Models.FacturaMetodoPagoModel;
import com.unah.ProyectoBD.Models.FacturaModel;
import com.unah.ProyectoBD.Models.MetodoPagoModel;
import com.unah.ProyectoBD.Models.RangosCAIModel;
import com.unah.ProyectoBD.Models.TutoriaModel;
import com.unah.ProyectoBD.Repositories.DetalleFacturaRepository;
import com.unah.ProyectoBD.Repositories.EstablecimientoRepository;
import com.unah.ProyectoBD.Repositories.FacturaMetodoPagoRepository;
import com.unah.ProyectoBD.Repositories.FacturaRepository;
import com.unah.ProyectoBD.Repositories.MetodoPagoRepository;
import com.unah.ProyectoBD.Repositories.RangoCAIRepository;
import com.unah.ProyectoBD.Repositories.TutoriaRepository;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;
    @Autowired
    private FacturaMetodoPagoRepository facturaMetodoPagoRepository;
    @Autowired
    private TutoriaRepository tutoriaRepository;
    @Autowired
    private RangoCAIRepository rangoCAIRepository;
    @Autowired
    private EstablecimientoRepository establecimientoRepository;
    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    private static final BigDecimal PRECIO_FIJO_TUTORIA = new BigDecimal("3500.00");

    /**
     * Endpoint para generar una nueva factura.
     * Realiza validaciones sobre el Rango CAI, el número de factura y los métodos
     * de pago.
     * Crea la factura, su detalle y asocia los métodos de pago.
     * 
     * @param crearFacturaDTO Objeto DTO con los datos para crear la factura.
     * @return ResponseEntity con la factura creada o un mensaje de error.
     */
    @PostMapping("/generar")
    @Transactional
    public ResponseEntity<?> generarFactura(@Valid @RequestBody CrearFacturaDto crearFacturaDTO) {
        // 1. Obtener la tutoría
        Optional<TutoriaModel> tutoriaOptional = tutoriaRepository.findById(crearFacturaDTO.getIdTutoria());
        if (tutoriaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tutoría con ID " + crearFacturaDTO.getIdTutoria() + " no encontrada.");
        }
        TutoriaModel tutoria = tutoriaOptional.get();

        // 2. Obtener el Rango CAI activo
        Optional<RangosCAIModel> rangoCAIOptional = rangoCAIRepository.findActiveRangoCAI();
        if (rangoCAIOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se encontró un Rango CAI activo o válido para emitir facturas.");
        }
        RangosCAIModel rangoCAI = rangoCAIOptional.get();

        // 3. Validar la fecha límite de emisión del CAI
        if (LocalDate.now().isAfter(rangoCAI.getFechaLimiteEmision())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("La fecha actual supera la fecha límite de emisión del CAI ("
                            + rangoCAI.getFechaLimiteEmision() + "). No se puede emitir la factura.");
        }

        // 4. Obtener los datos del establecimiento (asumimos que siempre hay uno y es
        // el primero)
        Optional<EstablecimientoModel> establecimientoOptional = establecimientoRepository.findAll().stream()
                .findFirst();
        if (establecimientoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "No se encontró información del establecimiento. Por favor, configure los datos del establecimiento.");
        }
        EstablecimientoModel establecimiento = establecimientoOptional.get();

        // 5. Generar el número de factura
        String numeroFacturaGenerado;
        try {
            numeroFacturaGenerado = generarSiguienteNumeroFactura(rangoCAI);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

        // 6. Validar que el número de factura generado no exceda el rango final
        if (compareInvoiceNumbers(numeroFacturaGenerado, rangoCAI.getRangoFinal()) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El número de factura generado ("
                    + numeroFacturaGenerado + ") excede el rango final del CAI (" + rangoCAI.getRangoFinal() + ").");
        }

        // 7. Validar que el número de factura generado sea único (aunque la DB lo
        // asegura con UNIQUE)
        if (facturaRepository.findByNumeroFactura(numeroFacturaGenerado).isPresent()) {
            // Esto debería ser raro si la lógica de generación es correcta, pero es una
            // doble verificación
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El número de factura " + numeroFacturaGenerado + " ya existe. Intente nuevamente.");
        }

        // 8. Validar que todos los métodos de pago existan
        for (Integer idMetodoPago : crearFacturaDTO.getIdMetodosPago()) {
            Optional<MetodoPagoModel> metodoPagoOptional = metodoPagoRepository.findById(idMetodoPago);
            if (metodoPagoOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Método de pago con ID " + idMetodoPago + " no encontrado.");
            }
        }

        try {
            // Crear Factura
            FacturaModel nuevaFactura = new FacturaModel();
            nuevaFactura.setIdRangosCAI(rangoCAI.getIdRangosCai());
            nuevaFactura.setEmpresaEmisora(establecimiento.getNombreEstablecimiento()); // Usar nombre del
                                                                                        // establecimiento como empresa
                                                                                        // emisora
            nuevaFactura.setRtnEmpresaEmisora("08011980000123"); // RTN de ejemplo para la empresa emisora
            nuevaFactura.setNumeroFactura(numeroFacturaGenerado);
            nuevaFactura.setEstablecimiento(establecimiento.getIdEstablecimiento());
            nuevaFactura.setNombreCliente(crearFacturaDTO.getNombreCliente());
            nuevaFactura.setRtnCliente(crearFacturaDTO.getRtnCliente());
            nuevaFactura = facturaRepository.save(nuevaFactura);

            // Crear DetalleFactura
            DetalleFacturaModel detalleFactura = new DetalleFacturaModel();
            detalleFactura.setIdFactura(nuevaFactura.getIdFacturas());
            detalleFactura.setIdTutoria(tutoria.getIdTutoria());
            detalleFactura.setDescripcion("Tutoria de " + tutoria.getIdTopico()); // Podrías obtener el nombre del
                                                                                  // tópico aquí
            detalleFactura.setCantidad(1);
            detalleFactura.setSubtotal(PRECIO_FIJO_TUTORIA);
            detalleFactura.setTotal(PRECIO_FIJO_TUTORIA); // No ISV, no descuento
            detalleFacturaRepository.save(detalleFactura);

            // Asociar Métodos de Pago
            for (Integer idMetodoPago : crearFacturaDTO.getIdMetodosPago()) {
                FacturaMetodoPagoModel facturaMetodoPago = new FacturaMetodoPagoModel();
                facturaMetodoPago.setIdFactura(nuevaFactura.getIdFacturas());
                facturaMetodoPago.setIdMetodoPago(idMetodoPago);
                facturaMetodoPagoRepository.save(facturaMetodoPago);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar la factura: " + e.getMessage());
        }
    }

    /**
     * Genera el siguiente número de factura correlativo basado en el Rango CAI
     * activo.
     * Asume un formato de número de factura como "AAA-BBB-CC-DDDDDDDD".
     * 
     * @param rangoCAI El objeto RangoCAI activo.
     * @return El siguiente número de factura.
     * @throws IllegalStateException Si no se puede generar un número de factura
     *                               válido.
     */
    private String generarSiguienteNumeroFactura(RangosCAIModel rangoCAI) {
        String rangoInicialStr = rangoCAI.getRangoInicial();
        String rangoFinalStr = rangoCAI.getRangoFinal();

        // Extraer la parte numérica del rango inicial (ej. 00000001 de
        // "000-001-01-00000001")
        String[] partesInicial = rangoInicialStr.split("-");
        String prefijo = partesInicial[0] + "-" + partesInicial[1] + "-" + partesInicial[2] + "-";
        long numeroInicial = Long.parseLong(partesInicial[3]);

        // Extraer la parte numérica del rango final
        String[] partesFinal = rangoFinalStr.split("-");
        long numeroFinal = Long.parseLong(partesFinal[3]);

        Optional<String> lastNumeroFacturaOptional = facturaRepository
                .findLastNumeroFacturaByRangosCAIId(rangoCAI.getIdRangosCai());
        long siguienteNumero;

        if (lastNumeroFacturaOptional.isPresent()) {
            // Si ya hay facturas, toma el último número y lo incrementa
            String lastNumeroStr = lastNumeroFacturaOptional.get();
            String[] partesLast = lastNumeroStr.split("-");
            long ultimoNumeroRegistrado = Long.parseLong(partesLast[3]);
            siguienteNumero = ultimoNumeroRegistrado + 1;
        } else {
            // Si no hay facturas para este CAI, usa el rango inicial
            siguienteNumero = numeroInicial;
        }

        // Validar que el siguiente número no exceda el rango final
        if (siguienteNumero > numeroFinal) {
            throw new IllegalStateException("Se ha alcanzado el límite de números de factura para el CAI actual.");
        }

        // Formatear el siguiente número a 8 dígitos con ceros a la izquierda
        String numeroFormateado = String.format("%08d", siguienteNumero);
        return prefijo + numeroFormateado;
    }

    /**
     * Compara dos números de factura en formato "AAA-BBB-CC-DDDDDDDD".
     * Retorna un valor negativo si num1 < num2, 0 si num1 == num2, un valor
     * positivo si num1 > num2.
     * 
     * @param num1 Primer número de factura.
     * @param num2 Segundo número de factura.
     * @return Resultado de la comparación.
     */
    private int compareInvoiceNumbers(String num1, String num2) {
        // Asume que el formato es consistente y solo el último segmento numérico
        // cambia.
        long val1 = Long.parseLong(num1.substring(num1.lastIndexOf('-') + 1));
        long val2 = Long.parseLong(num2.substring(num2.lastIndexOf('-') + 1));
        return Long.compare(val1, val2);
    }
}
