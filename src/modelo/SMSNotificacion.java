package modelo;

import modelo.enumeraciones.TipoNotificacion;

public class SMSNotificacion extends Notificacion {
    private final String numeroTelefono;
    private final int numCaracteres;
    private final String proveedor;

    public SMSNotificacion(String codigo, TipoNotificacion tipo, String idDestinatario,
            String mensaje, String numeroTelefono, int numCaracteres, String proveedor) {
        super(codigo, tipo, idDestinatario, mensaje);
        this.numeroTelefono = numeroTelefono;
        this.numCaracteres = numCaracteres;
        this.proveedor = proveedor;
    }

    @Override
    public void enviar() {
        if (numeroTelefono == null || numeroTelefono.isBlank()) {
            throw new IllegalStateException("El numero de telefono es obligatorio");
        }
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public int getNumeroCaracteres() {
        return numCaracteres;
    }

    public String getProveedor() {
        return proveedor;
    }
}

