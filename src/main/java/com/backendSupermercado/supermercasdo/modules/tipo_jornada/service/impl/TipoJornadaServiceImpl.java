package com.backendSupermercado.supermercasdo.modules.tipo_jornada.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoJornada;
import com.backendSupermercado.supermercasdo.modules.tipo_jornada.dto.TipoJornadaDto;
import com.backendSupermercado.supermercasdo.modules.tipo_jornada.repository.TipoJornadaRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_jornada.service.TipoJornadaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoJornadaServiceImpl implements TipoJornadaService {

    private final TipoJornadaRepository tipoJornadaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoJornadaDto> listarTodos(String busqueda, Boolean estado) {
        List<TipoJornada> lista;

        if (busqueda != null && !busqueda.isBlank() && estado != null) {
            lista = tipoJornadaRepository.findByNombreContainingIgnoreCaseAndEstado(busqueda.trim(), estado);
        } else if (busqueda != null && !busqueda.isBlank()) {
            lista = tipoJornadaRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        } else if (estado != null) {
            lista = tipoJornadaRepository.findByEstado(estado);
        } else {
            lista = tipoJornadaRepository.findAll();
        }

        return lista.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoJornadaDto obtenerPorId(Long id) {
        return toDto(tipoJornadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado")));
    }

    @Override
    @Transactional
    public TipoJornadaDto crear(TipoJornadaDto dto) {
        if (tipoJornadaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de jornada con ese nombre");
        }
        TipoJornada entity = new TipoJornada();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEstado(true);
        return toDto(tipoJornadaRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoJornadaDto actualizar(Long id, TipoJornadaDto dto) {
        TipoJornada entity = tipoJornadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado"));
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        return toDto(tipoJornadaRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoJornadaDto activar(Long id) {
        TipoJornada entity = tipoJornadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado"));
        entity.setEstado(true);
        return toDto(tipoJornadaRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoJornadaDto desactivar(Long id) {
        TipoJornada entity = tipoJornadaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de jornada no encontrado"));
        entity.setEstado(false);
        return toDto(tipoJornadaRepository.save(entity));
    }

    private TipoJornadaDto toDto(TipoJornada entity) {
        return new TipoJornadaDto(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getEstado());
    }
}
