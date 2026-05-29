package com.backendSupermercado.supermercasdo.modules.seguridad.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
    private String email;
    private String pin;
    private String newPassword;
}
