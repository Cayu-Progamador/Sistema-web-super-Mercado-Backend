package com.backendSupermercado.supermercasdo.modules.seguridad.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.exceptions.ResourceConflictException;
import com.backendSupermercado.supermercasdo.modules.seguridad.entity.ConfiguracionCorreo;
import com.backendSupermercado.supermercasdo.modules.seguridad.repository.ConfiguracionCorreoRepository;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.EmailService;
import com.backendSupermercado.supermercasdo.modules.seguridad.service.MailConfigService;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

   private final MailConfigService mailConfigService;
   private final ConfiguracionCorreoRepository configuracionCorreoRepository;

    @Override
    public void sendRecoverEmail(String destino, String pin) {

        try {

            JavaMailSender mailSender = mailConfigService.javaMailSender();
            
            ConfiguracionCorreo config = configuracionCorreoRepository.findByActivoTrue()
                    .orElseThrow(() -> new ResourceConflictException("No se encontró configuración de correo activa"));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String correoRemitente = config.getEmpleado()
                    .getPersona()
                    .getContactos()
                    .stream()
                    .findFirst()
                    .map(c -> c.getCorreo())
                    .orElseThrow(() -> new ResourceConflictException("El empleado no tiene correo registrado"));

            helper.setTo(destino);
            helper.setSubject("Código de recuperación de contraseña");
            helper.setFrom(correoRemitente, "Supermercado");

            String contenido = """
                        <div style="font-family:Arial; padding:20px;">
                            <h2 style="color:#1976d2;">Recuperación de contraseña</h2>

                            <p>Tu código de verificación es:</p>

                            <h1 style="
                                text-align:center;
                                letter-spacing:5px;
                                color:#000;
                                background:#f0f0f0;
                                padding:10px;
                                border-radius:8px;
                            ">
                                %s
                            </h1>

                            <p>Este código expira en 10 minutos.</p>

                            <p style="color:red;">
                                No compartas este código con nadie.
                            </p>
                        </div>
                    """.formatted(pin);

            helper.setText(contenido, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new ResourceConflictException(
                    "Error al enviar el correo de recuperación: " + e.getMessage());
        }
    }

}
