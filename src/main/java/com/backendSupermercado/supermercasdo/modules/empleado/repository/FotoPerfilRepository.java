package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.FotoPerfil;

public interface FotoPerfilRepository extends JpaRepository<FotoPerfil, Long> {
    Optional<FotoPerfil> findByPersonaIdPersona(Long idPersona);
    void deleteByPersonaIdPersona(Long idPersona);
    boolean existsByPersonaIdPersona(Long idPersona);
}
