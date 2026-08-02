package com.backendSupermercado.supermercasdo.modules.empleado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;

public interface ContactoRepository extends JpaRepository<Contacto, Long> {

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndPersona_IdPersonaNot(String telefono, Long idPersona);

    boolean existsByCorreo(String correo);

    boolean existsByCorreoAndPersona_IdPersonaNot(String correo, Long idPersona);
}
