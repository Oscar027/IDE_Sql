package resources.classes;

public class toConnection {

    private static String Host;
    private static String Port;
    private static String Database;
    private static String User;
    private static String Password;
    private static String URL;

    public static String getHost() {
        return Host;
    }

    public static void setHost(String host) {
        Host = host;
    }

    public static String getPort() {
        return Port;
    }

    public static void setPort(String port) {
        Port = port;
    }

    public static String getDatabase() {
        return Database;
    }

    public static void setDatabase(String database) {
        Database = database;
    }

    public static String getUser() {
        return User;
    }

    public static void setUser(String user) {
        User = user;
    }

    public static String getPassword() {
        return Password;
    }

    public static void setPassword(String password) {
        Password = password;
    }

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        toConnection.URL = URL;
    }
}
