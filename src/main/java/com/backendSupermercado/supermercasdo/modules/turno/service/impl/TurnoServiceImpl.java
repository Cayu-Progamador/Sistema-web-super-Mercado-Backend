package com.backendSupermercado.supermercasdo.modules.turno.service.impl;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Turno;
import com.backendSupermercado.supermercasdo.modules.turno.dto.TurnoDto;
import com.backendSupermercado.supermercasdo.modules.turno.repository.TurnoRepository;
import com.backendSupermercado.supermercasdo.modules.turno.service.TurnoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurnoServiceImpl implements TurnoService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final TurnoRepository turnoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TurnoDto> listarTodos(String busqueda, Boolean estado) {
        List<Turno> lista;

        if (busqueda != null && !busqueda.isBlank() && estado != null) {
            lista = turnoRepository.findByNombreContainingIgnoreCaseAndEstado(busqueda.trim(), estado);
        } else if (busqueda != null && !busqueda.isBlank()) {
            lista = turnoRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        } else if (estado != null) {
            lista = turnoRepository.findByEstado(estado);
        } else {
            lista = turnoRepository.findAll();
        }

        return lista.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TurnoDto obtenerPorId(Long id) {
        return toDto(turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado")));
    }

    @Override
    @Transactional
    public TurnoDto crear(TurnoDto dto) {
        if (turnoRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un turno con ese nombre");
        }
        Turno entity = new Turno();
        entity.setNombre(dto.getNombre());
        entity.setHoraEntrada(LocalTime.parse(dto.getHoraEntrada(), TIME_FORMATTER));
        entity.setHoraSalida(LocalTime.parse(dto.getHoraSalida(), TIME_FORMATTER));
        entity.setEstado(true);
        return toDto(turnoRepository.save(entity));
    }

    @Override
    @Transactional
    public TurnoDto actualizar(Long id, TurnoDto dto) {
        Turno entity = turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        entity.setNombre(dto.getNombre());
        if (dto.getHoraEntrada() != null) {
            entity.setHoraEntrada(LocalTime.parse(dto.getHoraEntrada(), TIME_FORMATTER));
        }
        if (dto.getHoraSalida() != null) {
            entity.setHoraSalida(LocalTime.parse(dto.getHoraSalida(), TIME_FORMATTER));
        }
        return toDto(turnoRepository.save(entity));
    }

    @Override
    @Transactional
    public TurnoDto activar(Long id) {
        Turno entity = turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        entity.setEstado(true);
        return toDto(turnoRepository.save(entity));
    }

    @Override
    @Transactional
    public TurnoDto desactivar(Long id) {
        Turno entity = turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado"));
        entity.setEstado(false);
        return toDto(turnoRepository.save(entity));
    }

    private TurnoDto toDto(Turno entity) {
        return new TurnoDto(
                entity.getId(),
                entity.getNombre(),
                entity.getHoraEntrada() != null ? entity.getHoraEntrada().format(TIME_FORMATTER) : null,
                entity.getHoraSalida() != null ? entity.getHoraSalida().format(TIME_FORMATTER) : null,
                entity.getEstado()
        );
    }
}
