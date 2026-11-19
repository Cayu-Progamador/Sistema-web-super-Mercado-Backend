package com.backendSupermercado.supermercasdo.config.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.backendSupermercado.supermercasdo.security.filter.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**", "/uploads/**", "/api/ventas/verificar/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/usuarios/perfil").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/usuarios/perfil").hasAnyAuthority("ROLE_ADMIN", "PERFIL_EDITAR")
                    .requestMatchers(HttpMethod.POST, "/api/usuarios/cambiar-contrasena").hasAnyAuthority("ROLE_ADMIN", "PERFIL_EDITAR")
                    .requestMatchers(HttpMethod.POST, "/api/foto-perfil/subir").hasAnyAuthority("ROLE_ADMIN", "PERFIL_EDITAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/foto-perfil/eliminar").hasAnyAuthority("ROLE_ADMIN", "PERFIL_EDITAR")
                    .requestMatchers(HttpMethod.GET, "/api/foto-perfil/ver").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/permisos-sistema/mis-permisos").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/configuracion-correo").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/configuracion-correo").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/configuracion-empresa").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/api/configuracion-empresa").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/asistencias/hoy", "/api/asistencias/mis-asistencias", "/api/asistencias/mi-resumen", "/api/asistencias/mis-ausencias", "/api/asistencias/tiene-acceso", "/api/asistencias/descargar-reporte").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/asistencias/marcar", "/api/asistencias/justificar").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/solicitudes-permiso/mis-solicitudes").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/solicitudes-permiso").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/api/solicitudes-permiso/*/cancelar").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/asistencias/admin/**", "/api/asistencias/listar").hasAnyAuthority("ROLE_ADMIN", "PANEL_ASISTENCIA_VER")
                    .requestMatchers(HttpMethod.POST, "/api/asistencias/admin/**").hasAnyAuthority("ROLE_ADMIN", "PANEL_ASISTENCIA_CREAR")
                    .requestMatchers(HttpMethod.PUT, "/api/asistencias/admin/**", "/api/asistencias/*/justificar").hasAnyAuthority("ROLE_ADMIN", "PANEL_ASISTENCIA_EDITAR")
                    .requestMatchers(HttpMethod.GET, "/api/solicitudes-permiso/todas", "/api/solicitudes-permiso/estado/**", "/api/solicitudes-permiso/exportar/**", "/api/solicitudes-permiso/*/historial").hasAnyAuthority("ROLE_ADMIN", "ADMIN_PERMISOS_VER")
                    .requestMatchers(HttpMethod.PUT, "/api/solicitudes-permiso/*/revisar").hasAnyAuthority("ROLE_ADMIN", "ADMIN_PERMISOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/solicitudes-permiso/*/aprobar", "/api/solicitudes-permiso/*/rechazar").hasAnyAuthority("ROLE_ADMIN", "ADMIN_PERMISOS_APROBAR")
                    // Lectura de módulos
                    .requestMatchers(HttpMethod.GET, "/api/reportes/ventas/**").hasAnyAuthority("ROLE_ADMIN", "HISTORIAL_VENTAS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/ventas/catalogo").hasAnyAuthority("ROLE_ADMIN", "NUEVA_VENTA_VER", "NUEVA_VENTA_CREAR")
                    .requestMatchers(HttpMethod.GET, "/api/ventas/**").hasAnyAuthority("ROLE_ADMIN", "HISTORIAL_VENTAS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyAuthority("ROLE_ADMIN", "CLIENTES_VER")
                    .requestMatchers(HttpMethod.GET, "/api/creditos/**").hasAnyAuthority("ROLE_ADMIN", "CREDITO_VER")
                    .requestMatchers(HttpMethod.GET, "/api/cajas/**").hasAnyAuthority("ROLE_ADMIN", "CAJA_VER")
                    .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyAuthority("ROLE_ADMIN", "PRODUCTOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/categorias/**", "/api/subcategorias/**").hasAnyAuthority("ROLE_ADMIN", "CATEGORIAS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/marcas/**").hasAnyAuthority("ROLE_ADMIN", "MARCAS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/unidades-medida/**").hasAnyAuthority("ROLE_ADMIN", "UNIDAD_MEDIDA_VER")
                    .requestMatchers(HttpMethod.GET, "/api/proveedores/**").hasAnyAuthority("ROLE_ADMIN", "PROVEEDORES_VER")
                    .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyAuthority("ROLE_ADMIN", "PEDIDOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/recepciones/**").hasAnyAuthority("ROLE_ADMIN", "RECEPCION_VER")
                    .requestMatchers(HttpMethod.GET, "/api/devoluciones/**").hasAnyAuthority("ROLE_ADMIN", "DEVOLUCIONES_VER")
                    .requestMatchers(HttpMethod.GET, "/api/empleados/**").hasAnyAuthority("ROLE_ADMIN", "EMPLEADOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/cargos/**").hasAnyAuthority("ROLE_ADMIN", "CARGOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/contratos/**").hasAnyAuthority("ROLE_ADMIN", "CONTRATOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/almacenes/**").hasAnyAuthority("ROLE_ADMIN", "ALMACEN_VER")
                    .requestMatchers(HttpMethod.GET, "/api/inventario/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_VER")
                    .requestMatchers(HttpMethod.GET, "/api/inventarios-fisicos/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_VER")
                    .requestMatchers(HttpMethod.GET, "/api/transferencias-almacen/**").hasAnyAuthority("ROLE_ADMIN", "TRANSFERENCIAS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/ubicaciones-almacen/**").hasAnyAuthority("ROLE_ADMIN", "UBICACIONES_VER")
                    .requestMatchers(HttpMethod.GET, "/api/ajustes-inventario/**").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_VER")
                    .requestMatchers(HttpMethod.GET, "/api/roles/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_VER", "ROLES_VER")
                    .requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_VER", "USUARIOS_VER")
                    .requestMatchers(HttpMethod.GET, "/api/permisos-sistema/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_VER", "PERMISOS_SISTEMA_VER")
                    // Creacion
                    .requestMatchers(HttpMethod.POST, "/api/inventarios-fisicos/*/aprobar", "/api/inventarios-fisicos/*/finalizar").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_APROBAR")
                    .requestMatchers(HttpMethod.POST, "/api/ajustes-inventario/*/aprobar", "/api/ajustes-inventario/*/rechazar", "/api/ajustes-inventario/*/aplicar").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_APROBAR")
                    .requestMatchers(HttpMethod.POST, "/api/ventas/**").hasAnyAuthority("ROLE_ADMIN", "NUEVA_VENTA_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasAnyAuthority("ROLE_ADMIN", "CLIENTES_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/creditos/**").hasAnyAuthority("ROLE_ADMIN", "CREDITO_EDITAR")
                    .requestMatchers(HttpMethod.POST, "/api/cajas/sesiones/abrir").hasAnyAuthority("ROLE_ADMIN", "CAJA_ABRIR", "CAJA_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/cajas/sesiones/*/cerrar").hasAnyAuthority("ROLE_ADMIN", "CAJA_CERRAR", "CAJA_EDITAR")
                    .requestMatchers(HttpMethod.POST, "/api/cajas/**").hasAnyAuthority("ROLE_ADMIN", "CAJA_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyAuthority("ROLE_ADMIN", "PRODUCTOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/categorias/**", "/api/subcategorias/**").hasAnyAuthority("ROLE_ADMIN", "CATEGORIAS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/marcas/**").hasAnyAuthority("ROLE_ADMIN", "MARCAS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/unidades-medida/**").hasAnyAuthority("ROLE_ADMIN", "UNIDAD_MEDIDA_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/proveedores/**").hasAnyAuthority("ROLE_ADMIN", "PROVEEDORES_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyAuthority("ROLE_ADMIN", "PEDIDOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/recepciones/**").hasAnyAuthority("ROLE_ADMIN", "RECEPCION_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/devoluciones/**").hasAnyAuthority("ROLE_ADMIN", "DEVOLUCIONES_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/empleados/**").hasAnyAuthority("ROLE_ADMIN", "EMPLEADOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/cargos/**").hasAnyAuthority("ROLE_ADMIN", "CARGOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/contratos/**").hasAnyAuthority("ROLE_ADMIN", "CONTRATOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/almacenes/**").hasAnyAuthority("ROLE_ADMIN", "ALMACEN_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/ubicaciones-almacen/**").hasAnyAuthority("ROLE_ADMIN", "UBICACIONES_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/transferencias-almacen/**").hasAnyAuthority("ROLE_ADMIN", "TRANSFERENCIAS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/inventarios-fisicos/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_CREAR", "INVENTARIO_FISICO_APROBAR")
                    .requestMatchers(HttpMethod.POST, "/api/ajustes-inventario/**").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_CREAR", "AJUSTES_INVENTARIO_APROBAR")
                    .requestMatchers(HttpMethod.POST, "/api/inventario/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/roles/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_CREAR", "ROLES_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_CREAR", "USUARIOS_CREAR")
                    .requestMatchers(HttpMethod.POST, "/api/permisos/**").hasAnyAuthority("ROLE_ADMIN", "SEGURIDAD_CREAR")
                    .requestMatchers(HttpMethod.PUT, "/api/permisos-sistema/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN", "SEGURIDAD_EDITAR", "PERMISOS_SISTEMA_EDITAR")
                    // Edición por módulo
                    .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasAnyAuthority("ROLE_ADMIN", "ROLES_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN", "USUARIOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/ventas/**").hasAnyAuthority("ROLE_ADMIN", "HISTORIAL_VENTAS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasAnyAuthority("ROLE_ADMIN", "CLIENTES_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/creditos/**").hasAnyAuthority("ROLE_ADMIN", "CREDITO_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/cajas/**").hasAnyAuthority("ROLE_ADMIN", "CAJA_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyAuthority("ROLE_ADMIN", "PRODUCTOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/categorias/**", "/api/subcategorias/**").hasAnyAuthority("ROLE_ADMIN", "CATEGORIAS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/marcas/**").hasAnyAuthority("ROLE_ADMIN", "MARCAS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/unidades-medida/**").hasAnyAuthority("ROLE_ADMIN", "UNIDAD_MEDIDA_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/proveedores/**").hasAnyAuthority("ROLE_ADMIN", "PROVEEDORES_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyAuthority("ROLE_ADMIN", "PEDIDOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/recepciones/**").hasAnyAuthority("ROLE_ADMIN", "RECEPCION_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/devoluciones/**").hasAnyAuthority("ROLE_ADMIN", "DEVOLUCIONES_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/empleados/**").hasAnyAuthority("ROLE_ADMIN", "EMPLEADOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/cargos/**").hasAnyAuthority("ROLE_ADMIN", "CARGOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/contratos/**").hasAnyAuthority("ROLE_ADMIN", "CONTRATOS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/almacenes/**").hasAnyAuthority("ROLE_ADMIN", "ALMACEN_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/ubicaciones-almacen/**").hasAnyAuthority("ROLE_ADMIN", "UBICACIONES_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/transferencias-almacen/**").hasAnyAuthority("ROLE_ADMIN", "TRANSFERENCIAS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/inventarios-fisicos/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/ajustes-inventario/**").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/inventario/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_EDITAR")
                    // Aprobación por módulo
                    .requestMatchers(HttpMethod.PATCH, "/api/permisos-admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN_PERMISOS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/permisos-sistema/**").hasAnyAuthority("ROLE_ADMIN", "PERMISOS_SISTEMA_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/ventas/**").hasAnyAuthority("ROLE_ADMIN", "HISTORIAL_VENTAS_EDITAR", "HISTORIAL_VENTAS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/creditos/**").hasAnyAuthority("ROLE_ADMIN", "CREDITO_EDITAR", "CREDITO_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/cajas/**").hasAnyAuthority("ROLE_ADMIN", "CAJA_EDITAR", "CAJA_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/proveedores/**").hasAnyAuthority("ROLE_ADMIN", "PROVEEDORES_EDITAR", "PROVEEDORES_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/pedidos/**").hasAnyAuthority("ROLE_ADMIN", "PEDIDOS_EDITAR", "PEDIDOS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/recepciones/**").hasAnyAuthority("ROLE_ADMIN", "RECEPCION_EDITAR", "RECEPCION_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/devoluciones/**").hasAnyAuthority("ROLE_ADMIN", "DEVOLUCIONES_EDITAR", "DEVOLUCIONES_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/empleados/**").hasAnyAuthority("ROLE_ADMIN", "EMPLEADOS_EDITAR", "EMPLEADOS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/cargos/**").hasAnyAuthority("ROLE_ADMIN", "CARGOS_EDITAR", "CARGOS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/contratos/**").hasAnyAuthority("ROLE_ADMIN", "CONTRATOS_EDITAR", "CONTRATOS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/almacenes/**").hasAnyAuthority("ROLE_ADMIN", "ALMACEN_EDITAR", "ALMACEN_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/ubicaciones-almacen/**").hasAnyAuthority("ROLE_ADMIN", "UBICACIONES_EDITAR", "UBICACIONES_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/transferencias-almacen/**").hasAnyAuthority("ROLE_ADMIN", "TRANSFERENCIAS_EDITAR", "TRANSFERENCIAS_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/inventarios-fisicos/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_EDITAR", "INVENTARIO_FISICO_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/ajustes-inventario/**").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_EDITAR", "AJUSTES_INVENTARIO_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/inventario/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_EDITAR", "INVENTARIO_APROBAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/usuarios/**").hasAnyAuthority("ROLE_ADMIN", "USUARIOS_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/roles/**").hasAnyAuthority("ROLE_ADMIN", "ROLES_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/productos/**").hasAnyAuthority("ROLE_ADMIN", "PRODUCTOS_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/categorias/**", "/api/subcategorias/**").hasAnyAuthority("ROLE_ADMIN", "CATEGORIAS_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/marcas/**").hasAnyAuthority("ROLE_ADMIN", "MARCAS_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/unidades-medida/**").hasAnyAuthority("ROLE_ADMIN", "UNIDAD_MEDIDA_EDITAR")
                    // Edicion, estados y eliminacion
                    .requestMatchers(HttpMethod.DELETE, "/api/ventas/**").hasAnyAuthority("ROLE_ADMIN", "HISTORIAL_VENTAS_EDITAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyAuthority("ROLE_ADMIN", "PRODUCTOS_EDITAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/inventarios-fisicos/**").hasAnyAuthority("ROLE_ADMIN", "INVENTARIO_FISICO_EDITAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/ajustes-inventario/**").hasAnyAuthority("ROLE_ADMIN", "AJUSTES_INVENTARIO_EDITAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/transferencias-almacen/**").hasAnyAuthority("ROLE_ADMIN", "TRANSFERENCIAS_EDITAR")
                    .requestMatchers(HttpMethod.PUT, "/api/**").hasAnyAuthority("ROLE_ADMIN", "GENERAL_EDITAR")
                    .requestMatchers(HttpMethod.PATCH, "/api/**").hasAnyAuthority("ROLE_ADMIN", "GENERAL_EDITAR", "GENERAL_APROBAR")
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority("ROLE_ADMIN", "GENERAL_ELIMINAR")
                    .anyRequest()
                    .authenticated()
                )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                    )
                );
        //filtro jwt
        http.addFilterBefore(
            jwtAuthFilter, 
            UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}
