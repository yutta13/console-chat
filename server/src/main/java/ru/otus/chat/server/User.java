package ru.otus.chat.server;


import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String login;
    private String password;
    private String username;
    private List<UserRoles> roles = new ArrayList<>();


    public String getLogin() {
        return login;
    }

    public User(Integer id, String login, String password, String username) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.username = username;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setRole(List<UserRoles> roles) {
        this.roles = roles;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", username=" + username +
                ", role=" + roles +
                '}';
    }
}