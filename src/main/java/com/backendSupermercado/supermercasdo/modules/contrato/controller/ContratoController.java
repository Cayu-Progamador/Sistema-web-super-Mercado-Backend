package com.backendSupermercado.supermercasdo.modules.contrato.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetalleDto;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDashboardDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoListadoDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.RenovarContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoRequestDto;
import com.backendSupermercado.supermercasdo.modules.contrato.service.ContratoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;

    @PostMapping
    public ResponseEntity<ContratoListadoDto> crear(@Valid @RequestBody ContratoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContratoListadoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContratoRequestDto dto) {
        return ResponseEntity.ok(contratoService.actualizar(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoListadoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.obtenerPorId(id));
    }

    @GetMapping("/{id}/detalle")
    public ResponseEntity<ContratoDetalleDto> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.obtenerDetalle(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = contratoService.exportarPdf(id, username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Contrato_" + id + ".pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<ContratoListadoDto> renovar(
            @PathVariable Long id,
            @Valid @RequestBody RenovarContratoRequestDto dto) {
        return ResponseEntity.ok(contratoService.renovar(id, dto));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ContratoListadoDto> activar(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.activar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ContratoListadoDto> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.desactivar(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ContratoListadoDto> finalizar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivoFin) {
        return ResponseEntity.ok(contratoService.finalizar(id, motivoFin));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ContratoDashboardDto> dashboard() {
        return ResponseEntity.ok(contratoService.obtenerDashboard());
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean controlaAsistencia,
            @RequestParam(required = false) String tipoContrato,
            @RequestParam(required = false) String tipoJornada,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String fechaFinDesde,
            @RequestParam(required = false) String fechaFinHasta,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ContratoFiltrosDto filtros = new ContratoFiltrosDto();
        filtros.setBusqueda(busqueda);
        filtros.setEstado(estado);
        filtros.setControlaAsistencia(controlaAsistencia);
        filtros.setTipoContrato(tipoContrato);
        filtros.setTipoJornada(tipoJornada);
        filtros.setEmpleadoId(empleadoId);
        filtros.setFechaDesde(fechaDesde);
        filtros.setFechaHasta(fechaHasta);
        filtros.setFechaFinDesde(fechaFinDesde);
        filtros.setFechaFinHasta(fechaFinHasta);
        String username = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = contratoService.exportarContratosPDF(filtros, username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_contratos.pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean controlaAsistencia,
            @RequestParam(required = false) String tipoContrato,
            @RequestParam(required = false) String tipoJornada,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String fechaFinDesde,
            @RequestParam(required = false) String fechaFinHasta) {
        ContratoFiltrosDto filtros = new ContratoFiltrosDto();
        filtros.setBusqueda(busqueda);
        filtros.setEstado(estado);
        filtros.setControlaAsistencia(controlaAsistencia);
        filtros.setTipoContrato(tipoContrato);
        filtros.setTipoJornada(tipoJornada);
        filtros.setEmpleadoId(empleadoId);
        filtros.setFechaDesde(fechaDesde);
        filtros.setFechaHasta(fechaHasta);
        filtros.setFechaFinDesde(fechaFinDesde);
        filtros.setFechaFinHasta(fechaFinHasta);
        byte[] excel = contratoService.exportarContratosExcel(filtros);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_contratos.xlsx");
        headers.setContentLength(excel.length);
        return ResponseEntity.ok().headers(headers).body(excel);
    }

    @GetMapping
    public ResponseEntity<Page<ContratoListadoDto>> listarContratos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean controlaAsistencia,
            @RequestParam(required = false) String tipoContrato,
            @RequestParam(required = false) String tipoJornada,
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String fechaFinDesde,
            @RequestParam(required = false) String fechaFinHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        String campoOrden = switch (sortBy) {
            case "cargoNombre" -> "cargo.nombre";
            case "tipoContratoNombre" -> "tipoContrato.nombre";
            case "tipoJornadaNombre" -> "tipoJornada.nombre";
            default -> sortBy;
        };

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(campoOrden).descending()
                : Sort.by(campoOrden).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(contratoService.listarContratos(
                search, estado, controlaAsistencia, tipoContrato, tipoJornada,
                empleadoId, fechaDesde, fechaHasta, fechaFinDesde, fechaFinHasta, pageable));
    }
}
