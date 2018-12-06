import connection.FXConnection;
import connection.FXConnectionSQLServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import splash.SplashController;

public class Main extends Application {

    private double xOffset;
    private double yOffset;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("splash/splash.fxml"));

        this.loadFonts();

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
            primaryStage.setOpacity(0.8);
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        root.setOnMouseReleased(event -> {
            primaryStage.setOpacity(1);
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    /*Funcion para cargar las fuentes   */
    private void loadFonts(){
        //Font for UI area
        Font.loadFont(
                getClass().getResourceAsStream("/resources/fonts/Gidole/Gidole-Regular.otf"),
                12
        );

        //Font for Editor text area
        Font.loadFont(
                getClass().getResourceAsStream("/resources/fonts/Hack/Hack-Regular.ttf"),
                12
        );

        //Font for Output area
        Font.loadFont(
                getClass().getResourceAsStream("/resources/fonts/Inconsolata/Inconsolata-Regular.ttf"),
                12
        );
    }


    public static void main(String[] args) {
        launch(args);
    }
}
