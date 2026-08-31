package com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DevolucionProveedor;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.RecepcionMercaderia;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

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
@Table(name = "movimiento_almacen")
public class MovimientoAlmacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_movimiento", nullable = false)
    private TipoMovimientoAlmacen tipoMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recepcion")
    private RecepcionMercaderia recepcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_devolucion")
    private DevolucionProveedor devolucion;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "referencia", length = 50)
    private String referencia;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @OneToMany(
            mappedBy = "movimiento",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Builder.Default
    private List<DetalleMovimientoAlmacen> detalles = new ArrayList<>();

}