package com.backendSupermercado.supermercasdo.modules.credito.controller;

import java.time.LocalDate;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.backendSupermercado.supermercasdo.modules.credito.dto.*;
import com.backendSupermercado.supermercasdo.modules.credito.service.CreditoService;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creditos")
public class CreditoController {
    private final CreditoService service;

    @GetMapping
    public Page<CreditoResponseDto> listar(@RequestParam(required = false) String buscar, @RequestParam(required = false) EstadoCuenta estado,
            @RequestParam(required = false) LocalDate desde, @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "6") int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("Paginación inválida");
        return service.listar(buscar, estado, desde, hasta, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaLimite")));
    }

    @GetMapping("/resumen")
    public CreditoResumenDto resumen() { return service.resumen(); }

    @PostMapping("/{idCuenta}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditoResponseDto registrarPago(@PathVariable Long idCuenta, @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody PagoCreditoRequestDto request) {
        if (user == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        return service.registrarPago(user.getId(), idCuenta, request);
    }
}
