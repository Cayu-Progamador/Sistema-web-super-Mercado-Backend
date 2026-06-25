package com.backendSupermercado.supermercasdo.modules.empleado.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "direccion")
public class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Long idDireccion;

    @Column(name = "zona")
    private String zona;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero")
    private String numero;

    @Column(name = "referencia")
    private String referencia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_persona",
            nullable = false,
            unique = true
    )
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_ciudad",
            nullable = false
    )
    private Ciudad ciudad;
}
