package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

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
@Table(name = "ajuste_almacen")
public class AjusteAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste")
    private Long idAjuste;

    @Column(name = "codigo", nullable = false, length = 30, unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private UbicacionAlmacen ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_ajuste", nullable = false)
    private TipoAjusteAlmacen tipoAjuste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_ajuste", nullable = false)
    private LocalDateTime fechaAjuste;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @OneToMany(
            mappedBy = "ajuste",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<DetalleAjusteAlmacen> detalles = new ArrayList<>();

}