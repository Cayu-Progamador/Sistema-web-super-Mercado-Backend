package com.backendSupermercado.supermercasdo.modules.reportes.caja.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.backendSupermercado.supermercasdo.modules.reportes.caja.model.TipoReporteCaja;
import com.backendSupermercado.supermercasdo.modules.reportes.caja.repository.ReporteCajaRepository;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaExcelService;
import com.backendSupermercado.supermercasdo.modules.reportes.venta.service.ReporteVentaPdfService;

@RestController
@RequestMapping("/api/reportes/caja")
public class ReporteCajaController {
    private final ReporteCajaRepository repository;
    private final ReporteVentaPdfService pdf;
    private final ReporteVentaExcelService excel;
    public ReporteCajaController(ReporteCajaRepository repository, ReporteVentaPdfService pdf,
            ReporteVentaExcelService excel) { this.repository = repository; this.pdf = pdf; this.excel = excel; }

    @GetMapping("/{tipo}")
    public Page<Map<String,Object>> consultar(@PathVariable String tipo,
            @RequestParam Map<String,String> filtros,
            @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return repository.consultar(TipoReporteCaja.desdeRuta(tipo).ruta(), normalizar(filtros), pagina(page,size));
    }

    @GetMapping(value="/{tipo}/pdf", produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable String tipo, @RequestParam Map<String,String> filtros) {
        TipoReporteCaja t=TipoReporteCaja.desdeRuta(tipo);
        byte[] archivo=pdf.generar(exportacion(t, normalizar(filtros)));
        return archivo(archivo, "reporte-caja-"+t.ruta()+".pdf", MediaType.APPLICATION_PDF);
    }

    @GetMapping(value="/{tipo}/excel", produces="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> excel(@PathVariable String tipo, @RequestParam Map<String,String> filtros) {
        TipoReporteCaja t=TipoReporteCaja.desdeRuta(tipo);
        MediaType media=MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return archivo(excel.generar(exportacion(t, normalizar(filtros))), "reporte-caja-"+t.ruta()+".xlsx", media);
    }

    private ReporteVentaExportacion exportacion(TipoReporteCaja tipo, Map<String,Object> filtros) {
        List<Map<String,Object>> filas=repository.consultar(tipo.ruta(), filtros, Pageable.unpaged()).getContent();
        List<String> columnas=switch(tipo) {
            case SESIONES -> List.of("Sesión","Fecha","Caja","Cajero","Monto inicial","Monto contado","Estado");
            case MOVIMIENTOS -> List.of("Fecha","Sesión","Tipo","Concepto","Responsable","Monto");
            case CONCILIACION -> List.of("Sesión","Fecha","Caja","Cajero","Monto esperado","Monto contado","Diferencia","Resultado");
            case RECAUDACION_METODO -> List.of("Método de pago","Operaciones","Sesiones","Participación","Recaudación");
            case VENTAS_SESION -> List.of("Sesión","Fecha","Caja","Cajero","Ventas","Recaudación","Estado");
            case RENDIMIENTO_CAJERO -> List.of("Cajero","Sesiones","Ventas","Recaudación","Promedio por venta","Diferencia");
        };
        List<List<String>> valores=filas.stream().map(f -> switch(tipo) {
            case SESIONES -> fila(f,"sesion","fecha","caja","cajero","apertura","cierre","estado");
            case MOVIMIENTOS -> fila(f,"fecha","sesion","tipo","concepto","responsable","monto");
            case CONCILIACION -> fila(f,"sesion","fecha","caja","cajero","esperado","contado","diferencia","resultado");
            case RECAUDACION_METODO -> fila(f,"metodo","operaciones","sesiones","porcentaje","total");
            case VENTAS_SESION -> fila(f,"sesion","fecha","caja","cajero","ventas","recaudacion","estado");
            case RENDIMIENTO_CAJERO -> fila(f,"cajero","sesiones","ventas","recaudacion","promedio","diferencia");
        }).toList();
        return new ReporteVentaExportacion(tipo.titulo(), resumen(filtros), columnas, valores);
    }
    private List<String> fila(Map<String,Object> f,String... campos) { List<String> r=new ArrayList<>(); for(String c:campos) r.add(f.get(c)==null?"-":String.valueOf(f.get(c))); return r; }
    private String resumen(Map<String,Object> f) { return "Filtros aplicados: " + f; }
    private Map<String,Object> normalizar(Map<String,String> entrada) {
        Map<String,Object> f=new LinkedHashMap<>();
        entrada.forEach((k,v)->{ if(v!=null && !String.valueOf(v).isBlank()) f.put(k, convertir(k,String.valueOf(v))); });
        LocalDate hoy=LocalDate.now();
        LocalDate inicio=fecha(f.get("fechaInicio")); LocalDate fin=fecha(f.get("fechaFin"));
        if ((inicio!=null && inicio.isAfter(hoy)) || (fin!=null && fin.isAfter(hoy))) throw new IllegalArgumentException("Las fechas del reporte no pueden ser futuras");
        if (inicio!=null && fin!=null && inicio.isAfter(fin)) throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
        return f;
    }
    private LocalDate fecha(Object valor) { if(valor==null) return null; try{return LocalDate.parse(String.valueOf(valor));}catch(Exception e){throw new IllegalArgumentException("La fecha del reporte no es válida");} }
    private Object convertir(String k,String v) {
        if (k.endsWith("Id") || k.equals("cajaId") || k.equals("cajeroId") || k.equals("sesionId")) try{return Long.valueOf(v);}catch(Exception ignored){}
        return v;
    }
    private Pageable pagina(int page,int size) { if(page<0||size<1||size>100) throw new IllegalArgumentException("Paginación no válida"); return PageRequest.of(page,size); }
    private ResponseEntity<byte[]> archivo(byte[] contenido,String nombre,MediaType tipo) { return ResponseEntity.ok().contentType(tipo).contentLength(contenido.length).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+nombre+"\"").header("X-Generated-Date", LocalDate.now().toString()).body(contenido); }
}
