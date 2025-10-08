package com.example.Notificaciones.Notificacion.Infraestructura.Input.rest;

import com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo.EnvioMensajesCorreoDTO;
import com.example.Notificaciones.Notificacion.Aplicacion.ports.Input.EnvioMensajesCorreoInputPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EnvioNotificacionesRestAdapterKafka {
    private EnvioMensajesCorreoInputPort envioMensajesCorreoInputPort;

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, maxDelay = 5000)
    )
    @KafkaListener(topics = "creacion-auncio",
                    groupId = "mcsv-notificaciones"
                    )
    public void EventoEnvioMensajesCorreo(String mensaje) throws JsonProcessingException {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            EnvioMensajesCorreoDTO mensajeDTO =  objectMapper.readValue(mensaje, EnvioMensajesCorreoDTO.class);
            this.envioMensajesCorreoInputPort.envioCorreo(mensajeDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
