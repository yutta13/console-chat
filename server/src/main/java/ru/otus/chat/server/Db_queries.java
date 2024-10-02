package ru.otus.chat.server;

public class Db_queries {
    public static String getUserRolesQuery() {
        return USER_ROLES_QUERY;
    }

    public static String getUsersQuery() {
        return USERS_QUERY;
    }

    public static String getUserNameQuery() {
        return USERNAME_QUERY;
    }

    public static String getUserloginCheck() {
        return USER_LOGIN_CHECK;
    }

    public static String getUsernameCheck() {
        return USERNAME_CHECK;
    }

    public static String addUser() {
        return ADD_USER;
    }
    public static String isAdminQuery() {
        return IS_ADMIN_QUERY;
    }

    private static final String USER_ROLES_QUERY = "select r.id as \"id\", r.role as \"role\" \n" +
            "from roles r\n" +
            " join user_role ur\n" +
            " on r.id = ur.role_id\n" +
            " where ur.user_id = ?" +
            " ORDER BY id;";

    private static final String IS_ADMIN_QUERY = "select count from roles r" +
            " join user_role ur\n" +
            " on r.id = ur.role_id\n" +
            " where ur.user_id = ?" +
            " and r.role = 'ADMIN'";

    private static final String USERS_QUERY = "SELECT * from clients";

    private static final String USERNAME_QUERY = "select c.username as \"username\" \n" +
            "from clients c\n" +
            " where c.login = ?\n" +
            " and c.password = ?";
    private static final String USER_LOGIN_CHECK = "select c.login as \"login\" \n" +
            "from clients c\n" +
            " where c.login = ?";
    private static final String USERNAME_CHECK = "select c.username as \"username\" \n" +
            "from clients c\n" +
            " where c.username = ?";

    private static final String ADD_USER = "insert into clients (login, password, username) values ( ?, ?, ?);\n" +
            "insert into user_role (role_id, user_id)\n" +
            " select 2, c.id\n" +
            " from clients c\n" +
            " where c.username = ? ;\n";
}
