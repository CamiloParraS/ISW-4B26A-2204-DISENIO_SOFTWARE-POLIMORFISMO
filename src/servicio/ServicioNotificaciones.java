package servicio;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import fabrica.NotificacionFactory;
import modelo.ConfigTipoNotificacion;
import modelo.Notificacion;
import modelo.PlantillaNotificacion;
import modelo.Usuario;
import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.RolUsuario;
import modelo.enumeraciones.TipoNotificacion;

public class ServicioNotificaciones {
    private final NotificacionFactory factory;
    private final ProcesadorDestinatario procesadorDestinatario;

    public ServicioNotificaciones(NotificacionFactory factory,
            ProcesadorDestinatario procesadorDestinatario) {
        this.factory = factory;
        this.procesadorDestinatario = procesadorDestinatario;
    }

    public void trigger(TipoNotificacion tipo, RolUsuario rolObjetivo,
            Map<String, String> placeholders) {
        for (CanalNotificacion canal : CanalNotificacion.values()) {
            ConfigTipoNotificacion config =
                    new ConfigTipoNotificacion(tipo, canal, true, true);
            if (!config.estaHabilitado()) {
                continue;
            }

            String codigo = tipo.name() + "_" + canal.name();
            String asunto = "Notificacion: " + tipo.name();
            String cuerpo =
                    "Hola {name}, tu tipo de notificacion es {type}. Generada en {timestamp}.";
            PlantillaNotificacion plantilla = new PlantillaNotificacion(
                    UUID.randomUUID().toString(), codigo, canal, asunto, cuerpo);
            for (Usuario destinatario : procesadorDestinatario.procesar(tipo, rolObjetivo,
                    canal)) {
                Notificacion notificacion =
                        factory.crear(tipo, destinatario, plantilla, placeholders, config);
                despachar(notificacion);
            }
        }
    }

    private void despachar(Notificacion notificacion) {
        try {
            notificacion.enviar();
            notificacion.marcarEnviado(LocalDateTime.now());
        } catch (RuntimeException ex) {
            notificacion.marcarFallido(ex.getMessage());
        }
    }
}

