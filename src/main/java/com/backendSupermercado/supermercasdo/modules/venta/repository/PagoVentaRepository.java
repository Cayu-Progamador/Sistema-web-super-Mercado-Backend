package com.backendSupermercado.supermercasdo.modules.venta.repository;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.venta.entity.PagoVenta;

public interface PagoVentaRepository extends JpaRepository<PagoVenta, Long> {

    List<PagoVenta> findByVenta_IdVentaOrderByFechaPagoAsc(Long idVenta);

    @Query("select coalesce(sum(coalesce(p.montoEfectivo, 0) - coalesce(p.cambio, 0)), 0) "
            + "from PagoVenta p where p.venta.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarEfectivoPorSesion(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(case when p.metodoPago = com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago.EFECTIVO then p.montoEfectivo - p.cambio else 0 end), 0) from PagoVenta p where p.venta.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarEfectivoDirecto(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(case when p.metodoPago = com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago.QR then p.montoQr else 0 end), 0) from PagoVenta p where p.venta.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarQrDirecto(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(case when p.metodoPago = com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago.MIXTO then p.montoEfectivo else 0 end), 0) from PagoVenta p where p.venta.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarEfectivoMixto(@Param("idSesion") Long idSesion);

    @Query("select coalesce(sum(case when p.metodoPago = com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago.MIXTO then p.montoQr else 0 end), 0) from PagoVenta p where p.venta.sesionCaja.idSesionCaja = :idSesion")
    BigDecimal sumarQrMixto(@Param("idSesion") Long idSesion);
}
