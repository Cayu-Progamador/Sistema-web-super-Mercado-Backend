package com.backendSupermercado.supermercasdo.modules.tipo_pago.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.TipoPago;
import com.backendSupermercado.supermercasdo.modules.tipo_pago.dto.TipoPagoDto;
import com.backendSupermercado.supermercasdo.modules.tipo_pago.repository.TipoPagoRepository;
import com.backendSupermercado.supermercasdo.modules.tipo_pago.service.TipoPagoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoPagoServiceImpl implements TipoPagoService {

    private final TipoPagoRepository tipoPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoPagoDto> listarTodos(String busqueda, Boolean estado) {
        List<TipoPago> lista;

        if (busqueda != null && !busqueda.isBlank() && estado != null) {
            lista = tipoPagoRepository.findByNombreContainingIgnoreCaseAndEstado(busqueda.trim(), estado);
        } else if (busqueda != null && !busqueda.isBlank()) {
            lista = tipoPagoRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        } else if (estado != null) {
            lista = tipoPagoRepository.findByEstado(estado);
        } else {
            lista = tipoPagoRepository.findAll();
        }

        return lista.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TipoPagoDto obtenerPorId(Long id) {
        return toDto(tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado")));
    }

    @Override
    @Transactional
    public TipoPagoDto crear(TipoPagoDto dto) {
        if (tipoPagoRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de pago con ese nombre");
        }
        TipoPago tipoPago = new TipoPago();
        tipoPago.setNombre(dto.getNombre());
        tipoPago.setDescripcion(dto.getDescripcion());
        tipoPago.setEstado(true);
        return toDto(tipoPagoRepository.save(tipoPago));
    }

    @Override
    @Transactional
    public TipoPagoDto actualizar(Long id, TipoPagoDto dto) {
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));
        tipoPago.setNombre(dto.getNombre());
        tipoPago.setDescripcion(dto.getDescripcion());
        return toDto(tipoPagoRepository.save(tipoPago));
    }

    @Override
    @Transactional
    public TipoPagoDto activar(Long id) {
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));
        tipoPago.setEstado(true);
        return toDto(tipoPagoRepository.save(tipoPago));
    }

    @Override
    @Transactional
    public TipoPagoDto desactivar(Long id) {
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));
        tipoPago.setEstado(false);
        return toDto(tipoPagoRepository.save(tipoPago));
    }

    private TipoPagoDto toDto(TipoPago tipoPago) {
        return new TipoPagoDto(tipoPago.getId(), tipoPago.getNombre(), tipoPago.getDescripcion(), tipoPago.getEstado());
    }
}
