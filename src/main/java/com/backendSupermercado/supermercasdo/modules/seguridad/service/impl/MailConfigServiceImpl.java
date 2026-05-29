package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionCorreo;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.ConfiguracionCorreoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.MailConfigService;

@Configuration
public class MailConfigServiceImpl implements MailConfigService {

    @Autowired
    private ConfiguracionCorreoRepository configuracionCorreoRepository;

    @Override
    @Bean
    public JavaMailSender javaMailSender() {
        // Obtener la configuracion de correo
        ConfiguracionCorreo config = configuracionCorreoRepository.findByActivoTrue()
                .orElseThrow(() -> new ResourceConflictException("No existe configuracion SMTP"));
        // Configurar y retornar el JavaMailSender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(config.getSmtp());
        mailSender.setPort(config.getPuerto());
        mailSender.setUsername(config.getCorreo());
        mailSender.setPassword(config.getPassword());

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
