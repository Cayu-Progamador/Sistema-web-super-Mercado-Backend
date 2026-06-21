package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Contacto;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionCorreo;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.ConfiguracionCorreoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.MailConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailConfigServiceImpl implements MailConfigService {

    private final ConfiguracionCorreoRepository configuracionCorreoRepository;

    @Override
    public JavaMailSender javaMailSender() {
        // Obtener la configuracion de correo
        ConfiguracionCorreo config = configuracionCorreoRepository.findByActivoTrue()
                .orElseThrow(() -> new ResourceConflictException("No existe configuracion SMTP"));
        
                // Obtener el correo de la persona
        String correo = config.getEmpleado()
            .getPersona()
            .getContactos()
            .stream()
            .findFirst()
            .map(Contacto::getCorreo)
            .orElseThrow(() -> new ResourceConflictException("El empleado no tiene correo registrado"));  
        
            // Configurar y retornar el JavaMailSender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(config.getSmtpConfig().getSmtp());
        mailSender.setPort(config.getSmtpConfig().getPuerto());
        mailSender.setUsername(correo);
        mailSender.setPassword(config.getPasswordEmail());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        return mailSender;
    }
}
