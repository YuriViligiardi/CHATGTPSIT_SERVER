package it.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket ss1 = new ServerSocket(3000);
        System.out.println("Il server è in ascolto");
        
        HashMap<String, Socket> connectedUsers = new HashMap<>();

        do {
            Socket s1 = ss1.accept();
            System.out.println("Qualcuno si è collegato");

            MyThread mt = new MyThread(s1, connectedUsers);
            mt.start();
        } while (true);
    }
}