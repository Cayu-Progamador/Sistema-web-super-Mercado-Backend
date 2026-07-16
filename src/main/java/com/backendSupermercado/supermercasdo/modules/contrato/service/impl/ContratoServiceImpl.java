package com.backendSupermercado.supermercasdo.modules.contrato.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.contrato.ContratoMapper;
import com.backendSupermercado.supermercasdo.modules.cargo.entity.Cargo;
import com.backendSupermercado.supermercasdo.modules.cargo.repository.CargoRepository;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetalleDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoEmpleadoResumenDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoListadoDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.RenovarContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoTurnoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.ContratoTurno;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoContrato;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoJornada;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoPago;
import com.backendSupermercado.supermercasdo.modules.contrato.entity.Turno;
import com.backendSupermercado.supermercasdo.modules.contrato.repository.ContratoRepository;
import com.backendSupermercado.supermercasdo.modules.contrato.service.ContratoPdfService;
import com.backendSupermercado.supermercasdo.modules.contrato.service.ContratoService;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Direccion;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_contrato.repository.TipoContratoRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_jornada.repository.TipoJornadaRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_pago.repository.TipoPagoRepository;
import com.backendSupermercado.supermercasdo.modules.turno.repository.TurnoRepository;
import com.backendSupermercado.supermercasdo.shared.specification.ContratoSpecification;
import com.backendSupermercado.supermercasdo.shared.util.ReporteContratoUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository contratoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CargoRepository cargoRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final TipoJornadaRepository tipoJornadaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final TurnoRepository turnoRepository;
    private final ContratoMapper contratoMapper;
    private final ContratoPdfService contratoPdfService;


    @Transactional
    public void actualizarVencidos() {
        List<Contrato> vencidos = contratoRepository.findAll(
                Specification.where(ContratoSpecification.estadoEqual("ACTIVO"))
                        .and(ContratoSpecification.fechaFinVencido())
        );
        for (Contrato c : vencidos) {
            c.setEstado("VENCIDO");
        }
        if (!vencidos.isEmpty()) {
            contratoRepository.saveAll(vencidos);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContratoListadoDto> listarContratos(
            String busqueda,
            String estado,
            Boolean controlaAsistencia,
            String tipoContrato,
            String tipoJornada,
            Long empleadoId,
            String fechaDesde,
            String fechaHasta,
            String fechaFinDesde,
            String fechaFinHasta,
            Pageable pageable) {

        actualizarVencidos();

        Specification<Contrato> spec = Specification
                .where(ContratoSpecification.busquedaGeneral(busqueda))
                .and(ContratoSpecification.estadoEqual(estado))
                .and(ContratoSpecification.controlaAsistenciaEqual(controlaAsistencia))
                .and(ContratoSpecification.tipoContratoNombreEqual(tipoContrato))
                .and(ContratoSpecification.tipoJornadaNombreEqual(tipoJornada))
                .and(ContratoSpecification.empleadoIdEqual(empleadoId))
                .and(ContratoSpecification.fechaInicioBetween(fechaDesde, fechaHasta))
                .and(ContratoSpecification.fechaFinBetween(fechaFinDesde, fechaFinHasta));

        return contratoRepository.findAll(spec, pageable)
                .map(this::toListadoDto);
    }

    @Override
    @Transactional
    public ContratoListadoDto crear(ContratoRequestDto dto) {
        validarCamposRequeridos(dto);

        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        if (Boolean.FALSE.equals(empleado.getEstado())) {
            throw new IllegalArgumentException("El empleado no está activo");
        }

        boolean tieneActivo = contratoRepository.findOne(
                ContratoSpecification.empleadoIdEqual(dto.getEmpleadoId())
                        .and(ContratoSpecification.estadoEqual("ACTIVO"))
        ).isPresent();

        if (tieneActivo) {
            throw new ResourceConflictException("El empleado ya tiene un contrato activo");
        }

        Cargo cargo = cargoRepository.findById(dto.getCargoId())
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado con id: " + dto.getCargoId()));

        TipoContrato tipoContrato = tipoContratoRepository.findById(dto.getTipoContratoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado con id: " + dto.getTipoContratoId()));

        TipoJornada tipoJornada = tipoJornadaRepository.findById(dto.getTipoJornadaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado con id: " + dto.getTipoJornadaId()));

        LocalDate fechaInicio = LocalDate.parse(dto.getFechaInicio());
        LocalDate fechaFin = null;
        if (dto.getFechaFin() != null && !dto.getFechaFin().isBlank()) {
            fechaFin = LocalDate.parse(dto.getFechaFin());
            if (!fechaFin.isAfter(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
            }
        }

        String estadoValido = dto.getEstado().toUpperCase();
        if (!List.of("ACTIVO", "SUSPENDIDO").contains(estadoValido)) {
            throw new IllegalArgumentException("Estado inválido: " + dto.getEstado() + ". Valores permitidos: ACTIVO, SUSPENDIDO");
        }

        Boolean controlaAsistencia = dto.getControlaAsistencia() != null ? dto.getControlaAsistencia() : true;

        if (Boolean.TRUE.equals(controlaAsistencia)) {
            if (dto.getHorasDia() == null || dto.getHorasDia() < 1) {
                throw new IllegalArgumentException("Si controla asistencia, las horas por día son obligatorias (mínimo 1)");
            }
            if (dto.getHorasSemana() == null || dto.getHorasSemana() < 1) {
                throw new IllegalArgumentException("Si controla asistencia, las horas por semana son obligatorias (mínimo 1)");
            }
        }

        validarSueldoBase(dto.getSueldoBase());

        TipoPago tipoPago = null;
        if (dto.getTipoPagoId() != null) {
            tipoPago = tipoPagoRepository.findById(dto.getTipoPagoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado con id: " + dto.getTipoPagoId()));
        }

        Contrato contrato = new Contrato();
        contrato.setEmpleado(empleado);
        contrato.setCargo(cargo);
        contrato.setTipoContrato(tipoContrato);
        contrato.setTipoJornada(tipoJornada);
        contrato.setFechaInicio(fechaInicio);
        contrato.setFechaFin(fechaFin);
        contrato.setSueldoBase(dto.getSueldoBase());
        contrato.setHorasDia(controlaAsistencia ? dto.getHorasDia() : null);
        contrato.setHorasSemana(controlaAsistencia ? dto.getHorasSemana() : null);
        contrato.setControlaAsistencia(controlaAsistencia);
        contrato.setToleranciaMinutos(dto.getToleranciaMinutos());
        contrato.setObservaciones(dto.getObservaciones());
        contrato.setTipoPago(tipoPago);
        contrato.setEstado(estadoValido);
        contrato.setContratoTurnos(new ArrayList<>());

        if (dto.getContratoTurno() != null) {
            ContratoTurnoRequestDto turnoDto = dto.getContratoTurno();
            Turno turno = turnoRepository.findById(turnoDto.getTurnoId())
                    .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado con id: " + turnoDto.getTurnoId()));

            ContratoTurno contratoTurno = new ContratoTurno();
            contratoTurno.setTurno(turno);
            contratoTurno.setContrato(contrato);
            contratoTurno.setLunes(turnoDto.getLunes() != null ? turnoDto.getLunes() : false);
            contratoTurno.setMartes(turnoDto.getMartes() != null ? turnoDto.getMartes() : false);
            contratoTurno.setMiercoles(turnoDto.getMiercoles() != null ? turnoDto.getMiercoles() : false);
            contratoTurno.setJueves(turnoDto.getJueves() != null ? turnoDto.getJueves() : false);
            contratoTurno.setViernes(turnoDto.getViernes() != null ? turnoDto.getViernes() : false);
            contratoTurno.setSabado(turnoDto.getSabado() != null ? turnoDto.getSabado() : false);
            contratoTurno.setDomingo(turnoDto.getDomingo() != null ? turnoDto.getDomingo() : false);
            contrato.getContratoTurnos().add(contratoTurno);
        }

        contrato = contratoRepository.save(contrato);
        return toListadoDto(contrato);
    }

    @Override
    @Transactional
    public ContratoListadoDto actualizar(Long id, ContratoRequestDto dto) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("FINALIZADO".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("No se puede editar un contrato finalizado");
        }

        validarCamposRequeridos(dto);

        Cargo cargo = cargoRepository.findById(dto.getCargoId())
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado con id: " + dto.getCargoId()));

        TipoContrato tipoContrato = tipoContratoRepository.findById(dto.getTipoContratoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado con id: " + dto.getTipoContratoId()));

        TipoJornada tipoJornada = tipoJornadaRepository.findById(dto.getTipoJornadaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado con id: " + dto.getTipoJornadaId()));

        LocalDate fechaInicio = LocalDate.parse(dto.getFechaInicio());
        LocalDate fechaFin = null;
        if (dto.getFechaFin() != null && !dto.getFechaFin().isBlank()) {
            fechaFin = LocalDate.parse(dto.getFechaFin());
            if (!fechaFin.isAfter(fechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
            }
        }

        validarSueldoBase(dto.getSueldoBase());

        contrato.setCargo(cargo);
        contrato.setTipoContrato(tipoContrato);
        contrato.setTipoJornada(tipoJornada);
        contrato.setFechaInicio(fechaInicio);
        contrato.setFechaFin(fechaFin);
        contrato.setSueldoBase(dto.getSueldoBase());
        contrato.setObservaciones(dto.getObservaciones());

        if ("ACTIVO".equals(contrato.getEstado())
                && contrato.getFechaFin() != null
                && contrato.getFechaFin().isBefore(LocalDate.now())) {
            contrato.setEstado("VENCIDO");
        }

        contrato = contratoRepository.save(contrato);
        return toListadoDto(contrato);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoListadoDto obtenerPorId(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("ACTIVO".equals(contrato.getEstado())
                && contrato.getFechaFin() != null
                && contrato.getFechaFin().isBefore(LocalDate.now())) {
            contrato.setEstado("VENCIDO");
            contratoRepository.save(contrato);
        }

        return toListadoDto(contrato);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratoDetalleDto obtenerDetalle(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("ACTIVO".equals(contrato.getEstado())
                && contrato.getFechaFin() != null
                && contrato.getFechaFin().isBefore(LocalDate.now())) {
            contrato.setEstado("VENCIDO");
            contratoRepository.save(contrato);
        }

        return toDetalleDto(contrato);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(Long id, String username) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        var pdfDto = contratoMapper.toPdfDto(contrato, username != null ? username : "Sistema");
        return contratoPdfService.generarPdf(pdfDto);
    }

    @Override
    @Transactional
    public ContratoListadoDto renovar(Long id, RenovarContratoRequestDto dto) {
        Contrato contratoActual = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        String estado = contratoActual.getEstado();
        if (!"VENCIDO".equals(estado) && !"FINALIZADO".equals(estado)) {
            throw new IllegalArgumentException("Solo se pueden renovar contratos en estado VENCIDO o FINALIZADO");
        }

        LocalDate nuevaFechaInicio;
        try {
            nuevaFechaInicio = LocalDate.parse(dto.getFechaInicio());
        } catch (Exception e) {
            throw new IllegalArgumentException("La fecha de inicio no tiene un formato v\u00e1lido (yyyy-MM-dd)");
        }

        LocalDate nuevaFechaFin = null;
        if (dto.getFechaFin() != null && !dto.getFechaFin().isBlank()) {
            try {
                nuevaFechaFin = LocalDate.parse(dto.getFechaFin());
            } catch (Exception e) {
                throw new IllegalArgumentException("La fecha de fin no tiene un formato v\u00e1lido (yyyy-MM-dd)");
            }
            if (!nuevaFechaFin.isAfter(nuevaFechaInicio)) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
            }
        }

        validarSueldoBase(dto.getSueldoBase());

        // Finalizar contrato actual
        contratoActual.setEstado("FINALIZADO");
        contratoActual.setMotivoFin("Renovado");
        contratoActual.setFechaFin(
                contratoActual.getFechaFin() != null && contratoActual.getFechaFin().isBefore(LocalDate.now())
                        ? contratoActual.getFechaFin()
                        : LocalDate.now());
        contratoRepository.save(contratoActual);

        // Crear nuevo contrato heredando datos del anterior
        Contrato nuevoContrato = new Contrato();
        nuevoContrato.setEmpleado(contratoActual.getEmpleado());
        nuevoContrato.setCargo(contratoActual.getCargo());
        nuevoContrato.setTipoContrato(contratoActual.getTipoContrato());
        nuevoContrato.setTipoJornada(contratoActual.getTipoJornada());
        nuevoContrato.setTipoPago(contratoActual.getTipoPago());
        nuevoContrato.setFechaInicio(nuevaFechaInicio);
        nuevoContrato.setFechaFin(nuevaFechaFin);
        nuevoContrato.setSueldoBase(dto.getSueldoBase());
        nuevoContrato.setControlaAsistencia(contratoActual.getControlaAsistencia());
        nuevoContrato.setHorasDia(contratoActual.getHorasDia());
        nuevoContrato.setHorasSemana(contratoActual.getHorasSemana());
        nuevoContrato.setToleranciaMinutos(contratoActual.getToleranciaMinutos());
        nuevoContrato.setObservaciones(dto.getObservaciones());
        nuevoContrato.setEstado("ACTIVO");
        nuevoContrato.setContratoRenovadoId(contratoActual.getId());
        nuevoContrato.setContratoTurnos(new ArrayList<>());

        if (contratoActual.getContratoTurnos() != null && !contratoActual.getContratoTurnos().isEmpty()) {
            for (ContratoTurno ct : contratoActual.getContratoTurnos()) {
                ContratoTurno nuevoCt = new ContratoTurno();
                nuevoCt.setTurno(ct.getTurno());
                nuevoCt.setContrato(nuevoContrato);
                nuevoCt.setLunes(ct.getLunes());
                nuevoCt.setMartes(ct.getMartes());
                nuevoCt.setMiercoles(ct.getMiercoles());
                nuevoCt.setJueves(ct.getJueves());
                nuevoCt.setViernes(ct.getViernes());
                nuevoCt.setSabado(ct.getSabado());
                nuevoCt.setDomingo(ct.getDomingo());
                nuevoContrato.getContratoTurnos().add(nuevoCt);
            }
        }

        nuevoContrato = contratoRepository.save(nuevoContrato);
        return toListadoDto(nuevoContrato);
    }

    @Override
    @Transactional
    public ContratoListadoDto activar(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("ACTIVO".equals(contrato.getEstado())) {
            throw new ResourceConflictException("El contrato ya está activo");
        }
        if ("FINALIZADO".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("No se puede reactivar un contrato finalizado");
        }

        contrato.setEstado("ACTIVO");
        contrato = contratoRepository.save(contrato);
        return toListadoDto(contrato);
    }

    @Override
    @Transactional
    public ContratoListadoDto desactivar(Long id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("SUSPENDIDO".equals(contrato.getEstado())) {
            throw new ResourceConflictException("El contrato ya está suspendido");
        }
        if ("FINALIZADO".equals(contrato.getEstado()) || "VENCIDO".equals(contrato.getEstado())) {
            throw new IllegalArgumentException("No se puede suspender un contrato " + contrato.getEstado().toLowerCase());
        }

        contrato.setEstado("SUSPENDIDO");
        contrato = contratoRepository.save(contrato);
        return toListadoDto(contrato);
    }

    @Override
    @Transactional
    public ContratoListadoDto finalizar(Long id, String motivoFin) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con id: " + id));

        if ("FINALIZADO".equals(contrato.getEstado())) {
            throw new ResourceConflictException("El contrato ya está finalizado");
        }

        contrato.setEstado("FINALIZADO");
        contrato.setMotivoFin(motivoFin != null && !motivoFin.isBlank() ? motivoFin.trim() : "Finalizado manualmente");

        if (contrato.getFechaFin() == null) {
            contrato.setFechaFin(LocalDate.now());
        }

        contrato = contratoRepository.save(contrato);
        return toListadoDto(contrato);
    }

    private void validarCamposRequeridos(ContratoRequestDto dto) {
        if (dto.getEmpleadoId() == null) {
            throw new IllegalArgumentException("El empleado es obligatorio");
        }
        if (dto.getCargoId() == null) {
            throw new IllegalArgumentException("El cargo es obligatorio");
        }
        if (dto.getTipoContratoId() == null) {
            throw new IllegalArgumentException("El tipo de contrato es obligatorio");
        }
        if (dto.getTipoJornadaId() == null) {
            throw new IllegalArgumentException("El tipo de jornada es obligatorio");
        }
        if (dto.getFechaInicio() == null || dto.getFechaInicio().isBlank()) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        try {
            LocalDate.parse(dto.getFechaInicio());
        } catch (Exception e) {
            throw new IllegalArgumentException("La fecha de inicio no tiene un formato válido (yyyy-MM-dd)");
        }
        if (dto.getFechaFin() != null && !dto.getFechaFin().isBlank()) {
            try {
                LocalDate.parse(dto.getFechaFin());
            } catch (Exception e) {
                throw new IllegalArgumentException("La fecha de fin no tiene un formato válido (yyyy-MM-dd)");
            }
        }
        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
    }

    private void validarSueldoBase(BigDecimal sueldoBase) {
        if (sueldoBase == null) {
            throw new IllegalArgumentException("El sueldo base es obligatorio");
        }
        if (sueldoBase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El sueldo base debe ser mayor a 0");
        }
        if (sueldoBase.scale() > 2) {
            throw new IllegalArgumentException("El sueldo base no puede tener más de 2 decimales");
        }
        if (sueldoBase.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException("El sueldo base excede el límite permitido");
        }
    }

    private ContratoListadoDto toListadoDto(Contrato contrato) {
        ContratoListadoDto dto = new ContratoListadoDto();
        dto.setId(contrato.getId());
        dto.setEmpleado(toEmpleadoResumen(contrato));
        dto.setCargoNombre(contrato.getCargo() != null ? contrato.getCargo().getNombre() : null);
        dto.setCargoId(contrato.getCargo() != null ? contrato.getCargo().getId() : null);
        dto.setTipoContratoNombre(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getNombre() : null);
        dto.setTipoContratoId(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getId() : null);
        dto.setTipoJornadaNombre(contrato.getTipoJornada() != null ? contrato.getTipoJornada().getNombre() : null);
        dto.setTipoJornadaId(contrato.getTipoJornada() != null ? contrato.getTipoJornada().getId() : null);
        dto.setSueldoBase(contrato.getSueldoBase());
        dto.setHorasDia(contrato.getHorasDia());
        dto.setHorasSemana(contrato.getHorasSemana());
        dto.setControlaAsistencia(contrato.getControlaAsistencia());
        dto.setToleranciaMinutos(contrato.getToleranciaMinutos());
        dto.setEstado(contrato.getEstado());
        dto.setFechaInicio(contrato.getFechaInicio());
        dto.setFechaFin(contrato.getFechaFin());
        dto.setObservaciones(contrato.getObservaciones());
        dto.setTipoPagoId(contrato.getTipoPago() != null ? contrato.getTipoPago().getId() : null);
        return dto;
    }

    private ContratoDetalleDto toDetalleDto(Contrato contrato) {
        ContratoDetalleDto dto = new ContratoDetalleDto();
        dto.setId(contrato.getId());
        dto.setNroContrato("CTR-" + String.format("%04d", contrato.getId()));
        dto.setEmpleado(toEmpleadoResumen(contrato));
        dto.setCargoNombre(contrato.getCargo() != null ? contrato.getCargo().getNombre() : null);
        dto.setTipoContratoNombre(contrato.getTipoContrato() != null ? contrato.getTipoContrato().getNombre() : null);
        dto.setTipoJornadaNombre(contrato.getTipoJornada() != null ? contrato.getTipoJornada().getNombre() : null);
        dto.setEstado(contrato.getEstado());
        dto.setSueldoBase(contrato.getSueldoBase());
        dto.setFechaInicio(contrato.getFechaInicio());
        dto.setFechaFin(contrato.getFechaFin());
        dto.setMotivoFin(contrato.getMotivoFin());
        dto.setObservaciones(contrato.getObservaciones());

        dto.setControlaAsistencia(contrato.getControlaAsistencia());
        dto.setHorasDia(contrato.getHorasDia());
        dto.setHorasSemana(contrato.getHorasSemana());
        dto.setToleranciaMinutos(contrato.getToleranciaMinutos());

        if (contrato.getTipoPago() != null) {
            dto.setTipoPagoNombre(contrato.getTipoPago().getNombre());
        }

        if (contrato.getContratoTurnos() != null && !contrato.getContratoTurnos().isEmpty()) {
            ContratoTurno ct = contrato.getContratoTurnos().get(0);
            if (ct.getTurno() != null) {
                dto.setTurnoNombre(ct.getTurno().getNombre());
                dto.setHoraEntrada(ct.getTurno().getHoraEntrada() != null
                        ? ct.getTurno().getHoraEntrada().toString() : null);
                dto.setHoraSalida(ct.getTurno().getHoraSalida() != null
                        ? ct.getTurno().getHoraSalida().toString() : null);
            }
            dto.setLunes(ct.getLunes());
            dto.setMartes(ct.getMartes());
            dto.setMiercoles(ct.getMiercoles());
            dto.setJueves(ct.getJueves());
            dto.setViernes(ct.getViernes());
            dto.setSabado(ct.getSabado());
            dto.setDomingo(ct.getDomingo());
        }

        return dto;
    }

    private ContratoEmpleadoResumenDto toEmpleadoResumen(Contrato contrato) {
        if (contrato.getEmpleado() == null) return null;

        var empleado = contrato.getEmpleado();
        ContratoEmpleadoResumenDto dto = new ContratoEmpleadoResumenDto();
        dto.setId(empleado.getIdEmpleado());

        Persona persona = empleado.getPersona();
        if (persona != null) {
            dto.setNombres(persona.getNombres());
            String apellidos = (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                    + (persona.getApellidoMaterno() != null ? " " + persona.getApellidoMaterno() : "");
            dto.setApellidos(apellidos.isBlank() ? null : apellidos.trim());
            dto.setCedula(persona.getCi());

            if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                Optional<Contacto> contacto = persona.getContactos().stream().findFirst();
                contacto.ifPresent(c -> {
                    dto.setEmail(c.getCorreo());
                    dto.setTelefono(c.getTelefono());
                });
            }

            Direccion direccion = persona.getDireccion();
            if (direccion != null) {
                StringBuilder dir = new StringBuilder();
                if (direccion.getCalle() != null) dir.append(direccion.getCalle());
                if (direccion.getNumero() != null) {
                    dir.append(dir.length() > 0 ? " #" : "#").append(direccion.getNumero());
                }
                if (direccion.getZona() != null) {
                    dir.append(dir.length() > 0 ? ", " : "").append(direccion.getZona());
                }
                dto.setDireccion(dir.length() > 0 ? dir.toString() : null);
            }
        }

        return dto;
    }

    @Override
    public ContratoDashboardDto obtenerDashboard() {
        actualizarVencidos();
        List<Contrato> todos = contratoRepository.findAll();
        long total = todos.size();
        long activos = todos.stream().filter(c -> "ACTIVO".equals(c.getEstado())).count();
        long vencidos = todos.stream().filter(c -> "VENCIDO".equals(c.getEstado())).count();
        long suspendidos = todos.stream().filter(c -> "SUSPENDIDO".equals(c.getEstado())).count();
        long finalizados = todos.stream().filter(c -> "FINALIZADO".equals(c.getEstado())).count();
        long proximosAVencer = todos.stream()
                .filter(c -> "ACTIVO".equals(c.getEstado()) && c.getFechaFin() != null)
                .filter(c -> !c.getFechaFin().isBefore(LocalDate.now()) && !c.getFechaFin().isAfter(LocalDate.now().plusDays(30)))
                .count();
        return new ContratoDashboardDto(total, activos, proximosAVencer, vencidos, suspendidos, finalizados);
    }

    @Override
    public List<ContratoListadoDto> exportarContratos(ContratoFiltrosDto filtros) {
        actualizarVencidos();
        Specification<Contrato> spec = Specification
                .where(ContratoSpecification.busquedaGeneral(filtros.getBusqueda()))
                .and(ContratoSpecification.estadoEqual(filtros.getEstado()))
                .and(ContratoSpecification.controlaAsistenciaEqual(filtros.getControlaAsistencia()))
                .and(ContratoSpecification.tipoContratoNombreEqual(filtros.getTipoContrato()))
                .and(ContratoSpecification.tipoJornadaNombreEqual(filtros.getTipoJornada()))
                .and(ContratoSpecification.empleadoIdEqual(filtros.getEmpleadoId()))
                .and(ContratoSpecification.fechaInicioBetween(filtros.getFechaDesde(), filtros.getFechaHasta()))
                .and(ContratoSpecification.fechaFinBetween(filtros.getFechaFinDesde(), filtros.getFechaFinHasta()));
        return contratoRepository.findAll(spec).stream()
                .map(this::toListadoDto)
                .toList();
    }

    @Override
    public byte[] exportarContratosPDF(ContratoFiltrosDto filtros, String username) {
        List<ContratoListadoDto> datos = exportarContratos(filtros);
        return ReporteContratoUtil.generarPdf(datos, username);
    }

    @Override
    public byte[] exportarContratosExcel(ContratoFiltrosDto filtros) {
        List<ContratoListadoDto> datos = exportarContratos(filtros);
        return ReporteContratoUtil.generarExcel(datos);
    }
}
