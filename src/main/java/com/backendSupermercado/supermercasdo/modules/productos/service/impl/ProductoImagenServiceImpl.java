package com.backendSupermercado.supermercasdo.modules.productos.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.productos.service.ProductoImagenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductoImagenServiceImpl implements ProductoImagenService {

    private static final Set<String> FORMATOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @Value("${app.uploads.directorio-productos:uploads/productos}")
    private String directorioProductos;

    @Override
    public String subirImagen(MultipartFile file) {
        validarArchivo(file);

        try {
            Files.createDirectories(Paths.get(directorioProductos));

            String extension = obtenerExtension(file.getOriginalFilename());
            String nombreArchivo = UUID.randomUUID().toString() + extension;

            Path rutaDestino = Paths.get(directorioProductos).resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            log.info("Imagen de producto subida: {}", nombreArchivo);
            return nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen del producto", e);
        }
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !FORMATOS_PERMITIDOS.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Formato no permitido. Usa JPG, JPEG, PNG o WEBP.");
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("El archivo no puede superar los 5MB");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".jpg";
        }
        String ext = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
        return ext.toLowerCase();
    }
}
