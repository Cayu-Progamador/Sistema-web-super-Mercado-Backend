package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.LoginUsuario;

@Repository
public interface LoginUsuarioRepository extends JpaRepository<LoginUsuario, Long> {

}
