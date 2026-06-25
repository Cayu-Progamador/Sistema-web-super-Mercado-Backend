package com.backendSupermercado.supermercasdo.modules.empleado.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FotoPerfilService {
    String subirFoto(Long idPersona, MultipartFile file);
    Resource obtenerFoto(Long idPersona);
    void eliminarFoto(Long idPersona);
    String obtenerUrlFoto(Long idPersona);
}
