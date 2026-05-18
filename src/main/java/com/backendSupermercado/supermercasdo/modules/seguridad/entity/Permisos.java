package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import java.util.List;

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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permiso")
public class Permisos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Long idPermiso;

    @Column(name = "codigo", unique = true, nullable = false, length = 80)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "estado")
    private Boolean estado = true;

    @OneToMany(mappedBy = "permiso", fetch = FetchType.LAZY)
    private List<RolPermisos> rolPermisos;

    @OneToMany(mappedBy = "permiso", fetch = FetchType.LAZY)
    private List<MenuPermisos> menuPermisos;
}
