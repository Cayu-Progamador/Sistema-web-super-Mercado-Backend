package com.backendSupermercado.supermercasdo.modules.configuracion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.configuracion.entity.ConfiguracionEmpresa;

public interface ConfiguracionEmpresaRepository extends JpaRepository<ConfiguracionEmpresa, Long> {
    Optional<ConfiguracionEmpresa> findFirstByOrderByIdAsc();
}
