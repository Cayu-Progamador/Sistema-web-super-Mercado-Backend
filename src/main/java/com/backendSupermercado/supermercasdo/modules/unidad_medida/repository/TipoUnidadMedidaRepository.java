package com.backendSupermercado.supermercasdo.modules.unidad_medida.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.TipoUnidadMedida;

public interface TipoUnidadMedidaRepository extends JpaRepository<TipoUnidadMedida, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}