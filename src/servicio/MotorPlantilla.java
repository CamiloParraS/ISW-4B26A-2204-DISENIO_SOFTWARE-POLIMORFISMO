package servicio;

import java.util.Map;

import modelo.PlantillaNotificacion;

public class MotorPlantilla {
    public String procesar(PlantillaNotificacion plantilla,
            Map<String, String> placeholders) {
        String asuntoProcesado = aplicarMarcadores(plantilla.getAsunto(), placeholders);
        String cuerpoProcesado =
                aplicarMarcadores(plantilla.getPlantillaCuerpo(), placeholders);
        return asuntoProcesado + System.lineSeparator() + cuerpoProcesado;
    }

    private String aplicarMarcadores(String texto, Map<String, String> placeholders) {
        String textoProcesado = texto;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            textoProcesado = textoProcesado.replace("{" + entry.getKey() + "}",
                    entry.getValue());
            textoProcesado = textoProcesado.replace("{{" + entry.getKey() + "}}",
                    entry.getValue());
        }
        return textoProcesado;
    }
}

