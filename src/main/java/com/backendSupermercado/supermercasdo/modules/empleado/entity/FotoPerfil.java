package com.backendSupermercado.supermercasdo.modules.empleado.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "foto_perfil")
public class FotoPerfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_persona",
            nullable = false,
            unique = true
    )
    private Persona persona;    
}
