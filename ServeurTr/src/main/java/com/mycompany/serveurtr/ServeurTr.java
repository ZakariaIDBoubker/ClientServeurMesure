package com.mycompany.serveurtr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurTr {
    public static void main(String[] args) {
        final int PORT = 1234;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion : " + clientSocket.getRemoteSocketAddress());
                Communication comm = new Communication(clientSocket);
                comm.start(); // thread par client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

