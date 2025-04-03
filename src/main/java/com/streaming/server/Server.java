package com.streaming.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Serveur en attente de connexion...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connecté !");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            for (int i = 0; i < 10; i++) {
                Packet packet = new Packet(i);
                sendObject(out, packet);
                Thread.sleep(500);
            }

            Packet lastPacket = new Packet(10);
            lastPacket.isLastPacket = true;
            sendObject(out, lastPacket);

            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendObject(ObjectOutputStream out, Object obj) throws IOException {
        out.writeObject(obj);
        out.flush();
    }
}
