package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    @Query("""
    SELECT COUNT(DISTINCT ur.usuario.idUsuario)
    FROM UsuarioRol ur
    WHERE ur.estado = true
    AND ur.rol.estado = true
    AND ur.rol.nombre = 'ROLE_ADMIN'
    """)
    Long contarAdministradores();
}
