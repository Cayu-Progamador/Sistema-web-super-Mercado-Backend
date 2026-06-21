package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Permisos;

public interface PermisosRepository extends JpaRepository<Permisos, Long> {

}
