package com.streaming.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Serveur en attente de connexion...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connecté !");

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            Person person = new Person("Alice", 25);
            out.writeObject(person);
            out.flush();

            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
