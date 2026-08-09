package com.backendSupermercado.supermercasdo.modules.productos.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.categorias.entity.Subcategoria;
import com.backendSupermercado.supermercasdo.modules.marcas.entity.Marca;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "codigo_interno", length = 50, unique = true)
    private String codigoInterno;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Builder.Default
    @Column(name = "controla_lote", nullable = false)
    private Boolean controlaLote = false;

    @Builder.Default
    @Column(name = "controla_vencimiento", nullable = false)
    private Boolean controlaVencimiento = false;


    @Column(name = "imagen")
    private String imagen;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_subcategoria", nullable = false)
    private Subcategoria subcategoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_marca")
    private Marca marca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_base", nullable = false)
    private UnidadMedida unidadBase;

    @OneToMany(
            mappedBy = "producto",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<PresentacionProducto> presentaciones;

}