package modelo;

import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.TipoNotificacion;

public class ExclusionUsuario {
    private final String idUsuario;
    private final TipoNotificacion tipoNotificacion;
    private final CanalNotificacion canal;

    public ExclusionUsuario(String idUsuario, TipoNotificacion tipoNotificacion,
            CanalNotificacion canal) {
        this.idUsuario = idUsuario;
        this.tipoNotificacion = tipoNotificacion;
        this.canal = canal;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public TipoNotificacion getTipoNotificacion() {
        return tipoNotificacion;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }
}

