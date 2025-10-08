package com.example.Notificaciones.Notificacion.Dominio;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Notificacion {
    private UUID id;
    private String descripcion;
}
