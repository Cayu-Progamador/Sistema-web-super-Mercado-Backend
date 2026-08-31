package com.backendSupermercado.supermercasdo.modules.devoluciones.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.CrearDevolucionRequestDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.DevolucionProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.RecepcionDisponibleDevolucionDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DetalleDevolucionProveedor;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DetalleOrigenDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.DevolucionProveedor;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.EstadoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.entity.MotivoDevolucion;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.DetalleDevolucionProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.DevolucionProveedorRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.EstadoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.repository.MotivoDevolucionRepository;
import com.backendSupermercado.supermercasdo.modules.devoluciones.service.DevolucionProveedorService;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.MovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.Proveedor;
import com.backendSupermercado.supermercasdo.modules.proveedor.entity.TipoProveedor;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.RecepcionMercaderia;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.DetalleRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.RecepcionMercaderiaRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevolucionProveedorServiceImpl implements DevolucionProveedorService {

    private final DevolucionProveedorRepository devolucionRepository;
    private final DetalleDevolucionProveedorRepository detalleDevolucionRepository;
    private final EstadoDevolucionRepository estadoRepository;
    private final MotivoDevolucionRepository motivoRepository;
    private final RecepcionMercaderiaRepository recepcionRepository;
    private final DetalleRecepcionRepository detalleRecepcionRepository;
    private final UbicacionAlmacenRepository ubicacionRepository;
    private final ExistenciaRepository existenciaRepository;
    private final TipoMovimientoAlmacenRepository tipoMovimientoRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> listarEstados() {
        return estadoRepository.findAllByActivoTrueOrderByIdEstadoDevolucionAsc().stream()
                .map(EstadoDevolucion::getNombre)
                .toList();
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto crearBorrador(Long idUsuario, CrearDevolucionRequestDto request) {
        Usuario usuario = buscarUsuario(idUsuario);
        RecepcionMercaderia recepcion = recepcionRepository.findById(request.getIdRecepcion())
                .orElseThrow(() -> new EntityNotFoundException("Recepción no encontrada: " + request.getIdRecepcion()));
        EstadoDevolucion borrador = buscarEstado("BORRADOR");

        DevolucionProveedor devolucion = DevolucionProveedor.builder()
                .recepcion(recepcion)
                .usuario(usuario)
                .fechaDevolucion(LocalDateTime.now())
                .estadoDevolucion(borrador)
                .observacion(request.getObservacion())
                .detalles(new ArrayList<>())
                .build();

        Set<Long> detallesUsados = new HashSet<>();
        for (CrearDevolucionRequestDto.DetalleRequestDto detalleDto : request.getDetalles()) {
            if (!detallesUsados.add(detalleDto.getIdDetalleRecepcion())) {
                throw new IllegalArgumentException("No puede repetir el mismo detalle de recepción");
            }
            DetalleRecepcion detalleRecepcion = detalleRecepcionRepository.findById(detalleDto.getIdDetalleRecepcion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Detalle de recepción no encontrado: " + detalleDto.getIdDetalleRecepcion()));
            if (!detalleRecepcion.getRecepcion().getIdRecepcion().equals(recepcion.getIdRecepcion())) {
                throw new IllegalArgumentException("El producto no pertenece a la recepción seleccionada");
            }
            MotivoDevolucion motivo = motivoRepository.findById(detalleDto.getIdMotivoDevolucion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Motivo de devolución no encontrado: " + detalleDto.getIdMotivoDevolucion()));
            if (!Boolean.TRUE.equals(motivo.getActivo())) {
                throw new ResourceConflictException("El motivo de devolución está inactivo");
            }

            DetalleDevolucionProveedor detalle = DetalleDevolucionProveedor.builder()
                    .devolucion(devolucion)
                    .detalleRecepcion(detalleRecepcion)
                    .cantidad(detalleDto.getCantidad())
                    .cantidadReemplazada(BigDecimal.ZERO)
                    .motivoDevolucion(motivo)
                    .observacion(detalleDto.getObservacion())
                    .origenes(new ArrayList<>())
                    .build();

            mapearOrigenes(detalleDto, detalle);
            validarSumaOrigenes(detalle);
            devolucion.getDetalles().add(detalle);
        }

        return toDto(devolucionRepository.save(devolucion));
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto actualizarBorrador(
            Long idUsuario, Long idDevolucion, CrearDevolucionRequestDto request) {
        Usuario usuario = buscarUsuario(idUsuario);
        DevolucionProveedor devolucion = buscarDevolucion(idDevolucion);
        exigirEstado(devolucion, "BORRADOR");

        if (!devolucion.getRecepcion().getIdRecepcion().equals(request.getIdRecepcion())) {
            throw new ResourceConflictException("No se puede cambiar la recepción de una devolución existente");
        }

        Set<Long> detallesUsados = new HashSet<>();
        List<DetalleDevolucionProveedor> nuevosDetalles = new ArrayList<>();
        for (CrearDevolucionRequestDto.DetalleRequestDto detalleDto : request.getDetalles()) {
            if (!detallesUsados.add(detalleDto.getIdDetalleRecepcion())) {
                throw new IllegalArgumentException("No puede repetir el mismo detalle de recepción");
            }
            DetalleRecepcion detalleRecepcion = detalleRecepcionRepository.findById(detalleDto.getIdDetalleRecepcion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Detalle de recepción no encontrado: " + detalleDto.getIdDetalleRecepcion()));
            if (!detalleRecepcion.getRecepcion().getIdRecepcion().equals(request.getIdRecepcion())) {
                throw new IllegalArgumentException("El producto no pertenece a la recepción seleccionada");
            }
            MotivoDevolucion motivo = motivoRepository.findById(detalleDto.getIdMotivoDevolucion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Motivo de devolución no encontrado: " + detalleDto.getIdMotivoDevolucion()));
            if (!Boolean.TRUE.equals(motivo.getActivo())) {
                throw new ResourceConflictException("El motivo de devolución está inactivo");
            }

            DetalleDevolucionProveedor detalle = DetalleDevolucionProveedor.builder()
                    .devolucion(devolucion)
                    .detalleRecepcion(detalleRecepcion)
                    .cantidad(detalleDto.getCantidad())
                    .cantidadReemplazada(BigDecimal.ZERO)
                    .motivoDevolucion(motivo)
                    .observacion(detalleDto.getObservacion())
                    .origenes(new ArrayList<>())
                    .build();
            mapearOrigenes(detalleDto, detalle);
            validarSumaOrigenes(detalle);
            nuevosDetalles.add(detalle);
        }

        devolucion.setUsuario(usuario);
        devolucion.setObservacion(request.getObservacion());
        devolucion.getDetalles().clear();
        devolucion.getDetalles().addAll(nuevosDetalles);
        return toDto(devolucionRepository.save(devolucion));
    }

    private void mapearOrigenes(CrearDevolucionRequestDto.DetalleRequestDto request,
            DetalleDevolucionProveedor detalle) {
        Set<Long> ubicacionesUsadas = new HashSet<>();
        for (CrearDevolucionRequestDto.OrigenRequestDto origenDto : request.getOrigenes()) {
            if (!ubicacionesUsadas.add(origenDto.getIdUbicacion())) {
                throw new IllegalArgumentException("No puede repetir una ubicación en el mismo producto");
            }
            UbicacionAlmacen ubicacion = ubicacionRepository.findById(origenDto.getIdUbicacion())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Ubicación no encontrada: " + origenDto.getIdUbicacion()));
            validarUbicacionActiva(ubicacion);
            detalle.getOrigenes().add(DetalleOrigenDevolucion.builder()
                    .detalleDevolucion(detalle)
                    .ubicacion(ubicacion)
                    .cantidad(origenDto.getCantidad())
                    .build());
        }
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto confirmar(Long idUsuario, Long idDevolucion) {
        Usuario usuario = buscarUsuario(idUsuario);
        DevolucionProveedor devolucion = buscarDevolucion(idDevolucion);
        exigirEstado(devolucion, "BORRADOR");

        TipoMovimientoAlmacen tipoSalida = tipoMovimientoRepository.findByNombreIgnoreCase("SALIDA_DEVOLUCION")
                .orElseThrow(() -> new EntityNotFoundException(
                        "Tipo de movimiento no encontrado: SALIDA_DEVOLUCION"));
        MovimientoAlmacen movimiento = MovimientoAlmacen.builder()
                .tipoMovimiento(tipoSalida)
                .usuario(usuario)
                .devolucion(devolucion)
                .fechaMovimiento(LocalDateTime.now())
                .referencia("DEV-" + String.format("%05d", devolucion.getIdDevolucion()))
                .observacion(devolucion.getObservacion())
                .detalles(new ArrayList<>())
                .build();

        for (DetalleDevolucionProveedor detalle : devolucion.getDetalles()) {
            validarSumaOrigenes(detalle);
            validarLimiteRecibido(detalle);
            for (DetalleOrigenDevolucion origen : detalle.getOrigenes()) {
                descontarExistencia(detalle, origen, movimiento);
            }
        }
        if (movimiento.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La devolución no contiene cantidades para retirar");
        }

        movimientoRepository.save(movimiento);
        devolucion.setEstadoDevolucion(buscarEstado("CONFIRMADA"));
        return toDto(devolucionRepository.save(devolucion));
    }

    private void descontarExistencia(DetalleDevolucionProveedor detalle, DetalleOrigenDevolucion origen,
            MovimientoAlmacen movimiento) {
        DetalleRecepcion recepcion = detalle.getDetalleRecepcion();
        Long idProducto = recepcion.getDetallePedido().getProducto().getIdProducto();
        Long idLote = recepcion.getLote() != null ? recepcion.getLote().getIdLote() : null;
        validarUbicacionActiva(origen.getUbicacion());

        Existencia existencia = existenciaRepository.buscarParaActualizar(
                        idProducto, origen.getUbicacion().getIdUbicacion(), idLote)
                .orElseThrow(() -> new ResourceConflictException(
                        "No existe stock del producto en la ubicación " + origen.getUbicacion().getNombre()));
        if (existencia.getCantidad().compareTo(origen.getCantidad()) < 0) {
            throw new ResourceConflictException("Stock insuficiente en " + origen.getUbicacion().getNombre()
                    + ". Disponible: " + existencia.getCantidad().stripTrailingZeros().toPlainString());
        }
        existencia.setCantidad(existencia.getCantidad().subtract(origen.getCantidad()));

        movimiento.getDetalles().add(DetalleMovimientoAlmacen.builder()
                .movimiento(movimiento)
                .producto(recepcion.getDetallePedido().getProducto())
                .lote(recepcion.getLote())
                .ubicacion(origen.getUbicacion())
                .detalleRecepcion(recepcion)
                .cantidad(origen.getCantidad())
                .observacion("Devolución al proveedor")
                .build());
    }

    private void validarLimiteRecibido(DetalleDevolucionProveedor detalle) {
        BigDecimal devuelto = detalleDevolucionRepository
                .sumarDevueltoConfirmado(detalle.getDetalleRecepcion().getIdDetalleRecepcion());
        BigDecimal disponibleParaDevolver = detalle.getDetalleRecepcion().getCantidadRecibida().subtract(devuelto);
        if (detalle.getCantidad().compareTo(disponibleParaDevolver) > 0) {
            throw new ResourceConflictException("La cantidad a devolver excede lo recibido pendiente de devolución");
        }
    }

    private void validarSumaOrigenes(DetalleDevolucionProveedor detalle) {
        BigDecimal suma = detalle.getOrigenes().stream()
                .map(DetalleOrigenDevolucion::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (suma.compareTo(detalle.getCantidad()) != 0) {
            throw new IllegalArgumentException("La suma de las ubicaciones debe ser igual a la cantidad a devolver");
        }
    }

    private void validarUbicacionActiva(UbicacionAlmacen ubicacion) {
        if (!Boolean.TRUE.equals(ubicacion.getActivo()) || ubicacion.getAlmacen() == null
                || !Boolean.TRUE.equals(ubicacion.getAlmacen().getActivo())) {
            throw new ResourceConflictException("La ubicación o su almacén están inactivos");
        }
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto enviar(Long idDevolucion) {
        DevolucionProveedor devolucion = buscarDevolucion(idDevolucion);
        exigirEstado(devolucion, "CONFIRMADA");
        devolucion.setEstadoDevolucion(buscarEstado("ENVIADA"));
        return toDto(devolucionRepository.save(devolucion));
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto completar(Long idDevolucion) {
        DevolucionProveedor devolucion = buscarDevolucion(idDevolucion);
        String estado = nombreEstado(devolucion);
        if (!"ENVIADA".equals(estado) && !"EN_PROCESO".equals(estado)) {
            throw new ResourceConflictException("Solo se puede completar una devolución enviada o en proceso");
        }
        devolucion.setEstadoDevolucion(buscarEstado("COMPLETADA"));
        return toDto(devolucionRepository.save(devolucion));
    }

    @Override
    @Transactional
    public DevolucionProveedorResponseDto cancelar(Long idDevolucion) {
        DevolucionProveedor devolucion = buscarDevolucion(idDevolucion);
        exigirEstado(devolucion, "BORRADOR");
        devolucion.setEstadoDevolucion(buscarEstado("CANCELADA"));
        return toDto(devolucionRepository.save(devolucion));
    }

    @Override
    @Transactional(readOnly = true)
    public DevolucionProveedorResponseDto obtener(Long idDevolucion) {
        return toDto(buscarDevolucion(idDevolucion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DevolucionProveedorResponseDto> listar() {
        return devolucionRepository.findAllByOrderByFechaDevolucionDesc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecepcionDisponibleDevolucionDto> listarRecepcionesDisponibles() {
        List<RecepcionDisponibleDevolucionDto> resultado = new ArrayList<>();
        for (RecepcionMercaderia recepcion : recepcionRepository.findAll()) {
            String estado = recepcion.getEstadoRecepcion() != null
                    ? recepcion.getEstadoRecepcion().getNombre() : null;
            if ("ANULADA".equals(estado) || "RECHAZADA".equals(estado)) continue;

            RecepcionDisponibleDevolucionDto dto = new RecepcionDisponibleDevolucionDto();
            dto.setIdRecepcion(recepcion.getIdRecepcion());
            dto.setNumero("RCP-" + String.format("%05d", recepcion.getIdRecepcion()));
            dto.setFechaRecepcion(recepcion.getFechaRecepcion());
            if (recepcion.getPedido() != null) dto.setProveedor(nombreProveedor(recepcion.getPedido().getProveedor()));

            List<RecepcionDisponibleDevolucionDto.DetalleDisponibleDto> detalles = new ArrayList<>();
            for (DetalleRecepcion detalle : recepcion.getDetalles()) {
                BigDecimal yaDevuelto = detalleDevolucionRepository
                        .sumarDevueltoConfirmado(detalle.getIdDetalleRecepcion());
                BigDecimal limiteRecepcion = detalle.getCantidadRecibida().subtract(yaDevuelto).max(BigDecimal.ZERO);
                if (limiteRecepcion.compareTo(BigDecimal.ZERO) <= 0) continue;

                Long idProducto = detalle.getDetallePedido().getProducto().getIdProducto();
                List<Existencia> existencias = detalle.getLote() != null
                        ? existenciaRepository.findAllByProducto_IdProductoAndLote_IdLoteAndCantidadGreaterThan(
                                idProducto, detalle.getLote().getIdLote(), BigDecimal.ZERO)
                        : existenciaRepository.findAllByProducto_IdProductoAndLoteIsNullAndCantidadGreaterThan(
                                idProducto, BigDecimal.ZERO);
                existencias = existencias.stream()
                        .filter(e -> Boolean.TRUE.equals(e.getUbicacion().getActivo()))
                        .filter(e -> e.getUbicacion().getAlmacen() != null
                                && Boolean.TRUE.equals(e.getUbicacion().getAlmacen().getActivo()))
                        .toList();
                BigDecimal stockTotal = existencias.stream().map(Existencia::getCantidad)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                if (stockTotal.compareTo(BigDecimal.ZERO) <= 0) continue;

                RecepcionDisponibleDevolucionDto.DetalleDisponibleDto detalleDto =
                        new RecepcionDisponibleDevolucionDto.DetalleDisponibleDto();
                detalleDto.setIdDetalleRecepcion(detalle.getIdDetalleRecepcion());
                detalleDto.setIdProducto(idProducto);
                detalleDto.setProducto(detalle.getDetallePedido().getProducto().getNombre());
                detalleDto.setRecibido(detalle.getCantidadRecibida());
                detalleDto.setYaDevuelto(yaDevuelto);
                detalleDto.setDisponibleParaDevolver(limiteRecepcion.min(stockTotal));
                if (detalle.getLote() != null) {
                    detalleDto.setIdLote(detalle.getLote().getIdLote());
                    detalleDto.setLote(detalle.getLote().getCodigoLote());
                }
                detalleDto.setUbicaciones(existencias.stream().map(this::toUbicacionDisponible).toList());
                detalles.add(detalleDto);
            }
            if (!detalles.isEmpty()) {
                dto.setDetalles(detalles);
                resultado.add(dto);
            }
        }
        return resultado;
    }

    private RecepcionDisponibleDevolucionDto.UbicacionDisponibleDto toUbicacionDisponible(Existencia existencia) {
        RecepcionDisponibleDevolucionDto.UbicacionDisponibleDto dto =
                new RecepcionDisponibleDevolucionDto.UbicacionDisponibleDto();
        UbicacionAlmacen ubicacion = existencia.getUbicacion();
        dto.setIdUbicacion(ubicacion.getIdUbicacion());
        dto.setCodigo(ubicacion.getCodigo());
        dto.setNombre(ubicacion.getNombre());
        dto.setIdAlmacen(ubicacion.getAlmacen().getIdAlmacen());
        dto.setAlmacen(ubicacion.getAlmacen().getNombre());
        dto.setTipoAlmacen(ubicacion.getAlmacen().getTipoAlmacen() != null
                ? ubicacion.getAlmacen().getTipoAlmacen().getNombre() : null);
        dto.setDisponible(existencia.getCantidad());
        return dto;
    }

    private Usuario buscarUsuario(Long idUsuario) {
        if (idUsuario == null) throw new IllegalArgumentException("El usuario autenticado es obligatorio");
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + idUsuario));
    }

    private DevolucionProveedor buscarDevolucion(Long id) {
        return devolucionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Devolución no encontrada: " + id));
    }

    private EstadoDevolucion buscarEstado(String nombre) {
        return estadoRepository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new EntityNotFoundException("Estado de devolución no encontrado: " + nombre));
    }

    private void exigirEstado(DevolucionProveedor devolucion, String esperado) {
        if (!esperado.equals(nombreEstado(devolucion))) {
            throw new ResourceConflictException("La devolución debe estar en estado " + esperado);
        }
    }

    private String nombreEstado(DevolucionProveedor devolucion) {
        return devolucion.getEstadoDevolucion() != null ? devolucion.getEstadoDevolucion().getNombre() : null;
    }

    private DevolucionProveedorResponseDto toDto(DevolucionProveedor devolucion) {
        DevolucionProveedorResponseDto dto = new DevolucionProveedorResponseDto();
        dto.setIdDevolucion(devolucion.getIdDevolucion());
        dto.setNumero("DEV-" + String.format("%05d", devolucion.getIdDevolucion()));
        dto.setFechaDevolucion(devolucion.getFechaDevolucion());
        dto.setEstado(nombreEstado(devolucion));
        dto.setObservacion(devolucion.getObservacion());
        dto.setIdUsuario(devolucion.getUsuario() != null ? devolucion.getUsuario().getIdUsuario() : null);
        dto.setUsuario(devolucion.getUsuario() != null ? devolucion.getUsuario().getUsername() : null);
        dto.setCantidadTotal(BigDecimal.ZERO);

        RecepcionMercaderia recepcion = devolucion.getRecepcion();
        if (recepcion != null) {
            dto.setIdRecepcion(recepcion.getIdRecepcion());
            dto.setNumeroRecepcion("RCP-" + String.format("%05d", recepcion.getIdRecepcion()));
            if (recepcion.getPedido() != null) dto.setProveedor(nombreProveedor(recepcion.getPedido().getProveedor()));
        }

        List<DevolucionProveedorResponseDto.DetalleDto> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleDevolucionProveedor detalle : devolucion.getDetalles()) {
            DevolucionProveedorResponseDto.DetalleDto detalleDto = new DevolucionProveedorResponseDto.DetalleDto();
            detalleDto.setIdDetalleDevolucion(detalle.getIdDetalleDevolucion());
            detalleDto.setIdDetalleRecepcion(detalle.getDetalleRecepcion().getIdDetalleRecepcion());
            detalleDto.setCantidad(detalle.getCantidad());
            detalleDto.setObservacion(detalle.getObservacion());
            detalleDto.setIdMotivoDevolucion(detalle.getMotivoDevolucion().getIdMotivoDevolucion());
            detalleDto.setMotivo(detalle.getMotivoDevolucion().getNombre());
            var producto = detalle.getDetalleRecepcion().getDetallePedido().getProducto();
            detalleDto.setIdProducto(producto.getIdProducto());
            detalleDto.setProducto(producto.getNombre());
            if (detalle.getDetalleRecepcion().getLote() != null) {
                detalleDto.setIdLote(detalle.getDetalleRecepcion().getLote().getIdLote());
                detalleDto.setLote(detalle.getDetalleRecepcion().getLote().getCodigoLote());
            }
            detalleDto.setOrigenes(detalle.getOrigenes().stream().map(origen -> {
                DevolucionProveedorResponseDto.OrigenDto origenDto = new DevolucionProveedorResponseDto.OrigenDto();
                origenDto.setIdDetalleOrigen(origen.getIdDetalleOrigen());
                origenDto.setIdUbicacion(origen.getUbicacion().getIdUbicacion());
                origenDto.setUbicacion(origen.getUbicacion().getNombre());
                origenDto.setAlmacen(origen.getUbicacion().getAlmacen().getNombre());
                origenDto.setCantidad(origen.getCantidad());
                return origenDto;
            }).toList());
            detalles.add(detalleDto);
            total = total.add(detalle.getCantidad());
        }
        dto.setDetalles(detalles);
        dto.setCantidadTotal(total);
        return dto;
    }

    private String nombreProveedor(Proveedor proveedor) {
        if (proveedor == null) return null;
        if (proveedor.getTipoProveedor() == TipoProveedor.EMPRESA && proveedor.getEmpresa() != null) {
            String comercial = proveedor.getEmpresa().getNombreComercial();
            return comercial != null && !comercial.isBlank() ? comercial : proveedor.getEmpresa().getRazonSocial();
        }
        if (proveedor.getPersona() != null) {
            return proveedor.getPersona().getNombres() + " "
                    + (proveedor.getPersona().getApellidoPaterno() != null
                            ? proveedor.getPersona().getApellidoPaterno() : "");
        }
        return "Proveedor " + proveedor.getIdProveedor();
    }
}
