package com.backendSupermercado.supermercasdo.modules.lotes.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.LoteResponseDto;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.LoteUpdateRequestDto;

public interface LoteService {
    Page<LoteResponseDto> listar(String buscar, Long idProducto, String estado, java.time.LocalDate desde,
            java.time.LocalDate hasta, Pageable pageable);
    LoteResponseDto actualizar(Long idLote, LoteUpdateRequestDto dto);
    byte[] exportarPdf(String buscar, Long idProducto, String estado, java.time.LocalDate desde,
            java.time.LocalDate hasta);
}
