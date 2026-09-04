package com.backendSupermercado.supermercasdo.modules.inventario_fisico.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.almacen.repository.AlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.categorias.entity.Categoria;
import com.backendSupermercado.supermercasdo.modules.categorias.repository.CategoriaRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.EmpleadoRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.MovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CrearInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoListadoDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoDetalleDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoPaginaDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.ConteoFisicoResumenDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarConteoFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.FinalizarConteoFisicoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.GuardarDiferenciasRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.CancelarInventarioFisicoRequest;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoEstadoResponse;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.dto.InventarioFisicoResponse.ItemSimpleDto;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.DetalleInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.InventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.EstadoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.enums.TipoInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.exception.InventarioFisicoConflictoException;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.exception.InventarioFisicoNoEncontradoException;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.repository.DetalleInventarioFisicoRepository;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.repository.InventarioFisicoRepository;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.service.InventarioFisicoPdfService;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.service.InventarioFisicoService;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.specification.InventarioFisicoSpecification;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.specification.DetalleInventarioFisicoSpecification;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioFisicoServiceImpl implements InventarioFisicoService {

    private static final Set<EstadoInventarioFisico> ESTADOS_ABIERTOS = EnumSet.of(
            EstadoInventarioFisico.BORRADOR,
            EstadoInventarioFisico.EN_CONTEO,
            EstadoInventarioFisico.EN_REVISION);

    private final InventarioFisicoRepository inventarioRepository;
    private final DetalleInventarioFisicoRepository detalleRepository;
    private final ExistenciaRepository existenciaRepository;
    private final AlmacenRepository almacenRepository;
    private final UbicacionAlmacenRepository ubicacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final TipoMovimientoAlmacenRepository tipoMovimientoRepository;
    private final InventarioFisicoPdfService pdfService;

    @Override
    @Transactional
    public InventarioFisicoResponse crear(CrearInventarioFisicoRequest request) {
        Usuario usuarioActual = obtenerUsuarioActual();
        validarConteoCiego(request.conteoCiego(), usuarioActual);

        Empleado responsable = empleadoRepository.findById(request.idResponsable())
                .filter(e -> Boolean.TRUE.equals(e.getEstado()))
                .orElseThrow(() -> new IllegalArgumentException("El responsable no existe o está inactivo"));

        Alcance alcance = resolverYValidarAlcance(request);
        validarConflicto(request.tipo(), alcance, null);

        InventarioFisico inventario = InventarioFisico.builder()
                .codigo(codigoTemporal())
                .tipo(request.tipo())
                .estado(EstadoInventarioFisico.BORRADOR)
                .fechaProgramada(request.fechaProgramada())
                .conteoCiego(request.conteoCiego())
                .observacion(limpiar(request.observacion()))
                .responsable(responsable)
                .almacen(alcance.almacen())
                .ubicacion(alcance.ubicacion())
                .categoria(alcance.categoria())
                .productosSeleccionados(alcance.productos())
                .creadoPor(usuarioActual)
                .fechaCreacion(LocalDateTime.now())
                .build();

        inventarioRepository.saveAndFlush(inventario);
        inventario.setCodigo("INV-" + String.format("%05d", inventario.getIdInventarioFisico()));
        return mapear(inventario);
    }

    @Override
    @Transactional
    public InventarioFisicoResponse actualizarBorrador(
            Long idInventarioFisico, CrearInventarioFisicoRequest request) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        if (inventario.getEstado() != EstadoInventarioFisico.BORRADOR) {
            throw new IllegalStateException("Solo se puede editar un inventario en estado BORRADOR");
        }

        Usuario usuarioActual = obtenerUsuarioActual();
        validarPermisoGestionBorrador(inventario, usuarioActual);
        validarConteoCiego(request.conteoCiego(), usuarioActual);
        Empleado responsable = empleadoRepository.findById(request.idResponsable())
                .filter(e -> Boolean.TRUE.equals(e.getEstado()))
                .orElseThrow(() -> new IllegalArgumentException("El responsable no existe o está inactivo"));
        Alcance alcance = resolverYValidarAlcance(request);
        validarConflicto(request.tipo(), alcance, idInventarioFisico);

        inventario.setTipo(request.tipo());
        inventario.setAlmacen(alcance.almacen());
        inventario.setUbicacion(alcance.ubicacion());
        inventario.setCategoria(alcance.categoria());
        inventario.getProductosSeleccionados().clear();
        inventario.getProductosSeleccionados().addAll(alcance.productos());
        inventario.setResponsable(responsable);
        inventario.setFechaProgramada(request.fechaProgramada());
        inventario.setConteoCiego(request.conteoCiego());
        inventario.setObservacion(limpiar(request.observacion()));
        inventarioRepository.saveAndFlush(inventario);
        return mapear(inventario);
    }

    @Override
    @Transactional
    public InventarioFisicoResponse iniciar(Long idInventarioFisico) {
        InventarioFisico inventario = buscar(idInventarioFisico);
        if (inventario.getEstado() != EstadoInventarioFisico.BORRADOR) {
            throw new IllegalStateException("Solo se puede iniciar un inventario en estado BORRADOR");
        }

        List<Existencia> existencias = buscarExistenciasDelAlcance(inventario);
        if (existencias.isEmpty()) {
            throw new IllegalStateException("No existen productos con stock para el alcance seleccionado");
        }

        List<DetalleInventarioFisico> detalles = existencias.stream()
                .map(existencia -> crearDetalle(inventario, existencia))
                .toList();
        detalleRepository.saveAll(detalles);

        inventario.setEstado(EstadoInventarioFisico.EN_CONTEO);
        inventario.setFechaInicio(LocalDateTime.now());
        return mapear(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioFisicoResponse obtener(Long idInventarioFisico) {
        return mapear(buscar(idInventarioFisico));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventarioFisicoListadoDto> listar(String buscar, EstadoInventarioFisico estado,
            Long idAlmacen, LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        if (fechaDesde != null && fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        return inventarioRepository.findAll(
                InventarioFisicoSpecification.filtrar(
                        buscar, estado, idAlmacen, fechaDesde, fechaHasta), pageable)
                .map(this::mapearListado);
    }

    @Override
    @Transactional(readOnly = true)
    public ConteoFisicoPaginaDto listarConteo(Long idInventarioFisico, String buscar,
            Boolean soloPendientes, Boolean soloDiferencias, Pageable pageable) {
        InventarioFisico inventario = buscar(idInventarioFisico);
        validarInventarioEnConteo(inventario);
        validarAccesoAlConteo(inventario, obtenerUsuarioActual());

        boolean ocultarValores = Boolean.TRUE.equals(inventario.getConteoCiego());
        Page<ConteoFisicoDetalleDto> detalles = detalleRepository.findAll(
                DetalleInventarioFisicoSpecification.filtrar(
                        idInventarioFisico, buscar, soloPendientes, soloDiferencias), pageable)
                .map(detalle -> mapearDetalleConteo(detalle, ocultarValores));

        ConteoFisicoResumenDto resumen = obtenerResumenConteo(idInventarioFisico);
        return new ConteoFisicoPaginaDto(
                inventario.getIdInventarioFisico(), inventario.getCodigo(), inventario.getEstado(),
                Boolean.TRUE.equals(inventario.getConteoCiego()), detalles,
                ocultarDiferenciasSiCorresponde(inventario, resumen));
    }

    @Override
    @Transactional
    public ConteoFisicoResumenDto guardarConteo(
            Long idInventarioFisico, GuardarConteoFisicoRequest request) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        validarInventarioEnConteo(inventario);
        validarAccesoAlConteo(inventario, obtenerUsuarioActual());

        if (request == null || request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos un producto contado");
        }

        List<Long> ids = request.detalles().stream()
                .map(GuardarConteoFisicoRequest.ItemConteo::idDetalle)
                .toList();
        if (ids.stream().anyMatch(java.util.Objects::isNull)) {
            throw new IllegalArgumentException("Todos los detalles deben tener identificador");
        }
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new IllegalArgumentException("No puede enviar el mismo detalle más de una vez");
        }

        List<DetalleInventarioFisico> detalles = detalleRepository.findAllById(ids);
        if (detalles.size() != ids.size()) {
            throw new IllegalArgumentException("Uno o más detalles del conteo no existen");
        }
        java.util.Map<Long, DetalleInventarioFisico> detallesPorId = detalles.stream()
                .collect(java.util.stream.Collectors.toMap(
                        DetalleInventarioFisico::getIdDetalleInventarioFisico, detalle -> detalle));
        LocalDateTime fechaGuardado = LocalDateTime.now();

        for (GuardarConteoFisicoRequest.ItemConteo item : request.detalles()) {
            DetalleInventarioFisico detalle = detallesPorId.get(item.idDetalle());
            if (!detalle.getInventarioFisico().getIdInventarioFisico().equals(idInventarioFisico)) {
                throw new IllegalArgumentException(
                        "El detalle " + item.idDetalle() + " no pertenece a este inventario físico");
            }
            validarCantidadFisica(item.cantidadFisica());
            detalle.setCantidadFisica(item.cantidadFisica());
            detalle.setDiferencia(item.cantidadFisica().subtract(detalle.getCantidadSistema()));
            detalle.setObservacion(limpiar(item.observacion()));
            detalle.setFechaConteo(fechaGuardado);
        }
        detalleRepository.saveAll(detalles);
        detalleRepository.flush();
        return ocultarDiferenciasSiCorresponde(
                inventario, obtenerResumenConteo(idInventarioFisico));
    }

    @Override
    @Transactional
    public FinalizarConteoFisicoResponse finalizarConteo(Long idInventarioFisico) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        validarInventarioEnConteo(inventario);

        Usuario usuarioActual = obtenerUsuarioActual();
        validarAccesoAlConteo(inventario, usuarioActual);

        ConteoFisicoResumenDto resumenActual = obtenerResumenConteo(idInventarioFisico);
        if (resumenActual.total() == 0) {
            throw new IllegalStateException("El inventario no contiene registros para finalizar");
        }
        if (resumenActual.pendientes() > 0) {
            throw new IllegalStateException(
                    "No se puede finalizar: existen " + resumenActual.pendientes()
                            + " registros pendientes de conteo");
        }

        List<DetalleInventarioFisico> detalles = detalleRepository
                .findByInventarioFisico_IdInventarioFisicoOrderByProducto_NombreAsc(idInventarioFisico);
        if (detalles.size() != resumenActual.total()) {
            throw new IllegalStateException(
                    "El detalle del inventario cambió durante la finalización; vuelva a intentarlo");
        }

        for (DetalleInventarioFisico detalle : detalles) {
            if (detalle.getCantidadFisica() == null) {
                throw new IllegalStateException(
                        "El producto " + detalle.getProducto().getNombre() + " todavía no fue contado");
            }
            validarCantidadFisica(detalle.getCantidadFisica());
            detalle.setDiferencia(
                    detalle.getCantidadFisica().subtract(detalle.getCantidadSistema()));
        }

        LocalDateTime fechaFinalizacion = LocalDateTime.now();
        inventario.setEstado(EstadoInventarioFisico.EN_REVISION);
        inventario.setFechaFinalizacion(fechaFinalizacion);
        inventario.setFinalizadoPor(usuarioActual);
        detalleRepository.saveAll(detalles);
        inventarioRepository.save(inventario);

        ConteoFisicoResumenDto resumenFinal = obtenerResumenConteo(idInventarioFisico);
        return new FinalizarConteoFisicoResponse(
                inventario.getIdInventarioFisico(), inventario.getCodigo(), inventario.getEstado(),
                inventario.getFechaFinalizacion(), usuarioActual.getUsername(), resumenFinal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConteoFisicoDetalleDto> listarDiferencias(Long idInventarioFisico, String buscar,
            Pageable pageable) {
        InventarioFisico inventario = buscar(idInventarioFisico);
        if (inventario.getEstado() != EstadoInventarioFisico.EN_REVISION) {
            throw new IllegalStateException("Las diferencias solo se revisan cuando el inventario está EN_REVISION");
        }
        validarAccesoAlConteo(inventario, obtenerUsuarioActual());
        return detalleRepository.findAll(
                DetalleInventarioFisicoSpecification.filtrar(idInventarioFisico, buscar, false, true), pageable)
                .map(detalle -> mapearDetalleConteo(detalle, false));
    }

    @Override
    @Transactional
    public InventarioFisicoEstadoResponse guardarDiferencias(Long idInventarioFisico,
            GuardarDiferenciasRequest request) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        if (inventario.getEstado() != EstadoInventarioFisico.EN_REVISION) {
            throw new IllegalStateException("El inventario no está disponible para revisión");
        }
        validarAccesoAlConteo(inventario, obtenerUsuarioActual());
        if (request == null || request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar al menos una diferencia");
        }

        Set<Long> ids = new HashSet<>();
        for (GuardarDiferenciasRequest.ItemDiferencia item : request.detalles()) {
            if (!ids.add(item.idDetalle())) {
                throw new IllegalArgumentException("No puede enviar la misma diferencia más de una vez");
            }
            DetalleInventarioFisico detalle = detalleRepository.findById(item.idDetalle())
                    .orElseThrow(() -> new IllegalArgumentException("El detalle de diferencia no existe"));
            validarDetalleDelInventario(detalle, idInventarioFisico);
            if (detalle.getDiferencia() == null || detalle.getDiferencia().signum() == 0) {
                throw new IllegalArgumentException("Solo se pueden revisar diferencias distintas de cero");
            }
            validarMotivo(item.motivo(), item.observacion());
            detalle.setMotivoDiferencia(item.motivo().trim());
            detalle.setObservacion(limpiar(item.observacion()));
            detalleRepository.save(detalle);
        }
        return estado(inventario, "Diferencias guardadas correctamente");
    }

    @Override
    @Transactional
    public InventarioFisicoEstadoResponse aprobar(Long idInventarioFisico) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        if (inventario.getEstado() != EstadoInventarioFisico.EN_REVISION) {
            throw new IllegalStateException("Solo se puede aprobar un inventario EN_REVISION");
        }
        Usuario usuario = obtenerUsuarioActual();
        if (!esAdministrador(usuario)) {
            throw new AccessDeniedException("Solo un administrador puede aprobar el inventario físico");
        }

        List<DetalleInventarioFisico> detalles = detalleRepository
                .findByInventarioFisico_IdInventarioFisicoOrderByProducto_NombreAsc(idInventarioFisico);
        List<DetalleInventarioFisico> diferencias = detalles.stream()
                .filter(d -> d.getDiferencia() != null && d.getDiferencia().signum() != 0).toList();
        for (DetalleInventarioFisico detalle : diferencias) {
            validarMotivo(detalle.getMotivoDiferencia(), detalle.getObservacion());
        }

        TipoMovimientoAlmacen entrada = buscarTipoMovimiento("AJUSTE_INVENTARIO");
        TipoMovimientoAlmacen salida = buscarTipoMovimiento("SALIDA_AJUSTE_INVENTARIO");
        String referencia = "INV_FISICO:" + inventario.getCodigo();
        for (DetalleInventarioFisico detalle : diferencias) {
            aplicarAjuste(detalle, usuario, detalle.getDiferencia().signum() > 0 ? entrada : salida, referencia);
        }
        inventario.setEstado(EstadoInventarioFisico.APROBADO);
        inventario.setFechaAprobacion(LocalDateTime.now());
        inventario.setAprobadoPor(usuario);
        inventarioRepository.save(inventario);
        return estado(inventario, "Inventario aprobado y existencias ajustadas correctamente");
    }

    @Override
    @Transactional
    public InventarioFisicoEstadoResponse cancelar(Long idInventarioFisico,
            CancelarInventarioFisicoRequest request) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        if (!ESTADOS_ABIERTOS.contains(inventario.getEstado())) {
            throw new IllegalStateException("El inventario ya no puede cancelarse en su estado actual");
        }
        Usuario usuario = obtenerUsuarioActual();
        validarAccesoAlConteo(inventario, usuario);
        if (request == null || request.motivo() == null || request.motivo().isBlank()) {
            throw new IllegalArgumentException("El motivo de cancelación es obligatorio");
        }
        inventario.setEstado(EstadoInventarioFisico.CANCELADO);
        inventario.setObservacion(limpiar(request.motivo()));
        inventarioRepository.save(inventario);
        return estado(inventario, "Inventario cancelado correctamente");
    }

    @Override
    @Transactional
    public void eliminarBorrador(Long idInventarioFisico) {
        InventarioFisico inventario = inventarioRepository.findByIdForUpdate(idInventarioFisico)
                .orElseThrow(() -> new InventarioFisicoNoEncontradoException(idInventarioFisico));
        if (inventario.getEstado() != EstadoInventarioFisico.BORRADOR) {
            throw new IllegalStateException("Solo se puede eliminar un inventario en estado BORRADOR");
        }
        Usuario usuario = obtenerUsuarioActual();
        boolean creador = inventario.getCreadoPor() != null
                && inventario.getCreadoPor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!creador && !esResponsable(inventario, usuario) && !esAdministrador(usuario)) {
            throw new AccessDeniedException("Solo el creador, responsable o administrador puede eliminar el borrador");
        }

        detalleRepository.deleteByInventarioFisico_IdInventarioFisico(idInventarioFisico);
        detalleRepository.flush();
        inventario.getProductosSeleccionados().clear();
        inventarioRepository.saveAndFlush(inventario);
        inventarioRepository.delete(inventario);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarActaPdf(Long idInventarioFisico) {
        InventarioFisico inventario = buscar(idInventarioFisico);
        if (inventario.getEstado() != EstadoInventarioFisico.APROBADO) {
            throw new IllegalStateException("El acta solo está disponible para inventarios APROBADOS");
        }
        Usuario usuario = obtenerUsuarioActual();
        validarAccesoInventario(inventario, usuario);
        List<DetalleInventarioFisico> detalles = detalleRepository
                .findByInventarioFisico_IdInventarioFisicoOrderByProducto_NombreAsc(idInventarioFisico);
        return pdfService.generar(inventario, detalles);
    }

    private void aplicarAjuste(DetalleInventarioFisico detalle, Usuario usuario,
            TipoMovimientoAlmacen tipo, String referencia) {
        Long idLote = detalle.getLote() == null ? null : detalle.getLote().getIdLote();
        Existencia existencia = existenciaRepository.buscarParaActualizar(
                        detalle.getProducto().getIdProducto(), detalle.getUbicacion().getIdUbicacion(), idLote)
                .orElseThrow(() -> new IllegalStateException("La existencia del producto ya no existe"));
        BigDecimal actual = existencia.getCantidad() == null ? BigDecimal.ZERO : existencia.getCantidad();
        if (actual.compareTo(detalle.getCantidadSistema()) != 0) {
            throw new IllegalStateException("El stock de " + detalle.getProducto().getNombre()
                    + " cambió después del conteo; no se puede aprobar automáticamente");
        }
        BigDecimal nuevo = detalle.getCantidadFisica();
        if (nuevo.signum() < 0) {
            throw new IllegalStateException("La cantidad física no puede ser negativa");
        }
        BigDecimal diferencia = nuevo.subtract(actual).abs();
        existencia.setCantidad(nuevo);
        existenciaRepository.save(existencia);

        DetalleMovimientoAlmacen movimientoDetalle = DetalleMovimientoAlmacen.builder()
                .producto(detalle.getProducto())
                .lote(detalle.getLote())
                .ubicacion(detalle.getUbicacion())
                .cantidad(diferencia)
                .saldoAnterior(actual)
                .saldoResultante(nuevo)
                .observacion("Inventario físico " + detalle.getInventarioFisico().getCodigo()
                        + " - " + detalle.getMotivoDiferencia())
                .build();
        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .tipoMovimiento(tipo)
                .usuario(usuario)
                .fechaMovimiento(LocalDateTime.now())
                .referencia(referencia)
                .observacion(detalle.getObservacion())
                .build();
        movimientoDetalle.setMovimiento(movimiento);
        movimiento.setDetalles(List.of(movimientoDetalle));
        movimientoRepository.save(movimiento);
    }

    private TipoMovimientoAlmacen buscarTipoMovimiento(String nombre) {
        return tipoMovimientoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el tipo de movimiento " + nombre));
    }

    private void validarMotivo(String motivo, String observacion) {
        if (motivo == null || motivo.isBlank() || motivo.length() > 100) {
            throw new IllegalArgumentException("El motivo de diferencia es obligatorio y no debe superar 100 caracteres");
        }
        if (("OTRO".equalsIgnoreCase(motivo.trim()) || "OTROS".equalsIgnoreCase(motivo.trim()))
                && (observacion == null || observacion.isBlank())) {
            throw new IllegalArgumentException("La observación es obligatoria cuando el motivo es OTRO");
        }
        if (observacion != null && observacion.length() > 500) {
            throw new IllegalArgumentException("La observación no debe superar 500 caracteres");
        }
    }

    private void validarDetalleDelInventario(DetalleInventarioFisico detalle, Long idInventarioFisico) {
        if (detalle.getInventarioFisico() == null
                || !idInventarioFisico.equals(detalle.getInventarioFisico().getIdInventarioFisico())) {
            throw new IllegalArgumentException("El detalle no pertenece al inventario físico indicado");
        }
    }

    private InventarioFisicoEstadoResponse estado(InventarioFisico inventario, String mensaje) {
        return new InventarioFisicoEstadoResponse(inventario.getIdInventarioFisico(), inventario.getCodigo(),
                inventario.getEstado(), LocalDateTime.now(), obtenerUsuarioActual().getUsername(), mensaje);
    }

    private Alcance resolverYValidarAlcance(CrearInventarioFisicoRequest request) {
        validarCamposPermitidos(request);
        Almacen almacen = null;
        UbicacionAlmacen ubicacion = null;
        Categoria categoria = null;
        List<Producto> productos = List.of();

        if (request.tipo() == TipoInventarioFisico.POR_ALMACEN
                || request.tipo() == TipoInventarioFisico.POR_UBICACION) {
            almacen = almacenRepository.findById(request.idAlmacen())
                    .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                    .orElseThrow(() -> new IllegalArgumentException("El almacén no existe o está inactivo"));
        }
        if (request.tipo() == TipoInventarioFisico.POR_UBICACION) {
            ubicacion = ubicacionRepository.findById(request.idUbicacion())
                    .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                    .orElseThrow(() -> new IllegalArgumentException("La ubicación no existe o está inactiva"));
            if (!ubicacion.getAlmacen().getIdAlmacen().equals(almacen.getIdAlmacen())) {
                throw new IllegalArgumentException("La ubicación no pertenece al almacén seleccionado");
            }
        }
        if (request.tipo() == TipoInventarioFisico.POR_CATEGORIA) {
            categoria = categoriaRepository.findById(request.idCategoria())
                    .filter(c -> Boolean.TRUE.equals(c.getEstado()))
                    .orElseThrow(() -> new IllegalArgumentException("La categoría no existe o está inactiva"));
        }
        if (request.tipo() == TipoInventarioFisico.PRODUCTOS) {
            Set<Long> idsUnicos = new HashSet<>(request.idsProductos());
            if (idsUnicos.size() != request.idsProductos().size()) {
                throw new IllegalArgumentException("La lista contiene productos repetidos");
            }
            productos = productoRepository.findAllById(idsUnicos);
            if (productos.size() != idsUnicos.size() || productos.stream().anyMatch(p -> !Boolean.TRUE.equals(p.getEstado()))) {
                throw new IllegalArgumentException("Uno o más productos no existen o están inactivos");
            }
        }
        return new Alcance(almacen, ubicacion, categoria, productos);
    }

    private void validarCamposPermitidos(CrearInventarioFisicoRequest request) {
        switch (request.tipo()) {
            case COMPLETO -> exigirAusentes(request, true, true, true, true);
            case POR_ALMACEN -> {
                exigir(request.idAlmacen(), "El almacén es obligatorio");
                exigirAusentes(request, false, true, true, true);
            }
            case POR_UBICACION -> {
                exigir(request.idAlmacen(), "El almacén es obligatorio");
                exigir(request.idUbicacion(), "La ubicación es obligatoria");
                exigirAusentes(request, false, false, true, true);
            }
            case POR_CATEGORIA -> {
                exigir(request.idCategoria(), "La categoría es obligatoria");
                exigirAusentes(request, true, true, false, true);
            }
            case PRODUCTOS -> {
                if (request.idsProductos() == null || request.idsProductos().isEmpty()) {
                    throw new IllegalArgumentException("Debe seleccionar al menos un producto");
                }
                exigirAusentes(request, true, true, true, false);
            }
        }
    }

    private void exigirAusentes(CrearInventarioFisicoRequest request, boolean almacen, boolean ubicacion,
            boolean categoria, boolean productos) {
        if (almacen && request.idAlmacen() != null || ubicacion && request.idUbicacion() != null
                || categoria && request.idCategoria() != null
                || productos && request.idsProductos() != null && !request.idsProductos().isEmpty()) {
            throw new IllegalArgumentException("Se enviaron campos que no corresponden al tipo de inventario");
        }
    }

    private void validarConflicto(TipoInventarioFisico tipo, Alcance alcance, Long idExcluir) {
        boolean conflicto = inventarioRepository.existeConflicto(ESTADOS_ABIERTOS,
                idExcluir,
                tipo == TipoInventarioFisico.COMPLETO,
                alcance.almacen() == null ? null : alcance.almacen().getIdAlmacen(),
                alcance.ubicacion() == null ? null : alcance.ubicacion().getIdUbicacion(),
                alcance.categoria() == null ? null : alcance.categoria().getIdCategoria());
        if (conflicto) {
            throw new InventarioFisicoConflictoException(
                    "Ya existe un inventario abierto que se superpone con el alcance seleccionado");
        }
    }

    private List<Existencia> buscarExistenciasDelAlcance(InventarioFisico inventario) {
        BigDecimal cero = BigDecimal.ZERO;
        return switch (inventario.getTipo()) {
            case COMPLETO -> existenciaRepository.findByCantidadGreaterThan(cero);
            case POR_ALMACEN -> existenciaRepository.findByUbicacion_Almacen_IdAlmacenAndCantidadGreaterThan(
                    inventario.getAlmacen().getIdAlmacen(), cero);
            case POR_UBICACION -> existenciaRepository.findByUbicacion_IdUbicacionAndCantidadGreaterThan(
                    inventario.getUbicacion().getIdUbicacion(), cero);
            case POR_CATEGORIA -> existenciaRepository
                    .findByProducto_Subcategoria_Categoria_IdCategoriaAndCantidadGreaterThan(
                            inventario.getCategoria().getIdCategoria(), cero);
            case PRODUCTOS -> existenciaRepository.findByProducto_IdProductoInAndCantidadGreaterThan(
                    inventario.getProductosSeleccionados().stream().map(Producto::getIdProducto).toList(), cero);
        };
    }

    private DetalleInventarioFisico crearDetalle(InventarioFisico inventario, Existencia existencia) {
        return DetalleInventarioFisico.builder()
                .inventarioFisico(inventario)
                .idExistenciaOrigen(existencia.getIdExistencia())
                .producto(existencia.getProducto())
                .almacen(existencia.getUbicacion().getAlmacen())
                .ubicacion(existencia.getUbicacion())
                .lote(existencia.getLote())
                .cantidadSistema(existencia.getCantidad())
                .build();
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Debe iniciar sesión para crear un inventario físico");
        }
        return usuarioRepository.findByUsernameWithRoles(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("No se encontró el usuario autenticado"));
    }

    private void validarConteoCiego(Boolean conteoCiego, Usuario usuario) {
        if (!esAdministrador(usuario) && Boolean.FALSE.equals(conteoCiego)) {
            throw new AccessDeniedException("Solo un administrador puede desactivar el conteo ciego");
        }
    }

    private void validarInventarioEnConteo(InventarioFisico inventario) {
        if (inventario.getEstado() != EstadoInventarioFisico.EN_CONTEO) {
            throw new IllegalStateException(
                    "El conteo solo puede consultarse o guardarse cuando el inventario está EN_CONTEO");
        }
    }

    private void validarAccesoAlConteo(InventarioFisico inventario, Usuario usuario) {
        if (!esResponsable(inventario, usuario) && !esAdministrador(usuario)) {
            throw new AccessDeniedException(
                    "Solo el responsable asignado o un administrador puede realizar este conteo");
        }
    }

    private boolean esResponsable(InventarioFisico inventario, Usuario usuario) {
        return usuario.getEmpleado() != null
                && inventario.getResponsable() != null
                && usuario.getEmpleado().getIdEmpleado().equals(inventario.getResponsable().getIdEmpleado());
    }

    private void validarPermisoGestionBorrador(InventarioFisico inventario, Usuario usuario) {
        boolean creador = inventario.getCreadoPor() != null
                && inventario.getCreadoPor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!creador && !esResponsable(inventario, usuario) && !esAdministrador(usuario)) {
            throw new AccessDeniedException(
                    "Solo el creador, responsable o administrador puede modificar el borrador");
        }
    }

    private void validarAccesoInventario(InventarioFisico inventario, Usuario usuario) {
        boolean creador = inventario.getCreadoPor() != null
                && inventario.getCreadoPor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!creador && !esResponsable(inventario, usuario) && !esAdministrador(usuario)) {
            throw new AccessDeniedException("No tiene permiso para descargar el acta de este inventario");
        }
    }

    private boolean esAdministrador(Usuario usuario) {
        return usuario.getUsuarioRoles().stream()
                .anyMatch(ur -> Boolean.TRUE.equals(ur.getEstado())
                        && Boolean.TRUE.equals(ur.getRol().getEstado())
                        && "ROLE_ADMIN".equals(ur.getRol().getNombre()));
    }

    private void validarCantidadFisica(BigDecimal cantidad) {
        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad física es obligatoria");
        }
        if (cantidad.signum() < 0) {
            throw new IllegalArgumentException("La cantidad física no puede ser negativa");
        }
        if (Math.max(cantidad.scale(), 0) > 3) {
            throw new IllegalArgumentException("La cantidad física no puede tener más de 3 decimales");
        }
        if (cantidad.precision() - Math.max(cantidad.scale(), 0) > 9) {
            throw new IllegalArgumentException("La cantidad física no puede superar 9 dígitos enteros");
        }
    }

    private ConteoFisicoDetalleDto mapearDetalleConteo(
            DetalleInventarioFisico detalle, boolean ocultarValores) {
        Producto producto = detalle.getProducto();
        return new ConteoFisicoDetalleDto(
                detalle.getIdDetalleInventarioFisico(),
                producto.getIdProducto(),
                producto.getCodigoInterno(),
                producto.getNombre(),
                producto.getUnidadBase() == null ? "--" : producto.getUnidadBase().getAbreviatura(),
                detalle.getAlmacen().getIdAlmacen(),
                detalle.getAlmacen().getNombre(),
                detalle.getUbicacion().getIdUbicacion(),
                detalle.getUbicacion().getNombre(),
                detalle.getLote() == null ? null : detalle.getLote().getIdLote(),
                detalle.getLote() == null ? null : detalle.getLote().getCodigoLote(),
                detalle.getLote() == null ? null : detalle.getLote().getFechaVencimiento(),
                ocultarValores ? null : detalle.getCantidadSistema(),
                detalle.getCantidadFisica(),
                ocultarValores ? null : detalle.getDiferencia(),
                detalle.getCantidadFisica() != null,
                detalle.getMotivoDiferencia(),
                detalle.getObservacion(),
                detalle.getFechaConteo());
    }

    private ConteoFisicoResumenDto obtenerResumenConteo(Long idInventarioFisico) {
        long total = detalleRepository.countByInventarioFisico_IdInventarioFisico(idInventarioFisico);
        long contados = detalleRepository.countContadosByInventario(idInventarioFisico);
        long pendientes = Math.max(total - contados, 0);
        long diferencias = detalleRepository.countDiferenciasByInventario(idInventarioFisico);
        int porcentaje = total == 0 ? 0 : (int) Math.round(contados * 100.0 / total);
        return new ConteoFisicoResumenDto(total, contados, pendientes, diferencias,
                porcentaje, detalleRepository.findUltimaFechaConteo(idInventarioFisico));
    }

    private ConteoFisicoResumenDto ocultarDiferenciasSiCorresponde(
            InventarioFisico inventario, ConteoFisicoResumenDto resumen) {
        if (!Boolean.TRUE.equals(inventario.getConteoCiego())
                || inventario.getEstado() != EstadoInventarioFisico.EN_CONTEO) {
            return resumen;
        }
        return new ConteoFisicoResumenDto(
                resumen.total(), resumen.contados(), resumen.pendientes(), null,
                resumen.porcentajeAvance(), resumen.ultimoGuardado());
    }

    private InventarioFisico buscar(Long id) {
        return inventarioRepository.findById(id).orElseThrow(() -> new InventarioFisicoNoEncontradoException(id));
    }

    private InventarioFisicoResponse mapear(InventarioFisico inventario) {
        long lineas = detalleRepository.countByInventarioFisico_IdInventarioFisico(inventario.getIdInventarioFisico());
        long productos = inventario.getEstado() == EstadoInventarioFisico.BORRADOR
                ? contarProductosEstimados(inventario)
                : detalleRepository.countProductosByInventario(inventario.getIdInventarioFisico());
        ConteoFisicoResumenDto resumen = obtenerResumenConteo(inventario.getIdInventarioFisico());
        boolean ocultarValores = Boolean.TRUE.equals(inventario.getConteoCiego())
                && inventario.getEstado() == EstadoInventarioFisico.EN_CONTEO;
        List<ConteoFisicoDetalleDto> detalles = detalleRepository
                .findByInventarioFisico_IdInventarioFisicoOrderByProducto_NombreAsc(inventario.getIdInventarioFisico())
                .stream().map(detalle -> mapearDetalleConteo(detalle, ocultarValores)).toList();
        return new InventarioFisicoResponse(
                inventario.getIdInventarioFisico(), inventario.getCodigo(), inventario.getTipo(), inventario.getEstado(),
                inventario.getFechaProgramada(), inventario.getConteoCiego(), inventario.getObservacion(),
                item(inventario.getResponsable()), item(inventario.getAlmacen()), item(inventario.getUbicacion()),
                item(inventario.getCategoria()), productos, lineas, inventario.getFechaCreacion(), inventario.getFechaInicio(),
                inventario.getFechaFinalizacion(), inventario.getFechaAprobacion(), item(inventario.getCreadoPor()),
                ocultarDiferenciasSiCorresponde(inventario, resumen), detalles,
                inventario.getProductosSeleccionados().stream().map(Producto::getIdProducto).toList());
    }

    private InventarioFisicoListadoDto mapearListado(InventarioFisico inventario) {
        Long id = inventario.getIdInventarioFisico();
        long totalProductos = inventario.getEstado() == EstadoInventarioFisico.BORRADOR
                ? contarProductosEstimados(inventario)
                : detalleRepository.countProductosByInventario(id);
        return new InventarioFisicoListadoDto(
                id,
                inventario.getCodigo(),
                inventario.getFechaProgramada(),
                inventario.getTipo(),
                etiquetaTipo(inventario.getTipo()),
                inventario.getAlmacen() == null ? "Todos los almacenes" : inventario.getAlmacen().getNombre(),
                item(inventario.getResponsable()).nombre(),
                detalleRepository.countContadosByInventario(id),
                totalProductos,
                detalleRepository.countDiferenciasByInventario(id),
                inventario.getEstado());
    }

    private String etiquetaTipo(TipoInventarioFisico tipo) {
        return switch (tipo) {
            case COMPLETO -> "Inventario completo";
            case POR_ALMACEN -> "Por almacén";
            case POR_UBICACION -> "Por ubicación";
            case POR_CATEGORIA -> "Por categoría";
            case PRODUCTOS -> "Productos seleccionados";
        };
    }

    private long contarProductosEstimados(InventarioFisico inventario) {
        return buscarExistenciasDelAlcance(inventario).stream()
                .map(e -> e.getProducto().getIdProducto())
                .distinct()
                .count();
    }

    private ItemSimpleDto item(Empleado empleado) {
        if (empleado == null) return null;
        Persona p = empleado.getPersona();
        String nombre = String.join(" ", p.getNombres(), nulo(p.getApellidoPaterno()), nulo(p.getApellidoMaterno())).trim();
        return new ItemSimpleDto(empleado.getIdEmpleado(), nombre.replaceAll("\\s+", " "));
    }

    private ItemSimpleDto item(Almacen almacen) {
        return almacen == null ? null : new ItemSimpleDto(almacen.getIdAlmacen(), almacen.getNombre());
    }

    private ItemSimpleDto item(UbicacionAlmacen ubicacion) {
        return ubicacion == null ? null : new ItemSimpleDto(ubicacion.getIdUbicacion(), ubicacion.getNombre());
    }

    private ItemSimpleDto item(Categoria categoria) {
        return categoria == null ? null : new ItemSimpleDto(categoria.getIdCategoria(), categoria.getNombre());
    }

    private ItemSimpleDto item(Usuario usuario) {
        return usuario == null ? null : new ItemSimpleDto(usuario.getIdUsuario(), usuario.getUsername());
    }

    private void exigir(Object valor, String mensaje) {
        if (valor == null) throw new IllegalArgumentException(mensaje);
    }

    private String codigoTemporal() {
        return "TMP-" + UUID.randomUUID().toString().substring(0, 16);
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String nulo(String valor) {
        return valor == null ? "" : valor;
    }

    private record Alcance(Almacen almacen, UbicacionAlmacen ubicacion, Categoria categoria,
            List<Producto> productos) {
    }
}
