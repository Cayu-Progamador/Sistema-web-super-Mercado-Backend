package com.backendSupermercado.supermercasdo.modules.permiso_personal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.HistorialSolicitud;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.SolicitudPermiso;

public interface HistorialSolicitudRepository extends JpaRepository<HistorialSolicitud, Long> {
    List<HistorialSolicitud> findBySolicitudOrderByFechaCambioAsc(SolicitudPermiso solicitud);
}
