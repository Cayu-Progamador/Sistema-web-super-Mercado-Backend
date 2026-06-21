package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "configuracion_smtp_global")
public class ConfiguracionSmtGlobal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable=false,
        length = 100
    )
    private String smtp;

    @Column(nullable=false)
    private Integer puerto;
}
