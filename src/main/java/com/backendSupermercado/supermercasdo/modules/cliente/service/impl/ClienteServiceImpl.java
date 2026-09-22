package com.backendSupermercado.supermercasdo.modules.cliente.service.impl;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.cliente.ClienteMapper;
import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteRequestDto;
import com.backendSupermercado.supermercasdo.modules.cliente.dto.ClienteResponseDto;
import com.backendSupermercado.supermercasdo.modules.cliente.entity.Cliente;
import com.backendSupermercado.supermercasdo.modules.cliente.repository.ClienteRepository;
import com.backendSupermercado.supermercasdo.modules.cliente.service.ClienteService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponseDto crearCliente(ClienteRequestDto dto) {
        String ci = normalizarObligatorio(dto.getCi());
        validarCiDisponible(ci, null);

        Cliente cliente = Cliente.builder()
                .nombres(normalizarObligatorio(dto.getNombres()))
                .apellidoPaterno(normalizarOpcional(dto.getApellidoPaterno()))
                .apellidoMaterno(normalizarOpcional(dto.getApellidoMaterno()))
                .ci(ci)
                .correo(normalizarCorreo(dto.getCorreo()))
                .telefono(normalizarOpcional(dto.getTelefono()))
                .limiteCredito(dto.getLimiteCredito())
                .direccion(normalizarOpcional(dto.getDireccion()))
                .estado(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        return guardar(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizarCliente(Long idCliente, ClienteRequestDto dto) {
        Cliente cliente = obtenerEntidad(idCliente);
        String ci = normalizarObligatorio(dto.getCi());
        validarCiDisponible(ci, idCliente);

        cliente.setNombres(normalizarObligatorio(dto.getNombres()));
        cliente.setApellidoPaterno(normalizarOpcional(dto.getApellidoPaterno()));
        cliente.setApellidoMaterno(normalizarOpcional(dto.getApellidoMaterno()));
        cliente.setCi(ci);
        cliente.setCorreo(normalizarCorreo(dto.getCorreo()));
        cliente.setTelefono(normalizarOpcional(dto.getTelefono()));
        cliente.setLimiteCredito(dto.getLimiteCredito());
        cliente.setDireccion(normalizarOpcional(dto.getDireccion()));

        return guardar(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerCliente(Long idCliente) {
        return clienteMapper.toResponseDto(obtenerEntidad(idCliente));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDto> listarClientes(String busqueda, Boolean estado, Pageable pageable) {
        String patron = busqueda == null || busqueda.isBlank()
                ? null
                : "%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%";

        return clienteRepository.filtrar(patron, estado, pageable)
                .map(clienteMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ClienteResponseDto activarCliente(Long idCliente) {
        Cliente cliente = obtenerEntidad(idCliente);
        if (Boolean.TRUE.equals(cliente.getEstado())) {
            throw new ResourceConflictException("El cliente ya está activo");
        }
        cliente.setEstado(true);
        return guardar(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto desactivarCliente(Long idCliente) {
        Cliente cliente = obtenerEntidad(idCliente);
        if (!Boolean.TRUE.equals(cliente.getEstado())) {
            throw new ResourceConflictException("El cliente ya está inactivo");
        }
        cliente.setEstado(false);
        return guardar(cliente);
    }

    private ClienteResponseDto guardar(Cliente cliente) {
        try {
            return clienteMapper.toResponseDto(clienteRepository.save(cliente));
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("Ya existe un cliente con ese CI");
        }
    }

    private Cliente obtenerEntidad(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
    }

    private void validarCiDisponible(String ci, Long idCliente) {
        boolean existe = idCliente == null
                ? clienteRepository.existsByCiIgnoreCase(ci)
                : clienteRepository.existsByCiIgnoreCaseAndIdClienteNot(ci, idCliente);
        if (existe) {
            throw new ResourceConflictException("Ya existe un cliente registrado con ese CI");
        }
    }

    private String normalizarObligatorio(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String normalizarCorreo(String correo) {
        return correo == null || correo.isBlank() ? null : correo.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
