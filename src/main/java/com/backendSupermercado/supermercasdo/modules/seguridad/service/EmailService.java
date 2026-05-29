package com.backendSupermercado.supermercasdo.modules.seguridad.service;

public interface EmailService {
    // Metodo para enviar un correo
    public void sendRecoverEmail(String destino, String pin);
}
