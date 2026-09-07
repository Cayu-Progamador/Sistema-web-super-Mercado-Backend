package com.backendSupermercado.supermercasdo.modules.cliente.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", nullable = false, length = 100)
    private String apellidoMaterno;

    @Column(name = "ci", nullable = false, unique = true, length = 20)
    private String ci;

    @Column(name = "correo", length = 150)
    private String correo;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
}
