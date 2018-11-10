package connection;

import resources.classes.toConnection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FXConnectionOracle implements FXConnection{

    private Connection connection;
    private String Username;
    private String Password;
    private String Alternative;

    public FXConnectionOracle(){
        this.connection = null;
    }

    @Override
    public void setData(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    @Override
    public void setData(String username, String password, String alternative) {
        this.Username = username;
        this.Password = password;
        this.Alternative = alternative;
    }

    @Override
    public void Connect() {
        try{
            Class.forName("oracle.jdbc.OracleDriver").getDeclaredConstructor().newInstance();
            this.connection = DriverManager.getConnection("jdbc:oracle:thin:@" + toConnection.getHost() + ":" + toConnection.getPort() + ":" + toConnection.getSID(),Username,Password);
        }
        catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void Disconnect() {
        try {
            this.connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
