package com.backendSupermercado.supermercasdo.modules.caja.repository;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendSupermercado.supermercasdo.modules.caja.entity.SesionCaja;
import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoSesionCaja;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface SesionCajaRepository extends JpaRepository<SesionCaja, Long> {
    Optional<SesionCaja> findByCajaIdCajaAndEstado(Long idCaja, EstadoSesionCaja estado);
    Page<SesionCaja> findAllByOrderByFechaAperturaDesc(Pageable pageable);
    Page<SesionCaja> findByUsuarioIdUsuarioOrderByFechaAperturaDesc(Long idUsuario, Pageable pageable);
    boolean existsByCajaIdCajaAndEstado(Long idCaja, EstadoSesionCaja estado);
    boolean existsByUsuarioIdUsuarioAndEstado(Long idUsuario, EstadoSesionCaja estado);
    Optional<SesionCaja> findByUsuarioIdUsuarioAndEstado(Long idUsuario, EstadoSesionCaja estado);

    @Query("select s from SesionCaja s where (:idUsuario is null or s.usuario.idUsuario = :idUsuario) "
            + "and (:idCaja is null or s.caja.idCaja = :idCaja) "
            + "and (:buscar = '' or lower(s.caja.nombre) like lower(concat('%', :buscar, '%')) "
            + "or lower(s.usuario.username) like lower(concat('%', :buscar, '%')) "
            + "or lower(concat(coalesce(s.usuario.empleado.persona.nombres, ''), ' ', coalesce(s.usuario.empleado.persona.apellidoPaterno, ''), ' ', coalesce(s.usuario.empleado.persona.apellidoMaterno, ''))) like lower(concat('%', :buscar, '%'))) "
            + "and s.fechaApertura >= coalesce(:desde, s.fechaApertura) "
            + "and s.fechaApertura <= coalesce(:hasta, s.fechaApertura)")
    Page<SesionCaja> filtrar(@Param("idUsuario") Long idUsuario, @Param("idCaja") Long idCaja,
            @Param("buscar") String buscar, @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta,
            Pageable pageable);
}
