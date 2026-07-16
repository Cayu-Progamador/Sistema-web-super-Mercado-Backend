package com.backendSupermercado.supermercasdo.modules.contrato.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratoDetallePdfDto {

    private EmpresaDto empresa;
    private EmpleadoPdfDto empleado;
    private ContratoPdfDto contrato;
    private List<String> clausulas;
    private String observaciones;
    private String generadoPor;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpresaDto {
        private String nombre;
        private String nit;
        private String direccion;
        private String telefono;
        private String ciudad;
        private String logoPath;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpleadoPdfDto {
        private String codigo;
        private String nombres;
        private String apellidos;
        private String documentoIdentidad;
        private LocalDate fechaNacimiento;
        private String telefono;
        private String correo;
        private String cargo;
        private String departamento;
        private LocalDate fechaIngreso;
        private Boolean estadoActivo;
        private String fotoPath;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContratoPdfDto {
        private String numeroContrato;
        private String tipoContrato;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String duracion;
        private String estado;
        private String motivo;
        private BigDecimal salarioMensual;
        private String salarioLetras;
        private String formaPago;
        private String jornadaLaboral;
        private String turno;
        private String horario;
        private Boolean controlaAsistencia;
        private String firmaEmpleadorPath;
        private String firmaEmpleadoPath;
    }
}
