package com.backendSupermercado.supermercasdo.modules.unidad_medida.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.unidad_medida.TipoUnidadMedidaMapper;
import com.backendSupermercado.supermercasdo.mapper.unidad_medida.UnidadMedidaMapper;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.TipoUnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaRequestDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.dto.UnidadMedidaResponseDto;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.TipoUnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.entity.UnidadMedida;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.TipoUnidadMedidaRepository;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.repository.UnidadMedidaRepository;
import com.backendSupermercado.supermercasdo.modules.unidad_medida.service.UnidadMedidaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;
    private final TipoUnidadMedidaRepository tipoUnidadMedidaRepository;
    private final UnidadMedidaMapper unidadMedidaMapper;
    private final TipoUnidadMedidaMapper tipoUnidadMedidaMapper;

    @Override
    @Transactional
    public UnidadMedidaResponseDto crearUnidadMedida(UnidadMedidaRequestDto dto) {
        String nombre = dto.getNombre().trim();
        String abreviatura = dto.getAbreviatura().trim();

        if (unidadMedidaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una unidad de medida con ese nombre");
        }

        TipoUnidadMedida tipo = tipoUnidadMedidaRepository.findById(dto.getIdTipoUnidad())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de unidad no encontrado"));

        UnidadMedida unidadMedida = UnidadMedida.builder()
                .nombre(nombre)
                .abreviatura(abreviatura)
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .tipoUnidadMedida(tipo)
                .build();

        try {
            return unidadMedidaMapper.toResponseDto(unidadMedidaRepository.save(unidadMedida));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Ya existe una unidad de medida con ese nombre o abreviatura");
        }
    }

    @Override
    @Transactional
    public UnidadMedidaResponseDto actualizarUnidadMedida(Long idUnidadMedida, UnidadMedidaRequestDto dto) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(idUnidadMedida)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));

        String nombre = dto.getNombre().trim();

        if (unidadMedidaRepository.existsByNombreIgnoreCase(nombre)
                && !unidadMedida.getNombre().equalsIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una unidad de medida con ese nombre");
        }

        TipoUnidadMedida tipo = tipoUnidadMedidaRepository.findById(dto.getIdTipoUnidad())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de unidad no encontrado"));

        unidadMedida.setNombre(nombre);
        unidadMedida.setAbreviatura(dto.getAbreviatura().trim());
        unidadMedida.setTipoUnidadMedida(tipo);
        unidadMedida.setFechaActualizacion(LocalDateTime.now());
        try {
            return unidadMedidaMapper.toResponseDto(unidadMedidaRepository.save(unidadMedida));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Ya existe una unidad de medida con ese nombre o abreviatura");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedidaResponseDto obtenerDetalleUnidadMedida(Long idUnidadMedida) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(idUnidadMedida)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));

        return unidadMedidaMapper.toResponseDto(unidadMedida);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedidaResponseDto> listarUnidadesMedida(String busqueda) {
        List<UnidadMedida> unidades = (busqueda == null || busqueda.isBlank())
                ? unidadMedidaRepository.findAll()
                : unidadMedidaRepository.findAll().stream()
                        .filter(u -> u.getNombre().toLowerCase().contains(busqueda.trim().toLowerCase())
                                || u.getAbreviatura().toLowerCase().contains(busqueda.trim().toLowerCase()))
                        .toList();

        return unidades.stream()
                .map(unidadMedidaMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public UnidadMedidaResponseDto activarUnidadMedida(Long idUnidadMedida) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(idUnidadMedida)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));

        if (unidadMedida.getEstado()) {
            throw new ResourceConflictException("La unidad de medida ya está activada");
        }

        unidadMedida.setEstado(true);
        return unidadMedidaMapper.toResponseDto(unidadMedidaRepository.save(unidadMedida));
    }

    @Override
    @Transactional
    public UnidadMedidaResponseDto desactivarUnidadMedida(Long idUnidadMedida) {
        UnidadMedida unidadMedida = unidadMedidaRepository.findById(idUnidadMedida)
                .orElseThrow(() -> new EntityNotFoundException("Unidad de medida no encontrada"));

        if (!unidadMedida.getEstado()) {
            throw new ResourceConflictException("La unidad de medida ya está desactivada");
        }

        unidadMedida.setEstado(false);
        return unidadMedidaMapper.toResponseDto(unidadMedidaRepository.save(unidadMedida));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnidadMedidaResponseDto> listarUnidadesMedidaPaginado(String busqueda, Boolean estado, Long idTipoUnidad, Pageable pageable) {
        String patron = (busqueda == null || busqueda.isBlank())
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return unidadMedidaRepository.filtrar(patron, estado, idTipoUnidad, pageable)
                .map(unidadMedidaMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoUnidadMedidaResponseDto> listarTiposUnidadMedida() {
        return tipoUnidadMedidaRepository.findAll().stream()
                .map(tipoUnidadMedidaMapper::toResponseDto)
                .toList();
    }
}