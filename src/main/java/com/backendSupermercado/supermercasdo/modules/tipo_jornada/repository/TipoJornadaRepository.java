package com.backendSupermercado.supermercasdo.modules.tipo_jornada.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoJornada;

public interface TipoJornadaRepository extends JpaRepository<TipoJornada, Long> {
    boolean existsByNombre(String nombre);
    List<TipoJornada> findByNombreContainingIgnoreCaseAndEstado(String nombre, Boolean estado);
    List<TipoJornada> findByNombreContainingIgnoreCase(String nombre);
    List<TipoJornada> findByEstado(Boolean estado);
}
