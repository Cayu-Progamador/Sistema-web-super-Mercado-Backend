package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    boolean existsByCi(String ci);

    boolean existsByCiAndIdPersonaNot(String ci, Long idPersona);
}
