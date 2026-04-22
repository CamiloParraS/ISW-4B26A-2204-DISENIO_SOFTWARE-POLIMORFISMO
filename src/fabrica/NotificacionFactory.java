package fabrica;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import modelo.ConfigTipoNotificacion;
import modelo.EmailNotificacion;
import modelo.Notificacion;
import modelo.PlantillaNotificacion;
import modelo.PushNotificacion;
import modelo.SMSNotificacion;
import modelo.Usuario;
import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.TipoNotificacion;
import servicio.MotorPlantilla;

public class NotificacionFactory {
    private final MotorPlantilla motorPlantilla;

    public NotificacionFactory(MotorPlantilla motorPlantilla) {
        this.motorPlantilla = motorPlantilla;
    }

    public Notificacion crear(TipoNotificacion tipo, Usuario destinatario,
            PlantillaNotificacion plantilla, Map<String, String> placeholders,
            ConfigTipoNotificacion config) {
        if (!config.estaHabilitado()) {
            throw new IllegalStateException(
                    "El tipo de notificacion esta deshabilitado para el canal "
                            + config.getCanal());
        }

        String msjProcesado = motorPlantilla.procesar(plantilla, placeholders);
        CanalNotificacion canal = config.getCanal();

        return switch (canal) {
            case EMAIL -> crearNotificacionEmail(tipo, destinatario, plantilla, msjProcesado);
            case SMS -> crearSMSNotificacion(tipo, destinatario, plantilla, msjProcesado);
            case PUSH -> crearPushNotificacion(tipo, destinatario, plantilla, msjProcesado);
        };
    }

    private EmailNotificacion crearNotificacionEmail(TipoNotificacion tipo, Usuario destinatario,
            PlantillaNotificacion plantilla, String msjProcesado) {
        return new EmailNotificacion(UUID.randomUUID().toString(), tipo, destinatario.getId(),
                msjProcesado, destinatario.getEmail(), plantilla.getAsunto(), msjProcesado,
                List.of());
    }

    private SMSNotificacion crearSMSNotificacion(TipoNotificacion tipo, Usuario destinatario,
            PlantillaNotificacion plantilla, String msjProcesado) {
        return new SMSNotificacion(UUID.randomUUID().toString(), tipo, destinatario.getId(),
                msjProcesado, destinatario.getTelefono(), msjProcesado.length(),
                "ProveedorSmsPorDefecto");
    }

    private PushNotificacion crearPushNotificacion(TipoNotificacion tipo,
            Usuario destinatario, PlantillaNotificacion plantilla, String msjProcesado) {
        return new PushNotificacion(UUID.randomUUID().toString(), tipo, destinatario.getId(),
                msjProcesado, destinatario.getTokenDispositivo(), "generica",
                "/notificaciones/" + plantilla.getCodigo(), "/assets/icons/notificacion.png");
    }
}

