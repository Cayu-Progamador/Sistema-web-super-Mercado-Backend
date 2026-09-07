package com.backendSupermercado.supermercasdo.modules.cliente.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteRequestDto;
import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteResponseDto;

public interface ClienteService {

    ClienteResponseDto crearCliente(ClienteRequestDto dto);

    ClienteResponseDto actualizarCliente(Long idCliente, ClienteRequestDto dto);

    ClienteResponseDto obtenerCliente(Long idCliente);

    Page<ClienteResponseDto> listarClientes(String busqueda, Boolean estado, Pageable pageable);

    ClienteResponseDto activarCliente(Long idCliente);

    ClienteResponseDto desactivarCliente(Long idCliente);
}
