package com.backendSupermercado.supermercasdo.modules.credito.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backendSupermercado.supermercasdo.modules.credito.dto.*;
import com.backendSupermercado.supermercasdo.modules.venta.enums.EstadoCuenta;

public interface CreditoService {
    Page<CreditoResponseDto> listar(String buscar, EstadoCuenta estado, java.time.LocalDate desde, java.time.LocalDate hasta, Pageable pageable);
    CreditoResumenDto resumen();
    CreditoResponseDto registrarPago(Long idUsuario, Long idCuenta, PagoCreditoRequestDto request);
}
