package com.backendSupermercado.supermercasdo.modules.permiso_personal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tipo_permiso",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tipo_permiso_nombre", columnNames = {"nombre"})
    }
)
public class TipoPermiso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    @Column(name = "es_remunerado")
    private Boolean esRemunerado = true;

    private Boolean estado = true;
}
