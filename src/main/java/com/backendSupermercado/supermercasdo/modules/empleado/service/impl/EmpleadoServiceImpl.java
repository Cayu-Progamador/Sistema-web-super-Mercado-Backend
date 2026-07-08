package com.backendSupermercado.supermercasdo.modules.empleado.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;
import com.backendSupermercado.supermercasdo.shared.specification.EmpleadoSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
        "nombreCompleto", "persona.nombres",
        "cargo", "cargo.nombre",
        "estado", "estado"
    );
    
    @Override
    public List<EmpleadoSelectDto> listarParaSelect() {
        return empleadoRepository.listarParaSelect();
    }

    @Override
    public List<EmpleadoSelectDto> listarParaEditar(Long usuarioId) {
        return empleadoRepository.listarParaEditar(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpleadoListadoDto> listarEmpleados(String busqueda, Boolean estado, Pageable pageable) {
        var spec = Specification
                .where(EmpleadoSpecification.busquedaGeneral(busqueda))
                .and(EmpleadoSpecification.estadoEqual(estado));

        Pageable pageableConSort = mapearSort(pageable);

        return empleadoRepository.findAll(spec, pageableConSort)
                .map(this::toListadoDto);
    }

    private Pageable mapearSort(Pageable pageable) {
        if (!pageable.getSort().isSorted()) return pageable;

        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> {
                    String mappedField = SORT_FIELD_MAP.getOrDefault(order.getProperty(), order.getProperty());
                    return new Sort.Order(order.getDirection(), mappedField);
                })
                .toList();

        return org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );
    }

    private EmpleadoListadoDto toListadoDto(Empleado empleado) {
        String nombreCompleto = "";
        String cargo = "";
        String telefono = "";

        if (empleado.getPersona() != null) {
            var p = empleado.getPersona();
            nombreCompleto = (p.getNombres() != null ? p.getNombres() : "")
                    + " " + (p.getApellidoPaterno() != null ? p.getApellidoPaterno() : "")
                    + " " + (p.getApellidoMaterno() != null ? p.getApellidoMaterno() : "");
            nombreCompleto = nombreCompleto.trim().replaceAll("\\s+", " ");

            if (p.getContactos() != null && !p.getContactos().isEmpty()) {
                telefono = p.getContactos().stream()
                        .findFirst()
                        .map(Contacto::getTelefono)
                        .orElse("");
            }
        }

        if (empleado.getCargo() != null) {
            cargo = empleado.getCargo().getNombre();
        }

        return new EmpleadoListadoDto(
                empleado.getIdEmpleado(),
                nombreCompleto,
                cargo,
                telefono,
                empleado.getEstado()
        );
    }

}
