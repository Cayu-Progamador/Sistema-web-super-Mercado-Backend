package com.backendSupermercado.supermercasdo.modules.productos.entity;

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
@Table(name = "codigo_barra")
public class CodigoBarra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo_barra")
    private Long idCodigoBarra;

    @Column(name = "codigo", nullable = false, length = 50)
    private String codigo;

    @Builder.Default
    @Column(name = "principal", nullable = false)
    private Boolean principal = false;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presentacion", nullable = false)
    private PresentacionProducto presentacionProducto;

}