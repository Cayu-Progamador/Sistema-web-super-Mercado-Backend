package com.backendSupermercado.supermercasdo.modules.venta.repository;

import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.venta.entity.Venta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    Optional<Venta> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    @EntityGraph(attributePaths = { "cliente", "usuario", "detalles", "detalles.producto" })
    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);

    @EntityGraph(attributePaths = {
            "cliente", "usuario", "detalles", "detalles.producto", "detalles.presentacion",
            "detalles.presentacion.unidadMedida", "detalles.lote", "cuentaPorCobrar"
    })
    Optional<Venta> findDetalleByIdVenta(Long idVenta);

    long countByEstadoNot(EstadoVenta estado);

    @Query("select coalesce(sum(v.total), 0) from Venta v where v.estado = :estado")
    BigDecimal sumarTotales(@Param("estado") EstadoVenta estado);

    @Query("select count(v) from Venta v where v.cuentaPorCobrar is not null and v.cuentaPorCobrar.saldoPendiente > 0 and v.estado <> :estado")
    long contarPendientes(@Param("estado") EstadoVenta estado);

    @Query("select coalesce(sum(d.importe - (coalesce(d.costoUnitario, 0) * coalesce(d.cantidadBase, d.cantidad))), 0) "
            + "from Venta v join v.detalles d where v.estado = :estado and v.fechaVenta >= :inicio and v.fechaVenta < :fin")
    BigDecimal calcularGanancia(@Param("estado") EstadoVenta estado, @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    @EntityGraph(attributePaths = { "detalles", "detalles.existencia", "detalles.existencia.lote", "detalles.producto" })
    List<Venta> findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThan(EstadoVenta estado,
            LocalDateTime inicio, LocalDateTime fin);
}
