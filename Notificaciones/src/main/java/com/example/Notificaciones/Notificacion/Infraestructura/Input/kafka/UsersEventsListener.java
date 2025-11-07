package com.example.Notificaciones.Notificacion.Infraestructura.Input.kafka;

import java.time.Instant;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo.EnvioMensajesCorreoDTO;
import com.example.Notificaciones.Notificacion.Aplicacion.ports.Input.EnvioMensajesCorreoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.comun.DTO.userEvents.PasswordResetRequestedEventDto;
import com.example.comun.DTO.userEvents.UserRegisteredEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersEventsListener {

    private final EnvioMensajesCorreoInputPort correoService;
    private final ObjectMapper mapper;

    // ========== 1) USER REGISTERED ==========
    @KafkaListener(
        topics = "${app.kafka.topic.users:users-events-v1}",
        groupId = "mcsv-notificaciones"
    )
    public void onUserRegistered(
        @Payload String payload,
        @Header(value = "eventType", required = false) String eventType,
        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("[REGISTER] Evento recibido: type={} key={} payload={}", eventType, key, payload);

        try {
            if (!"user.registered.v1".equals(eventType)) {
                log.warn("[REGISTER] eventType inesperado en este topic: {}", eventType);
                return;
            }

            UserRegisteredEvent p = mapper.readValue(payload, UserRegisteredEvent.class);

            String subject = "Bienvenido/a, " + p.nombre();
            String body = """
                    ¡Hola %s! 👋

                    Tu cuenta se ha creado correctamente. ¡Gracias por registrarte en nuestra plataforma! 🎬🍿

                    Ahora puedes iniciar sesión y disfrutar de nuestras funciones.

                    Saludos,
                    Equipo de Soporte
                    """.formatted(p.nombre());

            correoService.envioCorreo(new EnvioMensajesCorreoDTO(
                p.email(),
                subject,
                body
            ));

            log.info("[REGISTER] Correo de bienvenida enviado a {}", p.email());

        } catch (Exception ex) {
            log.error("[REGISTER] Error procesando evento: {}", ex.getMessage(), ex);
        }
    }

    // ========== 2) PASSWORD RESET REQUESTED ==========
    @KafkaListener(
        topics = "${app.kafka.topic.reset-password:reset-user-password}",
        groupId = "mcsv-notificaciones"
    )
    public void onPasswordResetRequested(
        @Payload String payload,
        @Header(value = "eventType", required = false) String eventType,
        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("[RESET] Evento recibido: type={} key={} payload={}", eventType, key, payload);

        try {
            if (!"user.password.reset.requested.v1".equals(eventType)) {
                log.warn("[RESET] eventType inesperado en este topic: {}", eventType);
                return;
            }

            PasswordResetRequestedEventDto p = mapper.readValue(payload, PasswordResetRequestedEventDto.class);

            String subject = "Restablecer tu contraseña";
            String body = """
                    Hola %s,

                    Recibimos una solicitud para restablecer tu contraseña.

                    Usa este enlace (válido hasta %s):
                    %s

                    Si no fuiste tú, puedes ignorar este correo.

                    Saludos,
                    Equipo de Soporte
                    """.formatted(p.nombre(), p.expiresAt(), p.link());

            correoService.envioCorreo(new EnvioMensajesCorreoDTO(
                p.email(),
                subject,
                body
            ));

            log.info("[RESET] Correo de reset enviado a {}", p.email());

        } catch (Exception ex) {
            log.error("[RESET] Error procesando evento: {}", ex.getMessage(), ex);
        }
    }

    // ===== DTOs que deben machear el JSON que manda el ms-usuarios =====

    

    // public static record PasswordResetPayload(
    //     String userId,
    //     String email,
    //     String nombre,
    //     String token,
    //     String link,
    //     Instant expiresAt
    // ) {}
}