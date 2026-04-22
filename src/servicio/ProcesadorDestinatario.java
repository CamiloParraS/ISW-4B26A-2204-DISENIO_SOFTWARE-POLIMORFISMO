package servicio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import modelo.ExclusionUsuario;
import modelo.Usuario;
import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.RolUsuario;
import modelo.enumeraciones.TipoNotificacion;

public class ProcesadorDestinatario {
    private final List<Usuario> usuarios;
    private final List<ExclusionUsuario> exclusiones;

    public ProcesadorDestinatario(List<Usuario> usuarios,
            List<ExclusionUsuario> exclusiones) {
        this.usuarios = new ArrayList<>(usuarios);
        this.exclusiones = new ArrayList<>(exclusiones);
    }

    public List<Usuario> procesar(TipoNotificacion tipo, RolUsuario rol,
            CanalNotificacion canal) {
        return usuarios.stream().filter(usuario -> usuario.getRol() == rol)
                .filter(usuario -> !estaExcluido(usuario.getId(), tipo, canal))
                .collect(Collectors.toList());
    }

    private boolean estaExcluido(String idUsuario, TipoNotificacion tipo,
            CanalNotificacion canal) {
        return exclusiones.stream().anyMatch(exclusion -> exclusion.getIdUsuario().equals(idUsuario)
                && exclusion.getTipoNotificacion() == tipo
                && exclusion.getCanal() == canal);
    }
}

