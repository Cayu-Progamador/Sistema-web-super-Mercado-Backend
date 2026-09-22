package com.backendSupermercado.supermercasdo.modules.venta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.venta.entity.VentaEnEspera;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;

public interface VentaEnEsperaRepository extends JpaRepository<VentaEnEspera, Long> {

    Optional<VentaEnEspera> findByCodigoAndEstado(String codigo, EstadoVenta estado);

    List<VentaEnEspera> findByUsuario_IdUsuarioAndEstadoOrderByFechaCreacionDesc(Long idUsuario, EstadoVenta estado);
}
