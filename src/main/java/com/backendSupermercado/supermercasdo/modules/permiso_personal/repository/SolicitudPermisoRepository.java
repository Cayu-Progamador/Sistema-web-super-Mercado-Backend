package com.backendSupermercado.supermercasdo.modules.permiso_personal.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.EstadoSolicitud;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.SolicitudPermiso;

public interface SolicitudPermisoRepository extends JpaRepository<SolicitudPermiso, Long> {
    @Query("SELECT s FROM SolicitudPermiso s ORDER BY s.createdAt DESC")
    List<SolicitudPermiso> findAllOrderByCreatedAtDesc();
    List<SolicitudPermiso> findByEstadoOrderByCreatedAtDesc(EstadoSolicitud estado);
    List<SolicitudPermiso> findByEmpleadoOrderByCreatedAtDesc(Empleado empleado);

    @Query("SELECT COUNT(s) > 0 FROM SolicitudPermiso s WHERE s.empleado = :empleado AND LOWER(s.estado.nombre) = LOWER(:estadoNombre)")
    boolean existsByEmpleadoAndEstadoNombre(@Param("empleado") Empleado empleado, @Param("estadoNombre") String estadoNombre);

    @Query("SELECT COUNT(s) FROM SolicitudPermiso s WHERE s.empleado = :empleado AND s.estado.nombre = 'Aprobado' AND s.fechaFin >= :hoy")
    long countPermisosVigentes(@Param("empleado") Empleado empleado, @Param("hoy") LocalDate hoy);

    @Query("SELECT COUNT(s) FROM SolicitudPermiso s WHERE s.empleado = :empleado AND s.estado.nombre = 'Aprobado' AND s.createdAt >= :inicioMes AND s.createdAt < :finMes")
    long countAprobadasEnMes(@Param("empleado") Empleado empleado, @Param("inicioMes") LocalDateTime inicioMes, @Param("finMes") LocalDateTime finMes);

    @Query("SELECT s FROM SolicitudPermiso s WHERE s.estado.nombre IN ('Pendiente', 'En revisión') AND s.fechaInicio < :hoy")
    List<SolicitudPermiso> findExpiredByFechaInicioBefore(@Param("hoy") LocalDate hoy);

    @Query("SELECT s FROM SolicitudPermiso s WHERE s.empleado = :empleado AND s.estado.nombre = 'Aprobado' AND s.fechaInicio <= :hoy AND s.fechaFin >= :hoy")
    List<SolicitudPermiso> findPermisosAprobadosActivos(@Param("empleado") Empleado empleado, @Param("hoy") LocalDate hoy);
}
