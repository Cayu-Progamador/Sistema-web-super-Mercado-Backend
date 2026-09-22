package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.RolPermisos;

public interface RolPermisosRepository extends JpaRepository<RolPermisos, Long> {
    List<RolPermisos> findByRol_IdRol(Long idRol);
    void deleteByRol_IdRol(Long idRol);
}
