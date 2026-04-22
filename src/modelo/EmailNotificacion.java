package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import modelo.enumeraciones.TipoNotificacion;

public class EmailNotificacion extends Notificacion {
    private final String direccionDestinatario;
    private final String asunto;
    private final String cuerpoHTML;
    private final List<String> adjuntos;

    public EmailNotificacion(String codigo, TipoNotificacion tipo, String idDestinatario,
            String mensaje, String direccionDestinatario, String asunto, String cuerpoHTML,
            List<String> adjuntos) {
        super(codigo, tipo, idDestinatario, mensaje);
        this.direccionDestinatario = direccionDestinatario;
        this.asunto = asunto;
        this.cuerpoHTML = cuerpoHTML;
        this.adjuntos = new ArrayList<>(adjuntos);
    }

    @Override
    public void enviar() {
        if (direccionDestinatario == null || direccionDestinatario.isBlank()) {
            throw new IllegalStateException("La direccion de email es obligatoria");
        }
    }

    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getCuerpoHTML() {
        return cuerpoHTML;
    }

    public List<String> getAdjuntos() {
        return Collections.unmodifiableList(adjuntos);
    }
}

