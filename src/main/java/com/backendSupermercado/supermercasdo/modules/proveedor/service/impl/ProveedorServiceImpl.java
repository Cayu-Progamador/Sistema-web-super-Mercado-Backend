package com.backendSupermercado.supermercasdo.modules.proveedor.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Ciudad;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Direccion;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Sexo;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.CiudadRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.ContactoRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.DireccionRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.PersonaRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.SexoRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorEstadisticasDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorListadoDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorRequestDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.ContactoEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.DireccionEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Empresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.repository.ContactoEmpresaRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.repository.EmpresaRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.repository.ProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.service.ProveedorService;
import com.backendSupermercado.supermercasdo.shared.specification.ProveedorSpecification;
import com.backendSupermercado.supermercasdo.shared.util.ReporteProveedorUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final EmpresaRepository empresaRepository;
    private final ContactoEmpresaRepository contactoEmpresaRepository;
    private final PersonaRepository personaRepository;
    private final ContactoRepository contactoRepository;
    private final DireccionRepository direccionRepository;
    private final SexoRepository sexoRepository;
    private final CiudadRepository ciudadRepository;

    @Override
    @Transactional(readOnly = true)
    public ProveedorEstadisticasDto obtenerEstadisticas() {
        long total = proveedorRepository.count();
        long empresas = proveedorRepository.countByTipoProveedor(TipoProveedor.EMPRESA);
        long personas = proveedorRepository.countByTipoProveedor(TipoProveedor.PERSONA);
        long activos = proveedorRepository.countByEstado(true);
        long inactivos = proveedorRepository.countByEstado(false);
        return new ProveedorEstadisticasDto(total, empresas, personas, activos, inactivos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDto obtenerProveedorPorId(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor con id " + id + " no encontrado"));
        return toResponseDto(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorListadoDto> listarProveedores(String busqueda, String tipo, Boolean estado, Long idCiudad, Pageable pageable) {
        Specification<Proveedor> spec = Specification
                .where(ProveedorSpecification.busquedaGeneral(busqueda))
                .and(ProveedorSpecification.tipoEqual(tipo))
                .and(ProveedorSpecification.estadoEqual(estado))
                .and(ProveedorSpecification.ciudadEqual(idCiudad));

        return proveedorRepository.findAll(spec, pageable)
                .map(this::toListadoDto);
    }

    @Override
    @Transactional
    public ProveedorResponseDto crearProveedor(ProveedorRequestDto dto) {
        TipoProveedor tipo = TipoProveedor.valueOf(dto.getTipo().toUpperCase());

        Proveedor proveedor = new Proveedor();
        proveedor.setTipoProveedor(tipo);
        proveedor.setEstado(true);
        proveedor.setFechaRegistro(LocalDate.now());

        if (tipo == TipoProveedor.EMPRESA) {
            crearProveedorEmpresa(proveedor, dto.getEmpresa());
        } else {
            crearProveedorPersona(proveedor, dto.getPersona());
        }

        proveedor = proveedorRepository.save(proveedor);
        return toResponseDto(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDto actualizarProveedor(Long id, ProveedorRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor con id " + id + " no encontrado"));

        if (dto.getTipo() == null || dto.getTipo().isBlank()) {
            throw new IllegalArgumentException("El tipo de proveedor es obligatorio");
        }
        TipoProveedor tipo;
        try {
            tipo = TipoProveedor.valueOf(dto.getTipo().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo de proveedor debe ser EMPRESA o PERSONA");
        }
        if (tipo != proveedor.getTipoProveedor()) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de proveedor de "
                    + proveedor.getTipoProveedor().name() + " a " + tipo.name());
        }

        if (tipo == TipoProveedor.EMPRESA) {
            actualizarProveedorEmpresa(proveedor, dto.getEmpresa());
        } else {
            actualizarProveedorPersona(proveedor, dto.getPersona());
        }

        proveedorRepository.save(proveedor);
        return toResponseDto(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDto activarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor con id " + id + " no encontrado"));
        proveedor.setEstado(true);
        proveedorRepository.save(proveedor);
        return toResponseDto(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponseDto desactivarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor con id " + id + " no encontrado"));
        proveedor.setEstado(false);
        proveedorRepository.save(proveedor);
        return toResponseDto(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorListadoDto> exportarProveedores(String busqueda, String tipo, Boolean estado, Long idCiudad) {
        Specification<Proveedor> spec = Specification
                .where(ProveedorSpecification.busquedaGeneral(busqueda))
                .and(ProveedorSpecification.tipoEqual(tipo))
                .and(ProveedorSpecification.estadoEqual(estado))
                .and(ProveedorSpecification.ciudadEqual(idCiudad));

        return proveedorRepository.findAll(spec)
                .stream()
                .map(this::toListadoDto)
                .toList();
    }

    @Override
    public byte[] exportarProveedoresPDF(String busqueda, String tipo, Boolean estado, Long idCiudad, String username) {
        List<ProveedorListadoDto> data = exportarProveedores(busqueda, tipo, estado, idCiudad);
        return ReporteProveedorUtil.generarPdf(data, username);
    }

    @Override
    public byte[] exportarProveedoresExcel(String busqueda, String tipo, Boolean estado, Long idCiudad) {
        List<ProveedorListadoDto> data = exportarProveedores(busqueda, tipo, estado, idCiudad);
        return ReporteProveedorUtil.generarExcel(data);
    }

    @Override
    public byte[] exportarProveedorDetallePDF(Long id, String username) {
        ProveedorResponseDto detalle = obtenerProveedorPorId(id);
        return ReporteProveedorUtil.generarPdfDetalle(detalle, username);
    }

    @Override
    public byte[] exportarProveedorDetalleExcel(Long id) {
        ProveedorResponseDto detalle = obtenerProveedorPorId(id);
        return ReporteProveedorUtil.generarExcelDetalle(detalle);
    }

    private void actualizarProveedorEmpresa(Proveedor proveedor, ProveedorRequestDto.EmpresaRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe enviar los datos de la empresa");
        }
        if (dto.getNit() == null || dto.getNit().isBlank()) {
            throw new IllegalArgumentException("El NIT de la empresa es obligatorio");
        }
        if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }

        Empresa empresa = proveedor.getEmpresa();
        if (empresa == null) {
            throw new IllegalStateException("La empresa del proveedor no existe");
        }

        if (empresaRepository.existsByNitAndIdEmpresaNot(dto.getNit().trim(), empresa.getIdEmpresa())) {
            throw new ResourceConflictException("El NIT " + dto.getNit() + " ya está registrado para otro proveedor");
        }

        List<ContactoEmpresa> nuevos = new ArrayList<>();
        Set<String> telefonosVistos = new HashSet<>();
        Set<String> correosVistos = new HashSet<>();
        if (dto.getContactos() != null) {
            for (ProveedorRequestDto.ContactoRequestDto c : dto.getContactos()) {
                String telefono = normalizar(c.getTelefono());
                String correo = normalizar(c.getCorreo());
                if (telefono == null && correo == null) {
                    continue;
                }
                if (telefono != null && !telefonosVistos.add(telefono)) {
                    throw new IllegalArgumentException("El teléfono " + telefono + " está repetido en la lista de contactos");
                }
                if (correo != null && !correosVistos.add(correo)) {
                    throw new IllegalArgumentException("El correo " + correo + " está repetido en la lista de contactos");
                }
                validarContactoUnicoEmpresa(telefono, correo, empresa.getIdEmpresa());
                ContactoEmpresa ce = new ContactoEmpresa();
                ce.setTelefono(telefono);
                ce.setCorreo(correo);
                ce.setEmpresa(empresa);
                nuevos.add(ce);
            }
        }
        if (nuevos.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un contacto (teléfono o correo)");
        }

        empresa.setNit(dto.getNit().trim());
        empresa.setRazonSocial(dto.getRazonSocial().trim());
        empresa.setNombreComercial(normalizar(dto.getNombreComercial()));
        empresa.setRepresentanteLegal(normalizar(dto.getRepresentanteLegal()));

        empresa.getContactos().clear();
        empresaRepository.saveAndFlush(empresa);
        empresa.getContactos().addAll(nuevos);
        empresaRepository.save(empresa);

        actualizarDireccionEmpresa(empresa, dto.getDireccion());
    }

    private void actualizarProveedorPersona(Proveedor proveedor, ProveedorRequestDto.PersonaRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe enviar los datos de la persona");
        }
        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (dto.getCi() == null || dto.getCi().isBlank()) {
            throw new IllegalArgumentException("El CI es obligatorio");
        }
        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        Persona persona = proveedor.getPersona();
        if (persona == null) {
            throw new IllegalStateException("La persona del proveedor no existe");
        }

        if (personaRepository.existsByCiAndIdPersonaNot(dto.getCi().trim(), persona.getIdPersona())) {
            throw new ResourceConflictException("El CI " + dto.getCi() + " ya está registrado para otra persona");
        }
        validarContactoUnicoPersona(dto.getTelefono().trim(), dto.getCorreo().trim(), persona.getIdPersona());

        Sexo sexo = sexoRepository.findByNombreIgnoreCase(dto.getSexo().trim())
                .orElseThrow(() -> new IllegalArgumentException("Sexo no encontrado"));

        LocalDate fechaNacimiento = parseFecha(dto.getFechaNacimiento());
        validarFechaNacimientoProveedor(fechaNacimiento);
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        persona.setNombres(dto.getNombres().trim());
        persona.setApellidoPaterno(normalizar(dto.getApellidoPaterno()));
        persona.setApellidoMaterno(normalizar(dto.getApellidoMaterno()));
        persona.setCi(dto.getCi().trim());
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setSexo(sexo);

        List<Contacto> viejos = persona.getContactos();
        if (viejos != null && !viejos.isEmpty()) {
            contactoRepository.deleteAll(viejos);
            contactoRepository.flush();
        }

        Contacto contacto = new Contacto();
        contacto.setTelefono(dto.getTelefono().trim());
        contacto.setCorreo(dto.getCorreo().trim());
        contacto.setPersona(persona);
        contacto = contactoRepository.save(contacto);
        persona.setContactos(new ArrayList<>(List.of(contacto)));

        actualizarDireccionPersona(persona, dto.getDireccion());

        personaRepository.save(persona);
    }

    private void crearProveedorEmpresa(Proveedor proveedor, ProveedorRequestDto.EmpresaRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe enviar los datos de la empresa");
        }
        if (dto.getNit() == null || dto.getNit().isBlank()) {
            throw new IllegalArgumentException("El NIT de la empresa es obligatorio");
        }
        if (dto.getRazonSocial() == null || dto.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }
        if (empresaRepository.existsByNit(dto.getNit().trim())) {
            throw new ResourceConflictException("El NIT " + dto.getNit() + " ya está registrado para otro proveedor");
        }

        Empresa empresa = new Empresa();
        empresa.setNit(dto.getNit().trim());
        empresa.setRazonSocial(dto.getRazonSocial().trim());
        empresa.setNombreComercial(normalizar(dto.getNombreComercial()));
        empresa.setRepresentanteLegal(normalizar(dto.getRepresentanteLegal()));
        empresa.setProveedor(proveedor);

        List<ContactoEmpresa> contactos = new ArrayList<>();
        if (dto.getContactos() != null) {
            for (ProveedorRequestDto.ContactoRequestDto c : dto.getContactos()) {
                String telefono = normalizar(c.getTelefono());
                String correo = normalizar(c.getCorreo());
                if (telefono == null && correo == null) {
                    continue;
                }
                validarContactoUnico(telefono, correo);
                ContactoEmpresa ce = new ContactoEmpresa();
                ce.setTelefono(telefono);
                ce.setCorreo(correo);
                ce.setEmpresa(empresa);
                contactos.add(ce);
            }
        }
        if (contactos.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos un contacto (teléfono o correo)");
        }
        empresa.setContactos(contactos);

        if (dto.getDireccion() != null && tieneDireccion(dto.getDireccion())) {
            DireccionEmpresa direccion = new DireccionEmpresa();
            direccion.setEmpresa(empresa);
            direccion.setZona(normalizar(dto.getDireccion().getZona()));
            direccion.setCalle(normalizar(dto.getDireccion().getCalle()));
            direccion.setNumero(normalizar(dto.getDireccion().getNumero()));
            direccion.setReferencia(normalizar(dto.getDireccion().getReferencia()));
            direccion.setCiudad(resolverCiudad(dto.getDireccion().getCiudad()));
            empresa.setDireccion(direccion);
        }

        proveedor.setEmpresa(empresa);
    }

    private void crearProveedorPersona(Proveedor proveedor, ProveedorRequestDto.PersonaRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Debe enviar los datos de la persona");
        }
        if (dto.getNombres() == null || dto.getNombres().isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios");
        }
        if (dto.getCi() == null || dto.getCi().isBlank()) {
            throw new IllegalArgumentException("El CI es obligatorio");
        }
        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }
        if (personaRepository.existsByCi(dto.getCi().trim())) {
            throw new ResourceConflictException("El CI " + dto.getCi() + " ya está registrado para otra persona");
        }
        validarContactoUnico(dto.getTelefono().trim(), dto.getCorreo().trim());

        Sexo sexo = sexoRepository.findByNombreIgnoreCase(dto.getSexo().trim())
                .orElseThrow(() -> new EntityNotFoundException("Sexo no encontrado"));

        LocalDate fechaNacimiento;
        try {
            fechaNacimiento = LocalDate.parse(dto.getFechaNacimiento());
        } catch (Exception e) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida");
        }

        validarFechaNacimientoProveedor(fechaNacimiento);

        Persona persona = new Persona();
        persona.setNombres(dto.getNombres().trim());
        persona.setApellidoPaterno(normalizar(dto.getApellidoPaterno()));
        persona.setApellidoMaterno(normalizar(dto.getApellidoMaterno()));
        persona.setCi(dto.getCi().trim());
        persona.setFechaNacimiento(fechaNacimiento);
        persona.setSexo(sexo);
        persona.setContactos(new ArrayList<>());
        persona = personaRepository.save(persona);

        Contacto contacto = new Contacto();
        contacto.setTelefono(dto.getTelefono().trim());
        contacto.setCorreo(dto.getCorreo().trim());
        contacto.setPersona(persona);
        contactoRepository.save(contacto);

        if (dto.getDireccion() != null && tieneDireccion(dto.getDireccion())) {
            Direccion direccion = new Direccion();
            direccion.setPersona(persona);
            direccion.setZona(normalizar(dto.getDireccion().getZona()));
            direccion.setCalle(normalizar(dto.getDireccion().getCalle()));
            direccion.setNumero(normalizar(dto.getDireccion().getNumero()));
            direccion.setReferencia(normalizar(dto.getDireccion().getReferencia()));
            direccion.setCiudad(resolverCiudad(dto.getDireccion().getCiudad()));
            direccionRepository.save(direccion);
        }

        proveedor.setPersona(persona);
    }

    private void validarContactoUnico(String telefono, String correo) {
        if (telefono != null) {
            if (contactoEmpresaRepository.existsByTelefono(telefono)
                    || contactoRepository.existsByTelefono(telefono)) {
                throw new ResourceConflictException("El teléfono " + telefono + " ya está registrado");
            }
        }
        if (correo != null) {
            if (contactoEmpresaRepository.existsByCorreo(correo)
                    || contactoRepository.existsByCorreo(correo)) {
                throw new ResourceConflictException("El correo " + correo + " ya está registrado");
            }
        }
    }

    private void validarContactoUnicoEmpresa(String telefono, String correo, Long idEmpresa) {
        if (telefono != null
                && (contactoEmpresaRepository.existsByTelefonoAndEmpresa_IdEmpresaNot(telefono, idEmpresa)
                        || contactoRepository.existsByTelefono(telefono))) {
            throw new ResourceConflictException("El teléfono " + telefono + " ya está registrado");
        }
        if (correo != null
                && (contactoEmpresaRepository.existsByCorreoAndEmpresa_IdEmpresaNot(correo, idEmpresa)
                        || contactoRepository.existsByCorreo(correo))) {
            throw new ResourceConflictException("El correo " + correo + " ya está registrado");
        }
    }

    private void validarContactoUnicoPersona(String telefono, String correo, Long idPersona) {
        if (telefono != null
                && (contactoRepository.existsByTelefonoAndPersona_IdPersonaNot(telefono, idPersona)
                        || contactoEmpresaRepository.existsByTelefono(telefono))) {
            throw new ResourceConflictException("El teléfono " + telefono + " ya está registrado");
        }
        if (correo != null
                && (contactoRepository.existsByCorreoAndPersona_IdPersonaNot(correo, idPersona)
                        || contactoEmpresaRepository.existsByCorreo(correo))) {
            throw new ResourceConflictException("El correo " + correo + " ya está registrado");
        }
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        try {
            return LocalDate.parse(fecha.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida");
        }
    }

    private void validarFechaNacimientoProveedor(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        int edad = Period.between(fechaNacimiento, hoy).getYears();
        if (edad < 18) {
            throw new IllegalArgumentException("El proveedor debe tener al menos 18 años");
        }
        if (edad > 110) {
            throw new IllegalArgumentException("La edad del proveedor no puede ser mayor a 110 años");
        }
    }

    private void actualizarDireccionEmpresa(Empresa empresa, ProveedorRequestDto.DireccionRequestDto dto) {
        if (dto == null || !tieneDireccion(dto)) {
            empresa.setDireccion(null);
            return;
        }
        DireccionEmpresa direccion = empresa.getDireccion();
        if (direccion == null) {
            direccion = new DireccionEmpresa();
            direccion.setEmpresa(empresa);
        }
        direccion.setZona(normalizar(dto.getZona()));
        direccion.setCalle(normalizar(dto.getCalle()));
        direccion.setNumero(normalizar(dto.getNumero()));
        direccion.setReferencia(normalizar(dto.getReferencia()));
        direccion.setCiudad(resolverCiudad(dto.getCiudad()));
        empresa.setDireccion(direccion);
    }

    private void actualizarDireccionPersona(Persona persona, ProveedorRequestDto.DireccionRequestDto dto) {
        if (dto == null || !tieneDireccion(dto)) {
            persona.setDireccion(null);
            return;
        }
        Direccion direccion = persona.getDireccion();
        if (direccion == null) {
            direccion = new Direccion();
            direccion.setPersona(persona);
        }
        direccion.setZona(normalizar(dto.getZona()));
        direccion.setCalle(normalizar(dto.getCalle()));
        direccion.setNumero(normalizar(dto.getNumero()));
        direccion.setReferencia(normalizar(dto.getReferencia()));
        direccion.setCiudad(resolverCiudad(dto.getCiudad()));
        persona.setDireccion(direccion);
    }

    private Ciudad resolverCiudad(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        return ciudadRepository.findFirstByNombreIgnoreCase(nombre.trim())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad '" + nombre + "' no encontrada"));
    }

    private boolean tieneDireccion(ProveedorRequestDto.DireccionRequestDto dto) {
        return dto.getCiudad() != null && !dto.getCiudad().isBlank()
                || dto.getZona() != null && !dto.getZona().isBlank()
                || dto.getCalle() != null && !dto.getCalle().isBlank()
                || dto.getNumero() != null && !dto.getNumero().isBlank()
                || dto.getReferencia() != null && !dto.getReferencia().isBlank();
    }

    private String normalizar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private ProveedorResponseDto toResponseDto(Proveedor proveedor) {
        ProveedorResponseDto dto = new ProveedorResponseDto();
        dto.setId(proveedor.getIdProveedor());
        dto.setTipo(proveedor.getTipoProveedor().name());
        dto.setEstado(proveedor.getEstado());
        dto.setFechaRegistro(proveedor.getFechaRegistro() != null ? proveedor.getFechaRegistro().toString() : null);

        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA && proveedor.getEmpresa() != null) {
            Empresa empresa = proveedor.getEmpresa();
            ProveedorResponseDto.EmpresaResponseDto e = new ProveedorResponseDto.EmpresaResponseDto();
            e.setId(empresa.getIdEmpresa());
            e.setNit(empresa.getNit());
            e.setRazonSocial(empresa.getRazonSocial());
            e.setNombreComercial(empresa.getNombreComercial());
            e.setRepresentanteLegal(empresa.getRepresentanteLegal());

            if (empresa.getContactos() != null) {
                e.setContactos(empresa.getContactos().stream().map(c -> {
                    ProveedorResponseDto.ContactoResponseDto ce = new ProveedorResponseDto.ContactoResponseDto();
                    ce.setId(c.getIdContactoEmpresa());
                    ce.setTelefono(c.getTelefono());
                    ce.setCorreo(c.getCorreo());
                    return ce;
                }).toList());
            }

            if (empresa.getDireccion() != null) {
                e.setDireccion(toDireccionDto(empresa.getDireccion().getIdDireccionEmpresa(), empresa.getDireccion()));
            }

            dto.setEmpresa(e);
        } else if (proveedor.getTipoProveedor() == TipoProveedor.PERSONA && proveedor.getPersona() != null) {
            Persona persona = proveedor.getPersona();
            ProveedorResponseDto.PersonaResponseDto p = new ProveedorResponseDto.PersonaResponseDto();
            p.setId(persona.getIdPersona());
            p.setNombreCompleto(nombreCompleto(persona));
            p.setNombres(persona.getNombres());
            p.setApellidoPaterno(persona.getApellidoPaterno());
            p.setApellidoMaterno(persona.getApellidoMaterno());
            p.setCi(persona.getCi());
            p.setFechaNacimiento(persona.getFechaNacimiento() != null ? persona.getFechaNacimiento().toString() : null);
            p.setSexo(persona.getSexo() != null ? persona.getSexo().getNombre() : null);
            if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                Contacto contacto = persona.getContactos().get(0);
                p.setTelefono(contacto.getTelefono());
                p.setCorreo(contacto.getCorreo());
            }
            if (persona.getDireccion() != null) {
                p.setDireccion(toDireccionDto(persona.getDireccion().getIdDireccion(), persona.getDireccion()));
            }
            dto.setPersona(p);
        }

        return dto;
    }

    private ProveedorResponseDto.DireccionResponseDto toDireccionDto(Long id, Object obj) {
        ProveedorResponseDto.DireccionResponseDto d = new ProveedorResponseDto.DireccionResponseDto();
        d.setId(id);
        if (obj instanceof Direccion dir) {
            d.setZona(dir.getZona());
            d.setCalle(dir.getCalle());
            d.setNumero(dir.getNumero());
            d.setReferencia(dir.getReferencia());
            if (dir.getCiudad() != null) {
                d.setCiudad(dir.getCiudad().getNombre());
            }
        } else if (obj instanceof DireccionEmpresa dir) {
            d.setZona(dir.getZona());
            d.setCalle(dir.getCalle());
            d.setNumero(dir.getNumero());
            d.setReferencia(dir.getReferencia());
            if (dir.getCiudad() != null) {
                d.setCiudad(dir.getCiudad().getNombre());
            }
        }
        return d;
    }

    private ProveedorListadoDto toListadoDto(Proveedor proveedor) {
        ProveedorListadoDto dto = new ProveedorListadoDto();
        dto.setId(proveedor.getIdProveedor());
        dto.setTipo(proveedor.getTipoProveedor().name());
        dto.setEstado(proveedor.getEstado());

        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA) {
            var empresa = proveedor.getEmpresa();
            if (empresa != null) {
                String nombre = empresa.getNombreComercial() != null && !empresa.getNombreComercial().isBlank()
                        ? empresa.getNombreComercial()
                        : empresa.getRazonSocial();
                dto.setNombre(nombre);
                dto.setDocumento(empresa.getNit());
                dto.setContacto(empresa.getRepresentanteLegal());
                if (empresa.getContactos() != null && !empresa.getContactos().isEmpty()) {
                    var contacto = empresa.getContactos().get(0);
                    dto.setTelefono(contacto.getTelefono());
                    dto.setCorreo(contacto.getCorreo());
                    dto.setContactos(empresa.getContactos().stream()
                            .map(c -> new ProveedorListadoDto.ContactoDto(c.getTelefono(), c.getCorreo()))
                            .toList());
                }
                if (empresa.getDireccion() != null && empresa.getDireccion().getCiudad() != null) {
                    dto.setCiudad(empresa.getDireccion().getCiudad().getNombre());
                }
            }
        } else if (proveedor.getTipoProveedor() == TipoProveedor.PERSONA) {
            var persona = proveedor.getPersona();
            if (persona != null) {
                dto.setNombre(nombreCompleto(persona));
                dto.setDocumento(persona.getCi());
                dto.setContacto(nombreCompleto(persona));
                if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                    Contacto contacto = persona.getContactos().get(0);
                    dto.setTelefono(contacto.getTelefono());
                    dto.setCorreo(contacto.getCorreo());
                    dto.setContactos(persona.getContactos().stream()
                            .map(c -> new ProveedorListadoDto.ContactoDto(c.getTelefono(), c.getCorreo()))
                            .toList());
                }
                if (persona.getDireccion() != null && persona.getDireccion().getCiudad() != null) {
                    dto.setCiudad(persona.getDireccion().getCiudad().getNombre());
                }
            }
        }

        return dto;
    }

    private String nombreCompleto(Persona persona) {
        String nombre = (persona.getNombres() != null ? persona.getNombres() : "")
                + " " + (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                + " " + (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
        return nombre.trim().replaceAll("\\s+", " ");
    }
}
