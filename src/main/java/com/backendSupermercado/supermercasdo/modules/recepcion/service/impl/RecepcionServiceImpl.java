package com.backendSupermercado.supermercasdo.modules.recepcion.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DetalleDevolucionProveedor;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DevolucionProveedor;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.EstadoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.MotivoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.DevolucionProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.EstadoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.MotivoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.lotes.repository.LoteRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.MovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.DetallePedidoProveedor;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.EstadoPedido;
import com.backendSupermercado.supermercasdo.modules.pedidos.entity.PedidoProveedor;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.DetallePedidoProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.EstadoPedidoRepository;
import com.backendSupermercado.supermercasdo.modules.pedidos.repository.PedidoRepository;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.ContactoEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.DireccionEmpresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Empresa;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.CrearRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DetalleRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DevolucionRecepcionDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DistribucionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.DistribuirRecepcionRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.LoteRequestDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.ProductoFaltanteDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionListadoDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.DetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.EmpresaDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.ProveedorPdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.RecepcionInnerDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.EstadoRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.RecepcionMercaderia;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.DetalleRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.EstadoRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.RecepcionMercaderiaRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.service.RecepcionPdfService;
import com.backendSupermercado.supermercasdo.modules.recepcion.service.RecepcionService;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.shared.specification.RecepcionSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecepcionServiceImpl implements RecepcionService {

    private final RecepcionMercaderiaRepository recepcionRepository;
    private final DetalleRecepcionRepository detalleRecepcionRepository;
    private final EstadoRecepcionRepository estadoRecepcionRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoProveedorRepository detallePedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoteRepository loteRepository;
    private final MotivoDevolucionRepository motivoDevolucionRepository;
    private final EstadoDevolucionRepository estadoDevolucionRepository;
    private final UbicacionAlmacenRepository ubicacionAlmacenRepository;
    private final ExistenciaRepository existenciaRepository;
    private final TipoMovimientoAlmacenRepository tipoMovimientoAlmacenRepository;
    private final MovimientoAlmacenRepository movimientoAlmacenRepository;
    private final DevolucionProveedorRepository devolucionProveedorRepository;
    private final RecepcionPdfService recepcionPdfService;

    @Value("${app.empresa.nombre}")
    private String empresaNombre;

    @Value("${app.empresa.nit}")
    private String empresaNit;

    @Value("${app.empresa.direccion}")
    private String empresaDireccion;

    @Value("${app.empresa.telefono}")
    private String empresaTelefono;

    @Value("${app.empresa.ciudad}")
    private String empresaCiudad;

    @Value("${app.empresa.logo}")
    private String empresaLogo;

    @Override
    @Transactional
    public RecepcionListadoDto crear(Long idUsuario, CrearRecepcionRequestDto dto) {
        validarRequest(dto);

        PedidoProveedor pedido = pedidoRepository.findById(dto.getIdPedido())
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con id: " + dto.getIdPedido()));

        DevolucionProveedor devolucionReemplazo = resolverDevolucionReemplazo(dto, pedido);

        String estadoPedidoActual = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
        if (devolucionReemplazo == null
                && !"ENVIADO".equals(estadoPedidoActual)
                && !"PARCIALMENTE_RECIBIDO".equals(estadoPedidoActual)) {
            throw new ResourceConflictException(
                    "El pedido debe estar ENVIADO o PARCIALMENTE_RECIBIDO para recibir mercadería (estado actual: "
                            + estadoPedidoActual + ")");
        }

        long enCurso = devolucionReemplazo == null ? recepcionRepository.countEnCursoPorPedido(pedido.getIdPedido()) : 0;
        if (enCurso > 0) {
            throw new ResourceConflictException("Ya existe una recepción en curso para este pedido");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));

        EstadoRecepcion estado = determinarEstadoRecepcion(totalAceptado(dto), totalDistribuido(dto));

        RecepcionMercaderia recepcion = RecepcionMercaderia.builder()
                .pedido(pedido)
                .usuario(usuario)
                .fechaRecepcion(LocalDateTime.now())
                .estadoRecepcion(estado)
                .observacion(dto.getObservacion())
                .detalles(new ArrayList<>())
                .devoluciones(new ArrayList<>())
                .build();

        recepcion = recepcionRepository.saveAndFlush(recepcion);

        List<DetalleRecepcion> detalles = mapearDetalles(dto, recepcion, usuario, devolucionReemplazo);
        recepcion.getDetalles().addAll(detalles);

        recepcion = recepcionRepository.save(recepcion);

        if (devolucionReemplazo != null) {
            actualizarDevolucionReemplazo(devolucionReemplazo, dto);
        }

        actualizarEstadoPedido(pedido);

        return toListadoDto(recepcion);
    }

    @Override
    @Transactional
    public RecepcionListadoDto anular(Long idUsuario, Long idRecepcion) {
        RecepcionMercaderia recepcion = recepcionRepository.findById(idRecepcion)
                .orElseThrow(() -> new EntityNotFoundException("Recepción no encontrada con id: " + idRecepcion));

        String estadoActual = recepcion.getEstadoRecepcion() != null
                ? recepcion.getEstadoRecepcion().getNombre() : null;

        if ("ANULADA".equals(estadoActual)) {
            throw new ResourceConflictException("La recepción " + recepcion.getIdRecepcion() + " ya está anulada");
        }
        if ("RECHAZADA".equals(estadoActual)) {
            throw new ResourceConflictException(
                    "No se puede anular una recepción rechazada; el rechazo ya fue gestionado con el proveedor");
        }

        BigDecimal distribuido = movimientoAlmacenRepository.sumarDistribuidoPorRecepcion(idRecepcion);
        if (distribuido != null && distribuido.compareTo(BigDecimal.ZERO) > 0) {
            throw new ResourceConflictException(
                    "No se puede anular una recepción ya distribuida. Debe anularse antes de distribuir la mercadería");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));

        if (recepcion.getDevoluciones() != null) {
            EstadoDevolucion estadoCancelada = estadoDevolucionRepository.findByNombreIgnoreCase("CANCELADA")
                    .orElseThrow(() -> new EntityNotFoundException("Estado de devolución no encontrado: CANCELADA"));
            for (DevolucionProveedor dev : recepcion.getDevoluciones()) {
                if (dev.getEstadoDevolucion() != null
                        && "EN_PROCESO".equals(dev.getEstadoDevolucion().getNombre())) {
                    dev.setEstadoDevolucion(estadoCancelada);
                }
            }
        }

        EstadoRecepcion estadoAnulada = estadoRecepcionRepository.findByNombreIgnoreCase("ANULADA")
                .orElseThrow(() -> new EntityNotFoundException("Estado de recepción no encontrado: ANULADA"));

        recepcion.setEstadoRecepcion(estadoAnulada);
        recepcion.setUsuarioAnulacion(usuario);
        recepcion.setFechaAnulacion(LocalDateTime.now());
        recepcionRepository.save(recepcion);

        actualizarEstadoPedido(recepcion.getPedido());

        return toListadoDto(recepcion);
    }

    @Override
    @Transactional
    public RecepcionListadoDto distribuir(Long idUsuario, Long idRecepcion, DistribuirRecepcionRequestDto dto) {
        RecepcionMercaderia recepcion = recepcionRepository.findById(idRecepcion)
                .orElseThrow(() -> new EntityNotFoundException("Recepción no encontrada con id: " + idRecepcion));

        String estado = recepcion.getEstadoRecepcion() != null ? recepcion.getEstadoRecepcion().getNombre() : null;
        if ("ANULADA".equals(estado)) {
            throw new ResourceConflictException("No se puede distribuir una recepción anulada");
        }
        if ("RECHAZADA".equals(estado)) {
            throw new ResourceConflictException("No se puede distribuir una recepción rechazada");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + idUsuario));

        TipoMovimientoAlmacen tipoEntrada = tipoMovimientoAlmacenRepository.findByNombreIgnoreCase("ENTRADA_COMPRA")
                .orElseThrow(() -> new EntityNotFoundException("Tipo de movimiento no encontrado: ENTRADA_COMPRA"));

        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .tipoMovimiento(tipoEntrada)
                .usuario(usuario)
                .recepcion(recepcion)
                .fechaMovimiento(LocalDateTime.now())
                .referencia("RCP-" + String.format("%05d", recepcion.getIdRecepcion()))
                .detalles(new ArrayList<>())
                .build();

        Map<Long, BigDecimal> yaDistribuido = cargarYaDistribuido(recepcion.getIdRecepcion());

        for (DistribuirRecepcionRequestDto.ItemDistribucionDto item : dto.getItems()) {
            DetalleRecepcion detalle = detalleRecepcionRepository.findById(item.getIdDetalleRecepcion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Detalle de recepción no encontrado con id: " + item.getIdDetalleRecepcion()));

            if (!detalle.getRecepcion().getIdRecepcion().equals(recepcion.getIdRecepcion())) {
                throw new IllegalArgumentException(
                        "El detalle de recepción " + item.getIdDetalleRecepcion() + " no pertenece a la recepción "
                                + recepcion.getIdRecepcion());
            }

            BigDecimal rechazado = rechazadoDeDetalle(detalle);
            BigDecimal aceptado = detalle.getCantidadRecibida().subtract(rechazado);
            if (aceptado.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El producto " + nombreProductoDe(detalle)
                        + " no tiene mercadería aceptada para distribuir");
            }

            BigDecimal sumaDistribucion = item.getDistribuciones() == null ? BigDecimal.ZERO
                    : item.getDistribuciones().stream()
                            .map(DistribucionRequestDto::getCantidad)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (sumaDistribucion.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Debe indicar al menos una cantidad para distribuir del producto "
                        + nombreProductoDe(detalle));
            }

            Long idDetalleRecepcion = detalle.getIdDetalleRecepcion();
            BigDecimal ya = yaDistribuido.getOrDefault(idDetalleRecepcion, BigDecimal.ZERO);
            BigDecimal disponible = aceptado.subtract(ya);
            if (sumaDistribucion.compareTo(disponible) > 0) {
                throw new IllegalArgumentException("La distribución del producto " + nombreProductoDe(detalle)
                        + " excede la cantidad disponible ("
                        + disponible.stripTrailingZeros().toPlainString() + ")");
            }

            Producto producto = detalle.getDetallePedido().getProducto();
            Lote lote = detalle.getLote();
            for (DistribucionRequestDto distDto : item.getDistribuciones()) {
                BigDecimal cantidad = distDto.getCantidad();
                if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) continue;
                UbicacionAlmacen ubicacion = ubicacionAlmacenRepository.findById(distDto.getIdUbicacion())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Ubicación de almacén no encontrada con id: " + distDto.getIdUbicacion()));
                agregarDistribucionExistencia(producto, lote, ubicacion, detalle, cantidad, movimiento);
            }

            yaDistribuido.put(idDetalleRecepcion, ya.add(sumaDistribucion));
        }

        if (!movimiento.getDetalles().isEmpty()) {
            movimientoAlmacenRepository.save(movimiento);
        }

        BigDecimal totalAceptado = totalAceptadoDeRecepcion(recepcion);
        BigDecimal totalDistribuido = movimientoAlmacenRepository.sumarDistribuidoPorRecepcion(recepcion.getIdRecepcion());
        recepcion.setEstadoRecepcion(determinarEstadoRecepcion(totalAceptado, totalDistribuido));
        recepcionRepository.save(recepcion);

        return toListadoDto(recepcion);
    }

    private BigDecimal totalAceptado(CrearRecepcionRequestDto dto) {
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (DetalleRecepcionRequestDto det : dto.getDetalles()) {
                if (det.getCantidadAceptada() != null) {
                    total = total.add(det.getCantidadAceptada());
                }
            }
        }
        return total;
    }

    private BigDecimal totalDistribuido(CrearRecepcionRequestDto dto) {
        BigDecimal total = BigDecimal.ZERO;
        if (dto.getDetalles() != null) {
            for (DetalleRecepcionRequestDto det : dto.getDetalles()) {
                if (det.getDistribuciones() != null) {
                    for (DistribucionRequestDto dist : det.getDistribuciones()) {
                        if (dist.getCantidad() != null) {
                            total = total.add(dist.getCantidad());
                        }
                    }
                }
            }
        }
        return total;
    }

    private EstadoRecepcion determinarEstadoRecepcion(BigDecimal totalAceptado, BigDecimal totalDistribuido) {
        String nombreEstado;
        if (totalAceptado.compareTo(BigDecimal.ZERO) <= 0) {
            nombreEstado = "RECHAZADA";
        } else if (totalDistribuido.compareTo(BigDecimal.ZERO) <= 0) {
            nombreEstado = "EN_REVISION";
        } else if (totalDistribuido.compareTo(totalAceptado) < 0) {
            nombreEstado = "PARCIAL";
        } else {
            nombreEstado = "COMPLETADA";
        }
        return estadoRecepcionRepository.findByNombreIgnoreCase(nombreEstado)
                .orElseThrow(() -> new EntityNotFoundException("Estado de recepción no encontrado: " + nombreEstado));
    }

    private BigDecimal rechazadoDeDetalle(DetalleRecepcion detalle) {
        if (detalle.getDevoluciones() == null || detalle.getDevoluciones().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalle.getDevoluciones().stream()
                .map(DetalleDevolucionProveedor::getCantidad)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalAceptadoDeRecepcion(RecepcionMercaderia recepcion) {
        BigDecimal total = BigDecimal.ZERO;
        if (recepcion.getDetalles() != null) {
            for (DetalleRecepcion det : recepcion.getDetalles()) {
                total = total.add(det.getCantidadRecibida().subtract(rechazadoDeDetalle(det)));
            }
        }
        return total;
    }

    private String nombreProductoDe(DetalleRecepcion detalle) {
        return detalle.getDetallePedido() != null && detalle.getDetallePedido().getProducto() != null
                ? detalle.getDetallePedido().getProducto().getNombre()
                : "desconocido";
    }

    private Map<Long, BigDecimal> cargarYaDistribuido(Long idRecepcion) {
        Map<Long, BigDecimal> mapa = new HashMap<>();
        List<Object[]> filas = movimientoAlmacenRepository.sumarDistribuidoPorDetalleRecepcion(idRecepcion);
        for (Object[] fila : filas) {
            Long idDetalleRecepcion = (Long) fila[0];
            BigDecimal cantidad = (BigDecimal) fila[1];
            mapa.put(idDetalleRecepcion, cantidad);
        }
        return mapa;
    }

    private void agregarDistribucionExistencia(Producto producto, Lote lote, UbicacionAlmacen ubicacion,
            DetalleRecepcion detalleRecepcion, BigDecimal cantidad, MovimientoAlmacen movimiento) {
        Existencia existencia;
        if (lote != null) {
            existencia = existenciaRepository
                    .findByProducto_IdProductoAndUbicacion_IdUbicacionAndLote_IdLote(
                            producto.getIdProducto(), ubicacion.getIdUbicacion(), lote.getIdLote())
                    .orElseGet(() -> existenciaRepository.save(Existencia.builder()
                            .producto(producto)
                            .ubicacion(ubicacion)
                            .lote(lote)
                            .cantidad(BigDecimal.ZERO)
                            .build()));
        } else {
            existencia = existenciaRepository
                    .findByProducto_IdProductoAndUbicacion_IdUbicacionAndLoteIsNull(
                            producto.getIdProducto(), ubicacion.getIdUbicacion())
                    .orElseGet(() -> existenciaRepository.save(Existencia.builder()
                            .producto(producto)
                            .ubicacion(ubicacion)
                            .lote(null)
                            .cantidad(BigDecimal.ZERO)
                            .build()));
        }
        existencia.setCantidad(existencia.getCantidad().add(cantidad));

        DetalleMovimientoAlmacen detalleMovimiento = DetalleMovimientoAlmacen.builder()
                .movimiento(movimiento)
                .producto(producto)
                .lote(lote)
                .ubicacion(ubicacion)
                .detalleRecepcion(detalleRecepcion)
                .cantidad(cantidad)
                .build();
        movimiento.getDetalles().add(detalleMovimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecepcionListadoDto> listar(String estado, String search, Long idProveedor,
            LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        Specification<RecepcionMercaderia> spec = Specification
                .where(RecepcionSpecification.estadoEqual(estado))
                .and(RecepcionSpecification.busquedaGeneral(search))
                .and(RecepcionSpecification.proveedorEqual(idProveedor))
                .and(RecepcionSpecification.fechaDesde(fechaDesde))
                .and(RecepcionSpecification.fechaHasta(fechaHasta));
        return recepcionRepository.findAll(spec, pageable)
                .map(this::toListadoDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return recepcionRepository.count();
        }
        return recepcionRepository.count(RecepcionSpecification.estadoEqual(estado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarEstados() {
        return estadoRecepcionRepository.findAllByActivoTrueOrderByIdEstadoRecepcionAsc()
                .stream()
                .map(EstadoRecepcion::getNombre)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportarPdf(Long id, String username) {
        RecepcionMercaderia recepcion = recepcionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepci\u00f3n no encontrada con id: " + id));
        RecepcionPdfDto pdfDto = toPdfDto(recepcion, username != null ? username : "Sistema");
        return recepcionPdfService.generarPdf(pdfDto);
    }

    private RecepcionPdfDto toPdfDto(RecepcionMercaderia recepcion, String generadoPor) {
        RecepcionPdfDto dto = new RecepcionPdfDto();
        dto.setEmpresa(mapearEmpresa());

        PedidoProveedor pedido = recepcion.getPedido();
        if (pedido != null && pedido.getProveedor() != null) {
            dto.setProveedor(mapearProveedor(pedido.getProveedor()));
        }

        RecepcionInnerDto rcp = new RecepcionInnerDto();
        rcp.setNumero("RCP-" + String.format("%05d", recepcion.getIdRecepcion()));
        rcp.setNumeroPedido(pedido != null ? "PED-" + String.format("%05d", pedido.getIdPedido()) : null);
        rcp.setFechaRecepcion(recepcion.getFechaRecepcion());
        rcp.setUsuario(recepcion.getUsuario() != null ? recepcion.getUsuario().getUsername() : null);
        rcp.setEstado(recepcion.getEstadoRecepcion() != null ? recepcion.getEstadoRecepcion().getNombre() : null);
        rcp.setObservacion(recepcion.getObservacion());
        rcp.setUsuarioAnulacion(recepcion.getUsuarioAnulacion() != null
                ? recepcion.getUsuarioAnulacion().getUsername() : null);
        rcp.setFechaAnulacion(recepcion.getFechaAnulacion());

        List<DetallePdfDto> detalles = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;
        BigDecimal totalAceptado = BigDecimal.ZERO;
        Map<Long, BigDecimal> distribuidoPorDetalle = cargarYaDistribuido(recepcion.getIdRecepcion());

        if (recepcion.getDetalles() != null) {
            for (DetalleRecepcion det : recepcion.getDetalles()) {
                DetallePdfDto detDto = new DetallePdfDto();
                detDto.setProducto(nombreProductoDe(det));
                detDto.setLote(det.getLote() != null ? det.getLote().getCodigoLote() : null);
                detDto.setVencimiento(det.getLote() != null && det.getLote().getFechaVencimiento() != null
                        ? det.getLote().getFechaVencimiento().toString() : null);
                detDto.setSolicitado(det.getDetallePedido() != null
                        ? det.getDetallePedido().getCantidadSolicitada() : null);
                detDto.setRecibido(det.getCantidadRecibida());

                BigDecimal rechazado = BigDecimal.ZERO;
                String motivo = null;
                if (det.getDevoluciones() != null && !det.getDevoluciones().isEmpty()) {
                    DetalleDevolucionProveedor dev = det.getDevoluciones().get(0);
                    rechazado = dev.getCantidad() != null ? dev.getCantidad() : BigDecimal.ZERO;
                    motivo = dev.getMotivoDevolucion() != null ? dev.getMotivoDevolucion().getNombre() : null;
                }
                detDto.setAceptado(det.getCantidadRecibida().subtract(rechazado));
                detDto.setRechazado(rechazado);
                detDto.setMotivo(motivo);
                detDto.setDistribuido(distribuidoPorDetalle.getOrDefault(det.getIdDetalleRecepcion(), BigDecimal.ZERO));
                detDto.setPrecio(det.getPrecioUnitario());
                BigDecimal subtotal = det.getCantidadRecibida().subtract(rechazado)
                        .multiply(det.getPrecioUnitario());
                detDto.setSubtotal(subtotal);

                totalGeneral = totalGeneral.add(subtotal);
                totalAceptado = totalAceptado.add(det.getCantidadRecibida().subtract(rechazado));
                detalles.add(detDto);
            }
        }
        rcp.setTotalProductos(detalles.size());
        rcp.setTotalGeneral(totalGeneral);
        rcp.setDistribucion(calcularEstadoDistribucion(recepcion.getIdRecepcion(), totalAceptado,
                rcp.getEstado()));
        dto.setRecepcion(rcp);
        dto.setDetalles(detalles);
        dto.setGeneradoPor(generadoPor);
        return dto;
    }

    private EmpresaDto mapearEmpresa() {
        EmpresaDto dto = new EmpresaDto();
        dto.setNombre(empresaNombre);
        dto.setNit(empresaNit);
        dto.setDireccion(empresaDireccion);
        dto.setTelefono(empresaTelefono);
        dto.setCiudad(empresaCiudad);
        dto.setLogoPath(empresaLogo);
        return dto;
    }

    private ProveedorPdfDto mapearProveedor(Proveedor proveedor) {
        ProveedorPdfDto dto = new ProveedorPdfDto();
        dto.setNombre(nombreProveedor(proveedor));

        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA && proveedor.getEmpresa() != null) {
            Empresa empresa = proveedor.getEmpresa();
            dto.setDocumento(empresa.getNit());
            dto.setDireccion(mapearDireccionEmpresa(empresa));
            if (empresa.getContactos() != null && !empresa.getContactos().isEmpty()) {
                ContactoEmpresa contacto = empresa.getContactos().get(0);
                dto.setTelefono(contacto.getTelefono());
                dto.setCorreo(contacto.getCorreo());
            }
        } else if (proveedor.getPersona() != null) {
            var persona = proveedor.getPersona();
            dto.setDocumento(persona.getCi());
            if (persona.getDireccion() != null) {
                var direccion = persona.getDireccion();
                String ciudad = direccion.getCiudad() != null ? direccion.getCiudad().getNombre() : null;
                dto.setDireccion(concatenarDireccion(direccion.getZona(), direccion.getCalle(),
                        direccion.getNumero(), ciudad));
            }
            if (persona.getContactos() != null && !persona.getContactos().isEmpty()) {
                var contacto = persona.getContactos().get(0);
                dto.setTelefono(contacto.getTelefono());
                dto.setCorreo(contacto.getCorreo());
            }
        }
        return dto;
    }

    private String mapearDireccionEmpresa(Empresa empresa) {
        DireccionEmpresa direccion = empresa.getDireccion();
        if (direccion == null) return null;
        String ciudad = direccion.getCiudad() != null ? direccion.getCiudad().getNombre() : null;
        return concatenarDireccion(direccion.getZona(), direccion.getCalle(), direccion.getNumero(), ciudad);
    }

    private String concatenarDireccion(String zona, String calle, String numero, String ciudad) {
        StringBuilder sb = new StringBuilder();
        if (zona != null && !zona.isBlank()) sb.append(zona);
        if (calle != null && !calle.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(calle);
        }
        if (numero != null && !numero.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(numero);
        }
        if (ciudad != null && !ciudad.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ciudad);
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoFaltanteDto> listarFaltantes() {
        List<ProductoFaltanteDto> resultado = new ArrayList<>();
        for (PedidoProveedor pedido : pedidoRepository.findAll()) {
            String estado = pedido.getEstadoPedido() != null ? pedido.getEstadoPedido().getNombre() : null;
            if (!"PARCIALMENTE_RECIBIDO".equals(estado)) {
                continue;
            }
            if (pedido.getDetalles() == null) continue;
            for (DetallePedidoProveedor det : pedido.getDetalles()) {
                BigDecimal recibido = detalleRecepcionRepository
                        .sumarRecibidoPorDetallePedido(det.getIdDetallePedido());
                BigDecimal faltante = det.getCantidadSolicitada().subtract(recibido);
                if (faltante.compareTo(BigDecimal.ZERO) <= 0) continue;

                ProductoFaltanteDto dto = new ProductoFaltanteDto();
                dto.setIdPedido(pedido.getIdPedido());
                dto.setIdDetallePedido(det.getIdDetallePedido());
                dto.setNumeroPedido("PED-" + String.format("%05d", pedido.getIdPedido()));
                dto.setProveedor(pedido.getProveedor() != null ? nombreProveedor(pedido.getProveedor()) : null);
                dto.setProducto(det.getProducto() != null ? det.getProducto().getNombre() : null);
                dto.setUnidad(det.getUnidadMedida() != null ? det.getUnidadMedida().getNombre() : null);
                dto.setSolicitado(det.getCantidadSolicitada());
                dto.setRecibido(recibido);
                dto.setFaltante(faltante);
                resultado.add(dto);
            }
        }
        resultado.sort(Comparator.comparing(ProductoFaltanteDto::getNumeroPedido));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionRecepcionDto> listarDevoluciones() {
        List<DevolucionProveedor> devoluciones = new ArrayList<>(devolucionProveedorRepository.findAll());
        devoluciones.sort(Comparator.comparing(DevolucionProveedor::getFechaDevolucion).reversed());

        List<DevolucionRecepcionDto> resultado = new ArrayList<>();
        for (DevolucionProveedor dev : devoluciones) {
            DevolucionRecepcionDto dto = new DevolucionRecepcionDto();
            dto.setIdDevolucion(dev.getIdDevolucion());
            RecepcionMercaderia recepcion = dev.getRecepcion();
            if (recepcion != null) {
                dto.setIdRecepcion(recepcion.getIdRecepcion());
                dto.setIdPedido(recepcion.getPedido() != null ? recepcion.getPedido().getIdPedido() : null);
                dto.setNumeroRecepcion("RCP-" + String.format("%05d", recepcion.getIdRecepcion()));
                if (recepcion.getPedido() != null && recepcion.getPedido().getProveedor() != null) {
                    dto.setProveedor(nombreProveedor(recepcion.getPedido().getProveedor()));
                }
            }
            dto.setFechaDevolucion(dev.getFechaDevolucion());
            dto.setEstado(dev.getEstadoDevolucion() != null ? dev.getEstadoDevolucion().getNombre() : null);
            dto.setObservacion(dev.getObservacion());

            List<DevolucionRecepcionDto.ItemDto> items = new ArrayList<>();
            if (dev.getDetalles() != null) {
                for (DetalleDevolucionProveedor det : dev.getDetalles()) {
                    DevolucionRecepcionDto.ItemDto item = new DevolucionRecepcionDto.ItemDto();
                    item.setProducto(det.getDetalleRecepcion() != null
                            ? nombreProductoDe(det.getDetalleRecepcion()) : null);
                    item.setCantidad(det.getCantidad());
                    item.setCantidadReemplazada(det.getCantidadReemplazada() != null
                            ? det.getCantidadReemplazada() : BigDecimal.ZERO);
                    item.setPendienteReemplazo(det.getCantidad().subtract(item.getCantidadReemplazada()));
                    item.setIdDetallePedido(det.getDetalleRecepcion() != null
                            ? det.getDetalleRecepcion().getDetallePedido().getIdDetallePedido() : null);
                    item.setMotivo(det.getMotivoDevolucion() != null
                            ? det.getMotivoDevolucion().getNombre() : null);
                    items.add(item);
                }
            }
            dto.setItems(items);
            resultado.add(dto);
        }
        return resultado;
    }

    private void validarRequest(CrearRecepcionRequestDto dto) {
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un detalle de recepción");
        }
    }

    private DevolucionProveedor resolverDevolucionReemplazo(CrearRecepcionRequestDto dto, PedidoProveedor pedido) {
        if (dto.getIdDevolucionReemplazo() == null) return null;

        DevolucionProveedor devolucion = devolucionProveedorRepository.findById(dto.getIdDevolucionReemplazo())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Devolución no encontrada con id: " + dto.getIdDevolucionReemplazo()));

        if (devolucion.getRecepcion() == null || devolucion.getRecepcion().getPedido() == null
                || !pedido.getIdPedido().equals(devolucion.getRecepcion().getPedido().getIdPedido())) {
            throw new IllegalArgumentException("La devolución no corresponde al pedido indicado");
        }
        if (devolucion.getEstadoDevolucion() == null
                || !"EN_PROCESO".equals(devolucion.getEstadoDevolucion().getNombre())) {
            throw new ResourceConflictException("La devolución ya fue resuelta o cancelada");
        }

        for (DetalleRecepcionRequestDto detalle : dto.getDetalles()) {
            DetalleDevolucionProveedor detalleDevolucion = devolucion.getDetalles().stream()
                    .filter(d -> d.getDetalleRecepcion().getDetallePedido().getIdDetallePedido()
                            .equals(detalle.getIdDetallePedido()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El producto recibido no pertenece a la devolución seleccionada"));
            BigDecimal reemplazada = detalleDevolucion.getCantidadReemplazada() != null
                    ? detalleDevolucion.getCantidadReemplazada() : BigDecimal.ZERO;
            BigDecimal pendiente = detalleDevolucion.getCantidad().subtract(reemplazada);
            if (detalle.getCantidadAceptada().compareTo(pendiente) > 0) {
                throw new IllegalArgumentException("La cantidad aceptada excede el reemplazo pendiente");
            }
        }
        return devolucion;
    }

    private void actualizarDevolucionReemplazo(DevolucionProveedor devolucion, CrearRecepcionRequestDto dto) {
        for (DetalleRecepcionRequestDto detalle : dto.getDetalles()) {
            DetalleDevolucionProveedor detalleDevolucion = devolucion.getDetalles().stream()
                    .filter(d -> d.getDetalleRecepcion().getDetallePedido().getIdDetallePedido()
                            .equals(detalle.getIdDetallePedido()))
                    .findFirst()
                    .orElseThrow();
            BigDecimal reemplazada = detalleDevolucion.getCantidadReemplazada() != null
                    ? detalleDevolucion.getCantidadReemplazada() : BigDecimal.ZERO;
            detalleDevolucion.setCantidadReemplazada(reemplazada.add(detalle.getCantidadAceptada()));
        }

        boolean completada = devolucion.getDetalles().stream().allMatch(det -> {
            BigDecimal reemplazada = det.getCantidadReemplazada() != null ? det.getCantidadReemplazada() : BigDecimal.ZERO;
            return reemplazada.compareTo(det.getCantidad()) >= 0;
        });
        if (completada) {
            EstadoDevolucion estadoCompletada = estadoDevolucionRepository.findByNombreIgnoreCase("COMPLETADA")
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Estado de devolución no encontrado: COMPLETADA"));
            devolucion.setEstadoDevolucion(estadoCompletada);
        }
        devolucionProveedorRepository.save(devolucion);
    }

    private List<DetalleRecepcion> mapearDetalles(CrearRecepcionRequestDto dto, RecepcionMercaderia recepcion,
            Usuario usuario, DevolucionProveedor devolucionReemplazo) {
        List<DetalleRecepcion> detalles = new ArrayList<>();

        for (DetalleRecepcionRequestDto detDto : dto.getDetalles()) {
            DetallePedidoProveedor detallePedido = detallePedidoRepository.findById(detDto.getIdDetallePedido())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Detalle de pedido no encontrado con id: " + detDto.getIdDetallePedido()));

            if (!detallePedido.getPedido().getIdPedido().equals(recepcion.getPedido().getIdPedido())) {
                throw new IllegalArgumentException(
                        "El detalle de pedido " + detDto.getIdDetallePedido() + " no pertenece al pedido "
                                + recepcion.getPedido().getIdPedido());
            }

            validarCantidades(detDto, detallePedido, devolucionReemplazo != null);

            Producto producto = detallePedido.getProducto();

            Lote lote = resolverLote(producto, detDto, detallePedido);

            BigDecimal cantidadRecibida = detDto.getCantidadRecibida();
            BigDecimal cantidadAceptada = detDto.getCantidadAceptada();
            BigDecimal cantidadRechazada = detDto.getCantidadRechazada();

            DetalleRecepcion detalle = DetalleRecepcion.builder()
                    .recepcion(recepcion)
                    .detallePedido(detallePedido)
                    .unidadMedida(detallePedido.getUnidadMedida())
                    .cantidadRecibida(cantidadRecibida)
                    .precioUnitario(detDto.getPrecioUnitario())
                    .lote(lote)
                    .devoluciones(new ArrayList<>())
                    .build();
            // La devolución referencia este detalle; debe existir antes de crearla.
            detalle = detalleRecepcionRepository.saveAndFlush(detalle);
            detalles.add(detalle);

            if (cantidadRechazada.compareTo(BigDecimal.ZERO) > 0) {
                crearDevolucion(recepcion, detalle, detDto, cantidadRechazada, usuario);
            }

            if (cantidadAceptada.compareTo(BigDecimal.ZERO) > 0) {
                actualizarInventario(recepcion, detalle, producto, lote, detDto, cantidadAceptada, usuario);
            }
        }

        return detalles;
    }

    private void validarCantidades(DetalleRecepcionRequestDto detDto, DetallePedidoProveedor detallePedido,
            boolean esReemplazo) {
        BigDecimal recibida = detDto.getCantidadRecibida();
        BigDecimal aceptada = detDto.getCantidadAceptada();
        BigDecimal rechazada = detDto.getCantidadRechazada();

        BigDecimal recibidoAnterior = detalleRecepcionRepository
                .sumarRecibidoPorDetallePedido(detallePedido.getIdDetallePedido());
        BigDecimal totalRecibido = recibidoAnterior.add(recibida);
        if (!esReemplazo && totalRecibido.compareTo(detallePedido.getCantidadSolicitada()) > 0) {
            throw new IllegalArgumentException("No se puede recibir más de lo solicitado para el producto "
                    + detallePedido.getProducto().getNombre() + " (solicitado: "
                    + detallePedido.getCantidadSolicitada().stripTrailingZeros().toPlainString() + ")");
        }

        BigDecimal suma = aceptada.add(rechazada);
        if (suma.compareTo(recibida) != 0) {
            throw new IllegalArgumentException(
                    "Aceptado + Rechazado debe ser igual a Recibido para el producto "
                            + detallePedido.getProducto().getNombre());
        }

        if (rechazada.compareTo(BigDecimal.ZERO) > 0 && detDto.getIdMotivoDevolucion() == null) {
            throw new IllegalArgumentException("Debe indicar el motivo de rechazo para el producto "
                    + detallePedido.getProducto().getNombre());
        }

        if (aceptada.compareTo(BigDecimal.ZERO) > 0) {
            if (detDto.getDistribuciones() != null && !detDto.getDistribuciones().isEmpty()) {
                BigDecimal totalDistribuido = detDto.getDistribuciones().stream()
                        .map(DistribucionRequestDto::getCantidad)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (totalDistribuido.compareTo(aceptada) > 0) {
                    throw new IllegalArgumentException("La suma de la distribución ("
                            + totalDistribuido.stripTrailingZeros().toPlainString()
                            + ") no puede exceder la cantidad aceptada ("
                            + aceptada.stripTrailingZeros().toPlainString()
                            + ") para el producto " + detallePedido.getProducto().getNombre());
                }
            }
        }
    }

    private Lote resolverLote(Producto producto, DetalleRecepcionRequestDto detDto,
            DetallePedidoProveedor detallePedido) {
        boolean controlaLote = Boolean.TRUE.equals(producto.getControlaLote());
        boolean controlaVencimiento = Boolean.TRUE.equals(producto.getControlaVencimiento());

        if (!controlaLote && !controlaVencimiento) {
            return null;
        }

        LoteRequestDto loteDto = detDto.getLote();
        if (loteDto == null || loteDto.getCodigoLote() == null || loteDto.getCodigoLote().isBlank()) {
            throw new IllegalArgumentException("El producto " + producto.getNombre()
                    + " requiere control de lote. Debe indicar el código de lote");
        }

        if (controlaVencimiento && loteDto.getFechaVencimiento() == null) {
            throw new IllegalArgumentException("El producto " + producto.getNombre()
                    + " requiere fecha de vencimiento");
        }

        if (loteDto.getFechaVencimiento() != null && loteDto.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento del producto "
                    + producto.getNombre() + " no puede ser anterior a hoy");
        }

        return loteRepository.findByProducto_IdProductoAndCodigoLote(producto.getIdProducto(), loteDto.getCodigoLote())
                .orElseGet(() -> loteRepository.save(Lote.builder()
                        .producto(producto)
                        .codigoLote(loteDto.getCodigoLote())
                        .fechaFabricacion(loteDto.getFechaFabricacion())
                        .fechaVencimiento(loteDto.getFechaVencimiento())
                        .fechaIngreso(LocalDateTime.now())
                        .activo(true)
                        .build()));
    }

    private void crearDevolucion(RecepcionMercaderia recepcion, DetalleRecepcion detalle,
            DetalleRecepcionRequestDto detDto, BigDecimal cantidadRechazada, Usuario usuario) {

        MotivoDevolucion motivo = motivoDevolucionRepository.findById(detDto.getIdMotivoDevolucion())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Motivo de devolución no encontrado con id: " + detDto.getIdMotivoDevolucion()));

        EstadoDevolucion estadoProceso = estadoDevolucionRepository.findByNombreIgnoreCase("EN_PROCESO")
                .orElseThrow(() -> new EntityNotFoundException("Estado de devolución no encontrado: EN_PROCESO"));

        DevolucionProveedor devolucion = DevolucionProveedor.builder()
                .recepcion(recepcion)
                .usuario(usuario)
                .fechaDevolucion(LocalDateTime.now())
                .estadoDevolucion(estadoProceso)
                .detalles(new ArrayList<>())
                .build();

        DetalleDevolucionProveedor detalleDevolucion = DetalleDevolucionProveedor.builder()
                .devolucion(devolucion)
                .detalleRecepcion(detalle)
                .cantidad(cantidadRechazada)
                .motivoDevolucion(motivo)
                .build();

        devolucion.getDetalles().add(detalleDevolucion);
        recepcion.getDevoluciones().add(devolucion);
        detalle.getDevoluciones().add(detalleDevolucion);
    }

    private void actualizarInventario(RecepcionMercaderia recepcion, DetalleRecepcion detalle, Producto producto,
            Lote lote, DetalleRecepcionRequestDto detDto, BigDecimal cantidadAceptada, Usuario usuario) {

        TipoMovimientoAlmacen tipoEntrada = tipoMovimientoAlmacenRepository.findByNombreIgnoreCase("ENTRADA_COMPRA")
                .orElseThrow(() -> new EntityNotFoundException("Tipo de movimiento no encontrado: ENTRADA_COMPRA"));

        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .tipoMovimiento(tipoEntrada)
                .usuario(usuario)
                .recepcion(recepcion)
                .fechaMovimiento(LocalDateTime.now())
                .referencia("RCP-" + String.format("%05d", recepcion.getIdRecepcion()))
                .detalles(new ArrayList<>())
                .build();

        for (DistribucionRequestDto distDto : detDto.getDistribuciones()) {
            BigDecimal cantidad = distDto.getCantidad();
            if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) continue;

            UbicacionAlmacen ubicacion = ubicacionAlmacenRepository.findById(distDto.getIdUbicacion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Ubicación de almacén no encontrada con id: " + distDto.getIdUbicacion()));

            agregarDistribucionExistencia(producto, lote, ubicacion, detalle, cantidad, movimiento);
        }

        if (!movimiento.getDetalles().isEmpty()) {
            movimientoAlmacenRepository.save(movimiento);
        }
    }

    private void actualizarEstadoPedido(PedidoProveedor pedido) {
        boolean completo = true;
        boolean algoRecibido = false;
        if (pedido.getDetalles() != null) {
            for (DetallePedidoProveedor det : pedido.getDetalles()) {
                BigDecimal recibido = detalleRecepcionRepository.sumarRecibidoPorDetallePedido(det.getIdDetallePedido());
                if (recibido.compareTo(BigDecimal.ZERO) > 0) {
                    algoRecibido = true;
                }
                if (recibido.compareTo(det.getCantidadSolicitada()) < 0) {
                    completo = false;
                }
            }
        }

        String nombreEstado;
        if (!algoRecibido) {
            nombreEstado = "ENVIADO";
        } else {
            nombreEstado = completo ? "COMPLETADO" : "PARCIALMENTE_RECIBIDO";
        }

        EstadoPedido estadoPedido = estadoPedidoRepository.findByNombreIgnoreCase(nombreEstado)
                .orElseThrow(() -> new EntityNotFoundException("Estado de pedido no encontrado: " + nombreEstado));
        pedido.setEstadoPedido(estadoPedido);
        pedidoRepository.save(pedido);
    }

    private RecepcionListadoDto toListadoDto(RecepcionMercaderia recepcion) {
        RecepcionListadoDto dto = new RecepcionListadoDto();
        dto.setIdRecepcion(recepcion.getIdRecepcion());
        dto.setNumero("RCP-" + String.format("%05d", recepcion.getIdRecepcion()));

        PedidoProveedor pedido = recepcion.getPedido();
        if (pedido != null) {
            dto.setIdPedido(pedido.getIdPedido());
            dto.setNumeroPedido("PED-" + String.format("%05d", pedido.getIdPedido()));
            if (pedido.getProveedor() != null) {
                dto.setIdProveedor(pedido.getProveedor().getIdProveedor());
                dto.setProveedor(nombreProveedor(pedido.getProveedor()));
            }
        }
        dto.setFechaRecepcion(recepcion.getFechaRecepcion());
        dto.setUsuario(recepcion.getUsuario() != null ? recepcion.getUsuario().getUsername() : null);
        dto.setEstado(recepcion.getEstadoRecepcion() != null ? recepcion.getEstadoRecepcion().getNombre() : null);
        dto.setObservacion(recepcion.getObservacion());

        List<RecepcionListadoDto.RecepcionDetalleListadoDto> detalles = new ArrayList<>();
        BigDecimal totalCantidad = BigDecimal.ZERO;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        BigDecimal totalAceptado = BigDecimal.ZERO;
        Map<Long, BigDecimal> distribuidoPorDetalle = cargarYaDistribuido(recepcion.getIdRecepcion());

        if (recepcion.getDetalles() != null) {
            for (DetalleRecepcion det : recepcion.getDetalles()) {
                RecepcionListadoDto.RecepcionDetalleListadoDto detDto = new RecepcionListadoDto.RecepcionDetalleListadoDto();
                detDto.setIdDetalleRecepcion(det.getIdDetalleRecepcion());
                detDto.setIdProducto(det.getDetallePedido() != null
                        ? det.getDetallePedido().getProducto().getIdProducto() : null);
                detDto.setProducto(det.getDetallePedido() != null
                        ? det.getDetallePedido().getProducto().getNombre() : null);
                detDto.setSolicitado(det.getDetallePedido() != null
                        ? det.getDetallePedido().getCantidadSolicitada() : null);
                detDto.setRecibido(det.getCantidadRecibida());

                BigDecimal rechazado = BigDecimal.ZERO;
                String motivo = null;
                if (det.getDevoluciones() != null && !det.getDevoluciones().isEmpty()) {
                    DetalleDevolucionProveedor dev = det.getDevoluciones().get(0);
                    rechazado = dev.getCantidad() != null ? dev.getCantidad() : BigDecimal.ZERO;
                    motivo = dev.getMotivoDevolucion() != null ? dev.getMotivoDevolucion().getNombre() : null;
                }
                detDto.setAceptado(det.getCantidadRecibida().subtract(rechazado));
                detDto.setRechazado(rechazado);
                detDto.setDistribuido(distribuidoPorDetalle.getOrDefault(det.getIdDetalleRecepcion(), BigDecimal.ZERO));
                detDto.setMotivo(motivo);
                detDto.setPrecio(det.getPrecioUnitario());
                BigDecimal subtotal = det.getCantidadRecibida().subtract(rechazado)
                        .multiply(det.getPrecioUnitario());
                detDto.setSubtotal(subtotal);
                detDto.setLote(det.getLote() != null ? det.getLote().getCodigoLote() : null);
                detDto.setVencimiento(det.getLote() != null && det.getLote().getFechaVencimiento() != null
                        ? det.getLote().getFechaVencimiento().toString() : null);

                totalCantidad = totalCantidad.add(det.getCantidadRecibida());
                totalGeneral = totalGeneral.add(subtotal);
                totalAceptado = totalAceptado.add(det.getCantidadRecibida().subtract(rechazado));
                detalles.add(detDto);
            }
        }
        dto.setDetalle(detalles);
        dto.setTotalCantidad(totalCantidad);
        dto.setTotalGeneral(totalGeneral);
        dto.setDistribucion(calcularEstadoDistribucion(recepcion.getIdRecepcion(), totalAceptado,
                recepcion.getEstadoRecepcion() != null ? recepcion.getEstadoRecepcion().getNombre() : null));
        return dto;
    }

    private String calcularEstadoDistribucion(Long idRecepcion, BigDecimal totalAceptado,
            String estadoRecepcion) {
        if ("ANULADA".equals(estadoRecepcion) || "RECHAZADA".equals(estadoRecepcion)) {
            return "NO_APLICA";
        }
        BigDecimal distribuido = movimientoAlmacenRepository.sumarDistribuidoPorRecepcion(idRecepcion);
        if (distribuido == null || distribuido.compareTo(BigDecimal.ZERO) == 0) {
            return "PENDIENTE_DE_DISTRIBUIR";
        }
        if (distribuido.compareTo(totalAceptado) < 0) {
            return "PARCIALMENTE_DISTRIBUIDA";
        }
        return "DISTRIBUIDA";
    }

    private String nombreProveedor(Proveedor proveedor) {
        if (proveedor == null) return null;
        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA
                && proveedor.getEmpresa() != null) {
            var empresa = proveedor.getEmpresa();
            return empresa.getNombreComercial() != null && !empresa.getNombreComercial().isBlank()
                    ? empresa.getNombreComercial()
                    : empresa.getRazonSocial();
        }
        if (proveedor.getPersona() != null) {
            var persona = proveedor.getPersona();
            String nombre = (persona.getNombres() != null ? persona.getNombres() : "")
                    + " " + (persona.getApellidoPaterno() != null ? persona.getApellidoPaterno() : "")
                    + " " + (persona.getApellidoMaterno() != null ? persona.getApellidoMaterno() : "");
            return nombre.trim().replaceAll("\\s+", " ");
        }
        return null;
    }
}
