package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.DashboardEmpleadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoDisponibleDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoRequestDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoSelectDto;
import com.backendSupermercado.supermercasdo.modules.empleado.service.EmpleadoService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping("/estadisticas")
    public ResponseEntity<DashboardEmpleadoDto> obtenerEstadisticasEmpleados() {
        return ResponseEntity.ok(empleadoService.obtenerEstadisticasEmpleado());
    }

    @GetMapping("/select")
    public ResponseEntity<List<EmpleadoSelectDto>> listarEmpleadoSeleccionado() {
        return ResponseEntity.ok(
            empleadoService.listarParaSelect()
        );
    }

    @GetMapping("/editar/{usuarioId}")
    public ResponseEntity<List<EmpleadoSelectDto>> listarParaEditar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(
            empleadoService.listarParaEditar(usuarioId)
        );
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<EmpleadoListadoDto>> listarEmpleados(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idEmpleado") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        String campoOrden = switch (sortBy) {
            case "nombreCompleto" -> "persona.nombres";
            case "fechaCreacion" -> "fechaContratacion";
            default -> sortBy;
        };
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(campoOrden).descending()
                : Sort.by(campoOrden).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(empleadoService.listarEmpleados(busqueda, estado, fechaDesde, fechaHasta, pageable));
    }

    @GetMapping("/disponibles-para-contrato")
    public ResponseEntity<Page<EmpleadoDisponibleDto>> listarDisponiblesParaContrato(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(empleadoService.listarDisponiblesParaContrato(busqueda, pageable));
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponseDto> crearEmpleado(@RequestBody EmpleadoRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crearEmpleado(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> actualizarEmpleado(
            @PathVariable Long id,
            @RequestBody EmpleadoRequestDto dto) {
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponseDto> obtenerEmpleado(@PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerEmpleado(id));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarEmpleado(@PathVariable Long id) {
        empleadoService.activarEmpleado(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarEmpleado(@PathVariable Long id) {
        empleadoService.desactivarEmpleado(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarEmpleadoDetallePDF(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = empleadoService.exportarEmpleadoDetallePDF(id, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ficha_empleado_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarEmpleadosPDF(
            EmpleadoFiltrosDto filtros,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = empleadoService.exportarEmpleadosPDF(filtros, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_empleados.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarEmpleadosExcel(EmpleadoFiltrosDto filtros) {
        byte[] excelBytes = empleadoService.exportarEmpleadosExcel(filtros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_empleados.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}

