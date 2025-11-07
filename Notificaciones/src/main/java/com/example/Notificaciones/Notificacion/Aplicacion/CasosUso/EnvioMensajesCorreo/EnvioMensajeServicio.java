package com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Notificaciones.Notificacion.Aplicacion.ports.Input.EnvioMensajesCorreoInputPort;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
@Transactional
public class EnvioMensajeServicio implements EnvioMensajesCorreoInputPort {

    private final JavaMailSender mailSender;

    public EnvioMensajeServicio(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public String envioCorreo(EnvioMensajesCorreoDTO dto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8"
            );

            helper.setTo(dto.getCorreo());
            helper.setFrom(new InternetAddress("oleoalexis@gmail.com", "Tu cine – LuxScreen"));
            helper.setSubject(dto.getMensaje());

            String html = """
                <div style="font-family:Arial,Helvetica,sans-serif;background:#f6f7fb;padding:24px;">
                  <div style="max-width:600px;margin:0 auto;background:white;border-radius:10px;padding:24px;">
                    <h2 style="color:#4f46e5;">%s</h2>
                    <p style="font-size:15px;color:#333;">%s</p>
                    <hr style="border:none;border-top:1px solid #eee;margin:20px 0;">
                    <p style="color:#777;font-size:12px;">Este mensaje fue enviado automáticamente por tu cine favorito – LuxScreen.</p>
                  </div>
                </div>
            """.formatted(dto.getMensaje(), dto.getDescripcion());

            helper.setText(html, true); 

            mailSender.send(mimeMessage);
            return "Correo HTML enviado";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error enviando correo: " + e.getMessage();
        }
    }
}