package com.backendSupermercado.supermercasdo.modules.venta.repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import com.backendSupermercado.supermercasdo.modules.venta.entity.CuentaPorCobrar;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;

public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long> {

    Optional<CuentaPorCobrar> findByVenta_IdVenta(Long idVenta);

    List<CuentaPorCobrar> findByCliente_IdClienteAndEstadoIn(Long idCliente, List<EstadoCuenta> estados);

    @Query("select coalesce(sum(c.saldoPendiente), 0) from CuentaPorCobrar c where c.cliente.idCliente = :idCliente and c.saldoPendiente > 0 and c.estado in :estados")
    BigDecimal sumarSaldoPendiente(@Param("idCliente") Long idCliente, @Param("estados") List<EstadoCuenta> estados);

    @EntityGraph(attributePaths = { "cliente", "venta", "venta.detalles", "venta.detalles.producto" })
    @Query("select c from CuentaPorCobrar c join c.venta v join c.cliente cl where "
            + "(:buscar = '' or lower(v.codigo) like lower(concat('%', :buscar, '%')) "
            + "or lower(concat(cl.nombres, ' ', coalesce(cl.apellidoPaterno, ''), ' ', coalesce(cl.apellidoMaterno, ''))) like lower(concat('%', :buscar, '%'))) "
            + "and (:estado is null or c.estado = :estado) and (:desde is null or c.fechaLimite >= :desde) "
            + "and (:hasta is null or c.fechaLimite <= :hasta)")
    Page<CuentaPorCobrar> filtrar(@Param("buscar") String buscar, @Param("estado") EstadoCuenta estado,
            @Param("desde") java.time.LocalDate desde, @Param("hasta") java.time.LocalDate hasta, Pageable pageable);

    @Query("select coalesce(sum(c.saldoPendiente), 0) from CuentaPorCobrar c where c.saldoPendiente > 0 and c.estado in :estados")
    BigDecimal sumarSaldos(@Param("estados") List<EstadoCuenta> estados);

    @Query("select coalesce(sum(c.saldoPendiente), 0) from CuentaPorCobrar c where c.saldoPendiente > 0 and c.estado in :estados and c.fechaLimite < :hoy")
    BigDecimal sumarVencidos(@Param("estados") List<EstadoCuenta> estados, @Param("hoy") java.time.LocalDate hoy);

    long countBySaldoPendienteGreaterThanAndEstadoIn(BigDecimal saldo, List<EstadoCuenta> estados);
}
