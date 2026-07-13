package com.backendSupermercado.supermercasdo.modules.empleado.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.DashboardEmpleadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoDisponibleDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoRequestDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Ciudad;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Direccion;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Sexo;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Pais;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.DepartamentoGeografico;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.CiudadRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.ContactoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.DepartamentoGeograficoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.DireccionRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.PaisRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.PersonaRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.SexoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;
import com.backendSupermercado.supermercasdo.shared.specification.EmpleadoSpecification;
import com.backendSupermercado.supermercasdo.shared.util.ReporteEmpleadoUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final ContactoRepository contactoRepository;
    private final DireccionRepository direccionRepository;
    private final SexoRepository sexoRepository;
    private final PaisRepository paisRepository;
    private final DepartamentoGeograficoRepository departamentoGeograficoRepository;
    private final CiudadRepository ciudadRepository;

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
    public DashboardEmpleadoDto obtenerEstadisticasEmpleado() {
        return new DashboardEmpleadoDto(
                empleadoRepository.count(),
                empleadoRepository.countByEstado(true),
                empleadoRepository.countByEstado(false),
                0L);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpleadoListadoDto> listarEmpleados(String busqueda, Boolean estado, String fechaDesde, String fechaHasta, Pageable pageable) {
        Specification<Empleado> spec = Specification
                .where(EmpleadoSpecification.busquedaGeneral(busqueda))
                .and(EmpleadoSpecification.estadoEqual(estado))
                .and(EmpleadoSpecification.fechaContratacionBetween(fechaDesde, fechaHasta));

        return empleadoRepository.findAll(spec, pageable)
                .map(this::toListadoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmpleadoDisponibleDto> listarDisponiblesParaContrato(String busqueda, Pageable pageable) {
        return empleadoRepository.listarDisponiblesParaContrato(busqueda, pageable);
    }

    @Override
    @Transactional
    public EmpleadoResponseDto crearEmpleado(EmpleadoRequestDto dto) {
        Sexo sexo = sexoRepository.findById(dto.getIdSexo())
                .orElseThrow(() -> new EntityNotFoundException("Sexo no encontrado"));

        Persona persona = new Persona();
        persona.setNombres(dto.getNombres());
        persona.setApellidoPaterno(dto.getApellidoPaterno());
        persona.setApellidoMaterno(dto.getApellidoMaterno());
        persona.setCi(dto.getCi());
        persona.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        persona.setSexo(sexo);
        persona.setContactos(new ArrayList<>());
        persona = personaRepository.save(persona);

        Contacto contacto = new Contacto();
        contacto.setTelefono(dto.getTelefono());
        contacto.setCorreo(dto.getCorreo());
        contacto.setPersona(persona);
        contactoRepository.save(contacto);

        Ciudad ciudad = resolverUbicacion(dto);

        Direccion direccion = new Direccion();
        direccion.setPersona(persona);
        direccion.setCiudad(ciudad);
        direccion.setZona(dto.getZona());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setReferencia(dto.getReferencia());
        direccionRepository.save(direccion);

        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        empleado.setEstado(true);
        if (dto.getFechaContratacion() != null && !dto.getFechaContratacion().isBlank()) {
            empleado.setFechaContratacion(LocalDate.parse(dto.getFechaContratacion()));
        }
        empleado = empleadoRepository.save(empleado);

        return toResponseDto(empleado, persona, contacto, direccion, sexo);
    }

    @Override
    @Transactional
    public EmpleadoResponseDto actualizarEmpleado(Long id, EmpleadoRequestDto dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));

        Persona persona = empleado.getPersona();
        if (persona == null) {
            throw new EntityNotFoundException("Persona no encontrada para el empleado");
        }

        Sexo sexo = sexoRepository.findById(dto.getIdSexo())
                .orElseThrow(() -> new EntityNotFoundException("Sexo no encontrado"));

        persona.setNombres(dto.getNombres());
        persona.setApellidoPaterno(dto.getApellidoPaterno());
        persona.setApellidoMaterno(dto.getApellidoMaterno());
        persona.setCi(dto.getCi());
        persona.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        persona.setSexo(sexo);
        personaRepository.save(persona);

        List<Contacto> contactos = persona.getContactos();
        Contacto contacto;
        if (contactos != null && !contactos.isEmpty()) {
            contacto = contactos.get(0);
        } else {
            contacto = new Contacto();
            contacto.setPersona(persona);
        }
        contacto.setTelefono(dto.getTelefono());
        contacto.setCorreo(dto.getCorreo());
        contactoRepository.save(contacto);

        Ciudad ciudad = resolverUbicacion(dto);

        Direccion direccion = persona.getDireccion();
        if (direccion == null) {
            direccion = new Direccion();
            direccion.setPersona(persona);
        }
        direccion.setCiudad(ciudad);
        direccion.setZona(dto.getZona());
        direccion.setCalle(dto.getCalle());
        direccion.setNumero(dto.getNumero());
        direccion.setReferencia(dto.getReferencia());
        direccionRepository.save(direccion);

        if (dto.getFechaContratacion() != null && !dto.getFechaContratacion().isBlank()) {
            empleado.setFechaContratacion(LocalDate.parse(dto.getFechaContratacion()));
        } else {
            empleado.setFechaContratacion(null);
        }
        empleadoRepository.save(empleado);

        return toResponseDto(empleado, persona, contacto, direccion, sexo);
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoResponseDto obtenerEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));

        Persona persona = empleado.getPersona();
        Sexo sexo = persona != null ? persona.getSexo() : null;

        Contacto contacto = null;
        if (persona != null && persona.getContactos() != null && !persona.getContactos().isEmpty()) {
            contacto = persona.getContactos().get(0);
        }

        Direccion direccion = persona != null ? persona.getDireccion() : null;

        return toResponseDto(empleado, persona, contacto, direccion, sexo);
    }

    @Override
    @Transactional
    public void activarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));
        empleado.setEstado(true);
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional
    public void desactivarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con id: " + id));
        empleado.setEstado(false);
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoListadoDto> exportarEmpleados(EmpleadoFiltrosDto filtros) {
        Specification<Empleado> spec = Specification
                .where(EmpleadoSpecification.busquedaGeneral(filtros.getBusqueda()))
                .and(EmpleadoSpecification.estadoEqual(filtros.getEstado()))
                .and(EmpleadoSpecification.fechaContratacionBetween(
                        filtros.getFechaDesde() != null ? filtros.getFechaDesde().toString() : null,
                        filtros.getFechaHasta() != null ? filtros.getFechaHasta().toString() : null));

        return empleadoRepository.findAll(spec)
                .stream()
                .map(this::toListadoDto)
                .toList();
    }

    @Override
    public byte[] exportarEmpleadosPDF(EmpleadoFiltrosDto filtros, String username) {
        List<EmpleadoListadoDto> data = exportarEmpleados(filtros);
        return ReporteEmpleadoUtil.generarPdf(data, username);
    }

    @Override
    public byte[] exportarEmpleadosExcel(EmpleadoFiltrosDto filtros) {
        List<EmpleadoListadoDto> data = exportarEmpleados(filtros);
        return ReporteEmpleadoUtil.generarExcel(data);
    }

    @Override
    public byte[] exportarEmpleadoDetallePDF(Long id, String username) {
        EmpleadoResponseDto detalle = obtenerEmpleado(id);
        return ReporteEmpleadoUtil.generarPdfDetalle(detalle, username);
    }

    private Ciudad resolverUbicacion(EmpleadoRequestDto dto) {
        if (dto.getCiudad() == null || dto.getCiudad().isBlank()) {
            return null;
        }

        Pais pais = null;
        if (dto.getPais() != null && !dto.getPais().isBlank()) {
            String nombrePais = dto.getPais();
            pais = paisRepository.findByNombre(nombrePais)
                    .orElseGet(() -> paisRepository.save(new Pais(null, nombrePais, new ArrayList<>())));
        }

        final Pais paisFinal = pais;

        DepartamentoGeografico dep = null;
        if (dto.getDepartamento() != null && !dto.getDepartamento().isBlank()) {
            String nombreDep = dto.getDepartamento();
            dep = departamentoGeograficoRepository.findByNombreAndPais(nombreDep, paisFinal)
                    .orElseGet(() -> departamentoGeograficoRepository.save(
                            new DepartamentoGeografico(null, nombreDep, paisFinal, new ArrayList<>())));
        }

        final DepartamentoGeografico depFinal = dep;

        return ciudadRepository.findByNombreAndDepartamento(dto.getCiudad(), depFinal)
                .orElseGet(() -> ciudadRepository.save(new Ciudad(null, dto.getCiudad(), depFinal, new ArrayList<>())));
    }

    private EmpleadoListadoDto toListadoDto(Empleado empleado) {
        String nombreCompleto = "";
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

        return new EmpleadoListadoDto(
                empleado.getIdEmpleado(),
                nombreCompleto,
                "",
                telefono,
                empleado.getEstado()
        );
    }

    private EmpleadoResponseDto toResponseDto(Empleado empleado, Persona persona,
                                               Contacto contacto, Direccion direccion,
                                               Sexo sexo) {
        EmpleadoResponseDto dto = new EmpleadoResponseDto();
        dto.setIdEmpleado(empleado.getIdEmpleado());
        dto.setEstado(empleado.getEstado());
        dto.setFechaContratacion(empleado.getFechaContratacion() != null
                ? empleado.getFechaContratacion().toString() : null);

        if (persona != null) {
            dto.setNombres(persona.getNombres());
            dto.setApellidoPaterno(persona.getApellidoPaterno());
            dto.setApellidoMaterno(persona.getApellidoMaterno());
            dto.setCi(persona.getCi());
            dto.setFechaNacimiento(persona.getFechaNacimiento() != null
                    ? persona.getFechaNacimiento().toString() : null);
            dto.setNombreCompleto((persona.getNombres() != null ? persona.getNombres() : "")
                    + " " + (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                    + " " + (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : ""));
            dto.setNombreCompleto(dto.getNombreCompleto().trim().replaceAll("\\s+", " "));
        }

        if (sexo != null) {
            dto.setIdSexo(sexo.getIdSexo());
            dto.setSexo(sexo.getNombre());
        }

        if (contacto != null) {
            dto.setTelefono(contacto.getTelefono());
            dto.setCorreo(contacto.getCorreo());
        }

        if (direccion != null) {
            if (direccion.getCiudad() != null) {
                dto.setPais(direccion.getCiudad().getDepartamento() != null
                        && direccion.getCiudad().getDepartamento().getPais() != null
                        ? direccion.getCiudad().getDepartamento().getPais().getNombre() : null);
                dto.setDepartamento(direccion.getCiudad().getDepartamento() != null
                        ? direccion.getCiudad().getDepartamento().getNombre() : null);
                dto.setCiudad(direccion.getCiudad().getNombre());
            }
            dto.setZona(direccion.getZona());
            dto.setCalle(direccion.getCalle());
            dto.setNumero(direccion.getNumero());
            dto.setReferencia(direccion.getReferencia());
        }

        return dto;
    }

}
