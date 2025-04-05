package client;

import shared.ProtocolType;
import shared.ServerInfo;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
        public static void main(String[] args) {
        try (Socket socket = new Socket(ServerInfo.getListenSocketIP(), ServerInfo.getListenSocketPort())) {
            Scanner scanner = new Scanner(System.in);

            // Demander à l'utilisateur de choisir un protocole
            System.out.println("Choisissez un protocole de streaming :");
            System.out.println("1 - UDP");
            System.out.println("2 - TCP");
            System.out.println("3 - RTP");
            int choice = scanner.nextInt();
            scanner.nextLine();

            ProtocolType protocol;
            switch (choice) {
                case 1:
                    protocol = ProtocolType.UDP;
                    break;
                case 2:
                    protocol = ProtocolType.TCP;
                    break;
                case 3:
                    protocol = ProtocolType.RTP;
                    break;
                default:
                    protocol = ProtocolType.UDP;
            }

            // Envoyer le protocole au serveur
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(protocol);
            oos.flush();

            // Lire l'URL du flux envoyé par le serveur
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String streamUrl = reader.readLine().trim();
            System.out.println("Le serveur envoie le stream à : " + streamUrl);

            // Lancer JavaFX pour afficher la vidéo
            ClientFX.startApp(streamUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

