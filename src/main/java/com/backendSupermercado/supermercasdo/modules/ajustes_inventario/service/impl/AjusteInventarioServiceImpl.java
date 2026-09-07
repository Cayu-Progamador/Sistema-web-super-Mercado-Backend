package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service.impl;

import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.buscar;
import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.estado;
import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.fechaDesde;
import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.fechaHasta;
import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.motivo;
import static com.backendSupermercado.supermercasdo.modules.ajustes_inventario.specification.AjusteInventarioSpecification.tipo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioListadoDto;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioResponse;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CancelarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.CrearAjusteInventarioRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.RechazarAjusteRequest;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity.AjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.entity.DetalleAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.MotivoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.TipoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.repository.AjusteInventarioRepository;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.repository.DetalleAjusteInventarioRepository;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service.AjusteInventarioService;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service.ReporteAjusteInventarioPdfService;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.lotes.repository.LoteRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.MovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.DetalleRecepcionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AjusteInventarioServiceImpl implements AjusteInventarioService {

    private static final DateTimeFormatter CODIGO_FORMATO = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final AjusteInventarioRepository ajusteRepository;
    private final DetalleAjusteInventarioRepository detalleAjusteRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final UbicacionAlmacenRepository ubicacionRepository;
    private final ExistenciaRepository existenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteAjusteInventarioPdfService reportePdfService;
    private final TipoMovimientoAlmacenRepository tipoMovimientoRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final DetalleRecepcionRepository detalleRecepcionRepository;

    @Override
    @Transactional
    public AjusteInventarioResponse crear(CrearAjusteInventarioRequest request) {
        Usuario usuario = usuarioActual();
        validarRequest(request);

        AjusteInventario ajuste = AjusteInventario.builder()
                .codigo(generarCodigo())
                .tipo(request.tipo())
                .motivo(request.motivo())
                .estado(EstadoAjusteInventario.BORRADOR)
                .observacion(limpiar(request.observacion()))
                .fechaLevantamiento(request.fechaLevantamiento())
                .responsableLevantamiento(limpiar(request.responsableLevantamiento()))
                .lugarHallazgo(limpiar(request.lugarHallazgo()))
                .fechaHallazgo(request.fechaHallazgo())
                .encontradoPor(limpiar(request.encontradoPor()))
                .creadoPor(usuario)
                .fechaCreacion(LocalDateTime.now())
                .build();

        agregarDetalles(ajuste, request);
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public AjusteInventarioResponse actualizarBorrador(Long id, CrearAjusteInventarioRequest request) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        validarCreadorOAdministrador(ajuste, usuario);
        validarEstado(ajuste, EstadoAjusteInventario.BORRADOR);
        validarRequest(request);
        if (request.tipo() != ajuste.getTipo()) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de un borrador; elimine y cree un ajuste nuevo");
        }
        if (request.motivo() != ajuste.getMotivo()) {
            throw new IllegalArgumentException("No se puede cambiar el motivo de un borrador; elimine y cree un ajuste nuevo");
        }

        ajuste.setTipo(request.tipo());
        ajuste.setMotivo(request.motivo());
        ajuste.setObservacion(limpiar(request.observacion()));
        ajuste.setFechaLevantamiento(request.fechaLevantamiento());
        ajuste.setResponsableLevantamiento(limpiar(request.responsableLevantamiento()));
        ajuste.setLugarHallazgo(limpiar(request.lugarHallazgo()));
        ajuste.setFechaHallazgo(request.fechaHallazgo());
        ajuste.setEncontradoPor(limpiar(request.encontradoPor()));
        ajuste.getDetalles().clear();
        agregarDetalles(ajuste, request);
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional(readOnly = true)
    public AjusteInventarioResponse obtener(Long id) {
        return toResponse(ajusteRepository.findDetalleById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el ajuste de inventario indicado")));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(Long id) {
        return reportePdfService.generar(obtener(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AjusteInventarioListadoDto> listar(
            String buscarTexto, TipoAjusteInventario tipoAjuste, MotivoAjusteInventario motivoAjuste,
            EstadoAjusteInventario estadoAjuste, LocalDate fechaInicio, LocalDate fechaFin,
            Pageable pageable) {
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        var specification = Specification
                .where(buscar(buscarTexto))
                .and(tipo(tipoAjuste))
                .and(motivo(motivoAjuste))
                .and(estado(estadoAjuste))
                .and(fechaDesde(fechaInicio))
                .and(fechaHasta(fechaFin));
        return ajusteRepository.findAll(specification, pageable).map(this::toListado);
    }

    @Override
    @Transactional
    public AjusteInventarioResponse enviar(Long id) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        validarCreadorOAdministrador(ajuste, usuarioActual());
        validarEstado(ajuste, EstadoAjusteInventario.BORRADOR);
        if (ajuste.getDetalles().isEmpty()) throw new IllegalStateException("El ajuste no tiene productos");
        ajuste.setEstado(EstadoAjusteInventario.PENDIENTE_APROBACION);
        ajuste.setFechaEnvio(LocalDateTime.now());
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public AjusteInventarioResponse aprobar(Long id) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        exigirAdministrador(usuario);
        validarEstado(ajuste, EstadoAjusteInventario.PENDIENTE_APROBACION);
        ajuste.setEstado(EstadoAjusteInventario.APROBADO);
        ajuste.setAprobadoPor(usuario);
        ajuste.setFechaAprobacion(LocalDateTime.now());
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public AjusteInventarioResponse rechazar(Long id, RechazarAjusteRequest request) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        exigirAdministrador(usuario);
        validarEstado(ajuste, EstadoAjusteInventario.PENDIENTE_APROBACION);
        ajuste.setEstado(EstadoAjusteInventario.RECHAZADO);
        ajuste.setMotivoRechazo(limpiar(request.motivo()));
        ajuste.setAprobadoPor(usuario);
        ajuste.setFechaAprobacion(LocalDateTime.now());
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public AjusteInventarioResponse aplicar(Long id) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        exigirAdministrador(usuario);
        validarEstado(ajuste, EstadoAjusteInventario.APROBADO);

        TipoMovimientoAlmacen tipoMovimiento = buscarTipoMovimiento(
                ajuste.getTipo() == TipoAjusteInventario.ENTRADA
                        ? "AJUSTE_INVENTARIO" : "SALIDA_AJUSTE_INVENTARIO");
        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .tipoMovimiento(tipoMovimiento)
                .usuario(usuario)
                .fechaMovimiento(LocalDateTime.now())
                .referencia("AJUSTE:" + ajuste.getCodigo())
                .observacion(ajuste.getObservacion())
                .build();

        for (DetalleAjusteInventario detalle : ajuste.getDetalles()) {
            aplicarDetalle(ajuste, detalle, movimiento);
        }
        movimientoRepository.save(movimiento);
        ajuste.setEstado(EstadoAjusteInventario.APLICADO);
        ajuste.setAplicadoPor(usuario);
        ajuste.setFechaAplicacion(LocalDateTime.now());
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public AjusteInventarioResponse cancelar(Long id, CancelarAjusteRequest request) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        exigirAdministrador(usuario);
        if (ajuste.getEstado() != EstadoAjusteInventario.BORRADOR
                && ajuste.getEstado() != EstadoAjusteInventario.PENDIENTE_APROBACION) {
            throw new IllegalStateException("El ajuste no puede cancelarse en su estado actual");
        }
        ajuste.setEstado(EstadoAjusteInventario.CANCELADO);
        ajuste.setMotivoRechazo(limpiar(request.motivo()));
        ajuste.setFechaCancelacion(LocalDateTime.now());
        return toResponse(ajusteRepository.save(ajuste));
    }

    @Override
    @Transactional
    public void eliminarBorrador(Long id) {
        AjusteInventario ajuste = ajusteForUpdate(id);
        Usuario usuario = usuarioActual();
        validarCreadorOAdministrador(ajuste, usuario);
        validarEstado(ajuste, EstadoAjusteInventario.BORRADOR);
        ajusteRepository.delete(ajuste);
    }

    private void agregarDetalles(AjusteInventario ajuste, CrearAjusteInventarioRequest request) {
        Set<String> claves = new HashSet<>();
        for (CrearAjusteInventarioRequest.DetalleRequest item : request.detalles()) {
            if (item.cantidad() == null || item.cantidad().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            }
            if (item.costoUnitario() == null || item.costoUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El costo unitario es obligatorio y debe ser mayor que cero");
            }
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el producto " + item.idProducto()));
            if (request.motivo() == MotivoAjusteInventario.INVENTARIO_INICIAL
                    && existenciaRepository.existsByProducto_IdProducto(producto.getIdProducto())) {
                throw new IllegalArgumentException("El producto ya tiene una existencia registrada; para agregar stock use recepcion o un ajuste autorizado");
            }
            if (!Boolean.TRUE.equals(producto.getEstado())) {
                throw new IllegalArgumentException("El producto no esta activo: " + producto.getNombre());
            }
            UbicacionAlmacen ubicacion = ubicacionRepository.findById(item.idUbicacion())
                    .orElseThrow(() -> new EntityNotFoundException("No existe la ubicacion " + item.idUbicacion()));
            if (!Boolean.TRUE.equals(ubicacion.getActivo()) || !Boolean.TRUE.equals(ubicacion.getAlmacen().getActivo())) {
                throw new IllegalArgumentException("La ubicacion o el almacen no estan activos");
            }
            BigDecimal cantidadBase = convertirCantidadBase(producto, item);
            Lote lote = resolverLote(producto, item, request.tipo());
            validarMotivoDetalle(request, producto, lote, item);
            String clave = producto.getIdProducto() + "-" + (lote == null ? "0" : lote.getIdLote())
                    + "-" + ubicacion.getIdUbicacion();
            if (!claves.add(clave)) throw new IllegalArgumentException("No puede repetir producto, lote y ubicacion");

            BigDecimal costo = item.costoUnitario();
            if (request.motivo() == MotivoAjusteInventario.MERMA
                    || request.motivo() == MotivoAjusteInventario.PRODUCTO_VENCIDO) {
                var detalleRecepcion = lote == null
                        ? detalleRecepcionRepository.findFirstByDetallePedido_Producto_IdProductoOrderByRecepcion_FechaRecepcionDesc(producto.getIdProducto())
                        : detalleRecepcionRepository.findFirstByLote_IdLoteOrderByRecepcion_FechaRecepcionDesc(lote.getIdLote());
                costo = detalleRecepcion.map(d -> costoBase(d, producto))
                        .orElseGet(() -> lote == null
                                ? detalleAjusteRepository
                                        .findFirstByProducto_IdProductoAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
                                                producto.getIdProducto(), EstadoAjusteInventario.APLICADO)
                                        .map(DetalleAjusteInventario::getCostoUnitario).orElse(null)
                                : detalleAjusteRepository
                                        .findFirstByProducto_IdProductoAndLote_IdLoteAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
                                                producto.getIdProducto(), lote.getIdLote(), EstadoAjusteInventario.APLICADO)
                                        .map(DetalleAjusteInventario::getCostoUnitario).orElse(null));
                if (costo == null || costo.signum() <= 0) {
                    throw new IllegalArgumentException("El lote no tiene un costo de compra registrado");
                }
            }
            BigDecimal stock = existenciaRepository.buscarParaActualizar(producto.getIdProducto(),
                    ubicacion.getIdUbicacion(), lote == null ? null : lote.getIdLote())
                    .map(Existencia::getCantidad).orElse(BigDecimal.ZERO);
            if (request.tipo() == TipoAjusteInventario.SALIDA && stock.compareTo(cantidadBase) < 0) {
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre());
            }
            BigDecimal resultado = request.tipo() == TipoAjusteInventario.ENTRADA
                    ? stock.add(cantidadBase) : stock.subtract(cantidadBase);
            DetalleAjusteInventario detalle = DetalleAjusteInventario.builder()
                    .producto(producto).lote(lote).ubicacion(ubicacion)
                    .codigoLoteCapturado(item.codigoLote())
                    .fechaFabricacionCapturada(item.fechaFabricacion())
                    .fechaVencimientoCapturada(item.fechaVencimiento())
                    .stockAnterior(stock).cantidad(cantidadBase).stockResultante(resultado)
                    .costoUnitario(costo).valorTotal(costo.multiply(cantidadBase))
                    .tipoMerma(limpiar(item.tipoMerma())).causaMerma(limpiar(item.causaMerma()))
                    .build();
            ajuste.agregarDetalle(detalle);
        }
    }

    private BigDecimal convertirCantidadBase(Producto producto,
            CrearAjusteInventarioRequest.DetalleRequest item) {
        if (item.idPresentacion() == null && item.cantidadPresentacion() == null) {
            return item.cantidad();
        }
        if (item.idPresentacion() == null || item.cantidadPresentacion() == null) {
            throw new IllegalArgumentException("La presentacion y su cantidad deben enviarse juntas");
        }
        if (item.cantidadPresentacion().signum() <= 0) {
            throw new IllegalArgumentException("La cantidad de la presentacion debe ser mayor que cero");
        }
        if (producto.getPresentaciones() == null || producto.getPresentaciones().isEmpty()) {
            throw new IllegalArgumentException("El producto no tiene presentaciones configuradas");
        }
        var presentacion = producto.getPresentaciones().stream()
                .filter(p -> Objects.equals(p.getIdPresentacion(), item.idPresentacion()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("La presentacion no pertenece al producto"));
        if (!Boolean.TRUE.equals(presentacion.getEstado())) {
            throw new IllegalArgumentException("La presentacion seleccionada esta inactiva");
        }
        BigDecimal factor = presentacion.getFactorConversion();
        if (factor == null || factor.signum() <= 0) {
            throw new IllegalArgumentException("La presentacion no tiene un factor de conversion valido");
        }
        return item.cantidadPresentacion().multiply(factor).setScale(6, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }

    private Lote resolverLote(Producto producto, CrearAjusteInventarioRequest.DetalleRequest item,
            TipoAjusteInventario tipoAjuste) {
        if (!Boolean.TRUE.equals(producto.getControlaLote())) {
            if (item.idLote() != null || (item.codigoLote() != null && !item.codigoLote().isBlank())) {
                throw new IllegalArgumentException("El producto no controla lotes");
            }
            return null;
        }
        if (item.idLote() != null) {
            Lote lote = loteRepository.findById(item.idLote())
                    .orElseThrow(() -> new EntityNotFoundException("No existe el lote indicado"));
            if (!Objects.equals(lote.getProducto().getIdProducto(), producto.getIdProducto())) {
                throw new IllegalArgumentException("El lote no pertenece al producto seleccionado");
            }
            if (!Boolean.TRUE.equals(lote.getActivo())) {
                throw new IllegalArgumentException("El lote seleccionado esta inactivo");
            }
            validarFechasLote(producto, lote.getFechaFabricacion(), lote.getFechaVencimiento());
            return lote;
        }
        if (tipoAjuste == TipoAjusteInventario.SALIDA) {
            throw new IllegalArgumentException("Para una salida debe seleccionar un lote existente");
        }
        if (item.codigoLote() == null || item.codigoLote().isBlank()) {
            throw new IllegalArgumentException("El codigo de lote es obligatorio");
        }
        if (loteRepository.existsByProducto_IdProductoAndCodigoLote(producto.getIdProducto(), item.codigoLote().trim())) {
            throw new IllegalArgumentException("Ya existe un lote con ese codigo para el producto");
        }
        validarFechasLote(producto, item.fechaFabricacion(), item.fechaVencimiento());
        // El lote capturado queda pendiente en el detalle y se crea al aplicar el ajuste.
        return null;
    }

    private void validarMotivoDetalle(CrearAjusteInventarioRequest request, Producto producto, Lote lote,
            CrearAjusteInventarioRequest.DetalleRequest item) {
        if (request.motivo() == MotivoAjusteInventario.INVENTARIO_INICIAL
                && (request.fechaLevantamiento() == null || vacio(request.responsableLevantamiento()))) {
            throw new IllegalArgumentException("El inventario inicial requiere fecha y responsable");
        }
        if (request.motivo() == MotivoAjusteInventario.PRODUCTO_ENCONTRADO
                && (request.fechaHallazgo() == null || vacio(request.lugarHallazgo()) || vacio(request.encontradoPor()))) {
            throw new IllegalArgumentException("El producto encontrado requiere lugar, fecha y persona");
        }
        if (request.motivo() == MotivoAjusteInventario.MERMA
                && (vacio(item.tipoMerma()) || vacio(item.causaMerma()))) {
            throw new IllegalArgumentException("La merma requiere tipo y causa");
        }
        if (request.motivo() == MotivoAjusteInventario.MERMA
                && !Set.of("DANADO", "ROTO", "DERRAME", "DETERIORO", "OTRO")
                        .contains(item.tipoMerma().trim().toUpperCase())) {
            throw new IllegalArgumentException("El tipo de merma no es valido");
        }
        if (request.motivo() == MotivoAjusteInventario.PRODUCTO_VENCIDO) {
            if (lote == null || lote.getFechaVencimiento() == null) {
                throw new IllegalArgumentException("El producto vencido requiere un lote con vencimiento");
            }
            if (!lote.getFechaVencimiento().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("El lote seleccionado aun no esta vencido");
            }
        }
    }

    private void validarRequest(CrearAjusteInventarioRequest request) {
        if (request == null || request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto");
        }
        if (request.tipo() == null || request.motivo() == null) {
            throw new IllegalArgumentException("El tipo y el motivo son obligatorios");
        }
        boolean entrada = request.tipo() == TipoAjusteInventario.ENTRADA;
        if (entrada && request.motivo() != MotivoAjusteInventario.INVENTARIO_INICIAL
                && request.motivo() != MotivoAjusteInventario.PRODUCTO_ENCONTRADO) {
            throw new IllegalArgumentException("El motivo no corresponde a una entrada");
        }
        if (!entrada && request.motivo() != MotivoAjusteInventario.PRODUCTO_VENCIDO
                && request.motivo() != MotivoAjusteInventario.MERMA) {
            throw new IllegalArgumentException("El motivo no corresponde a una salida");
        }
        if (vacio(request.observacion())) throw new IllegalArgumentException("La observacion es obligatoria");
        if (request.fechaLevantamiento() != null && request.fechaLevantamiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de levantamiento no puede ser futura");
        }
        if (request.fechaHallazgo() != null && request.fechaHallazgo().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del hallazgo no puede ser futura");
        }
    }

    private void aplicarDetalle(AjusteInventario ajuste, DetalleAjusteInventario detalle,
            MovimientoAlmacen movimiento) {
        if (detalle.getLote() == null && detalle.getCodigoLoteCapturado() != null) {
            Producto producto = detalle.getProducto();
            if (loteRepository.existsByProducto_IdProductoAndCodigoLote(producto.getIdProducto(),
                    detalle.getCodigoLoteCapturado())) {
                throw new IllegalStateException("El lote capturado ya existe para el producto");
            }
            Lote lote = loteRepository.save(Lote.builder()
                    .producto(producto)
                    .codigoLote(detalle.getCodigoLoteCapturado())
                    .fechaFabricacion(detalle.getFechaFabricacionCapturada())
                    .fechaVencimiento(detalle.getFechaVencimientoCapturada())
                    .fechaIngreso(LocalDateTime.now())
                    .activo(true)
                    .build());
            detalle.setLote(lote);
        }
        Long idLote = detalle.getLote() == null ? null : detalle.getLote().getIdLote();
        Existencia existencia = existenciaRepository.buscarParaActualizar(detalle.getProducto().getIdProducto(),
                detalle.getUbicacion().getIdUbicacion(), idLote).orElse(null);
        if (existencia == null) {
            if (ajuste.getTipo() == TipoAjusteInventario.SALIDA) {
                throw new IllegalStateException("No existe stock para realizar la salida");
            }
            existencia = Existencia.builder().producto(detalle.getProducto()).ubicacion(detalle.getUbicacion())
                    .lote(detalle.getLote()).cantidad(BigDecimal.ZERO).build();
        }
        BigDecimal actual = existencia.getCantidad() == null ? BigDecimal.ZERO : existencia.getCantidad();
        if (ajuste.getTipo() == TipoAjusteInventario.SALIDA && actual.compareTo(detalle.getCantidad()) < 0) {
            throw new IllegalStateException("El stock cambio y ya no es suficiente para aplicar el ajuste");
        }
        BigDecimal nuevo = ajuste.getTipo() == TipoAjusteInventario.ENTRADA
                ? actual.add(detalle.getCantidad()) : actual.subtract(detalle.getCantidad());
        existencia.setCantidad(nuevo);
        existenciaRepository.save(existencia);
        detalle.setStockAnterior(actual);
        detalle.setStockResultante(nuevo);

        DetalleMovimientoAlmacen movimientoDetalle = DetalleMovimientoAlmacen.builder()
                .movimiento(movimiento).producto(detalle.getProducto()).lote(detalle.getLote())
                .ubicacion(detalle.getUbicacion()).cantidad(detalle.getCantidad())
                .saldoAnterior(actual).saldoResultante(nuevo)
                .observacion(detalle.getCausaMerma()).build();
        movimiento.getDetalles().add(movimientoDetalle);
    }

    private TipoMovimientoAlmacen buscarTipoMovimiento(String nombre) {
        return tipoMovimientoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalStateException("No existe el tipo de movimiento " + nombre));
    }

    private AjusteInventario ajusteForUpdate(Long id) {
        return ajusteRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el ajuste de inventario indicado"));
    }

    private Usuario usuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("No se pudo identificar al usuario autenticado");
        }
        return usuarioRepository.findByUsernameWithRoles(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("El usuario autenticado no existe"));
    }

    private boolean esAdministrador(Usuario usuario) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ADMIN".equals(a.getAuthority()));
    }

    private void exigirAdministrador(Usuario usuario) {
        if (!esAdministrador(usuario)) throw new AccessDeniedException("Solo un administrador puede realizar esta operacion");
    }

    private void validarCreadorOAdministrador(AjusteInventario ajuste, Usuario usuario) {
        if (!esAdministrador(usuario) && !Objects.equals(ajuste.getCreadoPor().getIdUsuario(), usuario.getIdUsuario())) {
            throw new AccessDeniedException("Solo el creador o un administrador puede modificar este ajuste");
        }
    }

    private void validarEstado(AjusteInventario ajuste, EstadoAjusteInventario esperado) {
        if (ajuste.getEstado() != esperado) throw new IllegalStateException("El ajuste no esta en estado " + esperado);
    }

    private String generarCodigo() {
        return "AJ-" + LocalDateTime.now().format(CODIGO_FORMATO);
    }

    private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
    private String limpiar(String valor) { return valor == null ? null : valor.trim(); }

    private BigDecimal costoBase(DetalleRecepcion detalle, Producto producto) {
        BigDecimal factor = producto.getPresentaciones() == null ? null : producto.getPresentaciones().stream()
                .filter(p -> p.getUnidadMedida() != null && detalle.getUnidadMedida() != null
                        && p.getUnidadMedida().getIdUnidadMedida().equals(detalle.getUnidadMedida().getIdUnidadMedida()))
                .map(p -> p.getFactorConversion()).filter(Objects::nonNull)
                .findFirst().orElse(BigDecimal.ONE);
        return detalle.getPrecioUnitario().divide(factor, 4, java.math.RoundingMode.HALF_UP);
    }


    private void validarFechasLote(Producto producto, LocalDate fabricacion, LocalDate vencimiento) {
        if (fabricacion != null && vencimiento != null && vencimiento.isBefore(fabricacion)) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fabricacion");
        }
        if (Boolean.TRUE.equals(producto.getControlaVencimiento()) && vencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
        }
        if (!Boolean.TRUE.equals(producto.getControlaVencimiento()) && vencimiento != null) {
            throw new IllegalArgumentException("El producto no controla fecha de vencimiento");
        }
    }

    private AjusteInventarioListadoDto toListado(AjusteInventario ajuste) {
        BigDecimal cantidad = ajuste.getDetalles().stream().map(DetalleAjusteInventario::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valor = ajuste.getDetalles().stream().map(DetalleAjusteInventario::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        DetalleAjusteInventario primero = ajuste.getDetalles().isEmpty() ? null : ajuste.getDetalles().get(0);
        return new AjusteInventarioListadoDto(ajuste.getIdAjuste(), ajuste.getCodigo(), ajuste.getTipo(), ajuste.getMotivo(),
                ajuste.getEstado(), ajuste.getFechaCreacion(), nombre(ajuste.getCreadoPor()), ajuste.getDetalles().size(),
                cantidad, valor, primero == null ? null : primero.getUbicacion().getAlmacen().getNombre(),
                primero == null ? null : primero.getUbicacion().getNombre());
    }

    private AjusteInventarioResponse toResponse(AjusteInventario ajuste) {
        List<AjusteInventarioResponse.DetalleResponse> detalles = ajuste.getDetalles().stream().map(detalle -> {
            Lote lote = detalle.getLote();
            return new AjusteInventarioResponse.DetalleResponse(detalle.getIdDetalle(), detalle.getProducto().getIdProducto(),
                    detalle.getProducto().getCodigoInterno(), detalle.getProducto().getNombre(),
                    detalle.getProducto().getUnidadBase().getAbreviatura(), lote == null ? null : lote.getIdLote(),
                    lote == null ? detalle.getCodigoLoteCapturado() : lote.getCodigoLote(),
                    lote == null ? detalle.getFechaFabricacionCapturada() : lote.getFechaFabricacion(),
                    lote == null ? detalle.getFechaVencimientoCapturada() : lote.getFechaVencimiento(),
                    detalle.getUbicacion().getAlmacen().getIdAlmacen(), detalle.getUbicacion().getAlmacen().getNombre(),
                    detalle.getUbicacion().getIdUbicacion(), detalle.getUbicacion().getNombre(), detalle.getStockAnterior(),
                    detalle.getCantidad(), detalle.getStockResultante(), precioVentaBase(detalle.getProducto()),
                    detalle.getCostoUnitario(), detalle.getValorTotal(),
                    detalle.getTipoMerma(), detalle.getCausaMerma());
        }).toList();
        BigDecimal cantidad = detalles.stream().map(AjusteInventarioResponse.DetalleResponse::cantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valor = detalles.stream().map(AjusteInventarioResponse.DetalleResponse::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AjusteInventarioResponse(ajuste.getIdAjuste(), ajuste.getCodigo(), ajuste.getTipo(), ajuste.getMotivo(),
                ajuste.getEstado(), ajuste.getObservacion(), nombre(ajuste.getCreadoPor()), nombre(ajuste.getAprobadoPor()),
                nombre(ajuste.getAplicadoPor()), ajuste.getFechaCreacion(), ajuste.getFechaAprobacion(),
                ajuste.getFechaAplicacion(), ajuste.getFechaLevantamiento(), ajuste.getResponsableLevantamiento(),
                ajuste.getLugarHallazgo(), ajuste.getFechaHallazgo(), ajuste.getEncontradoPor(), cantidad, valor, detalles);
    }

    private String nombre(Usuario usuario) { return usuario == null ? null : usuario.getUsername(); }

    private BigDecimal precioVentaBase(Producto producto) {
        if (producto.getPresentaciones() == null) return BigDecimal.ZERO;
        return producto.getPresentaciones().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()) && p.getPrecioVenta() != null)
                .sorted((a, b) -> Boolean.compare(
                                Boolean.TRUE.equals(b.getEsPrincipal()),
                                Boolean.TRUE.equals(a.getEsPrincipal())))
                .findFirst()
                .map(p -> {
                    BigDecimal factor = p.getFactorConversion();
                    return factor == null || factor.signum() <= 0
                            ? p.getPrecioVenta()
                            : p.getPrecioVenta().divide(factor, 6, java.math.RoundingMode.HALF_UP);
                })
                .orElse(BigDecimal.ZERO);
    }
}
