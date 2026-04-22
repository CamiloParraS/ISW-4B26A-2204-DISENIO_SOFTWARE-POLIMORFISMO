package modelo;

import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.TipoNotificacion;

public class ConfigTipoNotificacion {
    private final TipoNotificacion tipoNotificacion;
    private final CanalNotificacion canal;
    private final boolean activo;
    private final boolean permitirDesuscripcion;

    public ConfigTipoNotificacion(TipoNotificacion tipoNotificacion,
            CanalNotificacion canal, boolean activo, boolean permitirDesuscripcion) {
        this.tipoNotificacion = tipoNotificacion;
        this.canal = canal;
        this.activo = activo;
        this.permitirDesuscripcion = permitirDesuscripcion;
    }

    public boolean estaHabilitado() {
        return activo;
    }

    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }

    public boolean permiteDesuscripcion() {
        return permitirDesuscripcion;
    }
}

