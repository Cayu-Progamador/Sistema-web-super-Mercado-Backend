package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity;

import java.math.BigDecimal;

import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
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
@Table(name = "detalle_movimiento_almacen")
public class DetalleMovimientoAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_movimiento")
    private Long idDetalleMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_movimiento", nullable = false)
    private MovimientoAlmacen movimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private UbicacionAlmacen ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_detalle_recepcion")
    private DetalleRecepcion detalleRecepcion;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "saldo_anterior", precision = 12, scale = 3)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_resultante", precision = 12, scale = 3)
    private BigDecimal saldoResultante;

    @Column(name = "observacion", length = 255)
    private String observacion;

}
