package com.backendSupermercado.supermercasdo.modules.cliente.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteRequestDto;
import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteResponseDto;
import com.backendSupermercado.supermercasdo.modules.cliente.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponseDto> crearCliente(@Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearCliente(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ClienteResponseDto>> listarClientes(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Boolean estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombres") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(clienteService.listarClientes(busqueda, estado, pageable));
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteResponseDto> obtenerCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.obtenerCliente(idCliente));
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteResponseDto> actualizarCliente(
            @PathVariable Long idCliente,
            @Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(idCliente, dto));
    }

    @PatchMapping("/{idCliente}/activar")
    public ResponseEntity<ClienteResponseDto> activarCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.activarCliente(idCliente));
    }

    @PatchMapping("/{idCliente}/desactivar")
    public ResponseEntity<ClienteResponseDto> desactivarCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.desactivarCliente(idCliente));
    }
}
