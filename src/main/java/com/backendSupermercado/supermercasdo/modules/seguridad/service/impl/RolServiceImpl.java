package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.mapper.roles.RolMapper;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRequestDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolRespuestaDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.dto.RolSelectDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.RolService;

@Service
public class RolServiceImpl implements RolService{

    @Autowired
    private RolRepository rolRepository;
    
    //listar roles en el nuevo usuario
    @Override
    public List<RolSelectDto> listarRolSeleccionado() {
        return rolRepository.listarRolSelect();
    }

    //tabla de roles en la tabla de roles para el crud
    public List<RolDto> listarRoles() {
        List<Rol> roles = rolRepository.findAll();
        return roles.stream()
                .map(RolMapper::toDto)
                .toList();
    }

    //crear un nuevo rol
    @Override
    public RolRespuestaDto crearRol(RolRequestDto dto) {
        //Validar duplicado
        if (rolRepository.existsByNombre(dto.getNombre())) {
            throw new ResourceConflictException("Ya existe un rol con ese nombre");
        }
        // 🟢 Crear entidad
        Rol rol = new Rol();
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        rol.setEstado(true); // default activo

        Rol saved = rolRepository.save(rol);
       
        // 📤 Mapear a DTO de respuesta
        RolRespuestaDto response = new RolRespuestaDto();
        response.setIdRol(saved.getIdRol());
        response.setNombre(saved.getNombre());
        response.setDescripcion(saved.getDescripcion());
        response.setEstado(saved.getEstado());

        return response;

    }

}
