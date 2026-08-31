package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity;

import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.AjusteAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.TransferenciaAlmacen;

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
import jakarta.persistence.UniqueConstraint;
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
@Table(
        name = "ubicacion_almacen",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ubicacion_codigo_almacen",
                        columnNames = {"id_almacen", "codigo"}
                ),
                @UniqueConstraint(
                        name = "uk_ubicacion_nombre_almacen",
                        columnNames = {"id_almacen", "nombre"}
                )
        }
)
public class UbicacionAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long idUbicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @Column(name = "codigo", nullable = false, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "tipo", length = 30)
    private String tipo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(
            mappedBy = "ubicacion",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<DetalleMovimientoAlmacen> detalleMovimientos = new ArrayList<>();

    @OneToMany(
            mappedBy = "ubicacionOrigen",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<TransferenciaAlmacen> transferenciasOrigen = new ArrayList<>();

    @OneToMany(
            mappedBy = "ubicacionDestino",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
        @Builder.Default
    private List<TransferenciaAlmacen> transferenciasDestino = new ArrayList<>();

    @OneToMany(
            mappedBy = "ubicacion",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AjusteAlmacen> ajustes = new ArrayList<>();

}
