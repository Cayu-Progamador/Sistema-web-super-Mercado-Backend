package com.backendSupermercado.supermercasdo.modules.usuario.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "login_usuario")
public class LoginUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_login", nullable = false)
    private Long idLogin;

    @Column(name = "fecha_login")
    private LocalDateTime fechaLogin;

    @Column(name = "ip", length = 50)
    private String ip;
    
    @ManyToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(name = "id_usuario",nullable = false)
    private Usuario usuario;

}
