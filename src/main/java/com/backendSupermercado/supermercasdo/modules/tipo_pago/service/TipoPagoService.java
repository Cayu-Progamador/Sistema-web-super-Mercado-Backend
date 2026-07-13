package com.backendSupermercado.supermercasdo.modules.tipo_pago.service;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.tipo_pago.dto.TipoPagoDto;

public interface TipoPagoService {
    List<TipoPagoDto> listarTodos(String busqueda, Boolean estado);
    TipoPagoDto obtenerPorId(Long id);
    TipoPagoDto crear(TipoPagoDto dto);
    TipoPagoDto actualizar(Long id, TipoPagoDto dto);
    TipoPagoDto activar(Long id);
    TipoPagoDto desactivar(Long id);
}
