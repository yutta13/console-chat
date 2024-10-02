package ru.otus.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;
    List<User> users = new ArrayList<>();
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                System.out.println("Клиент подключился ");
                //цикл аутентификации
                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith("/exit")) {
                            sendMessage("/exitok");
                            break;
                        }
                        // /auth login password
                        if (message.startsWith("/auth ")) {
                            String[] elements = message.split(" ");
                            if (elements.length != 3) {
                                sendMessage("Неверный формат команды /auth ");
                                continue;
                            }
                            if (server.getUserService()
                                    .authenticate(this, elements[1], elements[2])) {
                                break;
                            }
                            continue;
                        }
                        // /reg login password username
                        if (message.startsWith("/reg ")) {
                            String[] elements = message.split(" ");
                            if (elements.length != 4) {
                                sendMessage("Неверный формат команды /reg ");
                                continue;
                            }
                            if (server.getUserService()
                                    .registration(this, elements[1], elements[2], elements[3])) {
                                break;
                            }
                            continue;
                        }
                    }
                    sendMessage("Перед работой необходимо пройти аутентификацию командой " +
                            "/auth login password или регистрацию командой /reg login password username");
                }
                System.out.println("Клиент " + username + " успешно прошел аутентификацию. ");
                //цпкл работы

                while (true) {
                    String message = in.readUTF();
                    if (message.startsWith("/")) {
                        if (message.startsWith("/exit")) {
                            sendMessage("/exitok");
                            break;
                        }
                        // kick user under ADMIN role only
                        else if (message.startsWith("/w")) {
                            String[] splitMassage = message.split(" ");
                            String remoteUserName = splitMassage[1];
                            String[] remoteMsg = message.split(remoteUserName);
                            message = remoteMsg[1];
                            server.privateMessage(username + " : " + message, remoteUserName);
                        }

                        if (message.startsWith("/kick ")) {
                            String[] splitMassage = message.split(" ");
                            if (splitMassage.length != 2) {
                                sendMessage("Некорректный формат данных '/kick username'");
                            }
                            String remoteUserName = splitMassage[1];
                            server.kickUser(this, remoteUserName);
                        }
                        continue;

                    }
                    server.broadcastMessage(username + " : " + message);
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

