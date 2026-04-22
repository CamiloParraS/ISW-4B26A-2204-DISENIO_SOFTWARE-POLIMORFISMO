import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import fabrica.NotificacionFactory;
import modelo.ConfigTipoNotificacion;
import modelo.EmailNotificacion;
import modelo.Notificacion;
import modelo.PlantillaNotificacion;
import modelo.PushNotificacion;
import modelo.SMSNotificacion;
import modelo.Usuario;
import modelo.enumeraciones.CanalNotificacion;
import modelo.enumeraciones.RolUsuario;
import modelo.enumeraciones.TipoNotificacion;
import servicio.MotorPlantilla;
import servicio.ProcesadorDestinatario;
import servicio.ServicioNotificaciones;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Sample data
        List<Usuario> usuarios = List.of(
                new Usuario("u1", "Alice", "alice@example.com", "555-0101", RolUsuario.ESTUDIANTE,
                        "token-1"),
                new Usuario("u2", "Bob", "bob@example.com", "555-0202", RolUsuario.PROFESOR,
                        "token-2"),
                new Usuario("u3", "Carol", "carol@example.com", "", RolUsuario.ADMIN, ""));

        ProcesadorDestinatario procesador = new ProcesadorDestinatario(usuarios, List.of());
        MotorPlantilla motor = new MotorPlantilla();
        NotificacionFactory factory = new NotificacionFactory(motor);
        ServicioNotificaciones servicio = new ServicioNotificaciones(factory, procesador);

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Seleccione una opcion: ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1" -> listUsers(usuarios);
                case "2" -> listEnums();
                case "3" -> triggerBulk(scanner, servicio);
                case "4" -> sendManual(scanner, factory, usuarios);
                case "5" -> running = false;
                default -> System.out.println("Opcion invalida, intente de nuevo.");
            }
            System.out.println();
        }

        scanner.close();
        System.out.println("Saliendo...");
    }

    private static void printMenu() {
        System.out.println("=== Menu de Notificaciones ===");
        System.out.println("1) Listar usuarios de ejemplo");
        System.out.println("2) Mostrar tipos y roles disponibles");
        System.out.println("3) Trigger: enviar notificaciones por tipo y rol (bulk)");
        System.out.println("4) Enviar notificacion manual a usuario especifico");
        System.out.println("5) Salir");
    }

    private static void listUsers(List<Usuario> usuarios) {
        System.out.println("Usuarios disponibles:");
        for (Usuario u : usuarios) {
            System.out.printf("- id=%s, nombre=%s, email=%s, telefono=%s, rol=%s, token=%s%n",
                    u.getId(), u.getNombre(), u.getEmail(), u.getTelefono(), u.getRol(),
                    u.getTokenDispositivo());
        }
    }

    private static void listEnums() {
        System.out.println("Tipos de notificacion:");
        for (TipoNotificacion t : TipoNotificacion.values()) {
            System.out.println("- " + t.name());
        }
        System.out.println("Roles de usuario:");
        for (RolUsuario r : RolUsuario.values()) {
            System.out.println("- " + r.name());
        }
        System.out.println("Canales disponibles (se usan internamente):");
        for (CanalNotificacion c : CanalNotificacion.values()) {
            System.out.println("- " + c.name());
        }
    }

    private static void triggerBulk(Scanner scanner, ServicioNotificaciones servicio) {
        System.out.println("Selecciona el tipo de notificacion (por nombre):");
        for (TipoNotificacion t : TipoNotificacion.values()) {
            System.out.println("- " + t.name());
        }
        System.out.print("Tipo: ");
        String tipoInput = scanner.nextLine().trim();
        TipoNotificacion tipo;
        try {
            tipo = TipoNotificacion.valueOf(tipoInput);
        } catch (Exception ex) {
            System.out.println("Tipo invalido.");
            return;
        }

        System.out.println("Selecciona rol objetivo (por nombre):");
        for (RolUsuario r : RolUsuario.values()) {
            System.out.println("- " + r.name());
        }
        System.out.print("Rol: ");
        String rolInput = scanner.nextLine().trim();
        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(rolInput);
        } catch (Exception ex) {
            System.out.println("Rol invalido.");
            return;
        }

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "Usuario");
        placeholders.put("type", tipo.name());
        placeholders.put("timestamp", LocalDateTime.now().toString());

        System.out.println("Enviando notificaciones (simulado) para tipo=" + tipo + " rol=" + rol);
        servicio.trigger(tipo, rol, placeholders);
        System.out.println("Trigger completado.");
    }

    private static void sendManual(Scanner scanner, NotificacionFactory factory,
            List<Usuario> usuarios) {
        System.out.println("Enviar notificacion manual a usuario.");
        System.out.println("Usuarios:");
        for (Usuario u : usuarios) {
            System.out.printf("- %s (%s)%n", u.getId(), u.getNombre());
        }
        System.out.print("Id destinatario: ");
        String id = scanner.nextLine().trim();
        Usuario destino =
                usuarios.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (destino == null) {
            System.out.println("Usuario no encontrado.");
            return;
        }

        System.out.println("Selecciona canal (EMAIL, SMS, PUSH): ");
        String canalInput = scanner.nextLine().trim().toUpperCase();
        CanalNotificacion canal;
        try {
            canal = CanalNotificacion.valueOf(canalInput);
        } catch (Exception ex) {
            System.out.println("Canal invalido.");
            return;
        }

        System.out.println("Selecciona tipo de notificacion:");
        for (TipoNotificacion t : TipoNotificacion.values())
            System.out.println("- " + t.name());
        System.out.print("Tipo: ");
        String tipoInput = scanner.nextLine().trim();
        TipoNotificacion tipo;
        try {
            tipo = TipoNotificacion.valueOf(tipoInput);
        } catch (Exception ex) {
            System.out.println("Tipo invalido.");
            return;
        }

        System.out.print("Asunto (opcional): ");
        String asunto = scanner.nextLine();
        System.out.print("Cuerpo del mensaje: ");
        String cuerpo = scanner.nextLine();

        PlantillaNotificacion plantilla = new PlantillaNotificacion(
                java.util.UUID.randomUUID().toString(), tipo.name() + "_PLANTILLA", canal,
                asunto.isBlank() ? "Asunto" : asunto, cuerpo);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", destino.getNombre());
        placeholders.put("type", tipo.name());
        placeholders.put("timestamp", LocalDateTime.now().toString());

        ConfigTipoNotificacion config = new ConfigTipoNotificacion(tipo, canal, true, true);
        Notificacion notificacion = factory.crear(tipo, destino, plantilla, placeholders, config);

        System.out.println("Intentando enviar notificacion...");
        try {
            notificacion.enviar();
            notificacion.marcarEnviado(LocalDateTime.now());
            System.out.println("Enviada correctamente.");
        } catch (RuntimeException ex) {
            notificacion.marcarFallido(ex.getMessage());
            System.out.println("Fallo al enviar: " + ex.getMessage());
        }

        printNotificacionDetails(notificacion);
    }

    private static void printNotificacionDetails(Notificacion n) {
        System.out.println("--- Detalles de la notificacion ---");
        System.out.println("Id: " + n.getId());
        System.out.println("Tipo: " + n.getTipoNotificacion());
        System.out.println("DestinatarioId: " + n.getIdDestinatario());
        System.out.println("Estado: " + n.getEstado());
        System.out.println("Mensaje: " + n.getMensaje());
        System.out.println("Intentos: " + n.getContadorIntento());

        if (n instanceof EmailNotificacion email) {
            System.out.println("Email -> direccion: " + email.getDireccionDestinatario());
            System.out.println("Asunto: " + email.getAsunto());
        } else if (n instanceof SMSNotificacion sms) {
            System.out.println("SMS -> numero: " + sms.getNumeroTelefono());
            System.out.println("Proveedor: " + sms.getProveedor());
        } else if (n instanceof PushNotificacion push) {
            System.out.println("Push -> token: " + push.getTokenDispositivo());
            System.out.println("Plataforma: " + push.getPlataforma());
        }
    }
}
