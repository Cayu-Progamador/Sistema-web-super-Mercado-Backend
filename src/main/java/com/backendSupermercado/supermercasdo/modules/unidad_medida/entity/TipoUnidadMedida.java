package com.backendSupermercado.supermercasdo.modules.unidad_medida.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "tipo_unidad_medida")
public class TipoUnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_unidad")
    private Long idTipoUnidad;

    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @OneToMany(mappedBy = "tipoUnidadMedida", fetch = FetchType.LAZY)
    private List<UnidadMedida> unidades;

}