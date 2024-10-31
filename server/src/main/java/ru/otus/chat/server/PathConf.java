package ru.otus.chat.server;

public class PathConf {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/chat";
    private static final String dbLogin = "postgres";
    private static final String dbPassword = "postgres";
    public static String getDbLogin() {
        return dbLogin;
    }
    public static String getDbPassword() {
        return dbPassword;
    }
    public static String getDATABASE_URL() {
        return DATABASE_URL;
    }
}
