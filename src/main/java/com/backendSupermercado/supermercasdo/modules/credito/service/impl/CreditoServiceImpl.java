package com.backendSupermercado.supermercasdo.modules.credito.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backendSupermercado.supermercasdo.modules.cliente.entity.Cliente;
import com.backendSupermercado.supermercasdo.modules.credito.dto.*;
import com.backendSupermercado.supermercasdo.modules.credito.entity.PagoCredito;
import com.backendSupermercado.supermercasdo.modules.credito.repository.PagoCreditoRepository;
import com.backendSupermercado.supermercasdo.modules.venta.entity.CuentaPorCobrar;
import com.backendSupermercado.supermercasdo.modules.venta.entity.DetalleVenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;
import com.backendSupermercado.supermercasdo.modules.venta.repository.CuentaPorCobrarRepository;
import com.backendSupermercado.supermercasdo.modules.credito.service.CreditoService;
import com.backendSupermercado.supermercasdo.modules.caja.entity.SesionCaja;
import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoSesionCaja;
import com.backendSupermercado.supermercasdo.modules.caja.repository.SesionCajaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditoServiceImpl implements CreditoService {
    private static final BigDecimal CERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private final CuentaPorCobrarRepository cuentaRepository;
    private final PagoCreditoRepository pagoRepository;
    private final SesionCajaRepository sesionCajaRepository;

    @Override @Transactional(readOnly = true)
    public Page<CreditoResponseDto> listar(String buscar, EstadoCuenta estado, LocalDate desde, LocalDate hasta, Pageable pageable) {
        return cuentaRepository.filtrar(vacio(buscar) ? "" : buscar.trim(), estado, desde, hasta, pageable).map(this::toDto);
    }

    @Override @Transactional(readOnly = true)
    public CreditoResumenDto resumen() {
        List<EstadoCuenta> activos = List.of(EstadoCuenta.PENDIENTE, EstadoCuenta.PARCIAL);
        BigDecimal pendientes = cuentaRepository.sumarSaldos(activos);
        BigDecimal vencidos = cuentaRepository.sumarVencidos(activos, LocalDate.now());
        BigDecimal cobrados = cuentaRepository.findAll().stream().map(c -> c.getMontoTotal().subtract(c.getSaldoPendiente())).reduce(CERO, BigDecimal::add);
        return new CreditoResumenDto(pendientes, vencidos, cobrados, cuentaRepository.countBySaldoPendienteGreaterThanAndEstadoIn(CERO, activos));
    }

    @Override @Transactional
    public CreditoResponseDto registrarPago(Long idUsuario, Long idCuenta, PagoCreditoRequestDto request) {
        SesionCaja sesion = sesionCajaRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, EstadoSesionCaja.ABIERTA)
                .orElseThrow(() -> new IllegalStateException("Debe abrir una caja antes de registrar pagos"));
        CuentaPorCobrar cuenta = cuentaRepository.findById(idCuenta).orElseThrow(() -> new EntityNotFoundException("Cuenta de crédito no encontrada"));
        if (cuenta.getEstado() == EstadoCuenta.PAGADO || cuenta.getEstado() == EstadoCuenta.ANULADO) throw new IllegalArgumentException("La cuenta no admite más pagos");
        if (request.metodo() == MetodoPago.CREDITO) throw new IllegalArgumentException("El abono requiere un método de pago válido");
        BigDecimal monto = request.monto().setScale(2, RoundingMode.HALF_UP);
        if (monto.compareTo(cuenta.getSaldoPendiente()) > 0) throw new IllegalArgumentException("El abono supera el saldo pendiente");
        if ((request.metodo() == MetodoPago.QR || request.metodo() == MetodoPago.MIXTO) && vacio(request.referenciaQr())) throw new IllegalArgumentException("La referencia QR es obligatoria");
        BigDecimal efectivo = request.metodo() == MetodoPago.MIXTO ? cantidad(request.montoEfectivo()) : request.metodo() == MetodoPago.EFECTIVO ? monto : CERO;
        BigDecimal qr = request.metodo() == MetodoPago.MIXTO ? cantidad(request.montoQr()) : request.metodo() == MetodoPago.QR ? monto : CERO;
        if (request.metodo() == MetodoPago.MIXTO && (efectivo.signum() <= 0 || qr.signum() <= 0 || efectivo.add(qr).compareTo(monto) != 0)) throw new IllegalArgumentException("El efectivo y QR deben sumar el total del abono");
        pagoRepository.save(PagoCredito.builder().cuenta(cuenta).sesionCaja(sesion).metodoPago(request.metodo()).monto(monto).montoEfectivo(efectivo).montoQr(qr).referenciaQr(request.referenciaQr()).fechaPago(LocalDateTime.now()).build());
        BigDecimal saldo = cuenta.getSaldoPendiente().subtract(monto).max(CERO);
        cuenta.setSaldoPendiente(saldo);
        cuenta.setEstado(saldo.signum() == 0 ? EstadoCuenta.PAGADO : EstadoCuenta.PARCIAL);
        cuentaRepository.save(cuenta);
        return toDto(cuenta);
    }

    private CreditoResponseDto toDto(CuentaPorCobrar cuenta) {
        Cliente cliente = cuenta.getCliente();
        String nombre = String.join(" ", java.util.stream.Stream.of(cliente.getNombres(), cliente.getApellidoPaterno(), cliente.getApellidoMaterno()).filter(v -> v != null && !v.isBlank()).toList());
        List<CreditoProductoDto> productos = cuenta.getVenta().getDetalles().stream().map(this::producto).toList();
        List<PagoCreditoResponseDto> pagos = pagoRepository.findByCuenta_IdCuentaPorCobrarOrderByFechaPagoDesc(cuenta.getIdCuentaPorCobrar()).stream().map(p -> new PagoCreditoResponseDto(p.getIdPagoCredito(), p.getMonto(), p.getMontoEfectivo(), p.getMontoQr(), p.getMetodoPago(), p.getReferenciaQr(), p.getFechaPago())).toList();
        return new CreditoResponseDto(cuenta.getIdCuentaPorCobrar(), cuenta.getVenta().getIdVenta(), cuenta.getVenta().getCodigo(), nombre, cliente.getTelefono(), cuenta.getMontoTotal(), cuenta.getPagoInicial(), cuenta.getSaldoPendiente(), cuenta.getFechaLimite(), cuenta.getEstado(), cuenta.getVenta().getDetalles().size(), productos, pagos);
    }

    private CreditoProductoDto producto(DetalleVenta detalle) { return new CreditoProductoDto(detalle.getProducto().getCodigoInterno(), detalle.getProducto().getNombre(), detalle.getCantidad(), detalle.getImporte()); }
    private boolean vacio(String valor) { return valor == null || valor.isBlank(); }
    private BigDecimal cantidad(BigDecimal valor) { return valor == null ? CERO : valor.setScale(2, RoundingMode.HALF_UP); }
}
