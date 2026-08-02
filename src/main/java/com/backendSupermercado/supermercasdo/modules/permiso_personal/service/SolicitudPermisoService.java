package com.backendSupermercado.supermercasdo.modules.permiso_personal.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.HistorialSolicitudDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoRequestDto;
import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoResponseDto;

public interface SolicitudPermisoService {
    SolicitudPermisoResponseDto crear(Long idUsuario, SolicitudPermisoRequestDto dto);
    List<SolicitudPermisoResponseDto> listarTodas();
    List<SolicitudPermisoResponseDto> listarPorEstado(String estado);
    List<SolicitudPermisoResponseDto> listarMisSolicitudes(Long idUsuario);
    SolicitudPermisoResponseDto aprobar(Long idSolicitud, Long idAdmin, String comentario);
    SolicitudPermisoResponseDto rechazar(Long idSolicitud, Long idAdmin, String comentario);
    SolicitudPermisoResponseDto revisar(Long idSolicitud, Long idAdmin);
    SolicitudPermisoResponseDto cancelar(Long idSolicitud, Long idUsuario, String motivo);
    List<HistorialSolicitudDto> obtenerHistorial(Long idSolicitud);
    void expirarVencidas();
    byte[] exportarPdf(String estado, String username);
    byte[] exportarExcel(String estado);
}
