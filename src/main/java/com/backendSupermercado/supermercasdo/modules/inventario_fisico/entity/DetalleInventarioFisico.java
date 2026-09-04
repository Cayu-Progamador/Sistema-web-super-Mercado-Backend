package com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_inventario_fisico", uniqueConstraints = @UniqueConstraint(
        name = "uk_detalle_inventario_existencia", columnNames = {"id_inventario_fisico", "id_existencia_origen"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleInventarioFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_inventario_fisico")
    private Long idDetalleInventarioFisico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_inventario_fisico", nullable = false)
    private InventarioFisico inventarioFisico;

    @Column(name = "id_existencia_origen", nullable = false)
    private Long idExistenciaOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_almacen", nullable = false)
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ubicacion", nullable = false)
    private UbicacionAlmacen ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    @Column(name = "cantidad_sistema", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadSistema;

    @Column(name = "cantidad_fisica", precision = 12, scale = 3)
    private BigDecimal cantidadFisica;

    @Column(precision = 12, scale = 3)
    private BigDecimal diferencia;

    @Column(name = "motivo_diferencia", length = 100)
    private String motivoDiferencia;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_conteo")
    private LocalDateTime fechaConteo;
}
