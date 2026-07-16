package com.backendSupermercado.supermercasdo.modules.cargo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.cargo.dto.CargoDto;
import com.backendSupermercado.supermercasdo.modules.cargo.entity.Cargo;
import com.backendSupermercado.supermercasdo.modules.cargo.repository.CargoRepository;
import com.backendSupermercado.supermercasdo.modules.cargo.service.CargoService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CargoServiceImpl implements CargoService {

    private final CargoRepository cargoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CargoDto> listarTodos(String busqueda, Boolean estado) {
        List<Cargo> lista;

        if (busqueda != null && !busqueda.isBlank() && estado != null) {
            lista = cargoRepository.findByNombreContainingIgnoreCaseAndEstado(busqueda.trim(), estado);
        } else if (busqueda != null && !busqueda.isBlank()) {
            lista = cargoRepository.findByNombreContainingIgnoreCase(busqueda.trim());
        } else if (estado != null) {
            lista = cargoRepository.findByEstado(estado);
        } else {
            lista = cargoRepository.findAll();
        }

        return lista.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CargoDto obtenerPorId(Long id) {
        return toDto(cargoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado")));
    }

    @Override
    @Transactional
    public CargoDto crear(CargoDto dto) {
        if (cargoRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un cargo con ese nombre");
        }
        Cargo cargo = new Cargo();
        cargo.setNombre(dto.getNombre());
        cargo.setDescripcion(dto.getDescripcion());
        cargo.setEstado(true);
        return toDto(cargoRepository.save(cargo));
    }

    @Override
    @Transactional
    public CargoDto actualizar(Long id, CargoDto dto) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));
        if (cargoRepository.existsByNombreIgnoreCase(dto.getNombre()) && !cargo.getNombre().equalsIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un cargo con ese nombre");
        }
        cargo.setNombre(dto.getNombre());
        cargo.setDescripcion(dto.getDescripcion());
        return toDto(cargoRepository.save(cargo));
    }

    @Override
    @Transactional
    public CargoDto activar(Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));
        cargo.setEstado(true);
        return toDto(cargoRepository.save(cargo));
    }

    @Override
    @Transactional
    public CargoDto desactivar(Long id) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cargo no encontrado"));
        cargo.setEstado(false);
        return toDto(cargoRepository.save(cargo));
    }

    private CargoDto toDto(Cargo cargo) {
        return new CargoDto(cargo.getId(), cargo.getNombre(), cargo.getDescripcion(), cargo.getEstado());
    }
}
