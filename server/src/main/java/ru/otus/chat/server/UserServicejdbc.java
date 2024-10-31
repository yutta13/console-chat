package ru.otus.chat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserServicejdbc implements UserService {
    public Server server;
    private Connection connection;
    private ArrayList<Object> users;


    public UserServicejdbc(Server server) {
        this.server = server;
        this.users = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(PathConf.getDATABASE_URL(), PathConf.getDbLogin(), PathConf.getDbPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserServicejdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void initialize() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(Db_queries.getUsersQuery())) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    String username = resultSet.getString("username");
                    User user = new User(id, login, password, username);
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setRolesToUsers(users);
        return users;
    }

    public String getUsernameByLoginAndPassword(String userlogin, String userpassword) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getUserNameQuery())) {
            preparedStatement.setString(1, userlogin);
            preparedStatement.setString(2, userpassword);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                //String un = resultSet.getString("username");
                //System.out.println(un);
                return resultSet.getString("username");
            }
        }
    }


    public Boolean isLoginAlreadyExist(String userlogin) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getUserloginCheck())) {
            preparedStatement.setString(1, userlogin);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) { // Check if a row exists
                    return true; // Login exists
                } else {
                    return false; // Login doesn't exist
                }
            }
        }
    }


    public Boolean isUsernameAlreadyExist(String username) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getUsernameCheck())) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) { // Check if a row exists
                    return true; // Login exists
                } else {
                    return false; // Login doesn't exist
                }
            }
        }
    }

    private void setRolesToUsers(List<User> users) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.getUserRolesQuery())) {
            for (User user : users) {
                preparedStatement.setInt(1, user.getId());
                List<UserRoles> userRoles = new ArrayList<>();
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String role = resultSet.getString(2);
                        UserRoles roles = new UserRoles(id, role);
                        userRoles.add(roles);
                    }
                }
                user.setRole(userRoles);
            }
        }
    }

    public synchronized String addUser(String userlogin, String userpassword, String username) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.addUser());
        preparedStatement.setString(1, userlogin);
        preparedStatement.setString(2, userpassword);
        preparedStatement.setString(3, username);
        preparedStatement.setString(4, username);
        int rows = preparedStatement.executeUpdate();
        System.out.printf("%d rows added " + username, rows);
        return username;
    }

    @Override
    public synchronized boolean authenticate(ClientHandler clientHandler, String login, String password) throws SQLException {
        String authName = getUsernameByLoginAndPassword(login, password);
        if (authName == null) {
            clientHandler.sendMessage("Некорректный логин/пароль");
            return false;
        }

        if (server.isUsernameBusy(authName)) {
            clientHandler.sendMessage("Учетная запись уже занята");
            return false;
        }

        clientHandler.setUsername(authName);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/authok " + authName);
        return true;
    }

    @Override
    public boolean registration(ClientHandler clientHandler, String login, String password, String username) throws SQLException {
        if (login.trim().length() < 3 || password.trim().length() < 6
                || username.trim().length() < 2) {
            clientHandler.sendMessage("Требования логин 3+ символа, пароль 6+ символа," +
                    "имя пользователя 2+ символа не выполнены");
            return false;
        }
        if (isLoginAlreadyExist(login)) {
            clientHandler.sendMessage("Указанный логин уже занят");
            return false;
        }
        if (isUsernameAlreadyExist(username)) {
            clientHandler.sendMessage("Указанное имя пользователя уже занято");
            return false;
        }
        username = addUser(login, password, username);
        clientHandler.setUsername(username);
        server.subscribe(clientHandler);
        clientHandler.sendMessage("/regok " + username);
        return true;
    }

    @Override
    public boolean isAdmin(int userId) {
        int flag = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(Db_queries.isAdminQuery())) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                   flag = resultSet.getInt(1);
                    return flag == 1;
                }
            }
        }catch (Exception e){
        e.printStackTrace();
        }
        return flag == 0;
    }


        @Override
        public void close () throws Exception {
            connection.close();
        }
    }
