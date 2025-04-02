package com.streaming.client;

import com.streaming.server.Person;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Person person = (Person) in.readObject();

            System.out.println("Reçu : " + person.ToString());

            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
