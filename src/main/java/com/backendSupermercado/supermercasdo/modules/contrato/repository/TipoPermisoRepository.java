package com.backendSupermercado.supermercasdo.modules.contrato.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.TipoPermiso;

public interface TipoPermisoRepository extends JpaRepository<TipoPermiso, Long> {
    List<TipoPermiso> findByEstado(Boolean estado);
}
