package com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo;

import com.example.Notificaciones.Notificacion.Aplicacion.ports.Input.EnvioMensajesCorreoInputPort;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnvioMensajesCorreoDTO {
    private String correo;
    private String mensaje;
    private String descripcion;

}
