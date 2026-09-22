package com.backendSupermercado.supermercasdo.modules.configuracion.service;

import org.springframework.web.multipart.MultipartFile;

import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaRequestDto;
import com.backendSupermercado.supermercasdo.modules.configuracion.dto.ConfiguracionEmpresaResponseDto;

public interface ConfiguracionEmpresaService {
    ConfiguracionEmpresaResponseDto obtener();

    ConfiguracionEmpresaResponseDto guardar(
            ConfiguracionEmpresaRequestDto request,
            MultipartFile logo,
            MultipartFile fondoLogin);
}
