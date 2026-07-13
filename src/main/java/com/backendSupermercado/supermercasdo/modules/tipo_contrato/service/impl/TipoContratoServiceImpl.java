package com.backendSupermercado.supermercasdo.modules.tipo_contrato.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoContrato;
import com.backendSupermercado.supermercasdo.modules.tipo_contrato.dto.TipoContratoDto;
import com.backendSupermercado.supermercasdo.modules.tipo_contrato.repository.TipoContratoRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_contrato.service.TipoContratoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoContratoServiceImpl implements TipoContratoService {

    private final TipoContratoRepository tipoContratoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoContratoDto> listarTodos(String busqueda, Boolean estado) {
        List<TipoContrato> lista;

        if (busqueda != null && !busqueda.isBlank() && estado != null) {
            lista = tipoContratoRepository.findByNombreContainingIgnoreCaseAndEstado(busqueda.trim(), estado);
        } else if (busqueda != null && !busqueda.isBlank()) {
            lista = tipoContratoRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        } else if (estado != null) {
            lista = tipoContratoRepository.findByEstado(estado);
        } else {
            lista = tipoContratoRepository.findAll();
        }

        return lista.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoContratoDto obtenerPorId(Long id) {
        return toDto(tipoContratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado")));
    }

    @Override
    @Transactional
    public TipoContratoDto crear(TipoContratoDto dto) {
        if (tipoContratoRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de contrato con ese nombre");
        }
        TipoContrato entity = new TipoContrato();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setEstado(true);
        return toDto(tipoContratoRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoContratoDto actualizar(Long id, TipoContratoDto dto) {
        TipoContrato entity = tipoContratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado"));
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        return toDto(tipoContratoRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoContratoDto activar(Long id) {
        TipoContrato entity = tipoContratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado"));
        entity.setEstado(true);
        return toDto(tipoContratoRepository.save(entity));
    }

    @Override
    @Transactional
    public TipoContratoDto desactivar(Long id) {
        TipoContrato entity = tipoContratoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de contrato no encontrado"));
        entity.setEstado(false);
        return toDto(tipoContratoRepository.save(entity));
    }

    private TipoContratoDto toDto(TipoContrato entity) {
        return new TipoContratoDto(entity.getId(), entity.getNombre(), entity.getDescripcion(), entity.getEstado());
    }
}
