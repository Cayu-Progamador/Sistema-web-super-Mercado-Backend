package com.backendSupermercado.supermercasdo.modules.devoluciones.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;

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
@Table(name = "detalle_devolucion_proveedor")
public class DetalleDevolucionProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_devolucion")
    private Long idDetalleDevolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion", nullable = false)
    private DevolucionProveedor devolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_recepcion", nullable = false)
    private DetalleRecepcion detalleRecepcion;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Builder.Default
    @Column(name = "cantidad_reemplazada", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadReemplazada = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_motivo_devolucion", nullable = false)
    private MotivoDevolucion motivoDevolucion;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @OneToMany(mappedBy = "detalleDevolucion", cascade = jakarta.persistence.CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<DetalleOrigenDevolucion> origenes = new ArrayList<>();

}
