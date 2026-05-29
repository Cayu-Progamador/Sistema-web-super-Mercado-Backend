package com.backendSupermercado.supermercasdo.modules.seguridad.entity;

import java.time.LocalDateTime;

import com.backendSupermercado.supermercasdo.modules.usuario.entity.Usuario;

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
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @Column(
        name = "fecha_expiracion",
        nullable = false
    )
    private LocalDateTime fechaExpiracion;

    @Column(nullable = false, length = 6)
    private String pin;

    // control de intentos fallidos (MUY IMPORTANTE)
    @Column(nullable = false)
    private int attempts = 0;

    @Column(
        nullable = false
    )
    private Boolean used = false;

    // fecha de creación (auditoría)
    @Column(nullable = false)
    private LocalDateTime createdAt;

    //relacion de usuario 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
