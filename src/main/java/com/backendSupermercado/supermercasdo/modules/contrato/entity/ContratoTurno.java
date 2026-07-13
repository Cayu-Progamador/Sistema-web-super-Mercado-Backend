package com.backendSupermercado.supermercasdo.modules.contrato.entity;

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
@Table(name = "contrato_turno",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_contrato_turno", columnNames = {"id_contrato", "id_turno"})
    }
)
public class ContratoTurno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean lunes = false;

    private Boolean martes = false;

    private Boolean miercoles = false;

    private Boolean jueves = false;

    private Boolean viernes = false;

    private Boolean sabado = false;

    private Boolean domingo = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato", nullable = false)
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno", nullable = false)
    private Turno turno;
}
