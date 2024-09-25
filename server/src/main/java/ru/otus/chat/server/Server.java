package ru.otus.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        clients = new ArrayList<>();
        authenticatedProvider = new InMemoryAuthenticationProvider(this);
        authenticatedProvider.initialize();
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized void privateMessage(String message, String username){
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)){
                client.sendMessage(message);
                return;
            }
        }
        System.out.println("not found");
    }

    public boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    //    «/kick username»
    public synchronized void kickUser(ClientHandler sender,String remoteUsername){
        if (!this.authenticatedProvider.isAdmin(sender.getUsername())) {
            sender.sendMessage("You don't have enough permissions to kick user " + remoteUsername);
            return;
        }
        if (sender.getUsername().equals(remoteUsername)) {
            sender.sendMessage("You can't kick yourself! ");
            return;
        }
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(remoteUsername)){
                client.sendMessage(sender.getUsername()+ " blocked " + remoteUsername );
                client.sendMessage("/removed");
                clients.remove(client);
                this.broadcastMessage(remoteUsername + " was removed from chat by " + sender.getUsername() );
                return;
            }
        }
        sender.sendMessage(remoteUsername + " not found");
        }
    }

