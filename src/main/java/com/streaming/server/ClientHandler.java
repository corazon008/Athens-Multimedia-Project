package com.streaming.server;

import java.io.*;
import java.net.*;
import java.util.logging.*;

// Handles Client Requests
class ClientHandler extends Thread {
    private Socket clientSocket;
    private static final Logger logger = Logger.getLogger("ClientHandler");

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            out.println("Welcome to Streaming Server");
            String clientRequest = in.readLine();
            logger.info("Received request: " + clientRequest);

            // Example response - later replace with video streaming logic
            out.println("Processing request: " + clientRequest);

        } catch (IOException e) {
            logger.severe("Client error: " + e.getMessage());
        }
    }
}
