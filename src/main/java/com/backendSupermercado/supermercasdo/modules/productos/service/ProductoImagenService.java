package com.backendSupermercado.supermercasdo.modules.productos.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProductoImagenService {

    String subirImagen(MultipartFile file);
}
