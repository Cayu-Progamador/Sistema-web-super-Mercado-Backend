package com.backendSupermercado.supermercasdo.modules.productos.entity;

import java.math.BigDecimal;
import java.util.List;

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
@Table(name = "presentacion_producto")
public class PresentacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presentacion")
    private Long idPresentacion;

    @Column(name = "nombre_presentacion", nullable = false, length = 100)
    private String nombrePresentacion;

    @Column(name = "factor_conversion", precision = 10, scale = 4)
    private BigDecimal factorConversion;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Builder.Default
    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @OneToMany(
            mappedBy = "presentacionProducto",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<CodigoBarra> codigosBarra;

}