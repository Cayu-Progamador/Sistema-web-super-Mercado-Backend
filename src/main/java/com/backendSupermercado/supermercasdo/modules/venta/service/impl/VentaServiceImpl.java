package com.backendSupermercado.supermercasdo.modules.venta.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backendSupermercado.supermercasdo.modules.cliente.entity.Cliente;
import com.backendSupermercado.supermercasdo.modules.cliente.repository.ClienteRepository;
import com.backendSupermercado.supermercasdo.modules.caja.entity.SesionCaja;
import com.backendSupermercado.supermercasdo.modules.caja.repository.SesionCajaRepository;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.inventario.repository.ExistenciaRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.DetalleMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.MovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.entity.TipoMovimientoAlmacen;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.MovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.movimiento_almacen.repository.TipoMovimientoAlmacenRepository;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.recepcion.entity.DetalleRecepcion;
import com.backendSupermercado.supermercasdo.modules.recepcion.repository.DetalleRecepcionRepository;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.repository.DetalleAjusteInventarioRepository;
import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.enums.EstadoAjusteInventario;
import com.backendSupermercado.supermercasdo.modules.venta.dto.CrearVentaRequest;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaCatalogoDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResponseDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VerificacionVentaDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResumenDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.DashboardVentasDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaDetalleResponseDto;
import com.backendSupermercado.supermercasdo.modules.venta.entity.CuentaPorCobrar;
import com.backendSupermercado.supermercasdo.modules.venta.entity.DetalleVenta;
import com.backendSupermercado.supermercasdo.modules.venta.entity.PagoVenta;
import com.backendSupermercado.supermercasdo.modules.venta.entity.Venta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoVenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;
import com.backendSupermercado.supermercasdo.modules.venta.repository.VentaRepository;
import com.backendSupermercado.supermercasdo.modules.venta.repository.CuentaPorCobrarRepository;
import com.backendSupermercado.supermercasdo.modules.venta.service.VentaComprobantePdfService;
import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;
import com.backendSupermercado.supermercasdo.modules.productos.repository.PresentacionProductoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements com.backendSupermercado.supermercasdo.modules.venta.service.VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaServiceImpl.class);
    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final String UBICACION_VENTA = "SALA DE VENTA";

    private final VentaRepository ventaRepository;
    private final CuentaPorCobrarRepository cuentaPorCobrarRepository;
    private final ExistenciaRepository existenciaRepository;
    private final ClienteRepository clienteRepository;
    private final SesionCajaRepository sesionCajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMovimientoAlmacenRepository tipoMovimientoRepository;
    private final MovimientoAlmacenRepository movimientoRepository;
    private final PresentacionProductoRepository presentacionRepository;
    private final DetalleRecepcionRepository detalleRecepcionRepository;
    private final DetalleAjusteInventarioRepository detalleAjusteRepository;
    private final VentaComprobantePdfService comprobantePdfService;

    @Override
    @Transactional
    public VentaResponseDto crear(Long idUsuario, CrearVentaRequest request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));
        validarPagoBasico(request.pago());
        Cliente cliente = obtenerClienteActivo(request.idCliente(), request.pago().metodo());
        if (request.idSesionCaja() == null)
            throw new IllegalStateException("Debe abrir una caja antes de registrar la venta");
        SesionCaja sesionCaja = sesionCajaRepository.findById(request.idSesionCaja())
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));
        if (sesionCaja.getEstado() != com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoSesionCaja.ABIERTA)
            throw new IllegalStateException("La caja no está abierta");
        if (!sesionCaja.getUsuario().getIdUsuario().equals(idUsuario))
            throw new IllegalStateException("La sesión de caja no pertenece al cajero autenticado");

        Venta venta = Venta.builder()
                .codigo(generarCodigo())
                .cliente(cliente)
                .usuario(usuario)
                .idCaja(sesionCaja.getCaja().getIdCaja())
                .sesionCaja(sesionCaja)
                .estado(EstadoVenta.CONFIRMADA)
                .subtotal(CERO)
                .descuento(cantidad(request.descuento()))
                .total(CERO)
                .observacion(request.observacion())
                .build();

        BigDecimal subtotal = CERO;
        for (CrearVentaRequest.DetalleRequest item : request.detalles()) {
            Existencia existencia = bloquearExistencia(item.idExistencia());
            PresentacionProducto presentacion = obtenerPresentacion(existencia, item.idPresentacion());
            BigDecimal factor = factor(presentacion);
            BigDecimal cantidadBase = item.cantidad().multiply(factor).setScale(4, RoundingMode.HALF_UP);
            validarExistenciaVendible(existencia, cantidadBase);

            BigDecimal importe = presentacion.getPrecioVenta().multiply(item.cantidad()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costoUnitario = obtenerCostoBase(existencia);
            subtotal = subtotal.add(importe);
            existencia.setCantidad(existencia.getCantidad().subtract(cantidadBase));

            venta.agregarDetalle(DetalleVenta.builder()
                    .producto(existencia.getProducto())
                    .existencia(existencia)
                    .lote(existencia.getLote())
                    .presentacion(presentacion)
                    .cantidad(item.cantidad())
                    .cantidadBase(cantidadBase)
                    .precioUnitario(presentacion.getPrecioVenta())
                    .costoUnitario(costoUnitario)
                    .descuento(CERO)
                    .importe(importe)
                    .build());
        }

        BigDecimal descuento = cantidad(request.descuento());
        if (descuento.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("El descuento no puede superar el subtotal");
        }
        BigDecimal total = subtotal.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotal);
        venta.setTotal(total);

        validarPagoContraTotal(request.pago(), total, cliente);
        PagoVenta pago = crearPago(venta, request.pago(), total);
        venta.getPagos().add(pago);

        if (request.pago().metodo() == MetodoPago.CREDITO) {
            CuentaPorCobrar cuenta = crearCuenta(venta, cliente, request.pago(), total);
            venta.setCuentaPorCobrar(cuenta);
        }

        Venta guardada = ventaRepository.save(venta);
        registrarSalidaKardex(guardada, usuario);
        return toDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDto obtener(Long idVenta) {
        return toDto(ventaRepository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponseDto> listar(Pageable pageable) {
        return ventaRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaCatalogoDto> catalogo(String buscar) {
        String texto = buscar == null ? "" : buscar.trim().toLowerCase(Locale.ROOT);
        List<Existencia> existencias = existenciaRepository.findAll();
        log.info("[VENTAS_CATALOGO] Existencias encontradas: {}, texto: '{}'", existencias.size(), texto);
        existencias.forEach(e -> {
            String producto = e.getProducto() == null ? "SIN_PRODUCTO" : e.getProducto().getNombre();
            String ubicacion = e.getUbicacion() == null ? "SIN_UBICACION" : e.getUbicacion().getNombre();
            String almacen = e.getUbicacion() == null || e.getUbicacion().getAlmacen() == null
                    ? "SIN_ALMACEN" : e.getUbicacion().getAlmacen().getNombre();
            int presentaciones = e.getProducto() == null ? 0
                    : presentacionRepository.findAllByProductoId(e.getProducto().getIdProducto()).size();
            log.info("[VENTAS_CATALOGO] producto='{}', stock={}, ubicacion='{}', almacen='{}', presentaciones={}",
                    producto, e.getCantidad(), ubicacion, almacen, presentaciones);
        });
        return existencias.stream()
                .filter(this::esExistenciaVendible)
                .filter(e -> texto.isBlank()
                        || contiene(e.getProducto().getNombre(), texto)
                        || contiene(e.getProducto().getCodigoInterno(), texto)
                        || tieneCodigoBarra(e, texto))
                .flatMap(e -> presentacionRepository.findAllByProductoId(e.getProducto().getIdProducto()).stream()
                        .filter(p -> Boolean.TRUE.equals(p.getEstado())
                                && p.getPrecioVenta() != null && p.getPrecioVenta().signum() > 0
                                && p.getFactorConversion() != null && p.getFactorConversion().signum() > 0)
                        .map(p -> catalogo(e, p)))
                .sorted(Comparator.comparing(VentaCatalogoDto::nombreProducto, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(VentaCatalogoDto::presentacionPrincipal, Comparator.reverseOrder()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VerificacionVentaDto verificar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de venta es obligatorio");
        }
        Venta venta = ventaRepository.findByCodigo(codigo.trim())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        return new VerificacionVentaDto(venta.getCodigo(), venta.getFechaVenta(), venta.getTotal(),
                venta.getEstado(), venta.getEstado() != EstadoVenta.ANULADA);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarComprobantePdf(Long idVenta) {
        Venta venta = ventaRepository.findDetalleByIdVenta(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        return comprobantePdfService.generar(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal gananciaDelDia() {
        LocalDate hoy = LocalDate.now();
        return ventaRepository.findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThan(
                EstadoVenta.CONFIRMADA, hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay()).stream()
                .flatMap(v -> v.getDetalles().stream())
                .map(this::gananciaDetalle)
                .reduce(CERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal gananciaDetalle(DetalleVenta detalle) {
        BigDecimal costo = detalle.getCostoUnitario();
        if (costo == null && detalle.getExistencia() != null) costo = obtenerCostoBase(detalle.getExistencia());
        if (costo == null && detalle.getProducto() != null) {
            costo = detalleRecepcionRepository
                    .findFirstByDetallePedido_Producto_IdProductoOrderByRecepcion_FechaRecepcionDesc(
                            detalle.getProducto().getIdProducto())
                    .map(d -> costoBase(d, detalle.getProducto()))
                    .orElse(null);
        }
        if (costo == null && detalle.getProducto() != null) {
            costo = obtenerCostoAjuste(detalle.getProducto(), detalle.getLote());
        }
        if (costo == null || detalle.getImporte() == null) return CERO;
        BigDecimal cantidad = detalle.getCantidadBase() == null ? detalle.getCantidad() : detalle.getCantidadBase();
        return detalle.getImporte().subtract(costo.multiply(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResumenDto resumen() {
        return new VentaResumenDto(
                ventaRepository.countByEstadoNot(EstadoVenta.ANULADA),
                ventaRepository.sumarTotales(EstadoVenta.CONFIRMADA),
                ventaRepository.contarPendientes(EstadoVenta.ANULADA),
                gananciaDelDia());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardVentasDto dashboard() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.minusDays(6);
        List<Venta> ventas = ventaRepository.findByEstadoAndFechaVentaGreaterThanEqualAndFechaVentaLessThan(
                EstadoVenta.CONFIRMADA, inicioSemana.atStartOfDay(), hoy.plusDays(1).atStartOfDay());

        Map<LocalDate, BigDecimal> totalesPorDia = new LinkedHashMap<>();
        Map<LocalDate, Long> cantidadesPorDia = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate fecha = inicioSemana.plusDays(i);
            totalesPorDia.put(fecha, CERO);
            cantidadesPorDia.put(fecha, 0L);
        }
        Map<String, BigDecimal> totalesPorMetodo = new LinkedHashMap<>();
        Map<String, BigDecimal> cantidadesPorProducto = new LinkedHashMap<>();

        for (Venta venta : ventas) {
            LocalDate fecha = venta.getFechaVenta().toLocalDate();
            BigDecimal total = venta.getTotal() == null ? CERO : venta.getTotal();
            totalesPorDia.put(fecha, totalesPorDia.getOrDefault(fecha, CERO).add(total));
            cantidadesPorDia.put(fecha, cantidadesPorDia.getOrDefault(fecha, 0L) + 1);

            for (PagoVenta pago : venta.getPagos()) {
                String metodo = pago.getMetodoPago() == null ? "OTROS" : pago.getMetodoPago().name();
                totalesPorMetodo.put(metodo, totalesPorMetodo.getOrDefault(metodo, CERO).add(total));
            }
            for (DetalleVenta detalle : venta.getDetalles()) {
                String producto = detalle.getProducto() == null ? "Producto sin nombre" : detalle.getProducto().getNombre();
                BigDecimal cantidad = detalle.getCantidadBase() == null ? detalle.getCantidad() : detalle.getCantidadBase();
                cantidadesPorProducto.put(producto, cantidadesPorProducto.getOrDefault(producto, BigDecimal.ZERO)
                        .add(cantidad == null ? BigDecimal.ZERO : cantidad));
            }
        }

        List<DashboardVentasDto.VentaPorDiaDto> serie = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> item : totalesPorDia.entrySet()) {
            serie.add(new DashboardVentasDto.VentaPorDiaDto(item.getKey().toString(),
                    item.getKey().getDayOfWeek().getDisplayName(java.time.format.TextStyle.SHORT, Locale.forLanguageTag("es-BO")),
                    item.getValue(), cantidadesPorDia.get(item.getKey())));
        }
        List<DashboardVentasDto.VentaPorMetodoPagoDto> metodos = totalesPorMetodo.entrySet().stream()
                .map(item -> new DashboardVentasDto.VentaPorMetodoPagoDto(item.getKey(), item.getValue()))
                .toList();
        List<DashboardVentasDto.ProductoMasVendidoDto> productos = cantidadesPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed()).limit(5)
                .map(item -> new DashboardVentasDto.ProductoMasVendidoDto(item.getKey(), item.getValue())).toList();
        BigDecimal ventasHoy = totalesPorDia.getOrDefault(hoy, CERO);
        return new DashboardVentasDto(ventasHoy, cantidadesPorDia.getOrDefault(hoy, 0L),
                totalesPorDia.values().stream().reduce(CERO, BigDecimal::add), serie, metodos, productos);
    }

    @Override
    @Transactional
    public void anular(Long idUsuario, Long idVenta, String motivo) {
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("El motivo de anulacion es obligatorio");
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
        if (venta.getEstado() == EstadoVenta.ANULADA) throw new IllegalArgumentException("La venta ya esta anulada");
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        for (DetalleVenta detalle : venta.getDetalles()) {
            Existencia existencia = bloquearExistencia(detalle.getExistencia().getIdExistencia());
            BigDecimal cantidadBase = detalle.getCantidadBase() == null ? detalle.getCantidad() : detalle.getCantidadBase();
            existencia.setCantidad(existencia.getCantidad().add(cantidadBase));
        }
        venta.setEstado(EstadoVenta.ANULADA);
        venta.setObservacion("Anulada por usuario " + usuario.getUsername() + ": " + motivo.trim());
        if (venta.getCuentaPorCobrar() != null) {
            venta.getCuentaPorCobrar().setEstado(EstadoCuenta.ANULADO);
            venta.getCuentaPorCobrar().setSaldoPendiente(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
        ventaRepository.save(venta);
    }

    private Existencia bloquearExistencia(Long idExistencia) {
        Existencia base = existenciaRepository.findById(idExistencia)
                .orElseThrow(() -> new EntityNotFoundException("Existencia no encontrada: " + idExistencia));
        return existenciaRepository.buscarParaActualizar(
                base.getProducto().getIdProducto(),
                base.getUbicacion().getIdUbicacion(),
                base.getLote() == null ? null : base.getLote().getIdLote())
                .orElseThrow(() -> new EntityNotFoundException("La existencia ya no esta disponible"));
    }

    private void validarExistenciaVendible(Existencia existencia, BigDecimal cantidad) {
        if (existencia.getUbicacion() == null
                || existencia.getUbicacion().getAlmacen() == null
                || !esSalaVenta(existencia.getUbicacion().getAlmacen().getNombre())) {
            throw new IllegalArgumentException("Solo se pueden vender productos del almacen Sala de Ventas");
        }
        if (!Boolean.TRUE.equals(existencia.getUbicacion().getActivo())
                || existencia.getUbicacion().getAlmacen() == null
                || !Boolean.TRUE.equals(existencia.getUbicacion().getAlmacen().getActivo())) {
            throw new IllegalArgumentException("La ubicacion de venta esta inactiva");
        }
        if (existencia.getProducto() == null || !Boolean.TRUE.equals(existencia.getProducto().getEstado())) {
            throw new IllegalArgumentException("El producto esta inactivo");
        }
        if (existencia.getCantidad() == null || existencia.getCantidad().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("Stock insuficiente para " + existencia.getProducto().getNombre());
        }
        if (existencia.getLote() != null && existencia.getLote().getFechaVencimiento() != null
                && !existencia.getLote().getFechaVencimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede vender el producto vencido o que vence hoy: "
                    + existencia.getProducto().getNombre());
        }
    }

    private PresentacionProducto obtenerPresentacion(Existencia existencia, Long idPresentacion) {
        PresentacionProducto presentacion = presentacionRepository.findById(idPresentacion)
                .orElseThrow(() -> new EntityNotFoundException("Presentacion no encontrada"));
        if (!Boolean.TRUE.equals(presentacion.getEstado())
                || presentacion.getProducto() == null
                || !presentacion.getProducto().getIdProducto().equals(existencia.getProducto().getIdProducto())) {
            throw new IllegalArgumentException("La presentacion no pertenece al producto seleccionado");
        }
        if (presentacion.getPrecioVenta() == null || presentacion.getPrecioVenta().signum() <= 0) {
            throw new IllegalArgumentException("La presentacion no tiene un precio de venta valido");
        }
        factor(presentacion);
        return presentacion;
    }

    private BigDecimal factor(PresentacionProducto presentacion) {
        if (presentacion.getFactorConversion() == null
                || presentacion.getFactorConversion().signum() <= 0) {
            throw new IllegalArgumentException("La presentacion no tiene un factor de conversion valido");
        }
        return presentacion.getFactorConversion();
    }

    private boolean esExistenciaVendible(Existencia existencia) {
        if (existencia.getProducto() == null || !Boolean.TRUE.equals(existencia.getProducto().getEstado())
                || existencia.getCantidad() == null || existencia.getCantidad().signum() <= 0
                || existencia.getUbicacion() == null
                || !Boolean.TRUE.equals(existencia.getUbicacion().getActivo())
                || existencia.getUbicacion().getAlmacen() == null
                || !Boolean.TRUE.equals(existencia.getUbicacion().getAlmacen().getActivo())
                || !esSalaVenta(existencia.getUbicacion().getAlmacen().getNombre())) {
            return false;
        }
        return existencia.getLote() == null || existencia.getLote().getFechaVencimiento() == null
                || existencia.getLote().getFechaVencimiento().isAfter(LocalDate.now());
    }

    private boolean esSalaVenta(String nombre) {
        if (nombre == null) return false;
        String normalizado = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        return UBICACION_VENTA.equals(normalizado)
                || "SALA VENTA".equals(normalizado)
                || "SALA DE VENTAS".equals(normalizado);
    }

    private VentaCatalogoDto catalogo(Existencia existencia, PresentacionProducto presentacion) {
        BigDecimal factor = factor(presentacion);
        BigDecimal stock = existencia.getCantidad().divide(factor, 4, RoundingMode.DOWN);
        return new VentaCatalogoDto(existencia.getIdExistencia(), existencia.getProducto().getIdProducto(),
                existencia.getProducto().getCodigoInterno(), existencia.getProducto().getNombre(),
                presentacion.getIdPresentacion(), presentacion.getNombrePresentacion(),
                codigoBarraPrincipal(presentacion),
                presentacion.getUnidadMedida().getNombre(), factor, presentacion.getPrecioVenta(), stock,
                existencia.getLote() == null ? null : existencia.getLote().getCodigoLote(),
                existencia.getLote() == null ? null : existencia.getLote().getFechaVencimiento(),
                existencia.getUbicacion().getAlmacen().getNombre(), existencia.getUbicacion().getNombre(),
                Boolean.TRUE.equals(presentacion.getEsPrincipal()));
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }

    private boolean tieneCodigoBarra(Existencia existencia, String texto) {
        return existencia.getProducto() != null
                && existencia.getProducto().getPresentaciones() != null
                && existencia.getProducto().getPresentaciones().stream()
                        .flatMap(p -> p.getCodigosBarra() == null ? java.util.stream.Stream.empty() : p.getCodigosBarra().stream())
                        .anyMatch(cb -> Boolean.TRUE.equals(cb.getEstado()) && contiene(cb.getCodigo(), texto));
    }

    private String codigoBarraPrincipal(PresentacionProducto presentacion) {
        if (presentacion.getCodigosBarra() == null) return null;
        return presentacion.getCodigosBarra().stream()
                .filter(cb -> Boolean.TRUE.equals(cb.getEstado()))
                .findFirst()
                .map(cb -> cb.getCodigo())
                .orElse(null);
    }

    private Cliente obtenerClienteActivo(Long idCliente, MetodoPago metodo) {
        if (metodo != MetodoPago.CREDITO && idCliente == null) return null;
        if (idCliente == null) throw new IllegalArgumentException("Una venta a credito requiere cliente");
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        if (!Boolean.TRUE.equals(cliente.getEstado())) throw new IllegalArgumentException("El cliente esta inactivo");
        return cliente;
    }

    private void validarPagoBasico(CrearVentaRequest.PagoRequest pago) {
        if (pago == null || pago.metodo() == null) throw new IllegalArgumentException("El pago es obligatorio");
        if (pago.metodo() == MetodoPago.QR && vacio(pago.referenciaQr())) throw new IllegalArgumentException("La referencia QR es obligatoria");
        if (pago.metodo() == MetodoPago.MIXTO && vacio(pago.referenciaQr())) throw new IllegalArgumentException("La referencia QR es obligatoria para pago mixto");
    }

    private void validarPagoContraTotal(CrearVentaRequest.PagoRequest pago, BigDecimal total, Cliente cliente) {
        BigDecimal efectivo = cantidad(pago.montoEfectivo());
        BigDecimal qr = cantidad(pago.montoQr());
        BigDecimal recibido = cantidad(pago.totalRecibido());
        if (pago.metodo() == MetodoPago.EFECTIVO && recibido.compareTo(total) < 0) throw new IllegalArgumentException("El monto recibido no cubre el total");
        if (pago.metodo() == MetodoPago.QR && qr.compareTo(total) != 0) throw new IllegalArgumentException("El pago QR debe ser igual al total");
        if (pago.metodo() == MetodoPago.MIXTO && efectivo.add(qr).compareTo(total) < 0) throw new IllegalArgumentException("Efectivo mas QR no cubre el total");
        if (pago.metodo() == MetodoPago.CREDITO) {
            BigDecimal inicial = cantidad(pago.pagoInicial());
            if (pago.fechaLimite() == null || !pago.fechaLimite().isAfter(LocalDate.now())) throw new IllegalArgumentException("La fecha limite debe ser futura");
            if (inicial.compareTo(total) > 0) throw new IllegalArgumentException("El pago inicial no puede superar el total");
            if (cliente == null) throw new IllegalArgumentException("Cliente obligatorio para credito");
            validarLimiteCredito(cliente, total.subtract(inicial));
        }
    }

    private void validarLimiteCredito(Cliente cliente, BigDecimal nuevoSaldo) {
        if (cliente.getLimiteCredito() == null) return;
        BigDecimal saldoActual = cuentaPorCobrarRepository.sumarSaldoPendiente(
                cliente.getIdCliente(), List.of(EstadoCuenta.PENDIENTE, EstadoCuenta.PARCIAL));
        BigDecimal saldoDespues = saldoActual.add(nuevoSaldo).setScale(2, RoundingMode.HALF_UP);
        if (saldoDespues.compareTo(cliente.getLimiteCredito()) > 0) {
            BigDecimal disponible = cliente.getLimiteCredito().subtract(saldoActual).max(CERO);
            throw new IllegalArgumentException("El cliente supera su limite de credito. Disponible: Bs "
                    + disponible.setScale(2, RoundingMode.HALF_UP));
        }
    }

    private PagoVenta crearPago(Venta venta, CrearVentaRequest.PagoRequest request, BigDecimal total) {
        BigDecimal efectivo = cantidad(request.montoEfectivo());
        BigDecimal qr = cantidad(request.montoQr());
        BigDecimal recibido = request.metodo() == MetodoPago.CREDITO ? cantidad(request.pagoInicial()) : efectivo.add(qr);
        BigDecimal cambio = recibido.subtract(total).max(CERO);
        return PagoVenta.builder().venta(venta).metodoPago(request.metodo()).montoEfectivo(efectivo).montoQr(qr)
                .totalRecibido(recibido).cambio(cambio).referenciaQr(request.referenciaQr()).fechaPago(LocalDateTime.now()).build();
    }

    private CuentaPorCobrar crearCuenta(Venta venta, Cliente cliente, CrearVentaRequest.PagoRequest pago, BigDecimal total) {
        BigDecimal inicial = cantidad(pago.pagoInicial());
        return CuentaPorCobrar.builder().venta(venta).cliente(cliente).montoTotal(total).pagoInicial(inicial)
                .saldoPendiente(total.subtract(inicial)).fechaLimite(pago.fechaLimite())
                .estado(inicial.signum() == 0 ? EstadoCuenta.PENDIENTE : EstadoCuenta.PARCIAL)
                .observacion(pago.observacion()).build();
    }

    private void registrarSalidaKardex(Venta venta, Usuario usuario) {
        TipoMovimientoAlmacen tipo = tipoMovimientoRepository.findByNombreIgnoreCase("SALIDA_VENTA")
                .orElseThrow(() -> new IllegalStateException("No existe el tipo de movimiento SALIDA_VENTA en la base de datos"));
        MovimientoAlmacen movimiento = MovimientoAlmacen.builder().tipoMovimiento(tipo).usuario(usuario)
                .fechaMovimiento(LocalDateTime.now()).referencia(venta.getCodigo()).observacion("Venta confirmada").build();
        for (DetalleVenta detalle : venta.getDetalles()) {
            Existencia existencia = detalle.getExistencia();
            BigDecimal cantidadBase = detalle.getCantidadBase() == null ? detalle.getCantidad() : detalle.getCantidadBase();
            movimiento.getDetalles().add(DetalleMovimientoAlmacen.builder().movimiento(movimiento)
                    .producto(detalle.getProducto()).lote(detalle.getLote()).ubicacion(existencia.getUbicacion())
                    .cantidad(cantidadBase).saldoAnterior(existencia.getCantidad().add(cantidadBase))
                    .saldoResultante(existencia.getCantidad()).observacion("Salida por venta").build());
        }
        movimientoRepository.save(movimiento);
    }

    private VentaResponseDto toDto(Venta venta) {
        BigDecimal saldo = venta.getCuentaPorCobrar() == null ? CERO : venta.getCuentaPorCobrar().getSaldoPendiente();
        return new VentaResponseDto(venta.getIdVenta(), venta.getCodigo(), venta.getFechaVenta(),
                venta.getCliente() == null ? "Consumidor final" : venta.getCliente().getNombres(),
                nombreCompletoUsuario(venta.getUsuario()), venta.getSubtotal(), venta.getDescuento(), venta.getTotal(),
                venta.getEstado(), venta.getPagos().isEmpty() ? null : venta.getPagos().get(0).getMetodoPago().name(), saldo,
                venta.getDetalles() == null ? 0 : venta.getDetalles().size(),
                venta.getDetalles() == null ? List.of() : venta.getDetalles().stream().map(detalle -> new VentaDetalleResponseDto(
                        detalle.getProducto() == null ? "--" : detalle.getProducto().getCodigoInterno(),
                        detalle.getProducto() == null ? "--" : detalle.getProducto().getNombre(),
                        detalle.getPresentacion() == null || detalle.getPresentacion().getUnidadMedida() == null
                                ? "--" : detalle.getPresentacion().getUnidadMedida().getNombre(),
                        detalle.getCantidad(), detalle.getPrecioUnitario(), detalle.getImporte())).toList(),
                venta.getCuentaPorCobrar() == null ? null : venta.getCuentaPorCobrar().getPagoInicial(),
                venta.getCuentaPorCobrar() == null ? null : venta.getCuentaPorCobrar().getEstado().name(),
                venta.getCuentaPorCobrar() == null ? null : venta.getCuentaPorCobrar().getFechaLimite(),
                ultimoPago(venta) == null ? null : ultimoPago(venta).getMontoEfectivo(),
                ultimoPago(venta) == null ? null : ultimoPago(venta).getMontoQr(),
                ultimoPago(venta) == null ? null : ultimoPago(venta).getTotalRecibido(),
                ultimoPago(venta) == null ? null : ultimoPago(venta).getCambio(),
                ultimoPago(venta) == null ? null : ultimoPago(venta).getReferenciaQr());
    }

    private PagoVenta ultimoPago(Venta venta) {
        return venta.getPagos() == null || venta.getPagos().isEmpty() ? null
                : venta.getPagos().get(venta.getPagos().size() - 1);
    }

    private String nombreCompletoUsuario(Usuario usuario) {
        if (usuario == null) return "--";
        if (usuario.getEmpleado() != null && usuario.getEmpleado().getPersona() != null) {
            var persona = usuario.getEmpleado().getPersona();
            String nombre = String.join(" ", java.util.stream.Stream.of(
                    persona.getNombres(), persona.getApellidoPaterno(), persona.getApellidoMaterno())
                    .filter(java.util.Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).toList());
            if (!nombre.isBlank()) return nombre;
        }
        return usuario.getUsername();
    }

    private BigDecimal obtenerCostoBase(Existencia existencia) {
        if (existencia.getProducto() == null) return null;
        java.util.Optional<DetalleRecepcion> detalle = existencia.getLote() == null
                ? detalleRecepcionRepository.findFirstByDetallePedido_Producto_IdProductoOrderByRecepcion_FechaRecepcionDesc(
                        existencia.getProducto().getIdProducto())
                : detalleRecepcionRepository.findFirstByLote_IdLoteOrderByRecepcion_FechaRecepcionDesc(
                        existencia.getLote().getIdLote());
        return detalle.map(d -> costoBase(d, existencia.getProducto()))
                .orElseGet(() -> obtenerCostoAjuste(existencia.getProducto(), existencia.getLote()));
    }

    private BigDecimal obtenerCostoAjuste(com.backendSupermercado.supermercasdo.modules.productos.entity.Producto producto,
            com.backendSupermercado.supermercasdo.modules.lotes.entity.Lote lote) {
        if (lote != null) {
            var porLote = detalleAjusteRepository
                    .findFirstByProducto_IdProductoAndLote_IdLoteAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
                            producto.getIdProducto(), lote.getIdLote(), EstadoAjusteInventario.APLICADO);
            if (porLote.isPresent()) return porLote.get().getCostoUnitario();
        }
        return detalleAjusteRepository
                .findFirstByProducto_IdProductoAndAjuste_EstadoOrderByAjuste_FechaAplicacionDesc(
                        producto.getIdProducto(), EstadoAjusteInventario.APLICADO)
                .map(d -> d.getCostoUnitario()).orElse(null);
    }

    private BigDecimal costoBase(DetalleRecepcion detalle, com.backendSupermercado.supermercasdo.modules.productos.entity.Producto producto) {
        BigDecimal factor = producto.getPresentaciones() == null ? null : producto.getPresentaciones().stream()
                .filter(p -> p.getUnidadMedida() != null && detalle.getUnidadMedida() != null
                        && p.getUnidadMedida().getIdUnidadMedida().equals(detalle.getUnidadMedida().getIdUnidadMedida()))
                .map(PresentacionProducto::getFactorConversion).filter(java.util.Objects::nonNull)
                .findFirst().orElse(BigDecimal.ONE);
        return detalle.getPrecioUnitario().divide(factor, 4, RoundingMode.HALF_UP);
    }

    private String generarCodigo() { return "V-" + System.currentTimeMillis(); }
    private BigDecimal cantidad(BigDecimal valor) { return valor == null ? CERO : valor.setScale(2, RoundingMode.HALF_UP); }
    private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
}
