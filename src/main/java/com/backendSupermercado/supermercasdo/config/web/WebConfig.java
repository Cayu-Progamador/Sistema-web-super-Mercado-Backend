package com.backendSupermercado.supermercasdo.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.uploads.directorio:uploads/fotos-perfil}")
    private String directorioUploads;

    @Value("${app.uploads.directorio-productos:uploads/productos}")
    private String directorioProductos;

    @Value("${app.uploads.directorio-configuracion:uploads/configuracion}")
    private String directorioConfiguracion;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/configuracion/**")
                .addResourceLocations(Path.of(directorioConfiguracion).toAbsolutePath().normalize().toUri().toString());
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + directorioUploads + "/", "file:" + directorioProductos + "/");
    }
}
