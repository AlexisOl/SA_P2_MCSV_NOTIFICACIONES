package com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo;

import com.example.Notificaciones.Notificacion.Aplicacion.ports.Input.EnvioMensajesCorreoInputPort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnvioMensajeServicio implements EnvioMensajesCorreoInputPort {

    private final JavaMailSender mailSender;

    public EnvioMensajeServicio(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }


    @Override
    public String envioCorreo(EnvioMensajesCorreoDTO envioMensajesCorreoDTO) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(envioMensajesCorreoDTO.getCorreo());
        simpleMailMessage.setSubject(envioMensajesCorreoDTO.getMensaje());
        simpleMailMessage.setText(envioMensajesCorreoDTO.getDescripcion());
        simpleMailMessage.setFrom("oleoalexis@gmail.com");
        mailSender.send(simpleMailMessage);

        return "correo enviado";
    }
}
