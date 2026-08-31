package com.backendSupermercado.supermercasdo.modules.devoluciones.entity;

import java.math.BigDecimal;

import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "detalle_origen_devolucion", uniqueConstraints = @UniqueConstraint(
        name = "uk_devolucion_detalle_ubicacion",
        columnNames = {"id_detalle_devolucion", "id_ubicacion"}))
public class DetalleOrigenDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_origen")
    private Long idDetalleOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_devolucion", nullable = false)
    private DetalleDevolucionProveedor detalleDevolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private UbicacionAlmacen ubicacion;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
}
