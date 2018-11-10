package oracle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import principal.PrincipalController;
import resources.classes.toConnection;

import java.net.URL;
import java.util.ResourceBundle;

public class OracleController implements Initializable {

    private PrincipalController principalController;

    @FXML
    TextField Host, Port, User, URL;

    @FXML
    PasswordField Password;

    @FXML
    javafx.scene.control.Button OK;

    @FXML
    ComboBox SID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SID.getItems().addAll("xe","orcl");
        Host.setText("localhost");
        Port.setText("1521");
        URL.setText("jdbc:oracle:thin:@localhost:1521:orcl");
        OK.setOnAction(event -> {
            toConnection.setSID(SID.getSelectionModel().getSelectedItem().toString());
            toConnection.setUser(User.getText());
            toConnection.setPassword(Password.getText());
            toConnection.setHost(Host.getText());
            toConnection.setPort(Port.getText());
            principalController.getDatabaseOracle("Oracle",principalController.oracle);
            OK.getScene().getWindow().hide();
        });
    }

    public void getControllerPrincipal(PrincipalController controller){
        principalController = controller;
    }
}
