package connection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FXConnectionMySQL implements FXConnection {

    private String Username = "";
    private String Password = "";
    private String Database = "mysql";
    private Connection Conn;

    @Override
    public void setData(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    @Override
    public void setData(String username, String password, String database) {
        this.Username = username;
        this.Password = password;
        this.Database = database;
    }

    @Override
    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            this.Conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + Database +"?useSSL=false&serverTimezone=UTC", Username, Password);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return this.Conn;
    }

    @Override
    public void Disconnect() {
        try {
            this.Conn.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
