package connection;

import java.sql.Connection;

public interface FXConnection {
    public void setData(String username, String password);
    public void setData(String username, String password, String database);
    public void Connect();
    public Connection getConnection();
    public void Disconnect();
}
