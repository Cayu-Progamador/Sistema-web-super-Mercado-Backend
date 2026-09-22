package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.MenuPermisos;

public interface MenuPermisosRepository extends JpaRepository<MenuPermisos, Long> {
    List<MenuPermisos> findAllByOrderByMenu_OrdenAsc();
}
