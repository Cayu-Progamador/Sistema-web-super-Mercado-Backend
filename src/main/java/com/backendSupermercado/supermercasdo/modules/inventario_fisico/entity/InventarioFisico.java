package com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario_fisico", uniqueConstraints = @UniqueConstraint(name = "uk_inventario_fisico_codigo", columnNames = "codigo"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario_fisico")
    private Long idInventarioFisico;

    @Column(nullable = false, length = 30)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoInventarioFisico tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoInventarioFisico estado;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "conteo_ciego", nullable = false)
    private Boolean conteoCiego;

    @Column(length = 500)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_responsable", nullable = false)
    private Empleado responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_almacen")
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion")
    private UbicacionAlmacen ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_creado_por", nullable = false)
    private Usuario creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_finalizacion")
    private LocalDateTime fechaFinalizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_finalizado_por")
    private Usuario finalizadoPor;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aprobado_por")
    private Usuario aprobadoPor;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "inventario_fisico_producto",
            joinColumns = @JoinColumn(name = "id_inventario_fisico"),
            inverseJoinColumns = @JoinColumn(name = "id_producto"),
            uniqueConstraints = @UniqueConstraint(name = "uk_inventario_fisico_producto", columnNames = {"id_inventario_fisico", "id_producto"}))
    @Builder.Default
    private List<Producto> productosSeleccionados = new ArrayList<>();

    @OneToMany(mappedBy = "inventarioFisico", fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetalleInventarioFisico> detalles = new ArrayList<>();

    @Version
    private Long version;
}
