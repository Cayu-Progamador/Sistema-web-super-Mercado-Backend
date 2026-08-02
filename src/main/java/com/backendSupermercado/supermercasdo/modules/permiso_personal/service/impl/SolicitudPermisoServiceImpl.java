package com.backendSupermercado.supermercasdo.modules.permiso_personal.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.contrato.repository.ContratoRepository;
import com.backendSupermercado.supermercasdo.modules.contrato.repository.TipoPermisoRepository;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.HistorialSolicitudDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoRequestDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoResponseDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.EstadoSolicitud;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.HistorialSolicitud;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.SolicitudPermiso;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.TipoPermiso;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.repository.EstadoSolicitudRepository;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.repository.HistorialSolicitudRepository;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.repository.SolicitudPermisoRepository;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.service.SolicitudPermisoService;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;

import com.backendSupermercado.supermercasdo.shared.util.ReportePermisoUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolicitudPermisoServiceImpl implements SolicitudPermisoService {

    private final SolicitudPermisoRepository solicitudPermisoRepository;
    private final TipoPermisoRepository tipoPermisoRepository;
    private final EstadoSolicitudRepository estadoSolicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialSolicitudRepository historialSolicitudRepository;
    private final ContratoRepository contratoRepository;

    @Override
    @Transactional
    public SolicitudPermisoResponseDto crear(Long idUsuario, SolicitudPermisoRequestDto dto) {
        Empleado empleado = obtenerEmpleadoPorUsuario(idUsuario);

        com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato contrato = contratoRepository
                .findByEmpleadoAndEstado(empleado, "ACTIVO")
                .orElseThrow(() -> new IllegalStateException("No tienes un contrato activo para solicitar permisos"));
        if (!Boolean.TRUE.equals(contrato.getControlaAsistencia())) {
            throw new IllegalStateException("No tienes control de asistencia activo para solicitar permisos");
        }

        TipoPermiso tipo = tipoPermisoRepository.findById(dto.getIdTipo())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de permiso no encontrado"));

        if (dto.getFechaInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        if (dto.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser anterior a hoy");
        }
        if (dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin no puede ser anterior a la fecha inicio");
        }

        if (solicitudPermisoRepository.existsByEmpleadoAndEstadoNombre(empleado, "Pendiente")) {
            throw new IllegalStateException("Ya tienes una solicitud pendiente. Debes esperar que sea revisada.");
        }

        LocalDate hoy = LocalDate.now();
        long vigentes = solicitudPermisoRepository.countPermisosVigentes(empleado, hoy);
        if (vigentes > 0) {
            throw new IllegalStateException("Tienes un permiso aprobado vigente. Debes esperar a que termine.");
        }

        LocalDateTime inicioMes = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1);
        long aprobadasMes = solicitudPermisoRepository.countAprobadasEnMes(empleado, inicioMes, finMes);
        if (aprobadasMes >= 2) {
            throw new IllegalStateException("Has alcanzado el limite de 2 solicitudes aprobadas este mes.");
        }

        EstadoSolicitud pendiente = estadoSolicitudRepository.findByNombreIgnoreCase("Pendiente")
                .orElseThrow(() -> new EntityNotFoundException("Estado Pendiente no encontrado"));

        SolicitudPermiso solicitud = new SolicitudPermiso();
        solicitud.setEmpleado(empleado);
        solicitud.setTipo(tipo);
        solicitud.setEstado(pendiente);
        solicitud.setFechaInicio(dto.getFechaInicio());
        solicitud.setFechaFin(dto.getFechaFin());
        solicitud.setMotivo(dto.getMotivo());
        solicitud = solicitudPermisoRepository.save(solicitud);

        guardarHistorial(solicitud, null, pendiente, usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado")), null);

        return toDto(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudPermisoResponseDto> listarTodas() {
        return solicitudPermisoRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudPermisoResponseDto> listarPorEstado(String estado) {
        EstadoSolicitud estadoEntity = estadoSolicitudRepository.findByNombreIgnoreCase(estado)
                .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + estado));
        return solicitudPermisoRepository.findByEstadoOrderByCreatedAtDesc(estadoEntity)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudPermisoResponseDto> listarMisSolicitudes(Long idUsuario) {
        Empleado empleado = obtenerEmpleadoPorUsuario(idUsuario);
        return solicitudPermisoRepository.findByEmpleadoOrderByCreatedAtDesc(empleado)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SolicitudPermisoResponseDto revisar(Long idSolicitud, Long idAdmin) {
        SolicitudPermiso solicitud = solicitudPermisoRepository.findById(idSolicitud)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (!"Pendiente".equalsIgnoreCase(solicitud.getEstado().getNombre())) {
            throw new IllegalStateException("Solo se pueden revisar solicitudes en estado Pendiente");
        }

        EstadoSolicitud enRevision = estadoSolicitudRepository.findByNombreIgnoreCase("En revisión")
                .orElseThrow(() -> new EntityNotFoundException("Estado En revisión no encontrado"));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(enRevision);
        solicitud = solicitudPermisoRepository.save(solicitud);

        Usuario admin = usuarioRepository.findById(idAdmin)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        guardarHistorial(solicitud, estadoAnterior, enRevision, admin, null);

        return toDto(solicitud);
    }

    @Override
    @Transactional
    public SolicitudPermisoResponseDto aprobar(Long idSolicitud, Long idAdmin, String comentario) {
        SolicitudPermiso solicitud = solicitudPermisoRepository.findById(idSolicitud)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        String estadoActual = solicitud.getEstado().getNombre();
        if (!"En revisión".equalsIgnoreCase(estadoActual) && !"Pendiente".equalsIgnoreCase(estadoActual)) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes en revisión o pendientes");
        }

        EstadoSolicitud aprobado = estadoSolicitudRepository.findByNombreIgnoreCase("Aprobado")
                .orElseThrow(() -> new EntityNotFoundException("Estado Aprobado no encontrado"));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(aprobado);
        solicitud.setComentarioAdmin(comentario);
        solicitud = solicitudPermisoRepository.save(solicitud);

        Usuario admin = usuarioRepository.findById(idAdmin)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        guardarHistorial(solicitud, estadoAnterior, aprobado, admin, comentario);

        return toDto(solicitud);
    }

    @Override
    @Transactional
    public SolicitudPermisoResponseDto rechazar(Long idSolicitud, Long idAdmin, String comentario) {
        SolicitudPermiso solicitud = solicitudPermisoRepository.findById(idSolicitud)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        String estadoActual = solicitud.getEstado().getNombre();
        if (!"En revisión".equalsIgnoreCase(estadoActual) && !"Pendiente".equalsIgnoreCase(estadoActual)) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes en revisión o pendientes");
        }

        EstadoSolicitud rechazado = estadoSolicitudRepository.findByNombreIgnoreCase("Rechazado")
                .orElseThrow(() -> new EntityNotFoundException("Estado Rechazado no encontrado"));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(rechazado);
        solicitud.setComentarioAdmin(comentario);
        solicitud = solicitudPermisoRepository.save(solicitud);

        Usuario admin = usuarioRepository.findById(idAdmin)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        guardarHistorial(solicitud, estadoAnterior, rechazado, admin, comentario);

        return toDto(solicitud);
    }

    @Override
    @Transactional
    public SolicitudPermisoResponseDto cancelar(Long idSolicitud, Long idUsuario, String motivo) {
        SolicitudPermiso solicitud = solicitudPermisoRepository.findById(idSolicitud)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));

        if (!"Pendiente".equalsIgnoreCase(solicitud.getEstado().getNombre())) {
            throw new IllegalStateException("Solo puedes cancelar solicitudes en estado Pendiente");
        }

        Empleado empleado = obtenerEmpleadoPorUsuario(idUsuario);
        if (!solicitud.getEmpleado().getIdEmpleado().equals(empleado.getIdEmpleado())) {
            throw new IllegalStateException("No puedes cancelar una solicitud que no te pertenece");
        }

        EstadoSolicitud cancelado = estadoSolicitudRepository.findByNombreIgnoreCase("Cancelado")
                .orElseThrow(() -> new EntityNotFoundException("Estado Cancelado no encontrado"));

        EstadoSolicitud estadoAnterior = solicitud.getEstado();
        solicitud.setEstado(cancelado);
        solicitud = solicitudPermisoRepository.save(solicitud);

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        guardarHistorial(solicitud, estadoAnterior, cancelado, usuario, motivo);

        return toDto(solicitud);
    }

    @Override
    @Transactional
    public void expirarVencidas() {
        List<SolicitudPermiso> vencidas = solicitudPermisoRepository.findExpiredByFechaInicioBefore(LocalDate.now());
        if (vencidas.isEmpty()) return;

        EstadoSolicitud expirado = estadoSolicitudRepository.findByNombreIgnoreCase("Expirado")
                .orElseThrow(() -> new EntityNotFoundException("Estado Expirado no encontrado"));

        Usuario systemUser = usuarioRepository.findByUsername("system")
                .orElseGet(() -> usuarioRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("No hay usuarios en el sistema")));

        for (SolicitudPermiso s : vencidas) {
            EstadoSolicitud anterior = s.getEstado();
            s.setEstado(expirado);
            solicitudPermisoRepository.save(s);
            guardarHistorial(s, anterior, expirado, systemUser, "Auto-expirado — fecha de inicio vencida");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialSolicitudDto> obtenerHistorial(Long idSolicitud) {
        SolicitudPermiso solicitud = solicitudPermisoRepository.findById(idSolicitud)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
        return historialSolicitudRepository.findBySolicitudOrderByFechaCambioAsc(solicitud)
                .stream()
                .map(this::toHistorialDto)
                .toList();
    }

    private void guardarHistorial(SolicitudPermiso solicitud, EstadoSolicitud anterior,
                                   EstadoSolicitud nuevo, Usuario usuario, String comentario) {
        HistorialSolicitud historial = new HistorialSolicitud();
        historial.setSolicitud(solicitud);
        historial.setEstadoAnterior(anterior);
        historial.setEstadoNuevo(nuevo);
        historial.setUsuarioAccion(usuario);
        historial.setComentario(comentario);
        historialSolicitudRepository.save(historial);
    }

    private SolicitudPermisoResponseDto toDto(SolicitudPermiso entity) {
        String nombreEmpleado = entity.getEmpleado().getPersona().getNombres()
                + " " + entity.getEmpleado().getPersona().getApellidoPaterno()
                + " " + entity.getEmpleado().getPersona().getApellidoMaterno();
        String nombreCargo = contratoRepository.findByEmpleadoAndEstado(entity.getEmpleado(), "ACTIVO")
                .map(c -> c.getCargo().getNombre())
                .orElse(null);
        return new SolicitudPermisoResponseDto(
                entity.getId(),
                entity.getEmpleado().getIdEmpleado(),
                nombreEmpleado.trim(),
                entity.getTipo().getId(),
                entity.getTipo().getNombre(),
                entity.getEstado().getId(),
                entity.getEstado().getNombre(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getMotivo(),
                entity.getComentarioAdmin(),
                nombreCargo,
                entity.getCreatedAt()
        );
    }

    @Override
    public byte[] exportarPdf(String estado, String username) {
        List<SolicitudPermisoResponseDto> solicitudes;
        if (estado != null && !estado.isBlank()) {
            solicitudes = listarPorEstado(estado);
        } else {
            solicitudes = listarTodas();
        }
        return ReportePermisoUtil.generarPdf(solicitudes, username);
    }

    @Override
    public byte[] exportarExcel(String estado) {
        List<SolicitudPermisoResponseDto> solicitudes;
        if (estado != null && !estado.isBlank()) {
            solicitudes = listarPorEstado(estado);
        } else {
            solicitudes = listarTodas();
        }
        return ReportePermisoUtil.generarExcel(solicitudes);
    }

    private HistorialSolicitudDto toHistorialDto(HistorialSolicitud entity) {
        String nombreUsuario = entity.getUsuarioAccion().getUsername();
        return new HistorialSolicitudDto(
                entity.getId(),
                entity.getSolicitud().getId(),
                entity.getEstadoAnterior() != null ? entity.getEstadoAnterior().getNombre() : null,
                entity.getEstadoNuevo().getNombre(),
                entity.getUsuarioAccion().getIdUsuario(),
                nombreUsuario,
                entity.getComentario(),
                entity.getFechaCambio()
        );
    }

    private Empleado obtenerEmpleadoPorUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (usuario.getEmpleado() == null) {
            throw new IllegalStateException("El usuario no tiene un empleado asociado");
        }
        return usuario.getEmpleado();
    }
}
