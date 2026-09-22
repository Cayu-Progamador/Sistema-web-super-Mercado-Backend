package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public final class PermisosMatrizDto {
    private PermisosMatrizDto() {}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Respuesta {
        private Long idRol;
        private String rol;
        private List<MenuItem> menus;
        private List<PermisoItem> permisos;
        private Set<Long> permisosSeleccionados;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuItem {
        private Long idMenu;
        private Long idMenuPadre;
        private String nombre;
        private String ruta;
        private String icono;
        private Integer orden;
        private Boolean visible;
        private List<PermisoItem> permisos;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisoItem {
        private Long idPermiso;
        private String codigo;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuardarRequest {
        private List<Long> idsPermiso;
    }
}
