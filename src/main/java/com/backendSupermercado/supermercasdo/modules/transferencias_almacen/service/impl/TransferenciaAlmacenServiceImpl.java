package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.persistence.EntityNotFoundException;

import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.TransferenciaListadoDto;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.TransferenciaAlmacen;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository.TransferenciaAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service.TransferenciaAlmacenService;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.specification.TransferenciaSpecification;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.dto.CrearTransferenciaRequest;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.DetalleTransferencia;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.EstadoTransferencia;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.repository.EstadoTransferenciaRepository;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.repository.UbicacionAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.productos.repository.ProductoRepository;
import com.backendSupermercado.supermercasdo.modules.lotes.repository.LoteRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.productos.entity.Producto;
import com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferenciaAlmacenServiceImpl implements TransferenciaAlmacenService {
    private final TransferenciaAlmacenRepository repository;
    private final EstadoTransferenciaRepository estadoRepository;
    private final UbicacionAlmacenRepository ubicacionRepository;
    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final ExistenciaRepository existenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public TransferenciaListadoDto crear(CrearTransferenciaRequest request, boolean borrador) {
        UbicacionAlmacen origen = request.idUbicacionOrigen() == null ? null : ubicacionRepository.findById(request.idUbicacionOrigen()).orElseThrow(() -> new EntityNotFoundException("Ubicación origen no encontrada"));
        UbicacionAlmacen destino = request.idUbicacionDestino() == null ? null : ubicacionRepository.findById(request.idUbicacionDestino()).orElseThrow(() -> new EntityNotFoundException("Ubicación destino no encontrada"));
        if (!borrador) {
            if (origen == null || destino == null) throw new IllegalArgumentException("Origen y destino son obligatorios");
            if (origen.getIdUbicacion().equals(destino.getIdUbicacion())) throw new IllegalArgumentException("Origen y destino deben ser diferentes");
        }
        String username = SecurityContextHolder.getContext().getAuthentication() == null ? "Sistema" : SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));
        String estadoNombre = borrador ? "BORRADOR" : "PENDIENTE";
        EstadoTransferencia estado = estadoRepository.findByNombreIgnoreCase(estadoNombre).orElseThrow(() -> new EntityNotFoundException("Estado de transferencia no encontrado: " + estadoNombre));
        TransferenciaAlmacen transferencia = TransferenciaAlmacen.builder().codigo("TRF-" + String.format("%05d", repository.count() + 1)).ubicacionOrigen(origen).ubicacionDestino(destino).estadoTransferencia(estado).usuario(usuario).fechaTransferencia(LocalDateTime.now()).observacion(request.observacion()).build();
        if (request.detalles() != null) for (CrearTransferenciaRequest.DetalleRequest item : request.detalles()) {
            if (item.idProducto() == null || item.cantidad() == null) { if (!borrador) throw new IllegalArgumentException("Producto y cantidad son obligatorios"); else continue; }
            if (item.cantidad().signum() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            Producto producto = productoRepository.findById(item.idProducto()).orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
            Lote lote = item.idLote() == null ? null : loteRepository.findById(item.idLote()).orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));
            if (!borrador) { var existencia = existenciaRepository.buscarParaActualizar(producto.getIdProducto(), origen.getIdUbicacion(), item.idLote()).orElseThrow(() -> new IllegalArgumentException("No existe stock para el producto en el origen")); if (existencia.getCantidad().compareTo(item.cantidad()) < 0) throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre()); }
            transferencia.getDetalles().add(DetalleTransferencia.builder().transferencia(transferencia).producto(producto).lote(lote).cantidad(item.cantidad()).build());
        }
        return toDto(repository.save(transferencia));
    }

    @Override
    @Transactional
    public TransferenciaListadoDto actualizarBorrador(Long id, CrearTransferenciaRequest request) {
        TransferenciaAlmacen transferencia = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transferencia no encontrada"));
        if (!"BORRADOR".equalsIgnoreCase(transferencia.getEstadoTransferencia().getNombre())) {
            throw new IllegalStateException("Solo se pueden editar transferencias en borrador");
        }
        UbicacionAlmacen origen = request.idUbicacionOrigen() == null ? null
                : ubicacionRepository.findById(request.idUbicacionOrigen())
                        .orElseThrow(() -> new EntityNotFoundException("Ubicación origen no encontrada"));
        UbicacionAlmacen destino = request.idUbicacionDestino() == null ? null
                : ubicacionRepository.findById(request.idUbicacionDestino())
                        .orElseThrow(() -> new EntityNotFoundException("Ubicación destino no encontrada"));
        if (origen != null && destino != null && origen.getIdUbicacion().equals(destino.getIdUbicacion())) {
            throw new IllegalArgumentException("Origen y destino deben ser diferentes");
        }
        transferencia.setUbicacionOrigen(origen);
        transferencia.setUbicacionDestino(destino);
        transferencia.setObservacion(request.observacion());
        transferencia.getDetalles().clear();
        if (request.detalles() != null) {
            for (CrearTransferenciaRequest.DetalleRequest item : request.detalles()) {
                if (item.idProducto() == null && item.cantidad() == null) continue;
                if (item.idProducto() == null || item.cantidad() == null) {
                    throw new IllegalArgumentException("Producto y cantidad deben completarse");
                }
                if (item.cantidad().signum() <= 0) {
                    throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
                }
                Producto producto = productoRepository.findById(item.idProducto())
                        .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
                Lote lote = item.idLote() == null ? null : loteRepository.findById(item.idLote())
                        .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));
                transferencia.getDetalles().add(DetalleTransferencia.builder().transferencia(transferencia)
                        .producto(producto).lote(lote).cantidad(item.cantidad()).build());
            }
        }
        return toDto(repository.save(transferencia));
    }

    @Override
    @Transactional
    public TransferenciaListadoDto cambiarEstado(Long id, String estadoNombre) {
        TransferenciaAlmacen t = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transferencia no encontrada"));
        String actual = t.getEstadoTransferencia().getNombre().toUpperCase();
        String nuevo = estadoNombre.toUpperCase();
        if (actual.equals("COMPLETADA") || actual.equals("CANCELADA")) throw new IllegalStateException("La transferencia ya no puede cambiar de estado");
        if (nuevo.equals("COMPLETADA") && !actual.equals("EN_PROCESO")) throw new IllegalStateException("Solo una transferencia en proceso puede completarse");
        if (nuevo.equals("EN_PROCESO")) validarParaProcesar(t);
        if (nuevo.equals("COMPLETADA")) moverInventario(t);
        if (nuevo.equals("EN_PROCESO") && !actual.equals("PENDIENTE")) throw new IllegalStateException("Solo una transferencia pendiente puede procesarse");
        if (nuevo.equals("CANCELADA") && actual.equals("BORRADOR")) throw new IllegalStateException("Use eliminar para descartar un borrador");
        EstadoTransferencia estado = estadoRepository.findByNombreIgnoreCase(nuevo).orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + nuevo));
        t.setEstadoTransferencia(estado);
        return toDto(repository.save(t));
    }

    @Override
    @Transactional
    public TransferenciaListadoDto confirmarBorrador(Long id) {
        TransferenciaAlmacen t = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transferencia no encontrada"));
        if (!"BORRADOR".equalsIgnoreCase(t.getEstadoTransferencia().getNombre())) {
            throw new IllegalStateException("Solo se pueden confirmar borradores");
        }
        validarParaProcesar(t);
        EstadoTransferencia estado = estadoRepository.findByNombreIgnoreCase("PENDIENTE")
                .orElseThrow(() -> new EntityNotFoundException("Estado PENDIENTE no encontrado"));
        t.setEstadoTransferencia(estado);
        return toDto(repository.save(t));
    }

    private void validarParaProcesar(TransferenciaAlmacen t) {
        if (t.getUbicacionOrigen() == null || t.getUbicacionDestino() == null) {
            throw new IllegalStateException("Debe completar origen y destino antes de procesar");
        }
        if (t.getUbicacionOrigen().getIdUbicacion().equals(t.getUbicacionDestino().getIdUbicacion())) {
            throw new IllegalArgumentException("Origen y destino deben ser diferentes");
        }
        if (t.getDetalles() == null || t.getDetalles().isEmpty()) {
            throw new IllegalStateException("Debe agregar al menos un producto");
        }
        for (DetalleTransferencia d : t.getDetalles()) {
            if (d.getProducto() == null || d.getCantidad() == null || d.getCantidad().signum() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad mayor que cero");
            }
            Long loteId = d.getLote() == null ? null : d.getLote().getIdLote();
            var existencia = existenciaRepository.buscarParaActualizar(d.getProducto().getIdProducto(),
                    t.getUbicacionOrigen().getIdUbicacion(), loteId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe stock de " + d.getProducto().getNombre() + " en el origen"));
            if (existencia.getCantidad().compareTo(d.getCantidad()) < 0) {
                throw new IllegalArgumentException("Stock insuficiente de " + d.getProducto().getNombre());
            }
        }
    }

    @Override
    @Transactional
    public void eliminarBorrador(Long id) {
        TransferenciaAlmacen t = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transferencia no encontrada"));
        if (!"BORRADOR".equalsIgnoreCase(t.getEstadoTransferencia().getNombre())) throw new IllegalStateException("Solo se pueden eliminar borradores");
        repository.delete(t);
    }

    private void moverInventario(TransferenciaAlmacen transferencia) {
        if (transferencia.getUbicacionOrigen() == null || transferencia.getUbicacionDestino() == null) {
            throw new IllegalStateException("La transferencia no tiene origen y destino completos");
        }
        for (DetalleTransferencia detalle : transferencia.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad().signum() <= 0) {
                throw new IllegalArgumentException("La cantidad de cada detalle debe ser mayor que cero");
            }
            Long loteId = detalle.getLote() == null ? null : detalle.getLote().getIdLote();
            var origen = existenciaRepository.buscarParaActualizar(detalle.getProducto().getIdProducto(), transferencia.getUbicacionOrigen().getIdUbicacion(), loteId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe stock de " + detalle.getProducto().getNombre() + " en el origen"));
            if (origen.getCantidad().compareTo(detalle.getCantidad()) < 0) {
                throw new IllegalArgumentException("Stock insuficiente de " + detalle.getProducto().getNombre());
            }
            var destino = existenciaRepository.buscarParaActualizar(detalle.getProducto().getIdProducto(), transferencia.getUbicacionDestino().getIdUbicacion(), loteId)
                    .orElseGet(() -> existenciaRepository.save(com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia.builder()
                            .producto(detalle.getProducto()).ubicacion(transferencia.getUbicacionDestino()).lote(detalle.getLote()).cantidad(BigDecimal.ZERO).build()));
            origen.setCantidad(origen.getCantidad().subtract(detalle.getCantidad()));
            destino.setCantidad(destino.getCantidad().add(detalle.getCantidad()));
            existenciaRepository.save(origen);
            existenciaRepository.save(destino);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferenciaListadoDto> listar(String texto, String estado, Long usuarioId, String responsable,
            Long almacenOrigenId, Long almacenDestinoId, LocalDate fechaDesde, LocalDate fechaHasta,
            Pageable pageable) {
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
        }
        LocalDateTime desde = fechaDesde == null ? null : fechaDesde.atStartOfDay();
        LocalDateTime hasta = fechaHasta == null ? null : fechaHasta.plusDays(1).atStartOfDay();
        Specification<TransferenciaAlmacen> spec = TransferenciaSpecification.filtros(texto, estado, usuarioId, responsable,
                almacenOrigenId, almacenDestinoId, desde, hasta);
        return repository.findAll(spec, pageable).map(this::toDto);
    }

    private TransferenciaListadoDto toDto(TransferenciaAlmacen t) {
        BigDecimal total = t.getDetalles() == null ? BigDecimal.ZERO : t.getDetalles().stream()
                .map(d -> d.getCantidad() == null ? BigDecimal.ZERO : d.getCantidad())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long productos = t.getDetalles() == null ? 0 : t.getDetalles().stream()
                .filter(d -> d.getProducto() != null).map(d -> d.getProducto().getIdProducto()).distinct().count();
        String origen = t.getUbicacionOrigen() == null ? "Sin definir" : t.getUbicacionOrigen().getAlmacen().getNombre() + " · " + t.getUbicacionOrigen().getNombre();
        String destino = t.getUbicacionDestino() == null ? "Sin definir" : t.getUbicacionDestino().getAlmacen().getNombre() + " · " + t.getUbicacionDestino().getNombre();
        var detallesDto = t.getDetalles() == null ? java.util.List.<TransferenciaListadoDto.DetalleDto>of() : t.getDetalles().stream().map(d -> new TransferenciaListadoDto.DetalleDto(
                d.getProducto() == null ? null : d.getProducto().getIdProducto(), d.getLote() == null ? null : d.getLote().getIdLote(),
                d.getProducto() == null ? "" : d.getProducto().getNombre(), d.getLote() == null ? "Sin lote" : d.getLote().getCodigoLote(), d.getCantidad())).toList();
        return new TransferenciaListadoDto(t.getIdTransferencia(), t.getCodigo(), t.getFechaTransferencia(),
                t.getUbicacionOrigen() == null || t.getUbicacionOrigen().getAlmacen() == null ? null : t.getUbicacionOrigen().getAlmacen().getIdAlmacen(),
                t.getUbicacionDestino() == null || t.getUbicacionDestino().getAlmacen() == null ? null : t.getUbicacionDestino().getAlmacen().getIdAlmacen(),
                t.getUbicacionOrigen() == null ? null : t.getUbicacionOrigen().getIdUbicacion(),
                t.getUbicacionDestino() == null ? null : t.getUbicacionDestino().getIdUbicacion(), origen,
                destino, productos, total, t.getEstadoTransferencia().getNombre(), t.getUsuario().getUsername(), t.getObservacion(), detallesDto);
    }
}
