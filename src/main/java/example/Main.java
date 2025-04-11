package example;

import java.util.logging.*;

public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        log.setUseParentHandlers(false); // Évite les logs en double

        // Crée un handler qui écrit sur System.out
        ConsoleHandler handler = new ConsoleHandler();

        handler.setLevel(Level.ALL);
        log.addHandler(handler);
        log.setLevel(Level.ALL);

        log.severe("Ceci est un message SEVERE");
        log.warning("Ceci est un message WARNING");
        log.info("Ceci est un message INFO");
        log.config("Ceci est un message CONFIG");
        log.fine("Ceci est un message FINE");
        log.finer("Ceci est un message FINER");
        log.finest("Ceci est un message FINEST");

        System.out.println("Fin du programme.");
    }
}
