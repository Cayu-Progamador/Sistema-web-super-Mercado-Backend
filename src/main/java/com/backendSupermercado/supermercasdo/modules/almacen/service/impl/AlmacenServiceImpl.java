package com.backendSupermercado.supermercasdo.modules.almacen.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenListadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenCreadoDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.CrearAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ActualizarAlmacenRequestDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.ResponsableAlmacenDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.AlmacenMetricasDto;
import com.backendSupermercado.supermercasdo.modules.almacen.dto.TipoAlmacenDto;
import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.almacen.entity.TipoAlmacen;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.AlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.TipoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.almacen.service.AlmacenService;
import com.backendSupermercado.supermercasdo.modules.almacen.specification.AlmacenSpecification;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.almacen.service.AlmacenInventarioPdfService;
import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlmacenServiceImpl implements AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final TipoAlmacenRepository tipoAlmacenRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ExistenciaRepository existenciaRepository;
    private final AlmacenInventarioPdfService almacenInventarioPdfService;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarInventarioPdf(Long idAlmacen, String generadoPor) {
        Almacen almacen = almacenRepository.findById(idAlmacen)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
        var existencias = existenciaRepository.listarInventarioParaReporte(idAlmacen);
        return almacenInventarioPdfService.generar(almacen, existencias, generadoPor);
    }

    @Override
    @Transactional
    public AlmacenCreadoDto crear(CrearAlmacenRequestDto dto) {
        String nombre = dto.getNombre().trim();
        if (almacenRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResourceConflictException("Ya existe un almacén con ese nombre");
        }

        TipoAlmacen tipo = tipoAlmacenRepository.findById(dto.getIdTipoAlmacen())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de almacén no encontrado"));
        if (!Boolean.TRUE.equals(tipo.getActivo())) {
            throw new ResourceConflictException("El tipo de almacén seleccionado está inactivo");
        }

        Empleado responsable = obtenerResponsableActivo(dto.getIdResponsable());
        Almacen almacen = Almacen.builder()
                .tipoAlmacen(tipo)
                .codigo("TMP-" + UUID.randomUUID().toString().substring(0, 16))
                .nombre(nombre)
                .descripcion(limpiar(dto.getDescripcion()))
                .direccion(limpiar(dto.getDireccion()))
                .responsable(responsable)
                .activo(true)
                .build();

        almacenRepository.saveAndFlush(almacen);
        almacen.setCodigo("ALM-" + String.format("%03d", almacen.getIdAlmacen()));
        return convertirCreado(almacenRepository.save(almacen));
    }

    @Override
    @Transactional
    public AlmacenCreadoDto actualizar(Long idAlmacen, ActualizarAlmacenRequestDto dto) {
        Almacen almacen = buscarAlmacen(idAlmacen);
        String nombre = dto.getNombre().trim();
        if (almacenRepository.existsByNombreIgnoreCaseAndIdAlmacenNot(nombre, idAlmacen)) {
            throw new ResourceConflictException("Ya existe otro almacén con ese nombre");
        }

        TipoAlmacen tipo = obtenerTipoActivo(dto.getIdTipoAlmacen());
        Empleado responsable = obtenerResponsableActivo(dto.getIdResponsable());
        almacen.setNombre(nombre);
        almacen.setTipoAlmacen(tipo);
        almacen.setDescripcion(limpiar(dto.getDescripcion()));
        almacen.setDireccion(limpiar(dto.getDireccion()));
        almacen.setResponsable(responsable);
        return convertirCreado(almacenRepository.save(almacen));
    }

    @Override
    @Transactional
    public AlmacenCreadoDto activar(Long idAlmacen) {
        Almacen almacen = buscarAlmacen(idAlmacen);
        if (Boolean.TRUE.equals(almacen.getActivo())) {
            throw new ResourceConflictException("El almacén ya está activo");
        }
        if (!Boolean.TRUE.equals(almacen.getTipoAlmacen().getActivo())) {
            throw new ResourceConflictException("No se puede activar porque su tipo de almacén está inactivo");
        }
        if (almacen.getResponsable() != null) obtenerResponsableActivo(almacen.getResponsable().getIdEmpleado());
        almacen.setActivo(true);
        return convertirCreado(almacenRepository.save(almacen));
    }

    @Override
    @Transactional
    public AlmacenCreadoDto desactivar(Long idAlmacen) {
        Almacen almacen = buscarAlmacen(idAlmacen);
        if (!Boolean.TRUE.equals(almacen.getActivo())) {
            throw new ResourceConflictException("El almacén ya está inactivo");
        }
        if (existenciaRepository.existsByUbicacion_Almacen_IdAlmacenAndCantidadGreaterThan(
                idAlmacen, BigDecimal.ZERO)) {
            throw new ResourceConflictException(
                    "No se puede desactivar el almacén porque todavía contiene productos");
        }
        almacen.setActivo(false);
        return convertirCreado(almacenRepository.save(almacen));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoAlmacenDto> listarTiposActivos() {
        return tipoAlmacenRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(tipo -> TipoAlmacenDto.builder()
                        .idTipoAlmacen(tipo.getIdTipoAlmacen())
                        .nombre(tipo.getNombre())
                        .descripcion(tipo.getDescripcion())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponsableAlmacenDto> listarResponsablesActivos() {
        return empleadoRepository.findByEstadoTrueOrderByPersonaNombresAsc().stream()
                .filter(empleado -> empleado.getPersona() != null
                        && Boolean.TRUE.equals(empleado.getPersona().getEstado()))
                .map(empleado -> ResponsableAlmacenDto.builder()
                        .idEmpleado(empleado.getIdEmpleado())
                        .nombreCompleto(nombreCompleto(empleado))
                        .telefono(telefono(empleado))
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlmacenListadoDto> listar(String search, String tipo, Boolean estado, Pageable pageable) {
        Specification<Almacen> specification = Specification
                .where(AlmacenSpecification.buscar(search))
                .and(AlmacenSpecification.porTipo(tipo))
                .and(AlmacenSpecification.porEstado(estado));

        Page<Almacen> pagina = almacenRepository.findAll(specification, pageable);
        if (pagina.isEmpty()) return pagina.map(almacen -> convertir(almacen, null));

        var ids = pagina.getContent().stream().map(Almacen::getIdAlmacen).toList();
        Map<Long, AlmacenMetricasDto> metricas = almacenRepository.obtenerMetricasPorIds(ids).stream()
                .collect(Collectors.toMap(AlmacenMetricasDto::getIdAlmacen, Function.identity()));
        return pagina.map(almacen -> convertir(almacen, metricas.get(almacen.getIdAlmacen())));
    }

    private AlmacenListadoDto convertir(Almacen almacen, AlmacenMetricasDto metricas) {
        Empleado responsable = almacen.getResponsable();
        return AlmacenListadoDto.builder()
                .idAlmacen(almacen.getIdAlmacen())
                .codigo(almacen.getCodigo())
                .nombre(almacen.getNombre())
                .descripcion(almacen.getDescripcion())
                .direccion(almacen.getDireccion())
                .idTipoAlmacen(almacen.getTipoAlmacen().getIdTipoAlmacen())
                .tipo(almacen.getTipoAlmacen().getNombre())
                .activo(almacen.getActivo())
                .idResponsable(responsable != null ? responsable.getIdEmpleado() : null)
                .responsable(nombreCompleto(responsable))
                .telefonoResponsable(telefono(responsable))
                .totalUbicaciones(metricas != null ? metricas.getTotalUbicaciones() : 0L)
                .totalProductos(metricas != null ? metricas.getTotalProductos() : 0L)
                .stockTotal(metricas != null ? metricas.getStockTotal() : BigDecimal.ZERO)
                .build();
    }

    private String nombreCompleto(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) return null;
        Persona persona = empleado.getPersona();
        return java.util.stream.Stream.of(
                        persona.getNombres(), persona.getApellidoPaterno(), persona.getApellidoMaterno())
                .filter(valor -> valor != null && !valor.isBlank())
                .collect(Collectors.joining(" "));
    }

    private Empleado obtenerResponsableActivo(Long idResponsable) {
        if (idResponsable == null) return null;
        Empleado empleado = empleadoRepository.findById(idResponsable)
                .orElseThrow(() -> new EntityNotFoundException("Empleado responsable no encontrado"));
        if (!Boolean.TRUE.equals(empleado.getEstado())
                || empleado.getPersona() == null
                || !Boolean.TRUE.equals(empleado.getPersona().getEstado())) {
            throw new ResourceConflictException("El empleado responsable seleccionado está inactivo");
        }
        return empleado;
    }

    private Almacen buscarAlmacen(Long idAlmacen) {
        return almacenRepository.findById(idAlmacen)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado"));
    }

    private TipoAlmacen obtenerTipoActivo(Long idTipoAlmacen) {
        TipoAlmacen tipo = tipoAlmacenRepository.findById(idTipoAlmacen)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de almacén no encontrado"));
        if (!Boolean.TRUE.equals(tipo.getActivo())) {
            throw new ResourceConflictException("El tipo de almacén seleccionado está inactivo");
        }
        return tipo;
    }

    private AlmacenCreadoDto convertirCreado(Almacen almacen) {
        Empleado responsable = almacen.getResponsable();
        return AlmacenCreadoDto.builder()
                .idAlmacen(almacen.getIdAlmacen())
                .codigo(almacen.getCodigo())
                .nombre(almacen.getNombre())
                .descripcion(almacen.getDescripcion())
                .direccion(almacen.getDireccion())
                .idTipoAlmacen(almacen.getTipoAlmacen().getIdTipoAlmacen())
                .tipo(almacen.getTipoAlmacen().getNombre())
                .activo(almacen.getActivo())
                .idResponsable(responsable != null ? responsable.getIdEmpleado() : null)
                .responsable(nombreCompleto(responsable))
                .telefonoResponsable(telefono(responsable))
                .build();
    }

    private String telefono(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null
                || empleado.getPersona().getContactos() == null) return null;
        return empleado.getPersona().getContactos().stream()
                .map(contacto -> contacto.getTelefono())
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst()
                .orElse(null);
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
