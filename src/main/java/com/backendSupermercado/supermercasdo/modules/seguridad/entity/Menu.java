package com.backendSupermercado.supermercasdo.modules.seguridad.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private Long idMenu;
    
    @Column(name= "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "ruta", nullable = false, unique = true, length = 255)
    private String ruta;

    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "orden")
    private Integer orden = 1;

    @Column(name = "visible")
    private Boolean visible = true;

    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    private List<MenuPermisos> menuPermisos = new ArrayList<>();
}
