package com.streaming.client;

import com.streaming.server.Packet;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Packet packet = (Packet) in.readObject();
                System.out.println("Reçu : " + packet.toString());
                if (packet.isLastPacket)
                    break;
            }
            in.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
