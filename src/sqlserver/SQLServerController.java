package sqlserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import principal.PrincipalController;
import resources.classes.toConnection;

import java.net.URL;
import java.util.ResourceBundle;

public class SQLServerController implements Initializable {

    private PrincipalController principalController;

    @FXML
    ComboBox CmbTypeConnection;

    @FXML
    Button OK;

    @FXML
    TextField User, URL, Host;

    @FXML
    PasswordField Password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Host.setText("localhost");
        CmbTypeConnection.getItems().addAll("Windows Authentication","SQL Server Authentication");
        User.setEditable(false);
        Password.setEditable(false);
        URL.setText("jdbc:sqlserver://localhost;");
        CmbTypeConnection.setOnAction(event ->{
            if (CmbTypeConnection.getSelectionModel().getSelectedItem().toString().equals("SQL Server Authentication")){
                User.setEditable(true);
                Password.setEditable(true);
                User.requestFocus();
            }
        });
        OK.setOnAction(event -> {
             String Selection = CmbTypeConnection.getSelectionModel().getSelectedItem().toString();
            toConnection.setAlternative(Selection);
            toConnection.setUser(User.getText());
            toConnection.setPassword(Password.getText());
            toConnection.setHost(Host.getText());
            principalController.getDatabaseSQLServer("SQL Server",principalController.sqlserver);
            OK.getScene().getWindow().hide();
        });
    }

    public void getControllerPrincipal(PrincipalController controller){
        principalController = controller;
    }
}
