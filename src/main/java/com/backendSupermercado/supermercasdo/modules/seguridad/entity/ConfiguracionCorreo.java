package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "configuracion_correo")
public class ConfiguracionCorreo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable=false,
        length = 150,
        unique = true
    )
    private String correo;

    @Column(
        nullable=false,
        length = 255
    )
    private String password;

    @Column(
        nullable=false,
        length = 100
    )
    private String smtp;

    @Column(nullable=false)
    private Integer puerto;

    @Column(nullable=false)
    private Boolean activo = true;

}
