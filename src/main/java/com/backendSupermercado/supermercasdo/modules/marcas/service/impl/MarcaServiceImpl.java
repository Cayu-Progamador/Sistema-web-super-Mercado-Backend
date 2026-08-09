package com.backendSupermercado.supermercasdo.modules.marcas.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.marcas.MarcaMapper;
import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaRequestDto;
import com.backendSupermercado.supermercasdo.modules.marcas.dto.MarcaResponseDto;
import com.backendSupermercado.supermercasdo.modules.marcas.entity.Marca;
import com.backendSupermercado.supermercasdo.modules.marcas.repository.MarcaRepository;
import com.backendSupermercado.supermercasdo.modules.marcas.service.MarcaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper marcaMapper;

    @Override
    @Transactional
    public MarcaResponseDto crearMarca(MarcaRequestDto dto) {
        String nombre = dto.getNombre().trim();

        if (marcaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una marca con ese nombre");
        }

        Marca marca = Marca.builder()
                .nombre(nombre)
                .descripcion(dto.getDescripcion() == null ? null : dto.getDescripcion().trim())
                .estado(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        try {
            return marcaMapper.toResponseDto(marcaRepository.save(marca));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Ya existe una marca con ese nombre");
        }
    }

    @Override
    @Transactional
    public MarcaResponseDto actualizarMarca(Long idMarca, MarcaRequestDto dto) {
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));

        String nombre = dto.getNombre().trim();

        if (marcaRepository.existsByNombreIgnoreCase(nombre)
                && !marca.getNombre().equalsIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe una marca con ese nombre");
        }

        marca.setNombre(nombre);
        marca.setDescripcion(dto.getDescripcion() == null ? null : dto.getDescripcion().trim());
        marca.setFechaActualizacion(LocalDateTime.now());

        try {
            return marcaMapper.toResponseDto(marcaRepository.save(marca));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Ya existe una marca con ese nombre");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MarcaResponseDto obtenerDetalleMarca(Long idMarca) {
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));

        return marcaMapper.toResponseDto(marca);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MarcaResponseDto> listarMarcasPaginado(String busqueda, Boolean estado, Pageable pageable) {
        String patron = (busqueda == null || busqueda.isBlank())
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";
        return marcaRepository.filtrar(patron, estado, pageable)
                .map(marcaMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarcaResponseDto> listarMarcas(String busqueda) {
        List<Marca> marcas = (busqueda == null || busqueda.isBlank())
                ? marcaRepository.findAll()
                : marcaRepository.findAll().stream()
                        .filter(m -> m.getNombre().toLowerCase().contains(busqueda.trim().toLowerCase())
                                || (m.getDescripcion() != null
                                        && m.getDescripcion().toLowerCase().contains(busqueda.trim().toLowerCase())))
                        .toList();

        return marcas.stream()
                .map(marcaMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public MarcaResponseDto activarMarca(Long idMarca) {
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));

        if (marca.getEstado()) {
            throw new ResourceConflictException("La marca ya está activada");
        }

        marca.setEstado(true);
        return marcaMapper.toResponseDto(marcaRepository.save(marca));
    }

    @Override
    @Transactional
    public MarcaResponseDto desactivarMarca(Long idMarca) {
        Marca marca = marcaRepository.findById(idMarca)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada"));

        if (!marca.getEstado()) {
            throw new ResourceConflictException("La marca ya está desactivada");
        }

        marca.setEstado(false);
        return marcaMapper.toResponseDto(marcaRepository.save(marca));
    }
}