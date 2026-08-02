package com.backendSupermercado.supermercasdo.modules.asistencia.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResumenDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.entity.Asistencia;
import com.backendSupermercado.supermercasdo.modules.asistencia.repository.AsistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.AsistenciaService;
import com.backendSupermercado.supermercasdo.shared.util.ReporteAsistenciaUtil;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.ContratoTurno;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Turno;
import com.backendSupermercado.supermercasdo.modules.contrato.repository.ContratoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.entity.SolicitudPermiso;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.repository.SolicitudPermisoRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsistenciaServiceImpl implements AsistenciaService {

    private static final String ESTADO_PRESENTE = "PRESENTE";
    private static final String ESTADO_TARDANZA = "TARDANZA";
    private static final String ESTADO_COMPLETO = "COMPLETO";
    private static final String ESTADO_FALTA = "FALTA";
    private static final String ESTADO_JUSTIFICADO = "JUSTIFICADO";
    private static final String ESTADO_PERMISO = "PERMISO";

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContratoRepository contratoRepository;
    private final SolicitudPermisoRepository solicitudPermisoRepository;

    @Override
    @Transactional
    public AsistenciaResponseDto marcarEntrada(String username) {
        Contrato contrato = obtenerContratoActivo(username);
        Turno turno = obtenerTurnoDelDia(contrato)
                .orElseThrow(() -> new IllegalStateException("No tienes un turno asignado para hoy"));
        int tolerancia = obtenerTolerancia(contrato, turno);

        LocalTime ahora = LocalTime.now();
        LocalTime horaEntradaEsperada = turno.getHoraEntrada();
        LocalTime inicioPermitido = horaEntradaEsperada.minusMinutes(tolerancia);

        if (ahora.isBefore(inicioPermitido)) {
            throw new IllegalStateException(
                    "Aún no puedes marcar entrada. Tu turno inicia a las " + horaEntradaEsperada
                            + ". Puedes marcar desde las " + inicioPermitido);
        }

        if (ahora.isAfter(turno.getHoraSalida())) {
            throw new IllegalStateException(
                    "Ya pasó la hora de salida de tu turno (" + turno.getHoraSalida()
                            + "). No puedes marcar entrada a las " + ahora);
        }

        LocalDate hoy = LocalDate.now();

        Asistencia asistencia = asistenciaRepository
                .findByContratoIdAndFecha(contrato.getId(), hoy)
                .orElseGet(() -> {
                    Asistencia nueva = new Asistencia();
                    nueva.setContrato(contrato);
                    nueva.setFecha(hoy);
                    return nueva;
                });

        if (asistencia.getHoraEntrada() != null) {
            throw new IllegalStateException("Ya registraste tu entrada hoy a las " + asistencia.getHoraEntrada());
        }

        if (ESTADO_PERMISO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("Tienes un permiso aprobado hoy, no puedes marcar entrada");
        }

        asistencia.setHoraEntrada(ahora);

        LocalTime limiteTardanza = horaEntradaEsperada.plusMinutes(tolerancia);

        if (ahora.isAfter(limiteTardanza)) {
            long retraso = ChronoUnit.MINUTES.between(horaEntradaEsperada, ahora);
            asistencia.setMinutosRetraso((int) retraso);
            asistencia.setEstado(ESTADO_TARDANZA);
        } else {
            asistencia.setMinutosRetraso(null);
            asistencia.setEstado(ESTADO_PRESENTE);
        }

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponseDto marcarSalida(String username) {
        Contrato contrato = obtenerContratoActivo(username);
        Turno turno = obtenerTurnoDelDia(contrato)
                .orElseThrow(() -> new IllegalStateException("No tienes un turno asignado para hoy"));

        LocalTime ahora = LocalTime.now();
        LocalTime horaSalidaEsperada = turno.getHoraSalida();

        if (ahora.isBefore(horaSalidaEsperada)) {
            throw new IllegalStateException(
                    "Aún no puedes marcar salida. Tu turno termina a las " + horaSalidaEsperada);
        }

        LocalDate hoy = LocalDate.now();

        Asistencia asistencia = asistenciaRepository
                .findByContratoIdAndFecha(contrato.getId(), hoy)
                .orElseThrow(() -> new IllegalStateException("No registraste entrada hoy"));

        if (asistencia.getHoraSalida() != null) {
            throw new IllegalStateException("Ya registraste tu salida hoy a las " + asistencia.getHoraSalida());
        }

        if (ESTADO_PERMISO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("Tienes un permiso aprobado hoy, no puedes marcar salida");
        }

        asistencia.setHoraSalida(ahora);
        if (!ESTADO_TARDANZA.equals(asistencia.getEstado())) {
            asistencia.setEstado(ESTADO_COMPLETO);
        }

        if (asistencia.getHoraEntrada() != null) {
            long segundos = ChronoUnit.SECONDS.between(asistencia.getHoraEntrada(), ahora);
            if (segundos > 0) {
                BigDecimal horas = BigDecimal.valueOf(segundos)
                        .divide(BigDecimal.valueOf(3600), 2, java.math.RoundingMode.HALF_UP);
                asistencia.setHorasTrabajadas(horas);

                long minutosEsperados = ChronoUnit.MINUTES.between(turno.getHoraEntrada(), horaSalidaEsperada);
                long minutos = segundos / 60;
                if (minutos > minutosEsperados) {
                    BigDecimal extra = BigDecimal.valueOf(minutos - minutosEsperados)
                            .divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
                    asistencia.setHorasExtra(extra);
                }
            }
        }

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponseDto justificarMiAsistencia(String username, AsistenciaJustificarRequestDto dto) {
        if (dto.getFecha() == null || dto.getFecha().isBlank()) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        LocalDate fecha = LocalDate.parse(dto.getFecha());
        LocalDate hoy = LocalDate.now();

        if (!fecha.isBefore(hoy)) {
            throw new IllegalStateException("No puedes justificar el día de hoy. La justificación es solo para días anteriores.");
        }

        if (fecha.isBefore(hoy.minusDays(7))) {
            throw new IllegalStateException("No puedes justificar días con más de 7 días de antigüedad.");
        }

        if (dto.getMotivo() == null || dto.getMotivo().isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }

        Contrato contrato = obtenerContratoActivo(username);

        Asistencia asistencia = asistenciaRepository
                .findByContratoIdAndFecha(contrato.getId(), fecha)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay registro de asistencia para el día " + fecha + ". No puedes justificar un día sin falta registrada."));

        if (ESTADO_JUSTIFICADO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("Esta fecha ya está justificada");
        }

        validarJustificacion(asistencia, "No puedes justificar un día en el que marcaste entrada.");

        asistencia.setEstado(ESTADO_JUSTIFICADO);
        asistencia.setTipoJustificacion(dto.getTipoJustificacion());
        asistencia.setMotivoJustificacion(dto.getMotivo());

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarAusenciasRecientes(String username) {
        Contrato contrato = obtenerContratoActivo(username);
        LocalDate hoy = LocalDate.now();

        List<Asistencia> ausencias = asistenciaRepository
                .findByContratoIdAndFechaBetweenAndEstado(
                        contrato.getId(), hoy.minusDays(7), hoy.minusDays(1), ESTADO_FALTA);

        return ausencias.stream()
                .sorted(Comparator.comparing(Asistencia::getFecha).reversed())
                .map(a -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("idAsistencia", a.getId());
                    m.put("fecha", a.getFecha().toString());
                    m.put("estado", a.getEstado());
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AsistenciaResponseDto justificarAsistencia(Long idAsistencia, AsistenciaJustificarRequestDto dto) {
        Asistencia asistencia = asistenciaRepository.findById(idAsistencia)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada con id: " + idAsistencia));

        if (ESTADO_JUSTIFICADO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("Esta asistencia ya está justificada");
        }

        validarJustificacion(asistencia, "No puedes justificar un registro que tiene entrada marcada.");

        asistencia.setEstado(ESTADO_JUSTIFICADO);
        asistencia.setTipoJustificacion(dto.getTipoJustificacion());
        asistencia.setMotivoJustificacion(dto.getMotivo());

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public long ejecutarCierreDiario() {
        return ejecutarCierreDiario(LocalDate.now());
    }

    @Transactional
    public synchronized long ejecutarCierreDiario(LocalDate fecha) {
        List<Contrato> contratosActivos = contratoRepository.findAllWithControlAsistencia();

        long marcados = 0;

        for (Contrato contrato : contratosActivos) {
            Optional<Turno> turnoOpt = obtenerTurnoDelDia(contrato);
            if (turnoOpt.isEmpty()) {
                continue;
            }

            boolean yaTieneRegistro = asistenciaRepository
                    .findByContratoIdAndFecha(contrato.getId(), fecha)
                    .isPresent();

            if (!yaTieneRegistro) {
                try {
                    List<SolicitudPermiso> permisos = solicitudPermisoRepository
                            .findPermisosAprobadosActivos(contrato.getEmpleado(), fecha);
                    if (!permisos.isEmpty()) {
                        Asistencia permisoReg = new Asistencia();
                        permisoReg.setContrato(contrato);
                        permisoReg.setFecha(fecha);
                        permisoReg.setEstado(ESTADO_PERMISO);
                        permisoReg.setSolicitudPermiso(permisos.get(0));
                        asistenciaRepository.save(permisoReg);
                    } else {
                        Asistencia falta = new Asistencia();
                        falta.setContrato(contrato);
                        falta.setFecha(fecha);
                        falta.setEstado(ESTADO_FALTA);
                        asistenciaRepository.save(falta);
                    }
                    marcados++;
                } catch (DataIntegrityViolationException e) {
                    log.warn("Cierre diario: registro duplicado omitido para contrato {} en fecha {}",
                            contrato.getId(), fecha);
                }
            }
        }

        log.info("Cierre diario ejecutado: {} registros creados para el día {}", marcados, fecha);
        return marcados;
    }

    @Override
    @Transactional
    public AsistenciaResponseDto obtenerAsistenciaHoy(String username) {
        Contrato contrato = obtenerContratoActivo(username);
        LocalDate hoy = LocalDate.now();

        AsistenciaResponseDto dto = asistenciaRepository
                .findByContratoIdAndFecha(contrato.getId(), hoy)
                .map(this::toResponseDto)
                .orElseGet(() -> {
                    List<SolicitudPermiso> permisos = solicitudPermisoRepository
                            .findPermisosAprobadosActivos(contrato.getEmpleado(), hoy);
                    if (!permisos.isEmpty()) {
                        SolicitudPermiso permiso = permisos.get(0);
                        Asistencia nueva = new Asistencia();
                        nueva.setContrato(contrato);
                        nueva.setFecha(hoy);
                        nueva.setEstado(ESTADO_PERMISO);
                        nueva.setSolicitudPermiso(permiso);
                        nueva = asistenciaRepository.save(nueva);
                        return toResponseDto(nueva);
                    }
                    AsistenciaResponseDto empty = new AsistenciaResponseDto();
                    empty.setIdEmpleado(contrato.getId());
                    empty.setFecha(hoy);
                    return empty;
                });

        obtenerTurnoDelDia(contrato).ifPresent(turno -> {
            int tolerancia = obtenerTolerancia(contrato, turno);
            dto.setTurnoNombre(turno.getNombre());
            dto.setHoraEntradaEsperada(turno.getHoraEntrada());
            dto.setHoraSalidaEsperada(turno.getHoraSalida());
            dto.setToleranciaMinutos(tolerancia);
        });

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, Pageable pageable) {
        return asistenciaRepository.buscarAsistencias(
                filtros.getIdContrato(),
                filtros.getFechaDesde() != null ? LocalDate.parse(filtros.getFechaDesde()) : null,
                filtros.getFechaHasta() != null ? LocalDate.parse(filtros.getFechaHasta()) : null,
                filtros.getEstado(),
                pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponseDto> listarMisAsistencias(String username, String fechaDesde, String fechaHasta) {
        Contrato contrato = obtenerContratoActivo(username);
        return asistenciaRepository.findMisAsistencias(
                contrato.getId(),
                fechaDesde != null ? LocalDate.parse(fechaDesde) : null,
                fechaHasta != null ? LocalDate.parse(fechaHasta) : null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneAccesoAsistencia(String username) {
        try {
            obtenerContratoActivo(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] descargarReporteSemanal(String username, String fechaDesde, String fechaHasta) {
        Contrato contrato = obtenerContratoActivo(username);
        Empleado empleado = contrato.getEmpleado();

        String nombreEmpleado = empleado.getPersona() != null
                ? String.format("%s %s %s",
                        empleado.getPersona().getNombres(),
                        empleado.getPersona().getApellidoPaterno(),
                        empleado.getPersona().getApellidoMaterno())
                        .trim().replaceAll("\\s+", " ")
                : "";

        LocalDate desde = fechaDesde != null ? LocalDate.parse(fechaDesde) : LocalDate.now().minusDays(6);
        LocalDate hasta = fechaHasta != null ? LocalDate.parse(fechaHasta) : LocalDate.now();

        List<AsistenciaResponseDto> registros = listarMisAsistencias(username, fechaDesde != null ? fechaDesde : desde.toString(), fechaHasta != null ? fechaHasta : hasta.toString());

        return ReporteAsistenciaUtil.generarPdf(registros, nombreEmpleado, desde, hasta, username);
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaResumenDto obtenerMiResumen(String username, int anio, int mes) {
        Contrato contrato = obtenerContratoActivo(username);

        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        long asistencias = asistenciaRepository.countAsistenciasDelMes(contrato.getId(), inicioMes, finMes);
        long tardanzas = asistenciaRepository.countTardanzasDelMes(contrato.getId(), inicioMes, finMes);
        long faltas = asistenciaRepository.countFaltasDelMes(contrato.getId(), inicioMes, finMes);
        long justificados = asistenciaRepository.countJustificadosDelMes(contrato.getId(), inicioMes, finMes);
        long permisos = asistenciaRepository.countPermisosDelMes(contrato.getId(), inicioMes, finMes);
        long total = asistencias + faltas + justificados + permisos;
        double porcentaje = total > 0 ? ((double) (asistencias - tardanzas) / total) * 100 : 0;
        return new AsistenciaResumenDto(asistencias, tardanzas, faltas, Math.round(porcentaje * 100.0) / 100.0, justificados, permisos);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void cierreDiarioAutomatico() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        ejecutarCierreDiario(ayer);
    }

    @Scheduled(fixedRate = 1800000)
    @Transactional
    public void marcarSalidaAutomatica() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        List<Contrato> contratosActivos = contratoRepository.findAllWithControlAsistencia();

        for (Contrato contrato : contratosActivos) {
            Optional<Turno> turnoOpt = obtenerTurnoDelDia(contrato);
            if (turnoOpt.isEmpty()) continue;

            Turno turno = turnoOpt.get();
            LocalTime horaSalida = turno.getHoraSalida();

            if (ahora.isBefore(horaSalida.plusMinutes(30))) continue;

            Optional<Asistencia> asistenciaOpt = asistenciaRepository
                    .findByContratoIdAndFecha(contrato.getId(), hoy);

            if (asistenciaOpt.isEmpty()) continue;

            Asistencia asistencia = asistenciaOpt.get();

            if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() == null) {
                asistencia.setHoraSalida(horaSalida);
                if (!ESTADO_TARDANZA.equals(asistencia.getEstado())) {
                    asistencia.setEstado(ESTADO_COMPLETO);
                }

                long segundos = ChronoUnit.SECONDS.between(asistencia.getHoraEntrada(), horaSalida);
                if (segundos > 0) {
                    BigDecimal horas = BigDecimal.valueOf(segundos)
                            .divide(BigDecimal.valueOf(3600), 2, java.math.RoundingMode.HALF_UP);
                    asistencia.setHorasTrabajadas(horas);
                }

                asistenciaRepository.save(asistencia);
            }
        }
    }

    private Optional<Turno> obtenerTurnoDelDia(Contrato contrato) {
        DayOfWeek dia = LocalDate.now().getDayOfWeek();
        return contrato.getContratoTurnos().stream()
                .filter(ct -> aplicaTurnoHoy(ct, dia))
                .findFirst()
                .map(ContratoTurno::getTurno);
    }

    private boolean aplicaTurnoHoy(ContratoTurno ct, DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> Boolean.TRUE.equals(ct.getLunes());
            case TUESDAY -> Boolean.TRUE.equals(ct.getMartes());
            case WEDNESDAY -> Boolean.TRUE.equals(ct.getMiercoles());
            case THURSDAY -> Boolean.TRUE.equals(ct.getJueves());
            case FRIDAY -> Boolean.TRUE.equals(ct.getViernes());
            case SATURDAY -> Boolean.TRUE.equals(ct.getSabado());
            case SUNDAY -> Boolean.TRUE.equals(ct.getDomingo());
        };
    }

    private void validarJustificacion(Asistencia asistencia, String mensajeError) {
        if (ESTADO_PERMISO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("No puedes justificar un día con permiso aprobado");
        }
        if (asistencia.getHoraEntrada() != null && !ESTADO_FALTA.equals(asistencia.getEstado())) {
            throw new IllegalStateException(mensajeError);
        }
    }

    private int obtenerTolerancia(Contrato contrato, Turno turno) {
        if (contrato.getToleranciaMinutos() != null) {
            return contrato.getToleranciaMinutos();
        }
        return turno.getToleranciaMinutos() != null ? turno.getToleranciaMinutos() : 0;
    }

    private Contrato obtenerContratoActivo(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + username));

        Empleado empleado = usuario.getEmpleado();
        if (empleado == null) {
            throw new IllegalStateException("El usuario no tiene un empleado asociado");
        }

        if (Boolean.FALSE.equals(empleado.getEstado())) {
            throw new IllegalStateException("Tu cuenta de empleado está inactiva. Consulta con administración.");
        }

        Contrato contrato = contratoRepository.findByEmpleadoAndEstado(empleado, "ACTIVO")
                .orElse(null);

        if (contrato != null) {
            if (contrato.getFechaFin() != null && contrato.getFechaFin().isBefore(LocalDate.now())) {
                contrato.setEstado("VENCIDO");
                contratoRepository.save(contrato);
                throw new IllegalStateException("No puedes marcar asistencia: tu contrato está vencido");
            }

            if (Boolean.FALSE.equals(contrato.getControlaAsistencia())) {
                throw new IllegalStateException("No tienes control de asistencia habilitado en tu contrato");
            }

            return contrato;
        }

        List<Contrato> todos = contratoRepository.findAll(
                com.backendSupermercado.supermercasdo.shared.specification.ContratoSpecification
                        .empleadoIdEqual(empleado.getIdEmpleado()));

        if (todos.isEmpty()) {
            throw new IllegalStateException("No tienes un contrato registrado. Consulta con administración.");
        }

        Contrato ultimo = todos.get(todos.size() - 1);
        String estado = ultimo.getEstado();

        String msg = switch (estado) {
            case "SUSPENDIDO" -> "No puedes marcar asistencia: tu contrato está suspendido";
            case "FINALIZADO" -> "No puedes marcar asistencia: tu contrato está finalizado";
            case "VENCIDO" -> "No puedes marcar asistencia: tu contrato está vencido";
            default -> "No puedes marcar asistencia: estado del contrato: " + estado;
        };
        throw new IllegalStateException(msg);
    }

    private AsistenciaResponseDto toResponseDto(Asistencia asistencia) {
        Contrato contrato = asistencia.getContrato();
        Empleado empleado = contrato.getEmpleado();

        String nombreEmpleado = empleado.getPersona() != null
                ? String.format("%s %s %s",
                        empleado.getPersona().getNombres(),
                        empleado.getPersona().getApellidoPaterno(),
                        empleado.getPersona().getApellidoMaterno())
                        .trim().replaceAll("\\s+", " ")
                : "";

        String estado = asistencia.getEstado() != null ? asistencia.getEstado() : ESTADO_FALTA;

        return new AsistenciaResponseDto(
                asistencia.getId(),
                contrato.getId(),
                nombreEmpleado,
                asistencia.getFecha(),
                asistencia.getHoraEntrada(),
                asistencia.getHoraSalida(),
                estado,
                asistencia.getMinutosRetraso());
    }
}
