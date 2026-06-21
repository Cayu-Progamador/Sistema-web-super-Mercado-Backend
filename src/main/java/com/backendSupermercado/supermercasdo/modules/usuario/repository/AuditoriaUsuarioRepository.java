package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.AuditoriaUsuario;

public interface AuditoriaUsuarioRepository extends JpaRepository<AuditoriaUsuario, Long> {

}
