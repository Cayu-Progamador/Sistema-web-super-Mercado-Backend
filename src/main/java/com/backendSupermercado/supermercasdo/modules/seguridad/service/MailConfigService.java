package com.backendSupermercado.supermercasdo.modules.seguridad.service;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailConfigService {
    // Metodo para enviar un correo para recuperar contrasena perdida 
    public JavaMailSender javaMailSender();
}
