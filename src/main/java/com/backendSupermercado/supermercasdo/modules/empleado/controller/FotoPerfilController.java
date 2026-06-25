package com.backendSupermercado.supermercasdo.modules.empleado.controller;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.empleado.service.FotoPerfilService;
import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;
import com.backendSupermercado.supermercasdo.modules.usuario.repository.UsuarioRepository;
import com.backendSupermercado.supermercasdo.security.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/foto-perfil")
@RequiredArgsConstructor
public class FotoPerfilController {

    private final FotoPerfilService fotoPerfilService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/subir")
    public ResponseEntity<Map<String, String>> subirFoto(
            @RequestParam("foto") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long idPersona = obtenerIdPersona(userDetails);
        String nombreArchivo = fotoPerfilService.subirFoto(idPersona, file);
        String url = "/uploads/" + nombreArchivo;

        return ResponseEntity.ok(Map.of(
                "mensaje", "Foto actualizada correctamente",
                "url", url
        ));
    }

    @GetMapping("/ver")
    public ResponseEntity<Resource> verFoto(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long idPersona = obtenerIdPersona(userDetails);
        Resource resource = fotoPerfilService.obtenerFoto(idPersona);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Map<String, String>> eliminarFoto(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long idPersona = obtenerIdPersona(userDetails);
        fotoPerfilService.eliminarFoto(idPersona);

        return ResponseEntity.ok(Map.of("mensaje", "Foto eliminada correctamente"));
    }

    private Long obtenerIdPersona(CustomUserDetails userDetails) {
        Usuario usuario = usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getEmpleado() == null || usuario.getEmpleado().getPersona() == null) {
            throw new RuntimeException("El usuario no tiene un empleado asociado");
        }

        return usuario.getEmpleado().getPersona().getIdPersona();
    }
}
