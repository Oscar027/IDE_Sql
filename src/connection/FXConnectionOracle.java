package connection;

import java.sql.Connection;

public class FXConnectionOracle implements FXConnection{

    private Connection connection;

    public FXConnectionOracle(){
        this.connection = null;
    }

    @Override
    public void setData(String username, String password) {

    }

    @Override
    public void setData(String username, String password, String database) {

    }

    @Override
    public void Connect() {

    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void Disconnect() {

    }
}
