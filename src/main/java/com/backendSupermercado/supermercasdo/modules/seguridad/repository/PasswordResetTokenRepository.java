package com.backendSupermercado.supermercasdo.modules.seguridad.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.seguridad.entity.PasswordResetToken;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") Usuario usuario);

    Optional<PasswordResetToken> findTopByUsuarioOrderByCreatedAtDesc(Usuario usuario);

}
