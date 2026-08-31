package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity;

import java.math.BigDecimal;

import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;

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
@Table(name = "detalle_ajuste_almacen")
public class DetalleAjusteAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_ajuste")
    private Long idDetalleAjuste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ajuste", nullable = false)
    private AjusteAlmacen ajuste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @Column(name = "cantidad_sistema", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadSistema;

    @Column(name = "cantidad_fisica", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadFisica;

}