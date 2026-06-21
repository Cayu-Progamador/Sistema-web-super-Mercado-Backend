package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionCorreo;

public interface ConfiguracionCorreoRepository extends JpaRepository<ConfiguracionCorreo,Long>{

    Optional<ConfiguracionCorreo> findByActivoTrue();
}
