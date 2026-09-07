package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "ajuste_inventario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AjusteInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ajuste")
    private Long idAjuste;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoAjusteInventario tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false, length = 40)
    private MotivoAjusteInventario motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAjusteInventario estado;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "fecha_levantamiento")
    private LocalDate fechaLevantamiento;

    @Column(name = "responsable_levantamiento", length = 150)
    private String responsableLevantamiento;

    @Column(name = "lugar_hallazgo", length = 200)
    private String lugarHallazgo;

    @Column(name = "fecha_hallazgo")
    private LocalDate fechaHallazgo;

    @Column(name = "encontrado_por", length = 150)
    private String encontradoPor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario_creador", nullable = false)
    private Usuario creadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_aprobador")
    private Usuario aprobadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_aplicador")
    private Usuario aplicadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @Column(name = "fecha_aplicacion")
    private LocalDateTime fechaAplicacion;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;

    @OneToMany(mappedBy = "ajuste", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetalleAjusteInventario> detalles = new ArrayList<>();

    public void agregarDetalle(DetalleAjusteInventario detalle) {
        detalles.add(detalle);
        detalle.setAjuste(this);
    }
}
