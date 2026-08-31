package com.backendSupermercado.supermercasdo.modules.inventario.entity;

import java.math.BigDecimal;

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
@Table(name = "configuracion_stock")
public class ConfiguracionStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion_stock")
    private Long idConfiguracionStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false, unique = true)
    private Producto producto;

    @Builder.Default
    @Column(name = "stock_minimo", nullable = false, precision = 12, scale = 3)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(name = "stock_maximo", precision = 12, scale = 3)
    private BigDecimal stockMaximo;

    @Column(name = "punto_reorden", precision = 12, scale = 3)
    private BigDecimal puntoReorden;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

}
