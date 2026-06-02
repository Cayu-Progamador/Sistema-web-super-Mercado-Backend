package com.backendSupermercado.supermercasdo.modules.usuario.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")    
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "bloqueado", nullable = false)
    private  Boolean bloqueado = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    //relacion empleado con usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;
    
    //relacion usuario rol
    @OneToMany(
        mappedBy = "usuario", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<UsuarioRol> usuarioRoles = new ArrayList<>();

//relacion usuario login
    @OneToMany(
        mappedBy = "usuario", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<LoginUsuario> loginUsuarios = new ArrayList<>();

    //relacion usuario auditoria
    @OneToMany(
        mappedBy = "usuario", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<AuditoriaUsuario> auditorias = new ArrayList<>();

    // Relación usuario - seguridad
    @OneToOne(
        mappedBy = "usuario",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY,
        orphanRemoval = true
    )
    private SeguridadUsuario seguridadUsuario;
}
