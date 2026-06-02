package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.SeguridadUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

@Repository
public interface SeguridadUsuarioRepository extends JpaRepository<SeguridadUsuario, Long> {
    Optional<SeguridadUsuario> findByUsuario(Usuario usuario);
}
