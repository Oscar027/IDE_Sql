package connection;

import resources.classes.toConnection;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FXConnectionSQLServer implements FXConnection{

    private String Username = "";
    private String Password = "";
    private String TypeConnection = "";
    private Connection connection;

    @Override
    public void setData(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    @Override
    public void setData(String username, String password, String alternative) {
        this.Username = username;
        this.Password = password;
        this.TypeConnection = alternative;
    }

    @Override
    public void Connect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").getDeclaredConstructor().newInstance();
            System.out.println(TypeConnection);
            if (TypeConnection.equals("Windows Authentication")){
                connection = DriverManager.getConnection("jdbc:sqlserver://" + toConnection.getHost() + ";integratedSecurity=true;");
            }
            else if (TypeConnection.equals("SQL Server Authentication")){
                connection = DriverManager.getConnection("jdbc:sqlserver://" + toConnection.getHost() + ";",  Username ,Password);
            }
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException | SQLException e) {
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
