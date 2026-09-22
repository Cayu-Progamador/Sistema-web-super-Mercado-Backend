package com.backendSupermercado.supermercasdo.modules.venta.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.venta.dto.CrearVentaRequest;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResponseDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaCatalogoDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VerificacionVentaDto;
import com.backendSupermercado.supermercasdo.modules.venta.dto.VentaResumenDto;
import java.util.List;
import java.math.BigDecimal;

public interface VentaService {

    VentaResponseDto crear(Long idUsuario, CrearVentaRequest request);

    VentaResponseDto obtener(Long idVenta);

    Page<VentaResponseDto> listar(Pageable pageable);

    List<VentaCatalogoDto> catalogo(String buscar);

    VerificacionVentaDto verificar(String codigo);

    byte[] generarComprobantePdf(Long idVenta);

    BigDecimal gananciaDelDia();

    VentaResumenDto resumen();

    void anular(Long idUsuario, Long idVenta, String motivo);
}
