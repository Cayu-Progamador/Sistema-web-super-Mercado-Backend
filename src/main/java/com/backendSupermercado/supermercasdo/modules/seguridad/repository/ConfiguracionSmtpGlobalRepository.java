package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionSmtGlobal;

public interface ConfiguracionSmtpGlobalRepository extends JpaRepository<ConfiguracionSmtGlobal, Long> {
    Optional<ConfiguracionSmtGlobal> findFirstByOrderByIdAsc();
}
