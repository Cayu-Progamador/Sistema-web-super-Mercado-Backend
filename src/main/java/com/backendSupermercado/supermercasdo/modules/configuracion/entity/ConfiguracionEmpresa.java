package com.backendSupermercado.supermercasdo.modules.configuracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "configuracion_empresa")
public class ConfiguracionEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Column(length = 30)
    private String nit;

    @Column(length = 30)
    private String telefono;

    @Column(length = 150)
    private String correo;

    @Column(length = 80)
    private String departamento;

    @Column(length = 250)
    private String direccion;

    @Column(name = "logo_path", length = 500)
    private String logoPath;

    @Column(name = "fondo_login_path", length = 500)
    private String fondoLoginPath;

    @Column(name = "mensaje_comprobante", columnDefinition = "TEXT")
    private String mensajeComprobante;
}
