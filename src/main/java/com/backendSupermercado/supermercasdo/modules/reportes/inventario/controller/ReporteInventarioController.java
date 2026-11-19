package com.backendSupermercado.supermercasdo.modules.reportes.inventario.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.backendSupermercado.supermercasdo.modules.reportes.inventario.model.TipoReporteInventario;
import com.backendSupermercado.supermercasdo.modules.reportes.inventario.repository.ReporteInventarioRepository;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaExcelService;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaPdfService;

@RestController
@RequestMapping("/api/reportes/inventario")
public class ReporteInventarioController {
    private final ReporteInventarioRepository repo;
    private final ReporteVentaPdfService pdf;
    private final ReporteVentaExcelService excel;

    public ReporteInventarioController(ReporteInventarioRepository repo, ReporteVentaPdfService pdf, ReporteVentaExcelService excel) {
        this.repo = repo; this.pdf = pdf; this.excel = excel;
    }

    @GetMapping("/{tipo}")
    public Page<Map<String, Object>> consultar(@PathVariable String tipo, @RequestParam Map<String, String> filtros,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return repo.consultar(TipoReporteInventario.desdeRuta(tipo).ruta(), normalizar(filtros), PageRequest.of(page, size));
    }

    @GetMapping(value = "/{tipo}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String tipo, @RequestParam Map<String, String> filtros) {
        TipoReporteInventario t = TipoReporteInventario.desdeRuta(tipo);
        return archivo(pdf.generar(exportar(t, normalizar(filtros))), "reporte-inventario-" + t.ruta() + ".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping(value = "/{tipo}/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> excel(@PathVariable String tipo, @RequestParam Map<String, String> filtros) {
        TipoReporteInventario t = TipoReporteInventario.desdeRuta(tipo);
        MediaType media = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return archivo(excel.generar(exportar(t, normalizar(filtros))), "reporte-inventario-" + t.ruta() + ".xlsx", media);
    }

    private ReporteVentaExportacion exportar(TipoReporteInventario t, Map<String, Object> filtros) {
        List<Map<String, Object>> datos = repo.consultar(t.ruta(), filtros, Pageable.unpaged()).getContent();
        List<String> columnas;
        List<String> claves;
        if (t == TipoReporteInventario.EXISTENCIAS) { columnas = List.of("Código", "Producto", "Categoría", "Almacén", "Ubicación", "Stock"); claves = List.of("codigo", "producto", "categoria", "almacen", "ubicacion", "stock"); }
        else if (t == TipoReporteInventario.STOCK_MINIMO || t == TipoReporteInventario.AGOTADOS) { columnas = List.of("Código", "Producto", "Categoría", "Mínimo", "Stock"); claves = List.of("codigo", "producto", "categoria", "minimo", "stock"); }
        else if (t == TipoReporteInventario.PROXIMOS || t == TipoReporteInventario.VENCIDOS) { columnas = List.of("Lote", "Código", "Producto", "Almacén", "Vencimiento", "Cantidad"); claves = List.of("lote", "codigo", "producto", "almacen", "vencimiento", "cantidad"); }
        else if (t == TipoReporteInventario.KARDEX) { columnas = List.of("Fecha", "Referencia", "Movimiento", "Código", "Producto", "Cantidad", "Saldo", "Ubicación", "Responsable"); claves = List.of("fecha", "referencia", "movimiento", "codigo", "producto", "cantidad", "saldo", "ubicacion", "responsable"); }
        else if (t == TipoReporteInventario.TRANSFERENCIAS) { columnas = List.of("Fecha", "Transferencia", "Código producto", "Producto", "Origen", "Destino", "Cantidad", "Estado", "Responsable"); claves = List.of("fecha", "codigo", "codigoProducto", "producto", "origen", "destino", "cantidad", "estado", "responsable"); }
        else { columnas = List.of("Código", "Fecha", "Estado", "Almacén", "Ubicación", "Código producto", "Producto", "Cantidad sistema", "Cantidad física", "Diferencia", "Responsable"); claves = List.of("codigo", "fecha", "estado", "almacen", "ubicacion", "codigoProducto", "producto", "cantidadSistema", "cantidadFisica", "diferencia", "responsable"); }
        List<List<String>> filas = datos.stream().map(fila -> { List<String> filaExportada = new ArrayList<>(); claves.forEach(clave -> filaExportada.add(valor(fila, clave))); return filaExportada; }).toList();
        return new ReporteVentaExportacion(t.titulo(), "Filtros aplicados: " + filtros, columnas, filas);
    }

    private String valor(Map<String, Object> fila, String clave) {
        Object dato = fila.get(clave);
        if (dato == null) dato = fila.get(clave.toLowerCase());
        if (dato == null) {
            String buscada = clave.replace("_", "").toLowerCase();
            for (Map.Entry<String, Object> entrada : fila.entrySet()) {
                String nombre = entrada.getKey().replace("_", "").toLowerCase();
                if (nombre.equals(buscada)) {
                    dato = entrada.getValue();
                    break;
                }
            }
        }
        if (dato == null && ("cantidadSistema".equals(clave) || "cantidadFisica".equals(clave) || "diferencia".equals(clave))) {
            return "0";
        }
        return dato == null ? "-" : String.valueOf(dato);
    }
    private Map<String, Object> normalizar(Map<String, String> entrada) { Map<String, Object> salida = new LinkedHashMap<>(); entrada.forEach((k, v) -> { if (v != null && !v.isBlank()) { try { salida.put(k, k.endsWith("Id") ? Long.valueOf(v) : v); } catch (Exception e) { throw new IllegalArgumentException("Filtro de inventario inválido"); } } }); return salida; }
    private ResponseEntity<byte[]> archivo(byte[] bytes, String nombre, MediaType tipo) { return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore()).contentType(tipo).contentLength(bytes.length).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombre + "\"").header("X-Generated-Date", LocalDate.now().toString()).body(bytes); }
}
