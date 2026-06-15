package com.backendSupermercado.supermercasdo.modules.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

   Optional<Usuario> findByUsername(String username);
   Optional<Usuario> findByEmpleadoIdEmpleado(Long idEmpleado);
   
   @Query("""
      SELECT u FROM Usuario u
      LEFT JOIN FETCH u.usuarioRoles ur
      LEFT JOIN FETCH ur.rol
      WHERE u.username = :username
      """)
   Optional<Usuario> findByUsernameWithRoles(String username);

   Optional<Usuario> findByEmpleadoEmail(String email);

   boolean existsByUsername(String username);

   Long countByActivoTrue();
   Long countByActivoFalse();

   @Query("""
         SELECT u FROM Usuario u
         WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))
         """)
   List<Usuario> buscarPorUsername(@Param("username") String username);
}
