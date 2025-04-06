package client;

import shared.ClientInfoPacket;
import shared.ProtocolType;
import shared.ServerInfo;
import shared.VideoFormat;
import shared.UserSelection;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        SpeedTest speedTest = new SpeedTest(5000);
        speedTest.StartSpeedTest();

        StreamSettings.Show();

        speedTest.WaitForSpeedTest();
        double downloadSpeed = speedTest.getDownloadSpeed().doubleValue() / 1000000; // Convertir en Mbps
        System.out.println("Vitesse de téléchargement : " + downloadSpeed + " Mbps");

        StreamSettings.Wait();
        System.out.println("Format sélectionné : " + UserSelection.format);
    }

    public static void main1(String[] args) {
        try (Socket socket = new Socket(ServerInfo.getListenSocketIP(), ServerInfo.getListenSocketPort())) {
            ClientInfoPacket clientInfo = new ClientInfoPacket(1000, VideoFormat.MP4);

            SendObject(clientInfo, socket);

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

    private static void SendObject(Object object, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(object);
        oos.flush();
    }

}

