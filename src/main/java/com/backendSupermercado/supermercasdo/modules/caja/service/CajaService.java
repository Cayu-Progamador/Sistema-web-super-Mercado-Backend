package com.backendSupermercado.supermercasdo.modules.caja.service;

import org.springframework.data.domain.*;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.caja.dto.*;

public interface CajaService {
    List<CajaResponse> listarCajas();

    SesionCajaResponse abrir(Long idUsuario, AbrirCajaRequest request);

    SesionCajaResponse cerrar(Long idUsuario, Long idSesion, CerrarCajaRequest request);

    Page<SesionCajaResponse> listarSesiones(String buscar, Long idCaja, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);

    Page<SesionCajaResponse> listarMisSesiones(Long idUsuario, String buscar, Long idCaja, java.time.LocalDateTime desde, java.time.LocalDateTime hasta, Pageable pageable);

    SesionCajaResponse sesionAbierta(Long idUsuario);

    byte[] generarArqueoPdf(Long idSesion, Long idUsuario, boolean esAdministrador);
}
