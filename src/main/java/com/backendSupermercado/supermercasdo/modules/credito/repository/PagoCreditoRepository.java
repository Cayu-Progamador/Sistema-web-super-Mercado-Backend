package com.backendSupermercado.supermercasdo.modules.credito.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.credito.entity.PagoCredito;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoCreditoRepository extends JpaRepository<PagoCredito, Long> {
    List<PagoCredito> findByCuenta_IdCuentaPorCobrarOrderByFechaPagoDesc(Long idCuenta);

    @Query("select coalesce(sum(p.montoEfectivo), 0) from PagoCredito p where p.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarEfectivoPorSesion(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(p.montoQr), 0) from PagoCredito p where p.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarQrPorSesion(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(p.monto), 0) from PagoCredito p where p.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarTotalPorSesion(@Param("idSesion") Long idSesion);
}
