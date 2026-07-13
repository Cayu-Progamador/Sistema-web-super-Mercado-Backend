package com.backendSupermercado.supermercasdo.modules.tipo_contrato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoContrato;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
    boolean existsByNombre(String nombre);
    List<TipoContrato> findByNombreContainingIgnoreCaseAndEstado(String nombre, Boolean estado);
    List<TipoContrato> findByNombreContainingIgnoreCase(String nombre);
    List<TipoContrato> findByEstado(Boolean estado);
}
