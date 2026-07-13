package com.backendSupermercado.supermercasdo.modules.cargo.entity;

import java.util.ArrayList;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.contrato.entity.Contrato;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cargo",
    uniqueConstraints = {
        @UniqueConstraint(name ="uk_cargo_nombre", columnNames = {"nombre"})
    }
)
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    private Boolean estado = true;

    @OneToMany(mappedBy = "cargo", fetch = FetchType.LAZY)
    private List<Contrato> contratos = new ArrayList<>();
}
