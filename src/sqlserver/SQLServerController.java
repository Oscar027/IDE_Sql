package sqlserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import principal.PrincipalController;

import java.net.URL;
import java.util.ResourceBundle;

public class SQLServerController implements Initializable {

    private PrincipalController principalController;

    @FXML
    ComboBox CmbTypeConnection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CmbTypeConnection.getItems().addAll("Windows Authentication","SQL Server Authentication");
    }

    public void getControllerPrincipal(PrincipalController controller){
        principalController = controller;
    }
}
