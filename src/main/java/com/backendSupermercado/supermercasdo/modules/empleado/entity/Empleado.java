package com.backendSupermercado.supermercasdo.modules.empleado.entity;

import java.time.LocalDate;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "empleado")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Long idEmpleado;

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    private Boolean estado = true;


    //relacion con persona
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    //relacion con usuario
    @OneToOne(
        mappedBy = "empleado", 
        fetch = FetchType.LAZY
    )
    private Usuario usuario;
    
}
