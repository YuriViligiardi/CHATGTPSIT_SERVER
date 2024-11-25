package it.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class MyThread extends Thread {

    private Socket s;
    private HashMap<String, Socket> connectedUsers;
    private String currentUsername;

    public MyThread(Socket s, HashMap<String, Socket> connectedUsers) {
        this.s = s;
        this.connectedUsers = connectedUsers;
    }

    @Override
    public void run() {
        boolean closed = true;
        boolean connected = false;

        do {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                String stringaRicevuta;
                String[] words;
                String stringaInviare;
                do {
                    stringaRicevuta = in.readLine();
                    System.out.println("Client invia: " + stringaRicevuta);
                    words = stringaRicevuta.split(" ");

                    switch (words[0]) {

                        // Connesione
                        case "CONNECT":
                            boolean check = false;
                            do {
                                if (connectedUsers.containsKey(words[1])) {
                                    stringaInviare = "KO user-not-available";
                                    out.writeBytes(stringaInviare + "\n");
                                } else {
                                    currentUsername = words[1];
                                    connectedUsers.put(currentUsername, s);
                                    stringaInviare = "JOIN " + currentUsername;
                                    SendGlobal(stringaInviare, null, null);
                                    out.writeBytes("JOIN" + "\n");
                                    System.out.println("Server invia: " + stringaInviare);

                                    check = true;
                                    connected = true;
                                    words = null;
                                }
                            } while (check = false);

                            break;

                        // Messaggi che non contengono comandi esistenti (nn serve perche il client gia
                        // controlla prima di inviare)
                        default:
                            stringaInviare = "KO incorrect-command";
                            out.writeBytes(stringaInviare + "\n");
                            System.out.println(stringaInviare);
                            break;
                    }
                } while (!connected);

                do {
                    stringaRicevuta = in.readLine();
                    stringaInviare = "";
                    words = stringaRicevuta.split(" ");

                    switch (words[0]) {

                        case "PRIVATE":

                            String destination = words[1];
                            for (int i = 2; i < words.length; i++) {
                                stringaInviare += words[i] + " ";
                            }

                            if (connectedUsers.containsKey(destination)) {
                                // out.writeBytes("OK" + "\n");
                                System.out.println("Server invia: OK, e inoltra: " + stringaInviare + "\n");

                                Socket destinationSocket = connectedUsers.get(destination);
                                DataOutputStream destinationOut = new DataOutputStream(
                                        destinationSocket.getOutputStream());
                                destinationOut.writeBytes("PRIVATE " + currentUsername + " " + stringaInviare + "\n");
                                System.out.println("Server invia: " + stringaInviare + "\n");

                            } else {
                                out.writeBytes("KO user-not-found\n");
                                System.out.println("Server invia: KO user-not-found\n");

                            }

                            break;

                        case "GLOBAL":

                            for (int i = 1; i < words.length; i++) {
                                stringaInviare += words[i] + " ";
                            }
                            SendGlobal(stringaInviare, words[0], currentUsername);

                            break;

                        case "CHANGE":

                            String newUsername = words[1];
                            if (connectedUsers.containsKey(newUsername)) {
                                stringaInviare = "KO";
                                out.writeBytes(stringaInviare + "\n");
                            } else {
                                stringaInviare = "ACCEPT ";
                                out.writeBytes(stringaInviare + "\n");
                                connectedUsers.remove(currentUsername); // Rimuove il vecchio username
                                connectedUsers.put(newUsername, s); // Aggiunge il nuovo username
                                currentUsername = newUsername; // Aggiorna il nome utente corrente
                                out.writeBytes(stringaInviare + currentUsername + "\n");
                                System.out.println("Username aggiornato: " + currentUsername);
                                connected = true;
                                words = null;
                            }
                            break;

                        case "USERS":
                            stringaInviare = "USERS";

                            for (String user : connectedUsers.keySet()) {
                                if (!user.equals(currentUsername)) {
                                    stringaInviare += " " + user;
                                }
                            }
                            out.writeBytes(stringaInviare + "\n");
                            System.out.println("Server invia:" + stringaInviare);
                            break;

                        // Uscita dalla chat
                        case "ESC":
                            connectedUsers.remove(currentUsername);

                            stringaInviare = "BYE " + currentUsername + "\n";

                            SendGlobal(stringaInviare, null, null);

                            s.close();

                            connected = false;

                            // return;

                            break;

                        // Messaggi che non contengono comandi esistenti (nn serve perche il client gia
                        // controlla prima di inviare)
                        default:
                            break;
                    }

                } while (connected);

            } catch (Exception e) {
                System.out.println("Errore, comunicazione fallita");
                closed = false;
            }
        } while (closed);

    }

    public void SendGlobal(String str, String type, String mitt) throws IOException {
        for (Socket clientSocket : connectedUsers.values()) {
            if (clientSocket != s) { // per evitare di mandare il messaggio globale anche a chi lo ha creato
                DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());

                if (type == null) {
                    clientOut.writeBytes(str + "\n");
                    System.out.println("Server invia: " + str + "\n");
                } else {
                    clientOut.writeBytes(type + " " + mitt + " " + str + "\n");
                }

            }
        }
    }
}