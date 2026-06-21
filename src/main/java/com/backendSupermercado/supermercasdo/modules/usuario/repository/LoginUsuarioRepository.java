package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;

public interface LoginUsuarioRepository extends JpaRepository<LoginUsuario, Long> {

}
