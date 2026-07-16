package com.backendSupermercado.supermercasdo.mapper.contrato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.ContratoPdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.EmpleadoPdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.EmpresaDto;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.ContratoTurno;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;

@Component
public class ContratoMapper {

    @Value("${app.empresa.nombre}")
    private String empresaNombre;

    @Value("${app.empresa.nit}")
    private String empresaNit;

    @Value("${app.empresa.direccion}")
    private String empresaDireccion;

    @Value("${app.empresa.telefono}")
    private String empresaTelefono;

    @Value("${app.empresa.ciudad}")
    private String empresaCiudad;

    @Value("${app.empresa.logo}")
    private String empresaLogo;

    public ContratoDetallePdfDto toPdfDto(Contrato contrato, String generadoPor) {
        ContratoDetallePdfDto dto = new ContratoDetallePdfDto();
        dto.setEmpresa(mapearEmpresa());
        dto.setEmpleado(mapearEmpleado(contrato));
        dto.setContrato(mapearContrato(contrato));
        dto.setClausulas(obtenerClausulas());
        dto.setObservaciones(contrato.getObservaciones());
        dto.setGeneradoPor(generadoPor);
        return dto;
    }

    private EmpresaDto mapearEmpresa() {
        EmpresaDto dto = new EmpresaDto();
        dto.setNombre(empresaNombre);
        dto.setNit(empresaNit);
        dto.setDireccion(empresaDireccion);
        dto.setTelefono(empresaTelefono);
        dto.setCiudad(empresaCiudad);
        dto.setLogoPath(empresaLogo);
        return dto;
    }

    private EmpleadoPdfDto mapearEmpleado(Contrato contrato) {
        Empleado empleado = contrato.getEmpleado();
        if (empleado == null) return null;

        EmpleadoPdfDto dto = new EmpleadoPdfDto();
        dto.setCodigo("EMP" + String.format("%04d", empleado.getIdEmpleado()));
        dto.setFechaIngreso(empleado.getFechaContratacion());
        dto.setEstadoActivo(empleado.getEstado());
        dto.setCargo(contrato.getCargo() != null ? contrato.getCargo().getNombre() : null);

        Persona persona = empleado.getPersona();
        if (persona != null) {
            dto.setNombres(persona.getNombres());
            String apellidos = (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                    + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : "");
            dto.setApellidos(apellidos.isBlank() ? null : apellidos.trim());
            dto.setDocumentoIdentidad(persona.getCi());
            dto.setFechaNacimiento(persona.getFechaNacimiento());

            if (persona.getFotoPerfil() != null) {
                dto.setFotoPath(persona.getFotoPerfil().getRutaArchivo());
            }

            if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                Optional<Contacto> contacto = persona.getContactos().stream().findFirst();
                contacto.ifPresent(c -> {
                    dto.setCorreo(c.getCorreo());
                    dto.setTelefono(c.getTelefono());
                });
            }
        }

