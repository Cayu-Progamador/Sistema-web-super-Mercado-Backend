package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.seguridad.dto.PermisosMatrizDto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Permisos;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.RolPermisos;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.Rol;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.MenuPermisosRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.MenuRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.PermisosRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolPermisosRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.RolRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.PermisosSistemaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermisosSistemaServiceImpl implements PermisosSistemaService {
    private final RolRepository rolRepository;
    private final MenuRepository menuRepository;
    private final PermisosRepository permisosRepository;
    private final MenuPermisosRepository menuPermisosRepository;
    private final RolPermisosRepository rolPermisosRepository;

    @Override
    @Transactional(readOnly = true)
    public PermisosMatrizDto.Respuesta obtenerMatriz(Long idRol) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        List<PermisosMatrizDto.MenuItem> menus = menuRepository.findAllByOrderByOrdenAsc().stream()
                .filter(menu -> Boolean.TRUE.equals(menu.getVisible()))
                .map(menu -> new PermisosMatrizDto.MenuItem(
                        menu.getIdMenu(),
                        menu.getMenuPadre() == null ? null : menu.getMenuPadre().getIdMenu(),
                        menu.getNombre(), menu.getRuta(), menu.getIcono(), menu.getOrden(), menu.getVisible(),
                        menu.getMenuPermisos().stream()
                                .map(mp -> mp.getPermiso())
                                .filter(p -> p != null && Boolean.TRUE.equals(p.getEstado()))
                                .map(p -> new PermisosMatrizDto.PermisoItem(p.getIdPermiso(), p.getCodigo(), p.getNombre()))
                                .toList()))
                .toList();

        Set<Long> disponibles = menuPermisosRepository.findAll().stream()
                .map(mp -> mp.getPermiso().getIdPermiso())
                .collect(Collectors.toSet());
        List<PermisosMatrizDto.PermisoItem> permisos = permisosRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()) && disponibles.contains(p.getIdPermiso()))
                .map(p -> new PermisosMatrizDto.PermisoItem(p.getIdPermiso(), p.getCodigo(), p.getNombre()))
                .toList();
        Set<Long> seleccionados = rolPermisosRepository.findByRol_IdRol(idRol).stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getEstado()) && rp.getPermiso() != null)
                .map(rp -> rp.getPermiso().getIdPermiso())
                .collect(Collectors.toSet());

        return new PermisosMatrizDto.Respuesta(idRol, rol.getNombre(), menus, permisos, seleccionados);
    }

    @Override
    @Transactional
    public void guardarMatriz(Long idRol, PermisosMatrizDto.GuardarRequest request) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        Set<Long> ids = request == null || request.getIdsPermiso() == null
                ? new HashSet<>() : new HashSet<>(request.getIdsPermiso());
        List<Permisos> permisos = permisosRepository.findAllById(ids);
        if (permisos.size() != ids.size()) {
            throw new IllegalArgumentException("Uno o más permisos no existen");
        }
        rolPermisosRepository.deleteByRol_IdRol(idRol);
        rolPermisosRepository.flush();
        List<RolPermisos> asignaciones = permisos.stream().map(permiso -> {
            RolPermisos rp = new RolPermisos();
            rp.setRol(rol);
            rp.setPermiso(permiso);
            rp.setEstado(true);
            return rp;
        }).toList();
        rolPermisosRepository.saveAll(asignaciones);
    }
}
