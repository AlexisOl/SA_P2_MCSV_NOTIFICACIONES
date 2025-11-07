package com.example.Notificaciones.Notificacion.Infraestructura.Input.kafka.dto;

import java.time.Instant;

public record PasswordResetRequestedEventDto(
        String userId,
        String email,
        String nombre,
        String token,
        String link,
        Instant expiresAt
) {}
