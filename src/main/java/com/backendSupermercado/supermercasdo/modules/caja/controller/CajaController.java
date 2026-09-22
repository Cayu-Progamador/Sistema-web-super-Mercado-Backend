package com.backendSupermercado.supermercasdo.modules.caja.controller;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.backendSupermercado.supermercasdo.modules.caja.dto.*;
import com.backendSupermercado.supermercasdo.modules.caja.service.CajaService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cajas")
public class CajaController {
    private final CajaService service;

    @GetMapping
    public java.util.List<CajaResponse> cajas() {
        return service.listarCajas();
    }

    @PostMapping("/sesiones/abrir")
    @ResponseStatus(HttpStatus.CREATED)
    public SesionCajaResponse abrir(@AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody AbrirCajaRequest r) {
        if (user == null)
            throw new IllegalArgumentException("Usuario autenticado requerido");
        return service.abrir(user.getId(), r);
    }

    @PostMapping("/sesiones/{id}/cerrar")
    public SesionCajaResponse cerrar(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id,
            @Valid @RequestBody CerrarCajaRequest r) {
        if (user == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        return service.cerrar(user.getId(), id, r);
    }

    @GetMapping("/sesiones")
    public Page<SesionCajaResponse> sesiones(@AuthenticationPrincipal CustomUserDetails user, @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long idCaja, @RequestParam(required = false) java.time.LocalDate desde,
            @RequestParam(required = false) java.time.LocalDate hasta, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        if (user == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaApertura"));
        boolean admin = user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ADMIN".equals(a.getAuthority()));
        java.time.LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
        java.time.LocalDateTime fin = hasta == null ? null : hasta.plusDays(1).atStartOfDay().minusNanos(1);
        return admin ? service.listarSesiones(buscar, idCaja, inicio, fin, pageable)
                : service.listarMisSesiones(user.getId(), buscar, idCaja, inicio, fin, pageable);
    }

    @GetMapping("/sesiones/abierta")
    public SesionCajaResponse abierta(@AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        return service.sesionAbierta(user.getId());
    }

    @GetMapping("/mis-sesiones")
    public Page<SesionCajaResponse> misSesiones(@AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (user == null) throw new IllegalArgumentException("Usuario autenticado requerido");
        return service.listarMisSesiones(user.getId(), null, null, null, null, PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "fechaApertura")));
    }
}
