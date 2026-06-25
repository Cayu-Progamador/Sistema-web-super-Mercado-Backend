package com.backendSupermercado.supermercasdo.modules.empleado.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.FotoPerfil;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.FotoPerfilRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.repository.PersonaRepository;
import com.backendSupermercado.supermercasdo.modules.empleado.service.FotoPerfilService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FotoPerfilServiceImpl implements FotoPerfilService {

    private final FotoPerfilRepository fotoPerfilRepository;
    private final PersonaRepository personaRepository;

    @Value("${app.uploads.directorio:uploads/fotos-perfil}")
    private String directorioUploads;

    @Override
    @Transactional
    public String subirFoto(Long idPersona, MultipartFile file) {
        Persona persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Persona no encontrada con id: " + idPersona));

        validarArchivo(file);

        try {
            Files.createDirectories(Paths.get(directorioUploads));

            String nombreOriginal = file.getOriginalFilename();
            String extension = obtenerExtension(nombreOriginal);
            String nombreSinExtension = nombreOriginal != null && nombreOriginal.contains(".")
                    ? nombreOriginal.substring(0, nombreOriginal.lastIndexOf("."))
                    : nombreOriginal;
            String nombreArchivo = UUID.randomUUID().toString() + "_" + nombreSinExtension + extension;

            Path rutaDestino = Paths.get(directorioUploads).resolve(nombreArchivo);
            Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

            FotoPerfil fotoPerfil = persona.getFotoPerfil();
            if (fotoPerfil != null) {
                eliminarArchivoAnterior(fotoPerfil.getRutaArchivo());
                fotoPerfil.setRutaArchivo(nombreArchivo);
            } else {
                fotoPerfil = new FotoPerfil();
                fotoPerfil.setRutaArchivo(nombreArchivo);
                fotoPerfil.setPersona(persona);
                persona.setFotoPerfil(fotoPerfil);
            }

            fotoPerfilRepository.save(fotoPerfil);
            log.info("Foto de perfil subida para persona {}: {}", idPersona, nombreArchivo);

            return nombreArchivo;

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo", e);
        }
    }

    @Override
    public Resource obtenerFoto(Long idPersona) {
        FotoPerfil fotoPerfil = fotoPerfilRepository.findByPersonaIdPersona(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Foto no encontrada para persona: " + idPersona));

        try {
            Path rutaArchivo = Paths.get(directorioUploads).resolve(fotoPerfil.getRutaArchivo());
            Resource resource = new UrlResource(rutaArchivo.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("No se pudo leer el archivo: " + fotoPerfil.getRutaArchivo());
            }

            return resource;
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo", e);
        }
    }

    @Override
    @Transactional
    public void eliminarFoto(Long idPersona) {
        FotoPerfil fotoPerfil = fotoPerfilRepository.findByPersonaIdPersona(idPersona)
                .orElseThrow(() -> new EntityNotFoundException("Foto no encontrada para persona: " + idPersona));

        eliminarArchivoAnterior(fotoPerfil.getRutaArchivo());
        fotoPerfilRepository.delete(fotoPerfil);
        log.info("Foto de perfil eliminada para persona {}", idPersona);
    }

    @Override
    public String obtenerUrlFoto(Long idPersona) {
        return fotoPerfilRepository.findByPersonaIdPersona(idPersona)
                .map(foto -> "/uploads/" + foto.getRutaArchivo())
                .orElse(null);
    }

    private void validarArchivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("El archivo no puede superar los 5MB");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".jpg";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }

    private void eliminarArchivoAnterior(String rutaArchivo) {
        try {
            Path ruta = Paths.get(directorioUploads).resolve(rutaArchivo);
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo anterior: {}", rutaArchivo);
        }
    }
}
