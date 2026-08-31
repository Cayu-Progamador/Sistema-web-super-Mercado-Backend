package com.backendSupermercado.supermercasdo.modules.pedidos.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
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
        name = "detalle_pedido_proveedor",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pedido_producto_unidad",
                columnNames = {"id_pedido", "id_producto", "id_unidad_medida"}
        )
)
public class DetallePedidoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")
    private Long idDetallePedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private PedidoProveedor pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(name = "cantidad_solicitada", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadSolicitada;

    @OneToMany(
            mappedBy = "detallePedido",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<DetalleRecepcion> recepciones = new ArrayList<>();

}
