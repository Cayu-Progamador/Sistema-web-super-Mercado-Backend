package com.backendSupermercado.supermercasdo.modules.reportes.venta.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaDto;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaFiltro;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.model.TipoReporteVenta;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaExcelService;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaPdfService;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaService;

@RestController
@RequestMapping("/api/reportes/ventas")
public class ReporteVentaController {

    private final ReporteVentaService service;
    private final ReporteVentaPdfService pdfService;
    private final ReporteVentaExcelService excelService;

    public ReporteVentaController(ReporteVentaService service, ReporteVentaPdfService pdfService,
            ReporteVentaExcelService excelService) {
        this.service = service;
        this.pdfService = pdfService;
        this.excelService = excelService;
    }

    @GetMapping("/general")
    public Page<ReporteVentaDto.General> general(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.general(filtro, pagina(page, size));
    }

    @GetMapping("/ganancias")
    public Page<ReporteVentaDto.Ganancia> ganancias(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.ganancias(filtro, pagina(page, size));
    }

    @GetMapping("/ganancias/{idVenta}/detalle")
    public List<ReporteVentaDto.DetalleGanancia> detalleGanancia(@PathVariable Long idVenta) {
        return service.detalleGanancia(idVenta);
    }

    @GetMapping("/productos-mas-vendidos")
    public Page<ReporteVentaDto.ProductoVendido> productosMasVendidos(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.productosMasVendidos(filtro, pagina(page, size));
    }

    @GetMapping("/productos-menos-vendidos")
    public Page<ReporteVentaDto.ProductoVendido> productosMenosVendidos(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.productosMenosVendidos(filtro, pagina(page, size));
    }

    @GetMapping("/productos-sin-ventas")
    public Page<ReporteVentaDto.ProductoSinVenta> productosSinVentas(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.productosSinVentas(filtro, pagina(page, size));
    }

    @GetMapping("/metodos-pago")
    public Page<ReporteVentaDto.PorMetodoPago> metodosPago(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.porMetodoPago(filtro, pagina(page, size));
    }

    @GetMapping("/cajeros")
    public Page<ReporteVentaDto.PorCajero> cajeros(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.porCajero(filtro, pagina(page, size));
    }

    @GetMapping("/contado-credito")
    public Page<ReporteVentaDto.ContadoCredito> contadoCredito(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.contadoCredito(filtro, pagina(page, size));
    }

    @GetMapping("/periodos")
    public Page<ReporteVentaDto.PorPeriodo> periodos(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.porPeriodo(filtro, pagina(page, size));
    }

    @GetMapping("/clientes")
    public Page<ReporteVentaDto.PorCliente> clientes(@ModelAttribute ReporteVentaFiltro filtro,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return service.porCliente(filtro, pagina(page, size));
    }

    @GetMapping(value = "/{tipo}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String tipo, @ModelAttribute ReporteVentaFiltro filtro) {
        TipoReporteVenta tipoReporte = TipoReporteVenta.desdeRuta(tipo);
        ReporteVentaExportacion reporte = service.prepararExportacion(tipoReporte, filtro);
        return archivo(pdfService.generar(reporte), "reporte-ventas-" + tipoReporte.ruta() + ".pdf",
                MediaType.APPLICATION_PDF);
    }

    @GetMapping(value = "/{tipo}/excel",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> excel(@PathVariable String tipo, @ModelAttribute ReporteVentaFiltro filtro) {
        TipoReporteVenta tipoReporte = TipoReporteVenta.desdeRuta(tipo);
        ReporteVentaExportacion reporte = service.prepararExportacion(tipoReporte, filtro);
        MediaType excel = MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return archivo(excelService.generar(reporte), "reporte-ventas-" + tipoReporte.ruta() + ".xlsx", excel);
    }

    private Pageable pagina(int page, int size) {
        if (page < 0) throw new IllegalArgumentException("La página no puede ser negativa");
        if (size < 1 || size > 100) throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");
        return PageRequest.of(page, size);
    }

    private ResponseEntity<byte[]> archivo(byte[] contenido, String nombre, MediaType tipo) {
        return ResponseEntity.ok()
                .contentType(tipo)
                .contentLength(contenido.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"")
                .header("X-Generated-Date", LocalDate.now().toString())
                .body(contenido);
    }
}
