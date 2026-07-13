package com.backendSupermercado.supermercasdo.modules.empleado.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Long idPersona;

    @Column(name = "nombres" , nullable = false , length = 255)
    private String nombres;

    @Column(name = "ci", length = 20, unique = true)
    private String ci;

    @Column(name = "apellido_paterno", length = 255)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 255)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento" , nullable = false)
    private LocalDate fechaNacimiento;

    private Boolean estado = true;

    
    //relacion con contacto 1...N
    @OneToMany(mappedBy = "persona", fetch = FetchType.LAZY)
    private List<Contacto> contactos;

    //relacion con foto perfil 1...1
    @OneToOne(
            mappedBy = "persona",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private FotoPerfil fotoPerfil;

    @OneToOne(
            mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Direccion direccion;

    //relacion con empleado 1...N
    @OneToOne(mappedBy = "persona",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sexo", nullable = false)
    private Sexo sexo;
}
