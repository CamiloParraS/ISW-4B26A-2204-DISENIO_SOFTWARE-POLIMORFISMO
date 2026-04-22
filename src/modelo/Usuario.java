package modelo;

import modelo.enumeraciones.RolUsuario;

public class Usuario {
    private final String id;
    private final String nombre;
    private final String email;
    private final String telefono;
    private final RolUsuario rol;
    private final String tokenDispositivo;

    public Usuario(String id, String nombre, String email, String telefono, RolUsuario rol,
            String tokenDispositivo) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.rol = rol;
        this.tokenDispositivo = tokenDispositivo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public String getTokenDispositivo() {
        return tokenDispositivo;
    }
}

