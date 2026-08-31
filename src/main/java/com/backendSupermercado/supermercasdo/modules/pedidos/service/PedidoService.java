package com.backendSupermercado.supermercasdo.modules.pedidos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoListadoDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoRequestDto;

public interface PedidoService {

    PedidoListadoDto crear(Long idUsuario, PedidoRequestDto dto);

    PedidoListadoDto actualizar(Long id, Long idUsuario, PedidoRequestDto dto);

    PedidoListadoDto enviar(Long id);

    PedidoListadoDto obtenerPorId(Long id);

    PedidoListadoDto cancelar(Long id);

    Page<PedidoListadoDto> listar(String estado, String search, Long idProveedor,
            LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable);

    long contarPorEstado(String estado);

    List<String> listarEstados();

    byte[] exportarPdf(Long id, String username);
}