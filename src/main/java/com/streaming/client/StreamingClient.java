package com.streaming.client;

import java.io.*;
import java.net.*;
import java.util.logging.*;

// Streaming Client
public class StreamingClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server");
            System.out.println(in.readLine());

            System.out.print("Enter video request: ");
            String request = userInput.readLine();
            out.println(request);

            System.out.println("Server response: " + in.readLine());

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}