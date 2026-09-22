package com.backendSupermercado.supermercasdo.modules.configuracion.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaRequestDto;
import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaResponseDto;
import com.backendSupermercado.supermercasdo.modules.configuracion.entity.ConfiguracionEmpresa;
import com.backendSupermercado.supermercasdo.modules.configuracion.repository.ConfiguracionEmpresaRepository;
import com.backendSupermercado.supermercasdo.modules.configuracion.service.ConfiguracionEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionEmpresaServiceImpl implements ConfiguracionEmpresaService {
    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    @Value("${app.uploads.directorio-configuracion:uploads/configuracion}")
    private String directorioConfiguracion;

    @Value("${app.empresa.nombre:SUPERMERCADO}")
    private String nombreInicial;

    @Value("${app.empresa.nit:}")
    private String nitInicial;

    @Value("${app.empresa.direccion:}")
    private String direccionInicial;

    @Value("${app.empresa.telefono:}")
    private String telefonoInicial;

    @Value("${app.empresa.ciudad:}")
    private String departamentoInicial;

    @Value("${app.empresa.logo:}")
    private String logoInicial;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracionEmpresaResponseDto obtener() {
        return configuracionEmpresaRepository.findFirstByOrderByIdAsc()
                .map(this::respuesta)
                .orElseGet(this::respuestaInicial);
    }

    @Override
    @Transactional
    public ConfiguracionEmpresaResponseDto guardar(
            ConfiguracionEmpresaRequestDto request,
            MultipartFile logo,
            MultipartFile fondoLogin) {
        validar(request);

        ConfiguracionEmpresa configuracion = configuracionEmpresaRepository.findFirstByOrderByIdAsc()
                .orElseGet(ConfiguracionEmpresa::new);
        configuracion.setNombreComercial(limpiar(request.getNombreComercial()));
        configuracion.setRazonSocial(limpiar(request.getRazonSocial()));
        configuracion.setNit(limpiar(request.getNit()));
        configuracion.setTelefono(limpiar(request.getTelefono()));
        configuracion.setCorreo(limpiar(request.getCorreo()));
        configuracion.setDepartamento(limpiar(request.getDepartamento()));
        configuracion.setDireccion(limpiar(request.getDireccion()));
        configuracion.setMensajeComprobante(valorODefecto(
                request.getMensajeComprobante(), "Gracias por su compra. Vuelva pronto."));

        if (logo != null && !logo.isEmpty()) {
            configuracion.setLogoPath(guardarArchivo(logo, "logo-", false));
        } else if (configuracion.getId() == null && !logoInicial.isBlank()) {
            configuracion.setLogoPath(logoInicial);
        }

        if (fondoLogin != null && !fondoLogin.isEmpty()) {
            configuracion.setFondoLoginPath(guardarArchivo(fondoLogin, "fondo-login-", true));
        }

        return respuesta(configuracionEmpresaRepository.save(configuracion));
    }

    private void validar(ConfiguracionEmpresaRequestDto request) {
        if (request == null || request.getNombreComercial() == null
                || request.getNombreComercial().isBlank()) {
            throw new IllegalArgumentException("El nombre comercial es obligatorio");
        }
        if (request.getCorreo() != null && !request.getCorreo().isBlank()
                && !request.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("El correo del supermercado no es válido");
        }
    }

    private String guardarArchivo(MultipartFile archivo, String prefijo, boolean permiteGif) {
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("La imagen no puede superar 5 MB");
        }
        String tipo = archivo.getContentType();
        String extension = EXTENSIONES.get(tipo);
        if (extension == null || (!permiteGif && "image/gif".equals(tipo))) {
            throw new IllegalArgumentException("Formato de imagen no permitido");
        }
        try {
            Path directorio = Path.of(directorioConfiguracion).toAbsolutePath().normalize();
            Files.createDirectories(directorio);
            String nombre = prefijo + UUID.randomUUID() + extension;
            Path destino = directorio.resolve(nombre).normalize();
            if (!destino.startsWith(directorio)) {
                throw new IllegalArgumentException("Nombre de archivo no válido");
            }
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return "uploads/configuracion/" + nombre;
        } catch (IOException e) {
            throw new ResourceConflictException("No se pudo guardar la imagen de configuración");
        }
    }

    private ConfiguracionEmpresaResponseDto respuesta(ConfiguracionEmpresa configuracion) {
        return new ConfiguracionEmpresaResponseDto(
                configuracion.getId(), configuracion.getNombreComercial(), configuracion.getRazonSocial(),
                configuracion.getNit(), configuracion.getTelefono(), configuracion.getCorreo(),
                configuracion.getDepartamento(), configuracion.getDireccion(), url(configuracion.getLogoPath()),
                url(configuracion.getFondoLoginPath()), configuracion.getMensajeComprobante());
    }

    private ConfiguracionEmpresaResponseDto respuestaInicial() {
        return new ConfiguracionEmpresaResponseDto(
                null, nombreInicial, "", nitInicial, telefonoInicial, "", departamentoInicial,
                direccionInicial, url(logoInicial), null, "Gracias por su compra. Vuelva pronto.");
    }

    private String url(String ruta) {
        if (ruta == null || ruta.isBlank()) return null;
        String normalizada = ruta.replace('\\', '/');
        return normalizada.startsWith("/") ? normalizada : "/" + normalizada;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String valorODefecto(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor.trim();
    }
}
