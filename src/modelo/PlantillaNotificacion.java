package modelo;

import modelo.enumeraciones.CanalNotificacion;

public class PlantillaNotificacion {
    private final String id;
    private final String codigo;
    private final CanalNotificacion canal;
    private final String asunto;
    private final String plantillaCuerpo;

    public PlantillaNotificacion(String id, String codigo, CanalNotificacion canal,
            String asunto, String plantillaCuerpo) {
        this.id = id;
        this.codigo = codigo;
        this.canal = canal;
        this.asunto = asunto;
        this.plantillaCuerpo = plantillaCuerpo;
    }

    public String getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public CanalNotificacion getCanal() {
        return canal;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getPlantillaCuerpo() {
        return plantillaCuerpo;
    }
}

