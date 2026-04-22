package modelo;

import modelo.enumeraciones.TipoNotificacion;

public class PushNotificacion extends Notificacion {
    private final String tokenDispositivo;
    private final String plataforma;
    private final String deepLinkUrl;
    private final String iconUrl;

    public PushNotificacion(String codigo, TipoNotificacion tipo, String idDestinatario,
            String mensaje, String tokenDispositivo, String plataforma, String deepLinkUrl,
            String iconUrl) {
        super(codigo, tipo, idDestinatario, mensaje);
        this.tokenDispositivo = tokenDispositivo;
        this.plataforma = plataforma;
        this.deepLinkUrl = deepLinkUrl;
        this.iconUrl = iconUrl;
    }

    @Override
    public void enviar() {
        if (tokenDispositivo == null || tokenDispositivo.isBlank()) {
            throw new IllegalStateException("El token del dispositivo es obligatorio");
        }
    }

    public String getTokenDispositivo() {
        return tokenDispositivo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public String getDeepLinkURL() {
        return deepLinkUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public String toString() {
        return super.toString() + " -> Push[token=" + tokenDispositivo + ", plataforma=" + plataforma + "]";
    }
}

