package com.example.Notificaciones.Notificacion.Aplicacion.ports.Input;

import com.example.Notificaciones.Notificacion.Aplicacion.CasosUso.EnvioMensajesCorreo.EnvioMensajesCorreoDTO;
import com.example.Notificaciones.Notificacion.Aplicacion.ports.Output.EnvioMensajesCorreoOutputPort;

public interface EnvioMensajesCorreoInputPort {
    String envioCorreo(EnvioMensajesCorreoDTO envioMensajesCorreoDTO) ;
}
