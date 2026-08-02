package com.backendSupermercado.supermercasdo.modules.permiso_personal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.EstadoSolicitud;

public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Long> {
    Optional<EstadoSolicitud> findByNombreIgnoreCase(String nombre);
}
