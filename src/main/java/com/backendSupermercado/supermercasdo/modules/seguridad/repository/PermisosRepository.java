package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Permisos;

@Repository
public interface PermisosRepository extends JpaRepository<Permisos, Long> {

}
