package com.backendSupermercado.supermercasdo.modules.caja.entity;

import java.util.ArrayList;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.caja.enums.EstadoCaja;
import jakarta.persistence.*;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Entity @Table(name = "caja") 
public class Caja {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id_caja")
    private Long idCaja;
    
    @Column(nullable = false, unique = true, length = 50) 
    private String nombre;
    
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false, length = 20)
    @Builder.Default 
    private EstadoCaja estado = EstadoCaja.ACTIVA;
    
    @OneToMany(mappedBy = "caja", fetch = FetchType.LAZY)
    @Builder.Default 
    private List<SesionCaja> sesiones = new ArrayList<>();
}
