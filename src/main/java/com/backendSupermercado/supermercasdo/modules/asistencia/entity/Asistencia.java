package com.backendSupermercado.supermercasdo.modules.asistencia.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.SolicitudPermiso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_asistencia_contrato_fecha", columnNames = {"id_contrato", "fecha"})
    }
)
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Column(name = "horas_trabajadas", precision = 4, scale = 2)
    private BigDecimal horasTrabajadas;

    @Column(name = "horas_extra", precision = 4, scale = 2)
    private BigDecimal horasExtra;

    @Column(name = "minutos_retraso")
    private Integer minutosRetraso;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "tipo_justificacion", length = 30)
    private String tipoJustificacion;

    @Column(name = "motivo_justificacion", length = 500)
    private String motivoJustificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato", nullable = false)
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud_permiso")
    private SolicitudPermiso solicitudPermiso;
}
