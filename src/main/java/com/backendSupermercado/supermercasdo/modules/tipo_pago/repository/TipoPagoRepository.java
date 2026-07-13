package com.backendSupermercado.supermercasdo.modules.tipo_pago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoPago;

public interface TipoPagoRepository extends JpaRepository<TipoPago, Long> {
    boolean existsByNombre(String nombre);
    List<TipoPago> findByNombreContainingIgnoreCaseAndEstado(String nombre, Boolean estado);
    List<TipoPago> findByNombreContainingIgnoreCase(String nombre);
    List<TipoPago> findByEstado(Boolean estado);
}
