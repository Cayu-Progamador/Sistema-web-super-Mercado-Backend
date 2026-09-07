package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
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
@Table(name = "detalle_ajuste_inventario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAjusteInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_ajuste")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ajuste", nullable = false)
    private AjusteInventario ajuste;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @Column(name = "codigo_lote_capturado", length = 50)
    private String codigoLoteCapturado;

    @Column(name = "fecha_fabricacion_capturada")
    private LocalDate fechaFabricacionCapturada;

    @Column(name = "fecha_vencimiento_capturada")
    private LocalDate fechaVencimientoCapturada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private UbicacionAlmacen ubicacion;


    @Column(name = "stock_anterior", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockAnterior;

    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "stock_resultante", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockResultante;

    @Column(name = "costo_unitario", nullable = false, precision = 14, scale = 4)
    private BigDecimal costoUnitario;

    @Column(name = "valor_total", nullable = false, precision = 14, scale = 4)
    private BigDecimal valorTotal;

    @Column(name = "tipo_merma", length = 40)
    private String tipoMerma;

    @Column(name = "causa_merma", length = 500)
    private String causaMerma;
}