        return dto;
    }

    private ContratoPdfDto mapearContrato(Contrato contrato) {
        ContratoPdfDto dto = new ContratoPdfDto();
        dto.setNumeroContrato("CTR-" + String.format("%04d", contrato.getId()));
        dto.setTipoContrato(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getNombre() : null);
        dto.setFechaInicio(contrato.getFechaInicio());
        dto.setFechaFin(contrato.getFechaFin());
        dto.setDuracion(calcularDuracion(contrato.getFechaInicio(), contrato.getFechaFin()));
        dto.setEstado(contrato.getEstado());
        dto.setMotivo(contrato.getMotivoFin());
        dto.setSalarioMensual(contrato.getSueldoBase());
        dto.setSalarioLetras(numeroALetras(contrato.getSueldoBase()));
        dto.setFormaPago(contrato.getTipoPago() != null ? contrato.getTipoPago().getNombre() : null);
        dto.setJornadaLaboral(contrato.getTipoJornada() != null ? contrato.getTipoJornada().getNombre() : null);
        dto.setControlaAsistencia(contrato.getControlaAsistencia());

        if (contrato.getContratoTurnos() != null && !contrato.getContratoTurnos().isEmpty()) {
            ContratoTurno ct = contrato.getContratoTurnos().get(0);
            if (ct.getTurno() != null) {
                dto.setTurno(ct.getTurno().getNombre());
                String horaEntrada = ct.getTurno().getHoraEntrada() != null
                        ? ct.getTurno().getHoraEntrada().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                String horaSalida = ct.getTurno().getHoraSalida() != null
                        ? ct.getTurno().getHoraSalida().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                dto.setHorario(horaEntrada + " - " + horaSalida);
            }
        }

        return dto;
    }

    private String calcularDuracion(LocalDate inicio, LocalDate fin) {
        if (inicio == null) return "";
        if (fin == null) return "Indefinido";
        long meses = ChronoUnit.MONTHS.between(inicio, fin);
        long dias = ChronoUnit.DAYS.between(inicio, fin) % 30;
        if (meses == 0) return dias + " d\u00edas";
        if (meses < 12) return meses + " mes" + (meses != 1 ? "es" : "");
        long años = meses / 12;
        long mesesResto = meses % 12;
        if (mesesResto == 0) return años + " a\u00f1o" + (años != 1 ? "s" : "");
        return años + " a\u00f1o" + (años != 1 ? "s" : "") + " " + mesesResto + " mes" + (mesesResto != 1 ? "es" : "");
    }

    private List<String> obtenerClausulas() {
        return List.of(
                "El empleado se compromete a cumplir con las funciones y obligaciones inherentes al cargo contratado.",
                "La jornada laboral ser\u00e1 la establecida por la empresa, pudiendo ser modificada seg\u00fan las necesidades operativas.",
                "El salario ser\u00e1 cancelado mensualmente en las fechas establecidas por la empresa.",
                "El presente contrato podr\u00e1 renovarse previo acuerdo entre ambas partes.",
                "Ambas partes podr\u00e1n rescindir el contrato conforme a la normativa laboral vigente.",
                "El empleado deber\u00e1 registrar su asistencia diariamente seg\u00fan los procedimientos establecidos."
        );
    }

    private String numeroALetras(BigDecimal numero) {
        if (numero == null) return "Cero";
        return NumeroALetras.convertir(numero.longValue()) + " " + obtenerMoneda(numero);
    }

    private String obtenerMoneda(BigDecimal numero) {
        if (numero == null) return "Bolivianos";
        int decimales = numero.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).intValue();
        if (decimales > 0) {
            return "Bolivianos con " + decimales + "/100";
        }
        return "Bolivianos";
    }

    private static class NumeroALetras {
        private static final String[] UNIDADES = {
                "", "un", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"
        };
        private static final String[] DECENAS = {
                "", "diez", "veinte", "treinta", "cuarenta", "cincuenta",
                "sesenta", "setenta", "ochenta", "noventa"
        };
        private static final String[] DECENAS_COMPUESTAS = {
                "", "once", "doce", "trece", "catorce", "quince",
                "diecis\u00e9is", "diecisiete", "dieciocho", "diecinueve"
        };
        private static final String[] CENTENAS = {
                "", "cien", "doscientos", "trescientos", "cuatrocientos", "quinientos",
                "seiscientos", "setecientos", "ochocientos", "novecientos"
        };

        static String convertir(long numero) {
            if (numero == 0) return "cero";
            return convertirParte(numero).trim();
        }

        private static String convertirParte(long n) {
            if (n < 10) return UNIDADES[(int) n];
            if (n < 20) return DECENAS_COMPUESTAS[(int) (n - 10)];
            if (n < 100) {
                long d = n / 10;
                long u = n % 10;
                if (u == 0) return DECENAS[(int) d];
                return DECENAS[(int) d] + " y " + UNIDADES[(int) u];
            }
            if (n < 1000) {
                long c = n / 100;
                long r = n % 100;
                if (c == 1 && r == 0) return "cien";
                String centena = (c == 1) ? "ciento" : CENTENAS[(int) c];
                if (r == 0) return centena;
                return centena + " " + convertirParte(r);
            }
            if (n < 1000000) {
                long m = n / 1000;
                long r = n % 1000;
                if (m == 1) return "mil" + (r > 0 ? " " + convertirParte(r) : "");
                return convertirParte(m) + " mil" + (r > 0 ? " " + convertirParte(r) : "");
            }
            if (n < 1000000000L) {
                long m = n / 1000000;
                long r = n % 1000000;
                if (m == 1) return "un mill\u00f3n" + (r > 0 ? " " + convertirParte(r) : "");
                return convertirParte(m) + " millones" + (r > 0 ? " " + convertirParte(r) : "");
            }
            return String.valueOf(n);
        }
    }
}
