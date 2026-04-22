package modelo;

import java.time.LocalDateTime;

import modelo.enumeraciones.EstadoNotificacion;
import modelo.enumeraciones.TipoNotificacion;

public abstract class Notificacion {
    private final String codigo;
    private final TipoNotificacion tipo;
    private final String idDestinatario;
    private final String mensaje;
    private EstadoNotificacion estado;
    private final LocalDateTime fechaEnvio;
    private LocalDateTime enviadoEn;
    private int contadorIntento;
    private String ultimoError;

    protected Notificacion(String codigo, TipoNotificacion tipo, String idDestinatario,
            String mensaje) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.idDestinatario = idDestinatario;
        this.mensaje = mensaje;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.fechaEnvio = LocalDateTime.now();
        this.contadorIntento = 0;
    }

    public abstract void enviar();

    public String getId() {
        return codigo;
    }

    public TipoNotificacion getTipoNotificacion() {
        return tipo;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getContadorIntento() {
        return contadorIntento;
    }

    public void marcarEnviado(LocalDateTime enviadoEn) {
        this.enviadoEn = enviadoEn;
        this.estado = EstadoNotificacion.ENVIADA;
    }

    public void marcarFallido(String mensajeError) {
        this.ultimoError = mensajeError;
        this.contadorIntento++;
        this.estado = EstadoNotificacion.FALLIDA;
    }

    public boolean puedeReintentar(int maximosReintentos) {
        return contadorIntento < maximosReintentos;
    }
}

