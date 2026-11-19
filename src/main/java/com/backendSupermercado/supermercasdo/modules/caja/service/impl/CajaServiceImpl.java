package com.backendSupermercado.supermercasdo.modules.caja.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backendSupermercado.supermercasdo.modules.caja.dto.*;
import com.backendSupermercado.supermercasdo.modules.caja.entity.*;
import com.backendSupermercado.supermercasdo.modules.caja.enums.*;
import com.backendSupermercado.supermercasdo.modules.caja.repository.*;
import com.backendSupermercado.supermercasdo.modules.caja.service.CajaArqueoPdfService;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.modules.venta.repository.PagoVentaRepository;
import com.backendSupermercado.supermercasdo.modules.credito.repository.PagoCreditoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements com.backendSupermercado.supermercasdo.modules.caja.service.CajaService {
    private final CajaRepository cajaRepository;
    private final SesionCajaRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PagoVentaRepository pagoVentaRepository;
    private final PagoCreditoRepository pagoCreditoRepository;
    private final CajaArqueoPdfService cajaArqueoPdfService;

    public List<CajaResponse> listarCajas() {
        return cajaRepository.findAll().stream().map(c -> new CajaResponse(c.getIdCaja(), c.getNombre(), c.getEstado(),
                sesionRepository.existsByCajaIdCajaAndEstado(c.getIdCaja(), EstadoSesionCaja.ABIERTA)))
                .toList();
    }

    @Transactional
    public SesionCajaResponse abrir(Long idUsuario, AbrirCajaRequest r) {
        Caja c = cajaRepository.findById(r.idCaja())
                .orElseThrow(() -> new EntityNotFoundException("Caja no encontrada"));
        if (c.getEstado() != EstadoCaja.ACTIVA)
            throw new IllegalStateException("La caja esta inactiva");
        if (sesionRepository.existsByCajaIdCajaAndEstado(c.getIdCaja(), EstadoSesionCaja.ABIERTA))
            throw new IllegalStateException("La caja ya está ocupada");
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        if (sesionRepository.existsByUsuarioIdUsuarioAndEstado(idUsuario, EstadoSesionCaja.ABIERTA))
            throw new IllegalStateException("El empleado ya tiene una caja abierta");
        if (!Boolean.TRUE.equals(u.getActivo()) || Boolean.TRUE.equals(u.getBloqueado()))
            throw new IllegalStateException("El usuario no esta activo");
        SesionCaja s = SesionCaja.builder().caja(c).usuario(u).montoInicial(r.montoInicial())
                .montoEsperado(r.montoInicial()).estado(EstadoSesionCaja.ABIERTA).observacion(r.observacion()).build();
        return response(sesionRepository.save(s));
    }

    @Transactional
    public SesionCajaResponse cerrar(Long idUsuario, Long idSesion, CerrarCajaRequest r) {
        SesionCaja s = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));
        if (s.getEstado() != EstadoSesionCaja.ABIERTA)
            throw new IllegalStateException("La sesión ya está cerrada");
        if (!s.getUsuario().getIdUsuario().equals(idUsuario))
            throw new IllegalStateException("Solo el cajero responsable puede cerrar la caja");
        BigDecimal esperado = actualizarSaldoEsperado(s);
        BigDecimal diferencia = r.efectivoContado().subtract(esperado);
        if (diferencia.compareTo(BigDecimal.ZERO) != 0 && (r.observacion() == null || r.observacion().isBlank()))
            throw new IllegalArgumentException("Debe registrar una observación cuando existe diferencia");
        s.setEfectivoContado(r.efectivoContado());
        s.setDiferencia(diferencia);
        s.setFechaCierre(LocalDateTime.now());
        s.setEstado(EstadoSesionCaja.CERRADA);
        s.setObservacion(r.observacion());
        return response(sesionRepository.save(s));
    }

    @Transactional(readOnly = true)
    public Page<SesionCajaResponse> listarSesiones(String buscar, Long idCaja, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable p) {
        return sesionRepository.filtrar(null, idCaja, buscar == null ? "" : buscar.trim(), desde, hasta, p).map(this::response);
    }

    @Transactional(readOnly = true)
    public Page<SesionCajaResponse> listarMisSesiones(Long idUsuario, String buscar, Long idCaja, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable p) {
        return sesionRepository.filtrar(idUsuario, idCaja, buscar == null ? "" : buscar.trim(), desde, hasta, p).map(this::response);
    }

    @Transactional(readOnly = true)
    public SesionCajaResponse sesionAbierta(Long idUsuario) {
        return sesionRepository.findByUsuarioIdUsuarioAndEstado(idUsuario, EstadoSesionCaja.ABIERTA)
                .map(s -> { actualizarSaldoEsperado(s); return response(s); }).orElse(null);
    }

    @Transactional(readOnly = true)
    public byte[] generarArqueoPdf(Long idSesion, Long idUsuario, boolean esAdministrador) {
        SesionCaja sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new EntityNotFoundException("Sesión de caja no encontrada"));

        if (!esAdministrador && !sesion.getUsuario().getIdUsuario().equals(idUsuario)) {
            throw new IllegalStateException("No tiene permiso para imprimir este arqueo");
        }
        if (sesion.getEstado() != EstadoSesionCaja.CERRADA) {
            throw new IllegalStateException("El arqueo solo está disponible para sesiones cerradas");
        }

        return cajaArqueoPdfService.generar(response(sesion));
    }

    private BigDecimal actualizarSaldoEsperado(SesionCaja s) {
        BigDecimal efectivoCobrado = pagoVentaRepository.sumarEfectivoPorSesion(s.getIdSesionCaja());
        BigDecimal efectivoPagosCredito = pagoCreditoRepository.sumarEfectivoPorSesion(s.getIdSesionCaja());
        BigDecimal esperado = s.getMontoInicial()
                .add(efectivoCobrado == null ? BigDecimal.ZERO : efectivoCobrado)
                .add(efectivoPagosCredito == null ? BigDecimal.ZERO : efectivoPagosCredito);
        s.setMontoEsperado(esperado);
        return esperado;
    }

    private SesionCajaResponse response(SesionCaja s) {
        BigDecimal efectivo = pagoVentaRepository.sumarEfectivoDirecto(s.getIdSesionCaja());
        BigDecimal qr = pagoVentaRepository.sumarQrDirecto(s.getIdSesionCaja());
        BigDecimal mixtoEfectivo = pagoVentaRepository.sumarEfectivoMixto(s.getIdSesionCaja());
        BigDecimal mixtoQr = pagoVentaRepository.sumarQrMixto(s.getIdSesionCaja());
        BigDecimal mixto = mixtoEfectivo.add(mixtoQr);
        BigDecimal credito = s.getVentas().stream().filter(v -> v.getCuentaPorCobrar() != null)
                .map(v -> v.getTotal() == null ? BigDecimal.ZERO : v.getTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pagosCredito = s.getVentas().stream().filter(v -> v.getCuentaPorCobrar() != null)
                .flatMap(v -> v.getPagos().stream()).map(p -> p.getTotalRecibido() == null ? BigDecimal.ZERO : p.getTotalRecibido())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoPendiente = s.getVentas().stream().filter(v -> v.getCuentaPorCobrar() != null)
                .map(v -> v.getCuentaPorCobrar().getSaldoPendiente() == null ? BigDecimal.ZERO : v.getCuentaPorCobrar().getSaldoPendiente())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoEfectivo = s.getVentas().stream().filter(v -> v.getCuentaPorCobrar() != null)
                .flatMap(v -> v.getPagos().stream()).map(p -> p.getMontoEfectivo() == null ? BigDecimal.ZERO : p.getMontoEfectivo())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoQr = s.getVentas().stream().filter(v -> v.getCuentaPorCobrar() != null)
                .flatMap(v -> v.getPagos().stream()).map(p -> p.getMontoQr() == null ? BigDecimal.ZERO : p.getMontoQr())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pagosCreditoTotal = pagoCreditoRepository.sumarTotalPorSesion(s.getIdSesionCaja());
        BigDecimal pagosCreditoEfectivo = pagoCreditoRepository.sumarEfectivoPorSesion(s.getIdSesionCaja());
        BigDecimal pagosCreditoQr = pagoCreditoRepository.sumarQrPorSesion(s.getIdSesionCaja());
        pagosCreditoTotal = pagosCreditoTotal == null ? BigDecimal.ZERO : pagosCreditoTotal;
        pagosCreditoEfectivo = pagosCreditoEfectivo == null ? BigDecimal.ZERO : pagosCreditoEfectivo;
        pagosCreditoQr = pagosCreditoQr == null ? BigDecimal.ZERO : pagosCreditoQr;
        pagosCredito = pagosCredito.add(pagosCreditoTotal);
        creditoEfectivo = creditoEfectivo.add(pagosCreditoEfectivo);
        creditoQr = creditoQr.add(pagosCreditoQr);
        String cajero = s.getUsuario().getEmpleado() == null || s.getUsuario().getEmpleado().getPersona() == null ? s.getUsuario().getUsername()
                : java.util.stream.Stream.of(s.getUsuario().getEmpleado().getPersona().getNombres(), s.getUsuario().getEmpleado().getPersona().getApellidoPaterno(), s.getUsuario().getEmpleado().getPersona().getApellidoMaterno())
                        .filter(java.util.Objects::nonNull).filter(v -> !v.isBlank()).reduce((a, b) -> a + " " + b)
                        .orElse(s.getUsuario().getUsername());
        return new SesionCajaResponse(s.getIdSesionCaja(), s.getCaja().getIdCaja(), s.getCaja().getNombre(),
                s.getUsuario().getIdUsuario(), cajero, s.getMontoInicial(), s.getMontoEsperado(), s.getEfectivoContado(),
                s.getDiferencia(), efectivo, qr, mixto, mixtoEfectivo, mixtoQr, credito, pagosCredito, creditoPendiente, creditoEfectivo, creditoQr, s.getFechaApertura(),
                s.getFechaCierre(), s.getEstado(), s.getObservacion());
    }
}
