import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import fabrica.NotificacionFactory;
import modelo.ConfigTipoNotificacion;
import modelo.Notificacion;
import modelo.PlantillaNotificacion;
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
        List<Usuario> usuarios = List.of(
                new Usuario("u1", "Alice", "alice@example.com", "555-0101", RolUsuario.ESTUDIANTE, "token-1"),
                new Usuario("u2", "Bob", "bob@example.com", "555-0202", RolUsuario.PROFESOR, "token-2"),
                new Usuario("u3", "Carol", "carol@example.com", "", RolUsuario.ADMIN, ""));
        List<Notificacion> historial = new ArrayList<>();
        ProcesadorDestinatario procesador = new ProcesadorDestinatario(usuarios, List.of());
        MotorPlantilla motor = new MotorPlantilla();
        NotificacionFactory factory = new NotificacionFactory(motor);
        ServicioNotificaciones servicio = new ServicioNotificaciones(factory, procesador);
        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Mostrar enums");
            System.out.println("3. Trigger bulk");
            System.out.println("4. Enviar manual");
            System.out.println("5. Mostrar historial de enviadas");
            System.out.println("6. Salir");
            System.out.print("Opcion: ");
            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> usuarios.forEach(u -> System.out.println(u.toString()));
                case "2" -> listEnums();
                case "3" -> historial.addAll(triggerBulk(scanner, servicio));
                case "4" -> {
                    Notificacion n = sendManual(scanner, factory, usuarios);
                    if (n != null) historial.add(n);
                }
                case "5" -> {
                    System.out.println("Historial:");
                    historial.forEach(n -> System.out.println(n.toString()));
                }
                case "6" -> running = false;
                default -> System.out.println("Invalida");
            }
        }
        scanner.close();
    }

    private static void listEnums() {
        System.out.println("Tipos:");
        for (TipoNotificacion t : TipoNotificacion.values()) System.out.println("- " + t);
        System.out.println("Roles:");
        for (RolUsuario r : RolUsuario.values()) System.out.println("- " + r);
        System.out.println("Canales:");
        for (CanalNotificacion c : CanalNotificacion.values()) System.out.println("- " + c);
    }

    private static <T extends Enum<T>> T selectEnum(Scanner s, String t, Class<T> c) {
        T[] vals = c.getEnumConstants();
        System.out.println("Seleccione " + t + ":");
        for (int i = 0; i < vals.length; i++) System.out.println((i + 1) + ". " + vals[i]);
        while (true) {
            try {
                int ch = Integer.parseInt(s.nextLine().trim());
                if (ch >= 1 && ch <= vals.length) return vals[ch - 1];
            } catch (Exception e) {}
            System.out.print("Invalido, reintente: ");
        }
    }

    private static List<Notificacion> triggerBulk(Scanner s, ServicioNotificaciones ser) {
        TipoNotificacion t = selectEnum(s, "Tipo", TipoNotificacion.class);
        RolUsuario r = selectEnum(s, "Rol", RolUsuario.class);
        Map<String, String> p = new HashMap<>();
        p.put("name", "Usuario");
        p.put("type", t.name());
        p.put("timestamp", LocalDateTime.now().toString());
        List<Notificacion> res = ser.trigger(t, r, p);
        System.out.println("Bulk enviado.");
        return res;
    }

    private static Notificacion sendManual(Scanner s, NotificacionFactory f, List<Usuario> us) {
        System.out.println("Seleccione usuario:");
        for (int i = 0; i < us.size(); i++) System.out.println((i + 1) + ". " + us.get(i).getNombre());
        Usuario u = null;
        while (u == null) {
            try {
                int ch = Integer.parseInt(s.nextLine().trim());
                if (ch >= 1 && ch <= us.size()) u = us.get(ch - 1);
            } catch (Exception e) {}
            if (u == null) System.out.print("Invalido: ");
        }
        CanalNotificacion c = selectEnum(s, "Canal", CanalNotificacion.class);
        TipoNotificacion t = selectEnum(s, "Tipo", TipoNotificacion.class);
        System.out.print("Asunto: ");
        String as = s.nextLine();
        System.out.print("Cuerpo: ");
        String cu = s.nextLine();
        PlantillaNotificacion pl = new PlantillaNotificacion(java.util.UUID.randomUUID().toString(), "MANUAL", c, as, cu);
        Map<String, String> ph = new HashMap<>();
        ph.put("name", u.getNombre());
        ph.put("type", t.name());
        ph.put("timestamp", LocalDateTime.now().toString());
        Notificacion n = f.crear(t, u, pl, ph, new ConfigTipoNotificacion(t, c, true, true));
        try {
            n.enviar();
            n.marcarEnviado(LocalDateTime.now());
            System.out.println("Enviada");
        } catch (Exception e) {
            n.marcarFallido(e.getMessage());
            System.out.println("Fallo: " + e.getMessage());
        }
        System.out.println(n.toString());
        return n;
    }
}
