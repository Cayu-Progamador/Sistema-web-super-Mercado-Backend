package com.backendSupermercado.supermercasdo.modules.seguridad.controller;

import org.springframework.web.bind.annotation.RestController;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDetalleDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolEstadisticaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    //lsiatr los roles para el nuevo usuario
    @GetMapping("/select-rol")
    public ResponseEntity<List<RolSelectDto>> selectRol() {
        return ResponseEntity.ok(
          rolService.listarRolSeleccionado()  
        );
    }

    //lista de roles para el crud (paginado)
    @GetMapping("/listar")
    public ResponseEntity<Page<RolDto>> listarRoleTable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(rolService.listarRoles(pageable));
    }

    //crear un nuevo rol
    @PostMapping("/crear")
    public ResponseEntity<RolRespuestaDto> crearRol(@RequestBody  RolRequestDto dto) {
        return ResponseEntity.ok(rolService.crearRol(dto));
    }
    //obtener estadisticas
    @GetMapping("/estadisticas")
    public ResponseEntity<RolEstadisticaDto> obtenerEstadisticas() {
        return ResponseEntity.ok(rolService.obtenerEstadisticas());
    }

    //activar rol
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarRol(@PathVariable Long id) {
        rolService.activarRol(id);
        return ResponseEntity.ok().build();
    }

    //desactivar rol
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarRol(@PathVariable Long id) {
        rolService.desactivarRol(id);
        return ResponseEntity.ok().build();
    }

    //buscar rol por nombre (paginado)
    @GetMapping("/buscar")
    public ResponseEntity<Page<RolDto>> buscarPorNombre(
            @RequestParam String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(rolService.buscarPorNombre(nombre, pageable));
    }

    //actualizar rol
    @PutMapping("/{id}")
    public ResponseEntity<RolRespuestaDto> actualizarRol(@PathVariable Long id, @RequestBody RolRequestDto dto) {
        return ResponseEntity.ok(rolService.actualizarRol(id, dto));
    }

    //detalle del rol
    @GetMapping("/{id}/detalle")
    public ResponseEntity<RolDetalleDto> obtenerDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.obtenerDetalle(id));
    }
    
}
