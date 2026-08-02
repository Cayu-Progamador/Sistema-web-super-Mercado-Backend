package com.backendSupermercado.supermercasdo.modules.usuario.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
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

import com.backendSupermercado.supermercasdo.modules.usuario.dto.CambiarPasswordrequestDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.ActualizarPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.DashboardUsuarioDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioDetalleDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioListadoResponseDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioPerfilDto;
import com.backendSupermercado.supermercasdo.modules.usuario.dto.UsuarioUpdateDto;
import com.backendSupermercado.supermercasdo.modules.usuario.service.UsuarioService;
import com.backendSupermercado.supermercasdo.security.auth.dto.RegistroRequestDto;
import com.backendSupermercado.supermercasdo.security.auth.dto.UsuarioResponseDto;
import com.backendSupermercado.supermercasdo.security.auth.service.AuthService;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    // ver perfil del usuario logueado
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDto> getProfile() {
        UsuarioPerfilDto perfil = usuarioService.getMyProfile();
        return ResponseEntity.ok(perfil);
    }

    // cambiar contrasena del usuario logueado
    @PostMapping("/cambiar-contrasena")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @RequestBody CambiarPasswordrequestDto request) {

        usuarioService.changePassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Contraseña cambiada correctamente"));
    }

    // actualizar correo y telefono del usuario logueado
    @PutMapping("/perfil")
    public ResponseEntity<Map<String, String>> actualizarPerfil(
            @RequestBody ActualizarPerfilDto dto) {

        usuarioService.actualizarMiPerfil(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Perfil actualizado correctamente"));
    }

    // listar usuarios
    @GetMapping("/listar")
    public ResponseEntity<Page<UsuarioListadoResponseDto>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idUsuario"));

        return ResponseEntity.ok(usuarioService.listarUsuarios(pageable));
    }

    // REGISTER nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDto> register(@RequestBody RegistroRequestDto request) {
        UsuarioResponseDto response = authService.registrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // poner inactivo al usuario
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivar(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Usuario desactivado correctamente");
        response.put("status", "OK");

        return ResponseEntity.ok(response);
    }

    // activar el usuario
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Map<String, Object>> activar(@PathVariable Long id) {
        usuarioService.activarUsuario(id);

        Map<String, Object> response = new HashMap<>();

        response.put("message", "Usuario activado correctamente");
        response.put("status", "OK");

        return ResponseEntity.ok(response);
    }

    // obtener estadisticas del usuario
    @GetMapping("/estadisticas")
    public ResponseEntity<DashboardUsuarioDto> obtenerEstadisticasUsuarios() {
        DashboardUsuarioDto dashboard = usuarioService.obtenerEstadisticasUsuario();
        return ResponseEntity.ok(dashboard);
    }

    // actualizar usuario
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Void> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody UsuarioUpdateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        usuarioService.actualizarUsuario(id, dto, userDetails.getId());

        return ResponseEntity.ok().build();
    }

    // buscar usuario por nombre
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioListadoResponseDto>> buscarUsuario(@RequestParam String username) {
        List<UsuarioListadoResponseDto> usuarios = usuarioService.buscarPorUsername(username);
        return ResponseEntity.ok(usuarios);
    }

    // buscar usuario por nombre paginado
    @GetMapping("/buscar-paginado")
    public ResponseEntity<Page<UsuarioListadoResponseDto>> buscarUsuarioPaginado(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idUsuario"));
        return ResponseEntity.ok(usuarioService.buscarPorUsernamePaginado(username, pageable));
    }

    //filtrar usuarios con criterios dinámicos
    @GetMapping("/filtrar")
    public ResponseEntity<Page<UsuarioListadoResponseDto>> filtrarUsuarios(
            UsuarioFiltrosDto filtros,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "idUsuario"));
        return ResponseEntity.ok(usuarioService.filtrarUsuarios(filtros, pageable));
    }

    //detalle de usuario
    @GetMapping("/detalle/{id}")
    public ResponseEntity<UsuarioDetalleDto> obtenerDetalleUsuarios(@PathVariable Long id){
        UsuarioDetalleDto usuario = usuarioService.obtenerDetalleUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    //exportar detalle de usuario a PDF
    @GetMapping("/detalle/{id}/pdf")
    public ResponseEntity<byte[]> exportarDetalleUsuarioPDF(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = usuarioService.exportarUsuarioDetallePDF(id, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ficha_usuario_" + id + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    //exportar usuarios (sin paginación)
    @GetMapping("/exportar")
    public ResponseEntity<List<UsuarioListadoResponseDto>> exportarUsuarios(UsuarioFiltrosDto filtros) {
        return ResponseEntity.ok(usuarioService.exportarUsuarios(filtros));
    }

    //exportar usuarios a PDF
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarUsuariosPDF(
            UsuarioFiltrosDto filtros,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "Usuario";
        byte[] pdfBytes = usuarioService.exportarUsuariosPDF(filtros, username);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_usuarios.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    //exportar usuarios a Excel
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarUsuariosExcel(UsuarioFiltrosDto filtros) {
        byte[] excelBytes = usuarioService.exportarUsuariosExcel(filtros);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_usuarios.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }

}
