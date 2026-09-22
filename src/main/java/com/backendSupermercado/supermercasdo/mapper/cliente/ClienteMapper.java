package com.backendSupermercado.supermercasdo.mapper.cliente;

import org.springframework.stereotype.Component;

import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteResponseDto;
import com.backendSupermercado.supermercasdo.modules.cliente.entity.Cliente;

@Component
public class ClienteMapper {

    public ClienteResponseDto toResponseDto(Cliente cliente) {
        return ClienteResponseDto.builder()
                .idCliente(cliente.getIdCliente())
                .nombres(cliente.getNombres())
                .apellidoPaterno(cliente.getApellidoPaterno())
                .apellidoMaterno(cliente.getApellidoMaterno())
                .ci(cliente.getCi())
                .correo(cliente.getCorreo())
                .telefono(cliente.getTelefono())
                .limiteCredito(cliente.getLimiteCredito())
                .direccion(cliente.getDireccion())
                .estado(cliente.getEstado())
                .fechaRegistro(cliente.getFechaRegistro())
                .build();
    }
}
