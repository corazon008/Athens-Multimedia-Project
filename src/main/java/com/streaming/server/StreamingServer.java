package com.streaming.server;

import java.io.*;
import java.net.*;
import java.util.logging.*;

// Streaming Server
public class StreamingServer {
    private static final int PORT = 5000;
    private static final Logger logger = Logger.getLogger("StreamingServer");

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            logger.severe("Server error: " + e.getMessage());
        }
    }
}