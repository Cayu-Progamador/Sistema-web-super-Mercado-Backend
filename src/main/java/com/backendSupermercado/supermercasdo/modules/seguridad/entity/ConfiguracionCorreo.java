package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        name="password_email",
        nullable=false,
        length = 255
    )
    private String passwordEmail;

    @Column(name="ultimo_uso")
    private LocalDateTime ultimoUso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smtp_config_id", nullable = false)
    private ConfiguracionSmtGlobal smtpConfig;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;


    @Column(nullable=false)
    private Boolean activo = true;

}
