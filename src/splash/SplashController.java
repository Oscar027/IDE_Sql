package splash;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    private AnchorPane AnchorPane_Test;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        starSplash();
        new splashSleep().start();
    }

    private void starSplash(){
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(800));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(AnchorPane_Test);
        fade.play();
    }

    private class splashSleep extends Thread{

        @Override
        public void run(){
            try{
                Thread.sleep(5000);
                FadeTransition fade = new FadeTransition();
                fade.setDuration(Duration.millis(800));
                fade.setFromValue(1);
                fade.setToValue(0);
                fade.setNode(AnchorPane_Test);
                fade.setOnFinished(event -> {
                    Platform.runLater(() -> {
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("../principal/principal.fxml"));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("../resources/styles/Stylesheet.css").toExternalForm());
                            Stage stage = new Stage();
                            stage.setScene(scene);
                            stage.setMaximized(true);
                            stage.show();
                            AnchorPane_Test.getScene().getWindow().hide();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                });
                fade.play();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
