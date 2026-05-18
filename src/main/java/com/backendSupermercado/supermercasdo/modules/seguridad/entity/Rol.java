package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import java.util.List;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.UsuarioRol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    @Column(name = "nombre", unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 150)
    private String descripcion;

    @Column(name = "estado")
    private Boolean estado = true;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<UsuarioRol> usuarioRoles;

    @OneToMany(mappedBy = "rol", fetch = FetchType.LAZY)
    private List<RolPermisos> rolPermisos;
}
