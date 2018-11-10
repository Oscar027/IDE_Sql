package mysql;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import principal.PrincipalController;
import resources.classes.toConnection;

import java.net.URL;
import java.util.ResourceBundle;

public class MySQLController implements Initializable {

    @FXML
    private TextField Host, Port, User,Database, URL;

    @FXML
    private PasswordField Password;

    @FXML
    private Button OK;

    @FXML
    private DialogPane PanelConnMySQL;

    private PrincipalController principalController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Host.setText("localhost");
        Port.setText("3306");
        URL.setText("jdbc:mysql://localhost:3306");
        User.requestFocus();
        OK.setOnAction(event -> {
            toConnection.setUser(User.getText());
            toConnection.setPassword(Password.getText());
            toConnection.setHost(Host.getText());
            toConnection.setPort(Port.getText());
            principalController.getDatabaseMySQL("MySQL",principalController.mysql);
            OK.getScene().getWindow().hide();
        });
    }

    public void getControllerPrincipal(PrincipalController controller){
        principalController = controller;
    }
}
