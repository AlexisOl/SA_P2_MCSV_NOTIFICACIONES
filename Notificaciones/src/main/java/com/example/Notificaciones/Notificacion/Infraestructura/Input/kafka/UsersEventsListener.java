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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersEventsListener {

    private final EnvioMensajesCorreoInputPort correoService;
    private final ObjectMapper mapper; // <- Usa el bean de Spring

    @KafkaListener(
        topics = "users-events-v1",
        groupId = "mcsv-notificaciones"
    )
    public void onMessage(
        @Payload String payload,
        @Header(value = "eventType", required = false) String eventType,
        @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) throws Exception {

        log.info("Evento recibido: type={} key={} payload={}", eventType, key, payload);

        if (eventType == null || eventType.isBlank()) {
            log.warn("Mensaje sin header 'eventType'. Se ignora.");
            return; // o intenta inferir por estructura del JSON si lo deseas
        }

        try {
            switch (eventType) {
                case "user.registered.v1" -> {
                    var p = mapper.readValue(payload, RegisteredPayload.class);
                    correoService.envioCorreo(new EnvioMensajesCorreoDTO(
                        p.email(),
                        "Bienvenido/a, " + p.nombre(),
                        "Tu cuenta se ha creado correctamente. ¡Gracias por registrarte!"
                    ));
                }
                case "user.password.reset.requested.v1" -> {
                    var p = mapper.readValue(payload, PasswordResetPayload.class);
                    // cuerpo del correo (usa HTML si quieres)
                    String subject = "Reestablecer tu contraseña";
                    String body = """
                        Hola %s,
                        Recibimos una solicitud para reestablecer tu contraseña.
                        Usa este enlace (válido hasta %s):
                        %s

                        Si no fuiste tú, ignora este correo.
                        """.formatted(p.nombre(), p.expiresAt(), p.link());
                    correoService.envioCorreo(new EnvioMensajesCorreoDTO(p.email(), subject, body));
                }
                
                case "user.wallet-credited.v1" -> {
                    var p = mapper.readValue(payload, WalletPayload.class);
                    correoService.envioCorreo(new EnvioMensajesCorreoDTO(
                        p.email(),
                        "¡Acreditación recibida!",
                        "Se acreditó " + p.monto() + ". Nuevo saldo: " + p.saldo()
                    ));
                }
                case "user.wallet-debited.v1" -> {
                    var p = mapper.readValue(payload, WalletPayload.class);
                    correoService.envioCorreo(new EnvioMensajesCorreoDTO(
                        p.email(),
                        "Débito realizado",
                        "Se debitó " + p.monto() + ". Nuevo saldo: " + p.saldo()
                    ));
                }
                default -> log.warn("Evento no manejado: {}", eventType);
            }
        } catch (Exception ex) {
            // Evita que reviente el listener y quede reintentando en loop
            log.error("Error procesando evento {}: {}", eventType, ex.getMessage(), ex);
            // Aquí puedes decidir: return (descartar) o relanzar para DLT si la tienes configurada
        }
    }

    // DTOs para mapear el JSON del user.ms (publisher)
    public static record PasswordResetPayload(
        String userId,
        String email,
        String nombre,
        String token,
        String link,
        Instant expiresAt
    ) {}
    public record RegisteredPayload(String userId, String nombre, String email) {}
    public record WalletPayload(String userId, java.math.BigDecimal monto, java.math.BigDecimal saldo, String email) {}
}