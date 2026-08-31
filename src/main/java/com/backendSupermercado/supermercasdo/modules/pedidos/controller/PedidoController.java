package com.backendSupermercado.supermercasdo.modules.pedidos.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoListadoDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoRequestDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.service.PedidoService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoListadoDto> crear(
            @Valid @RequestBody PedidoRequestDto dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(idUsuario, dto));
    }

    @GetMapping("/estados")
    public ResponseEntity<List<String>> listarEstados() {
        return ResponseEntity.ok(pedidoService.listarEstados());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoListadoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PedidoRequestDto dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idUsuario = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(pedidoService.actualizar(id, idUsuario, dto));
    }

    @PatchMapping("/{id}/enviar")
    public ResponseEntity<PedidoListadoDto> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.enviar(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoListadoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoListadoDto> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Sistema";
        byte[] pdf = pedidoService.exportarPdf(id, username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Pedido_" + id + ".pdf");
        headers.setContentLength(pdf.length);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @GetMapping
    public ResponseEntity<Page<PedidoListadoDto>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long idProveedor,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaPedido") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        String campoOrden = switch (sortBy) {
            case "proveedor" -> "proveedor.empresa.razonSocial";
            case "estado" -> "estadoPedido.nombre";
            default -> sortBy;
        };

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(campoOrden).descending()
                : Sort.by(campoOrden).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(pedidoService.listar(estado, search, idProveedor,
                parseFecha(fechaDesde), parseFecha(fechaHasta), pageable));
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Long>> kpi() {
        Map<String, Long> kpi = new HashMap<>();
        kpi.put("total", pedidoService.contarPorEstado(null));
        kpi.put("borradores", pedidoService.contarPorEstado("BORRADOR"));
        kpi.put("enviados", pedidoService.contarPorEstado("ENVIADO"));
        kpi.put("completados", pedidoService.contarPorEstado("COMPLETADO"));
        kpi.put("cancelados", pedidoService.contarPorEstado("CANCELADO"));
        kpi.put("parcialmenteRecibidos", pedidoService.contarPorEstado("PARCIALMENTE_RECIBIDO"));
        return ResponseEntity.ok(kpi);
    }
}