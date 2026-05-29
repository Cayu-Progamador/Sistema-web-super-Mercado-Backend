package com.backendSupermercado.supermercasdo.modules.empleado.entity;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email",  unique = true, length = 150)
    private String email;

    //relacion con usuario
    @OneToOne(
        mappedBy = "empleado", 
        fetch = FetchType.LAZY
    )
    private Usuario usuario;
}
