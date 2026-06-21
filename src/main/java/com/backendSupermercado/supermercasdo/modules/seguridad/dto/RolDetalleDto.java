package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolDetalleDto {
    private Long idRol;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private Integer cantidadUsuarios;
    private List<RolDetallePermiso> permisos;
    private List<RolDetalleUsuario> usuarios;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RolDetallePermiso {
        private Long idPermiso;
        private String codigo;
        private String nombre;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RolDetalleUsuario {
        private String username;
        private String nombreCompleto;
        private String correo;
        private Boolean activo;
    }
}
