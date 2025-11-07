package com.example.Notificaciones.Notificacion.Infraestructura.Input.kafka.dto;

public record UserRegisteredEvent(
        String userId,
        String nombre,
        String email
) {}
