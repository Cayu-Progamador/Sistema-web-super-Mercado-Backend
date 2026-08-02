package com.backendSupermercado.supermercasdo.modules.asistencia.service.impl;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaCrearRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaEditDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDetalleDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AusenteDto;
import com.backendSupermercado.supermercasdo.shared.util.ReporteAsistenciaUtil;
import com.backendSupermercado.supermercasdo.modules.asistencia.entity.Asistencia;
import com.backendSupermercado.supermercasdo.modules.asistencia.repository.AsistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.asistencia.service.AsistenciaAdminService;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.ContratoTurno;
import com.backendSupermercado.supermercasdo.modules.contrato.repository.ContratoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AsistenciaAdminServiceImpl implements AsistenciaAdminService {

    private static final String ESTADO_FALTA = "FALTA";
    private static final String ESTADO_JUSTIFICADO = "JUSTIFICADO";
    private static final String ESTADO_PERMISO = "PERMISO";

    private final AsistenciaRepository asistenciaRepository;
    private final ContratoRepository contratoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, Pageable pageable) {
        LocalDate fechaDesde = filtros.getFechaDesde() != null ? LocalDate.parse(filtros.getFechaDesde()) : null;
        LocalDate fechaHasta = filtros.getFechaHasta() != null ? LocalDate.parse(filtros.getFechaHasta()) : null;

        String busquedaParam = (busqueda != null && !busqueda.isBlank()) ? "%" + busqueda.trim().toLowerCase() + "%" : null;

        Page<AsistenciaResponseDto> page;
        if (fechaDesde != null && fechaDesde.equals(fechaHasta)) {
            DayOfWeek dia = fechaDesde.getDayOfWeek();
            Pageable sinOrden = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
            page = asistenciaRepository.buscarAsistenciasDelDia(
                    fechaDesde,
                    filtros.getEstado(),
                    filtros.getIdContrato(),
                    idTurno,
                    busquedaParam,
                    dia == DayOfWeek.MONDAY,
                    dia == DayOfWeek.TUESDAY,
                    dia == DayOfWeek.WEDNESDAY,
                    dia == DayOfWeek.THURSDAY,
                    dia == DayOfWeek.FRIDAY,
                    dia == DayOfWeek.SATURDAY,
                    dia == DayOfWeek.SUNDAY,
                    sinOrden);
        } else {
            page = asistenciaRepository.buscarAsistenciasAdmin(
                    fechaDesde, fechaHasta, filtros.getEstado(), filtros.getIdContrato(), idTurno, busquedaParam, pageable);
        }

        page.forEach(this::enriquecerConTurno);

        return page;
    }

    private void enriquecerConTurno(AsistenciaResponseDto dto) {
        if (dto.getFecha() == null) return;
        DayOfWeek dia = dto.getFecha().getDayOfWeek();

        Optional<Contrato> contratoOpt = contratoRepository.findById(dto.getIdEmpleado());
        if (contratoOpt.isEmpty()) return;

        Contrato contrato = contratoOpt.get();

        contrato.getContratoTurnos().stream()
                .filter(ct -> aplicaTurnoDia(ct, dia))
                .findFirst()
                .map(ContratoTurno::getTurno)
                .ifPresent(turno -> {
                    dto.setTurnoNombre(turno.getNombre());
                    dto.setHoraEntradaEsperada(turno.getHoraEntrada());
                    dto.setHoraSalidaEsperada(turno.getHoraSalida());
                    int tolerancia = contrato.getToleranciaMinutos() != null
                            ? contrato.getToleranciaMinutos()
                            : (turno.getToleranciaMinutos() != null ? turno.getToleranciaMinutos() : 0);
                    dto.setToleranciaMinutos(tolerancia);
                });
    }

    private boolean aplicaTurnoDia(ContratoTurno ct, DayOfWeek dia) {
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

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumenHoy() {
        LocalDate hoy = LocalDate.now();
        List<Contrato> contratos = contratoRepository.findAllWithControlAsistencia();

        long totalContratos = contratos.size();
        long presentes = 0;
        long ausentes = 0;
        long tardanzas = 0;
        long justificados = 0;
        long permisos = 0;

        for (Contrato contrato : contratos) {
            Optional<Asistencia> asistenciaOpt = asistenciaRepository
                    .findByContratoIdAndFecha(contrato.getId(), hoy);

            if (asistenciaOpt.isEmpty()) {
                boolean tieneTurnoHoy = contrato.getContratoTurnos().stream()
                        .anyMatch(ct -> aplicaTurnoDia(ct, hoy.getDayOfWeek()));
                if (!tieneTurnoHoy) {
                    continue;
                }
                ausentes++;
                continue;
            }

            Asistencia a = asistenciaOpt.get();
            switch (a.getEstado()) {
                case "PRESENTE" -> presentes++;
                case "COMPLETO" -> presentes++;
                case "TARDANZA" -> tardanzas++;
                case "JUSTIFICADO" -> justificados++;
                case "PERMISO" -> permisos++;
                default -> ausentes++;
            }
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("presentes", presentes);
        resumen.put("ausentes", ausentes);
        resumen.put("tardanzas", tardanzas);
        resumen.put("justificados", justificados);
        resumen.put("permisos", permisos);
        resumen.put("total", (int) totalContratos);
        return resumen;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerDetalleEmpleado(Long idContrato, int anio, int mes) {
        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));

        List<ContratoTurno> turnos = contrato.getContratoTurnos();

        List<Asistencia> asistencias = asistenciaRepository
                .findAsistenciasDelMesWithTurno(idContrato, inicioMes, finMes);

        long presentes = asistencias.stream()
                .filter(a -> "COMPLETO".equals(a.getEstado()) || "PRESENTE".equals(a.getEstado()))
                .count();
        long tardanzas = asistencias.stream()
                .filter(a -> "TARDANZA".equals(a.getEstado()))
                .count();
        long faltas = asistencias.stream()
                .filter(a -> "FALTA".equals(a.getEstado()))
                .count();
        long justificados = asistencias.stream()
                .filter(a -> "JUSTIFICADO".equals(a.getEstado()) || "PERMISO".equals(a.getEstado()))
                .count();

        String nombre = contrato.getEmpleado().getPersona() != null
                ? String.format("%s %s %s",
                        contrato.getEmpleado().getPersona().getNombres(),
                        contrato.getEmpleado().getPersona().getApellidoPaterno(),
                        contrato.getEmpleado().getPersona().getApellidoMaterno())
                        .trim().replaceAll("\\s+", " ")
                : "";

        Map<LocalDate, String> mapa = new HashMap<>();
        for (Asistencia a : asistencias) {
            mapa.put(a.getFecha(), a.getEstado() != null ? a.getEstado() : "AUSENTE");
        }

        LocalDate hoy = LocalDate.now();
        List<Map<String, Object>> dias = new ArrayList<>();
        for (int i = 1; i <= inicioMes.lengthOfMonth(); i++) {
            LocalDate fecha = LocalDate.of(anio, mes, i);
            Map<String, Object> d = new HashMap<>();
            d.put("fecha", fecha.toString());
            boolean esFuturo = fecha.isAfter(hoy);
            boolean diaLaborable = turnos.stream().anyMatch(ct -> aplicaTurnoDia(ct, fecha.getDayOfWeek()));
            String estado;
            if (mapa.containsKey(fecha)) {
                estado = mapa.get(fecha);
            } else if (!diaLaborable && !esFuturo) {
                estado = "DESCANSO";
            } else {
                estado = "AUSENTE";
            }
            d.put("estado", estado);
            dias.add(d);
        }

        Map<String, Object> detalle = new HashMap<>();
        detalle.put("nombreEmpleado", nombre);
        detalle.put("idEmpleado", contrato.getId());
        detalle.put("cargo", contrato.getCargo() != null ? contrato.getCargo().getNombre() : "");
        detalle.put("presentes", (int) presentes);
        detalle.put("tardanzas", (int) tardanzas);
        detalle.put("faltas", (int) faltas);
        detalle.put("justificados", (int) justificados);
        detalle.put("dias", dias);
        return detalle;
    }

    @Override
    @Transactional
    public AsistenciaResponseDto justificarAsistencia(Long idAsistencia, AsistenciaJustificarRequestDto dto) {
        Asistencia asistencia = asistenciaRepository.findById(idAsistencia)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada con id: " + idAsistencia));

        if (ESTADO_JUSTIFICADO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("Esta asistencia ya está justificada");
        }

        if (ESTADO_PERMISO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("No puedes justificar un día con permiso aprobado");
        }

        if (asistencia.getHoraEntrada() != null) {
            throw new IllegalStateException("No puedes justificar un registro que tiene entrada marcada.");
        }

        asistencia.setEstado(ESTADO_JUSTIFICADO);
        asistencia.setTipoJustificacion(dto.getTipoJustificacion());
        asistencia.setMotivoJustificacion(dto.getMotivo());

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponseDto justificarAusente(Long idContrato, AsistenciaJustificarRequestDto dto) {
        Contrato contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + idContrato));

        if (!"ACTIVO".equals(contrato.getEstado())) {
            throw new IllegalStateException("El contrato no está activo");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dto.getFecha());
        } catch (Exception e) {
            throw new IllegalStateException("Fecha inválida");
        }

        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalStateException("No puedes justificar una fecha futura");
        }

        boolean tieneTurno = contrato.getContratoTurnos().stream()
                .anyMatch(ct -> aplicaTurnoDia(ct, fecha.getDayOfWeek()));
        if (!tieneTurno) {
            throw new IllegalStateException("El empleado no tiene turno el día seleccionado");
        }

        Optional<Asistencia> existente = asistenciaRepository.findByContratoIdAndFecha(idContrato, fecha);
        if (existente.isPresent()) {
            return justificarAsistencia(existente.get().getId(), dto);
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setContrato(contrato);
        asistencia.setFecha(fecha);
        asistencia.setEstado(ESTADO_JUSTIFICADO);
        asistencia.setTipoJustificacion(dto.getTipoJustificacion());
        asistencia.setMotivoJustificacion(dto.getMotivo());
        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponseDto crearAsistencia(AsistenciaCrearRequestDto dto) {
        if (dto.getIdContrato() == null) {
            throw new IllegalStateException("El contrato es obligatorio");
        }

        Contrato contrato = contratoRepository.findById(dto.getIdContrato())
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + dto.getIdContrato()));

        if (!"ACTIVO".equals(contrato.getEstado())) {
            throw new IllegalStateException("El contrato no está activo");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(dto.getFecha());
        } catch (Exception e) {
            throw new IllegalStateException("Fecha inválida");
        }

        Optional<Asistencia> existente = asistenciaRepository.findByContratoIdAndFecha(dto.getIdContrato(), fecha);
        if (existente.isPresent()) {
            AsistenciaEditDto editar = new AsistenciaEditDto();
            editar.setHoraEntrada(dto.getHoraEntrada());
            editar.setHoraSalida(dto.getHoraSalida());
            editar.setEstado(dto.getEstado());
            return editarAsistencia(existente.get().getId(), editar);
        }

        Asistencia asistencia = new Asistencia();
        asistencia.setContrato(contrato);
        asistencia.setFecha(fecha);

        if (dto.getHoraEntrada() != null && !dto.getHoraEntrada().isBlank()) {
            asistencia.setHoraEntrada(LocalTime.parse(dto.getHoraEntrada()));
        }
        if (dto.getHoraSalida() != null && !dto.getHoraSalida().isBlank()) {
            asistencia.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
        }

        String estado = dto.getEstado() != null && !dto.getEstado().isBlank() ? dto.getEstado() : ESTADO_FALTA;
        asistencia.setEstado(estado);

        if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() != null) {
            long segundos = Duration.between(asistencia.getHoraEntrada(), asistencia.getHoraSalida()).getSeconds();
            if (segundos > 0) {
                asistencia.setHorasTrabajadas(BigDecimal.valueOf(segundos)
                        .divide(BigDecimal.valueOf(3600), 2, java.math.RoundingMode.HALF_UP));
            }
        }

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional
    public AsistenciaResponseDto editarAsistencia(Long idAsistencia, AsistenciaEditDto dto) {
        Asistencia asistencia = asistenciaRepository.findById(idAsistencia)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada con id: " + idAsistencia));

        if (ESTADO_PERMISO.equals(asistencia.getEstado())) {
            throw new IllegalStateException("No puedes editar un día con permiso aprobado.");
        }

        if (ESTADO_PERMISO.equals(dto.getEstado())) {
            throw new IllegalStateException(
                    "El estado 'Permiso' solo se asigna desde una solicitud de permiso aprobada.");
        }

        if (dto.getEstado() != null) {
            asistencia.setEstado(dto.getEstado());
        }

        if (dto.getHoraEntrada() != null && !dto.getHoraEntrada().isBlank()) {
            asistencia.setHoraEntrada(LocalTime.parse(dto.getHoraEntrada()));
        } else {
            asistencia.setHoraEntrada(null);
        }

        if (dto.getHoraSalida() != null && !dto.getHoraSalida().isBlank()) {
            asistencia.setHoraSalida(LocalTime.parse(dto.getHoraSalida()));
        } else {
            asistencia.setHoraSalida(null);
        }

        if (asistencia.getHoraEntrada() != null && asistencia.getHoraSalida() != null) {
            long segundos = Duration.between(asistencia.getHoraEntrada(), asistencia.getHoraSalida()).getSeconds();
            if (segundos > 0) {
                asistencia.setHorasTrabajadas(BigDecimal.valueOf(segundos)
                        .divide(BigDecimal.valueOf(3600), 2, java.math.RoundingMode.HALF_UP));
            } else {
                asistencia.setHorasTrabajadas(null);
            }
        } else {
            asistencia.setHorasTrabajadas(null);
        }

        asistencia = asistenciaRepository.save(asistencia);
        return toResponseDto(asistencia);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdfIndividual(Long idAsistencia) {
        Asistencia asistencia = asistenciaRepository.findById(idAsistencia)
                .orElseThrow(() -> new EntityNotFoundException("Asistencia no encontrada con id: " + idAsistencia));
        AsistenciaResponseDto dto = toResponseDto(asistencia);
        enriquecerConTurno(dto);
        return ReporteAsistenciaUtil.generarPdfIndividual(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerCalendarioMensual(Long idContrato, int anio, int mes) {
        LocalDate inicioMes = LocalDate.of(anio, mes, 1);
        LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
        LocalDate hoy = LocalDate.now();

        List<Asistencia> asistencias = asistenciaRepository
                .findAsistenciasDelMesWithTurno(idContrato, inicioMes, finMes);

        Map<LocalDate, String> mapa = new HashMap<>();
        for (Asistencia a : asistencias) {
            mapa.put(a.getFecha(), a.getEstado() != null ? a.getEstado() : "AUSENTE");
        }

        List<ContratoTurno> turnos = contratoRepository.findWithTurnosById(idContrato)
                .map(Contrato::getContratoTurnos)
                .orElseGet(ArrayList::new);

        List<Map<String, Object>> calendario = new ArrayList<>();
        for (int i = 1; i <= inicioMes.lengthOfMonth(); i++) {
            LocalDate fecha = LocalDate.of(anio, mes, i);
            Map<String, Object> dia = new HashMap<>();
            dia.put("numero", i);
            dia.put("fecha", fecha.toString());
            boolean esFuturo = fecha.isAfter(hoy);
            boolean esHoySinRegistro = fecha.equals(hoy) && !mapa.containsKey(fecha);
            boolean diaLaborable = turnos.stream().anyMatch(ct -> aplicaTurnoDia(ct, fecha.getDayOfWeek()));

            String estado;
            if (esFuturo || esHoySinRegistro) {
                estado = "FUTURO";
            } else if (mapa.containsKey(fecha)) {
                estado = mapa.get(fecha);
            } else if (!diaLaborable) {
                estado = "DESCANSO";
            } else {
                estado = "AUSENTE";
            }
            dia.put("estado", estado);
            dia.put("esHoy", fecha.equals(hoy));
            calendario.add(dia);
        }

        return calendario;
    }

    private AsistenciaResponseDto toResponseDto(Asistencia asistencia) {
        Contrato contrato = asistencia.getContrato();

        String nombreEmpleado = contrato.getEmpleado().getPersona() != null
                ? String.format("%s %s %s",
                        contrato.getEmpleado().getPersona().getNombres(),
                        contrato.getEmpleado().getPersona().getApellidoPaterno(),
                        contrato.getEmpleado().getPersona().getApellidoMaterno())
                        .trim().replaceAll("\\s+", " ")
                : "";

        String estado = asistencia.getEstado() != null ? asistencia.getEstado() : ESTADO_FALTA;
        String cargo = contrato.getCargo() != null ? contrato.getCargo().getNombre() : null;

        return new AsistenciaResponseDto(
                asistencia.getId(),
                contrato.getId(),
                nombreEmpleado,
                asistencia.getFecha(),
                asistencia.getHoraEntrada(),
                asistencia.getHoraSalida(),
                estado,
                asistencia.getMinutosRetraso(),
                asistencia.getHorasTrabajadas(),
                cargo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AusenteDto> listarAusentes(int mes, int anio) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
        return asistenciaRepository.findAusentesDelMes(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AusenteDto> listarAusentesDelDia(LocalDate fecha) {
        return asistenciaRepository.findAusentesDelMes(fecha, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AusenteDetalleDto> listarAusentesDetalle(int mes, int anio) {
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());
        List<Contrato> contratos = contratoRepository.findAllWithControlAsistencia();
        List<AusenteDetalleDto> resultado = new ArrayList<>();

        for (Contrato contrato : contratos) {
            List<LocalDate> diasTrabajo = new ArrayList<>();
            for (int i = 1; i <= fin.lengthOfMonth(); i++) {
                LocalDate fecha = LocalDate.of(anio, mes, i);
                DayOfWeek dia = fecha.getDayOfWeek();
                boolean laborable = contrato.getContratoTurnos().stream()
                        .anyMatch(ct -> aplicaTurnoDia(ct, dia));
                if (laborable) {
                    diasTrabajo.add(fecha);
                }
            }

            List<LocalDate> diasMarcados = asistenciaRepository
                    .findAsistenciasDelMesWithTurno(contrato.getId(), inicio, fin)
                    .stream()
                    .map(a -> a.getFecha())
                    .collect(Collectors.toList());

            Set<LocalDate> marcadosSet = Set.copyOf(diasMarcados);

            List<String> diasAusentes = diasTrabajo.stream()
                    .filter(d -> !marcadosSet.contains(d))
                    .map(LocalDate::toString)
                    .collect(Collectors.toList());

            if (!diasAusentes.isEmpty()) {
                String nombre = String.format("%s %s %s",
                        contrato.getEmpleado().getPersona().getNombres(),
                        contrato.getEmpleado().getPersona().getApellidoPaterno(),
                        contrato.getEmpleado().getPersona().getApellidoMaterno()).trim().replaceAll("\\s+", " ");
                String telefono = contrato.getEmpleado().getPersona().getContactos().stream()
                        .findFirst()
                        .map(c -> c.getTelefono())
                        .orElse(null);
                resultado.add(new AusenteDetalleDto(
                        contrato.getEmpleado().getIdEmpleado(),
                        nombre,
                        contrato.getCargo().getNombre(),
                        telefono,
                        diasAusentes));
            }
        }
        return resultado;
    }
}
