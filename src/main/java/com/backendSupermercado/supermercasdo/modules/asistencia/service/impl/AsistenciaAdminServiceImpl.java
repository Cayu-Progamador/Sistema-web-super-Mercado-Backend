package com.backendSupermercado.supermercasdo.modules.asistencia.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaJustificarRequestDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
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

    private final AsistenciaRepository asistenciaRepository;
    private final ContratoRepository contratoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AsistenciaResponseDto> listarAsistencias(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, Pageable pageable) {
        LocalDate fechaDesde = filtros.getFechaDesde() != null ? LocalDate.parse(filtros.getFechaDesde()) : null;
        LocalDate fechaHasta = filtros.getFechaHasta() != null ? LocalDate.parse(filtros.getFechaHasta()) : null;

        String busquedaParam = (busqueda != null && !busqueda.isBlank()) ? "%" + busqueda.trim().toLowerCase() + "%" : null;

        Page<AsistenciaResponseDto> page = asistenciaRepository.buscarAsistenciasAdmin(
                fechaDesde, fechaHasta, filtros.getEstado(), filtros.getIdContrato(), idTurno, busquedaParam, pageable);

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
                default -> ausentes++;
            }
        }

        Map<String, Object> resumen = new HashMap<>();
        resumen.put("presentes", presentes);
        resumen.put("ausentes", ausentes);
        resumen.put("tardanzas", tardanzas);
        resumen.put("permisos", justificados);
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
                .filter(a -> "JUSTIFICADO".equals(a.getEstado()))
                .count();

        String nombre = contrato.getEmpleado().getPersona() != null
                ? String.format("%s %s %s",
                        contrato.getEmpleado().getPersona().getNombres(),
                        contrato.getEmpleado().getPersona().getApellidoPaterno(),
                        contrato.getEmpleado().getPersona().getApellidoMaterno())
                        .trim().replaceAll("\\s+", " ")
                : "";

        List<Map<String, Object>> dias = asistencias.stream().map(a -> {
            Map<String, Object> d = new HashMap<>();
            d.put("fecha", a.getFecha().toString());
            d.put("estado", a.getEstado() != null ? a.getEstado() : "AUSENTE");
            return d;
        }).collect(Collectors.toList());

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

        List<Map<String, Object>> calendario = new ArrayList<>();
        for (int i = 1; i <= inicioMes.lengthOfMonth(); i++) {
            LocalDate fecha = LocalDate.of(anio, mes, i);
            Map<String, Object> dia = new HashMap<>();
            dia.put("numero", i);
            dia.put("fecha", fecha.toString());
            boolean esFuturo = fecha.isAfter(hoy);
            boolean esHoySinRegistro = fecha.equals(hoy) && !mapa.containsKey(fecha);
            dia.put("estado", esFuturo || esHoySinRegistro ? "FUTURO" : mapa.getOrDefault(fecha, "AUSENTE"));
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
}
