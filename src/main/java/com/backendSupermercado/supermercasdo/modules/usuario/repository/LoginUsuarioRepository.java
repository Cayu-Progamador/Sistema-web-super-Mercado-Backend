package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

public interface LoginUsuarioRepository extends JpaRepository<LoginUsuario, Long> {

    long countByUsuario(Usuario usuario);

    long countByUsuarioAndFechaLoginAfter(Usuario usuario, LocalDateTime fecha);

}
