package ru.otus.chat.server;

import java.sql.SQLException;
import java.util.List;

public interface UserService extends AutoCloseable {
    void initialize();

    List<User> getAll() throws SQLException;

    boolean authenticate(ClientHandler clientHandler, String login, String password) throws SQLException;

    boolean registration(ClientHandler clientHandler, String login, String password, String username) throws SQLException;

    boolean isAdmin(int userId) throws SQLException;

}