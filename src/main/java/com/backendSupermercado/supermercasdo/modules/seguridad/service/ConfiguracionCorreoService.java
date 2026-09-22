package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.ConfiguracionCorreoResponseDto;

public interface ConfiguracionCorreoService {
    ConfiguracionCorreoResponseDto obtener(String username);
    ConfiguracionCorreoResponseDto guardar(String username, ConfiguracionCorreoRequestDto request);
}
