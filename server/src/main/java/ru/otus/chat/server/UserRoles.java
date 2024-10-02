package ru.otus.chat.server;

public class UserRoles {

private int id;
private String role;

public UserRoles(int id, String role) {
        this.id = id;
        this.role = role;
        }

public int getId() {
        return id;
        }

public void setId(int id) {
        this.id = id;
        }

public String getRole() {
        return role;
        }



@Override
public String toString() {
        return "Role{" +
        "id=" + id +
        ", role ='" + role + '\'' +
        '}';
        }}
