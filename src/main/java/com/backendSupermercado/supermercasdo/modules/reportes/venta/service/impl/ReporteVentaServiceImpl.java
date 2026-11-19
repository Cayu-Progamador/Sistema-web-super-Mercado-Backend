package com.backendSupermercado.supermercasdo.modules.reportes.venta.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaDto;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaFiltro;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.model.TipoReporteVenta;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.repository.ReporteVentaRepository;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaService;

@Service
@Transactional(readOnly = true)
public class ReporteVentaServiceImpl implements ReporteVentaService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReporteVentaRepository repository;

    public ReporteVentaServiceImpl(ReporteVentaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ReporteVentaDto.General> general(ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.general(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.Ganancia> ganancias(ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.ganancias(validar(filtro), pageable);
    }

    @Override
    public List<ReporteVentaDto.DetalleGanancia> detalleGanancia(Long idVenta) {
        if (idVenta == null || idVenta < 1) throw new IllegalArgumentException("La venta es obligatoria");
        return repository.detalleGanancia(idVenta);
    }

    @Override
    public Page<ReporteVentaDto.ProductoVendido> productosMasVendidos(
            ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.productosVendidos(validar(filtro), pageable, true);
    }

    @Override
    public Page<ReporteVentaDto.ProductoVendido> productosMenosVendidos(
            ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.productosVendidos(validar(filtro), pageable, false);
    }

    @Override
    public Page<ReporteVentaDto.ProductoSinVenta> productosSinVentas(
            ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.productosSinVentas(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.PorMetodoPago> porMetodoPago(
            ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.porMetodoPago(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.PorCajero> porCajero(ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.porCajero(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.ContadoCredito> contadoCredito(
            ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.contadoCredito(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.PorPeriodo> porPeriodo(ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.porPeriodo(validar(filtro), pageable);
    }

    @Override
    public Page<ReporteVentaDto.PorCliente> porCliente(ReporteVentaFiltro filtro, Pageable pageable) {
        return repository.porCliente(validar(filtro), pageable);
    }

    @Override
    public ReporteVentaExportacion prepararExportacion(TipoReporteVenta tipo, ReporteVentaFiltro filtro) {
        ReporteVentaFiltro validado = validar(filtro);
        Pageable sinPaginacion = Pageable.unpaged();
        return switch (tipo) {
            case GENERAL -> exportacion(tipo, validado,
                    List.of("Fecha", "N.º venta", "Cliente", "Cajero", "Tipo", "Método de pago", "Estado", "Total"),
                    repository.general(validado, sinPaginacion).getContent(), item -> List.of(
                            fechaHora(item.fecha()), texto(item.numeroVenta()), texto(item.cliente()), texto(item.cajero()),
                            texto(item.tipoVenta()), texto(item.metodoPago()), texto(item.estado()), dinero(item.total())));
            case GANANCIAS -> exportacion(tipo, validado,
                    List.of("N.º venta", "Fecha", "Cajero", "Productos", "Total cobrado", "Costo",
                            "Ganancia neta", "Margen", "Estado del costo"),
                    repository.ganancias(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.numeroVenta()), fechaHora(item.fecha()), texto(item.cajero()),
                            texto(item.cantidadProductos()), dinero(item.totalVendido()), dineroDisponible(item.costoTotal()),
                            dineroDisponible(item.ganancia()), porcentajeDisponible(item.margenPorcentaje()),
                            texto(item.estadoCosto())));
            case PRODUCTOS_MAS_VENDIDOS -> productosExportacion(tipo, validado,
                    repository.productosVendidos(validado, sinPaginacion, true).getContent());
            case PRODUCTOS_MENOS_VENDIDOS -> productosExportacion(tipo, validado,
                    repository.productosVendidos(validado, sinPaginacion, false).getContent());
            case PRODUCTOS_SIN_VENTAS -> exportacion(tipo, validado,
                    List.of("Código", "Producto", "Categoría", "Stock", "Última venta", "Días sin venta"),
                    repository.productosSinVentas(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.codigo()), texto(item.producto()), texto(item.categoria()), numero(item.stockActual()),
                            fechaHora(item.ultimaVenta()), item.diasSinVenta() == null ? "Sin registro" : item.diasSinVenta().toString()));
            case METODOS_PAGO -> exportacion(tipo, validado,
                    List.of("Método de pago", "Operaciones", "Participación", "Total recibido"),
                    repository.porMetodoPago(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.metodoPago()), texto(item.cantidadOperaciones()), porcentaje(item.participacionPorcentaje()),
                            dinero(item.totalRecibido())));
            case CAJEROS -> exportacion(tipo, validado,
                    List.of("Cajero", "Caja", "Ventas", "Ticket promedio", "Total vendido"),
                    repository.porCajero(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.cajero()), texto(item.caja()), texto(item.cantidadVentas()),
                            dinero(item.ticketPromedio()), dinero(item.totalVendido())));
            case CONTADO_CREDITO -> exportacion(tipo, validado,
                    List.of("Tipo de venta", "Operaciones", "Importe total", "Monto cobrado", "Saldo pendiente"),
                    repository.contadoCredito(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.tipoVenta()), texto(item.cantidadOperaciones()), dinero(item.importeTotal()),
                            dinero(item.montoCobrado()), dinero(item.saldoPendiente())));
            case PERIODOS -> exportacion(tipo, validado,
                    List.of("Periodo", "Ventas", "Contado", "Crédito", "Total vendido", "Ticket promedio"),
                    repository.porPeriodo(validado, sinPaginacion).getContent(), item -> List.of(
                            fecha(item.periodo()), texto(item.cantidadVentas()), dinero(item.ventasContado()),
                            dinero(item.ventasCredito()), dinero(item.totalVendido()), dinero(item.ticketPromedio())));
            case CLIENTES -> exportacion(tipo, validado,
                    List.of("Documento", "Cliente", "Compras", "Última compra", "Contado", "Crédito", "Total gastado"),
                    repository.porCliente(validado, sinPaginacion).getContent(), item -> List.of(
                            texto(item.documento()), texto(item.cliente()), texto(item.cantidadCompras()),
                            fechaHora(item.ultimaCompra()), dinero(item.totalContado()), dinero(item.totalCredito()),
                            dinero(item.totalGastado())));
        };
    }

    private ReporteVentaExportacion productosExportacion(
            TipoReporteVenta tipo, ReporteVentaFiltro filtro, List<ReporteVentaDto.ProductoVendido> datos) {
        return exportacion(tipo, filtro,
                List.of("Código", "Producto", "Categoría", "Cantidad vendida", "Última venta", "Importe generado"),
                datos, item -> List.of(texto(item.codigo()), texto(item.producto()), texto(item.categoria()),
                        numero(item.cantidadVendida()), fechaHora(item.ultimaVenta()), dinero(item.importeGenerado())));
    }

    private <T> ReporteVentaExportacion exportacion(TipoReporteVenta tipo, ReporteVentaFiltro filtro,
            List<String> columnas, List<T> datos, Function<T, List<String>> conversor) {
        List<List<String>> filas = datos.stream().map(conversor).toList();
        return new ReporteVentaExportacion(tipo.titulo(), resumenFiltros(filtro), columnas, filas);
    }

    private ReporteVentaFiltro validar(ReporteVentaFiltro filtro) {
        ReporteVentaFiltro resultado = filtro == null ? new ReporteVentaFiltro() : filtro;
        LocalDate hoy = LocalDate.now();
        if ((resultado.getFechaInicio() != null && resultado.getFechaInicio().isAfter(hoy))
                || (resultado.getFechaFin() != null && resultado.getFechaFin().isAfter(hoy))) {
            throw new IllegalArgumentException("Las fechas del reporte no pueden ser futuras");
        }
        if (resultado.getFechaInicio() != null && resultado.getFechaFin() != null
                && resultado.getFechaInicio().isAfter(resultado.getFechaFin())) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        }
        if (resultado.getTipoVenta() != null) {
            String tipo = resultado.getTipoVenta().trim().toUpperCase(Locale.ROOT);
            if (!tipo.equals("CONTADO") && !tipo.equals("CREDITO")) {
                throw new IllegalArgumentException("tipoVenta debe ser CONTADO o CREDITO");
            }
            resultado.setTipoVenta(tipo);
        }
        String agrupacion = resultado.getAgrupacion() == null
                ? "DIA" : resultado.getAgrupacion().trim().toUpperCase(Locale.ROOT);
        if (!List.of("DIA", "SEMANA", "MES").contains(agrupacion)) {
            throw new IllegalArgumentException("agrupacion debe ser DIA, SEMANA o MES");
        }
        resultado.setAgrupacion(agrupacion);
        if (resultado.getMargenMinimo() != null && resultado.getMargenMaximo() != null
                && resultado.getMargenMinimo().compareTo(resultado.getMargenMaximo()) > 0) {
            throw new IllegalArgumentException("El margen mínimo no puede superar al margen máximo");
        }
        return resultado;
    }

    private String resumenFiltros(ReporteVentaFiltro filtro) {
        List<String> valores = new ArrayList<>();
        if (filtro.getFechaInicio() == null && filtro.getFechaFin() == null) {
            valores.add("Periodo: todas las fechas");
        } else {
            valores.add("Periodo: " + fecha(filtro.getFechaInicio()) + " - " + fecha(filtro.getFechaFin()));
        }
        if (filtro.getEstadoVenta() != null) valores.add("Estado: " + filtro.getEstadoVenta());
        if (filtro.getMetodoPago() != null) valores.add("Método: " + filtro.getMetodoPago());
        if (filtro.getTipoVenta() != null) valores.add("Tipo: " + filtro.getTipoVenta());
        if (filtro.getClienteId() != null) valores.add("Cliente ID: " + filtro.getClienteId());
        if (filtro.getCajeroId() != null) valores.add("Cajero ID: " + filtro.getCajeroId());
        if (filtro.getCajaId() != null) valores.add("Caja ID: " + filtro.getCajaId());
        if (filtro.getProductoId() != null) valores.add("Producto ID: " + filtro.getProductoId());
        if (filtro.getCategoriaId() != null) valores.add("Categoría ID: " + filtro.getCategoriaId());
        if (filtro.getMarcaId() != null) valores.add("Marca ID: " + filtro.getMarcaId());
        return String.join(" | ", valores);
    }

    private static String texto(Object valor) {
        return valor == null || valor.toString().isBlank() ? "-" : valor.toString();
    }

    private static String fecha(LocalDate valor) {
        return valor == null ? "-" : valor.format(FECHA);
    }

    private static String fechaHora(LocalDateTime valor) {
        return valor == null ? "Sin registro" : valor.format(FECHA_HORA);
    }

    private static String numero(BigDecimal valor) {
        return valor == null ? "0" : valor.stripTrailingZeros().toPlainString();
    }

    private static String dinero(BigDecimal valor) {
        return "Bs " + (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String dineroDisponible(BigDecimal valor) {
        return valor == null ? "No disponible" : dinero(valor);
    }

    private static String porcentaje(BigDecimal valor) {
        return (valor == null ? BigDecimal.ZERO : valor).setScale(2, RoundingMode.HALF_UP).toPlainString() + " %";
    }

    private static String porcentajeDisponible(BigDecimal valor) {
        return valor == null ? "No disponible" : porcentaje(valor);
    }
}
