package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   Optional<Usuario> findByUsername(String username);
   Optional<Usuario> findByEmpleadoIdEmpleado(Long idEmpleado);
}
