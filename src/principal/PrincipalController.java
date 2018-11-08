package principal;

import connection.FXConnection;
import connection.FXConnectionMySQL;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mysql.MySQLController;
import resources.classes.toConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML
    private BorderPane BorderPane_principal;

    @FXML
    private TreeView <String> treeView;

    @FXML
    private MenuItem toMySQL;

    public Image mysql = new Image(getClass().getResourceAsStream("../resources/images/mysql_color1.png"));
    public Image oracle = new Image(getClass().getResourceAsStream("../resources/images/oracle_logo1.png"));
    public Image sqlserver = new Image(getClass().getResourceAsStream("../resources/images/sql_server_color1.png"));
    private Image database = new Image(getClass().getResourceAsStream("../resources/images/database_small.png"));
    private Image table = new Image(getClass().getResourceAsStream("../resources/images/table.png"));
    private Image field = new Image(getClass().getResourceAsStream("../resources/images/field.png"));

    private PrincipalController controller;

    private List<TreeItem<String>> databases = new ArrayList<>();
    private List<TreeItem<String>> tables;
    private List<TreeItem<String>> fields;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
        starPrincipal();
        ConnectMySQL();
    }

    private void starPrincipal(){
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(100));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(BorderPane_principal);
        fade.play();
    }

    public void getDatabase(String db, Image image){
        TreeItem<String> manager = new TreeItem<>(db, new ImageView(image));
        FXConnection connection = new FXConnectionMySQL();
        connection.setData(toConnection.getUser(),toConnection.getPassword());
        connection.Connect();
        try {
            PreparedStatement ps1,ps2,ps3;
            String sql = "select schema_name from information_schema.schemata;";
            ResultSet rs1,rs2,rs3;
            ps1 = connection.getConnection().prepareStatement(sql);
            rs1 = ps1.executeQuery();
            int i = 0;
            while (rs1.next()) {
                if (rs1.getString("schema_name").equals("mysql") || rs1.getString("schema_name").equals("information_schema") || rs1.getString("schema_name").equals("performance_schema")) {
                    //no se toman en cuenta
                }
                else {
                    databases.add(new TreeItem<>(rs1.getString("schema_name"), new ImageView(database)));
                    sql = "show tables from " + rs1.getString("schema_name") + ";";
                    ps2 = connection.getConnection().prepareStatement(sql);
                    rs2 = ps2.executeQuery();
                    tables = new ArrayList<>();
                    int j = 0;
                    while (rs2.next()) {
                        tables.add(new TreeItem<>(rs2.getString("tables_in_" + rs1.getString("schema_name")), new ImageView(table)));
                        sql = "show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";";
                        //System.out.println("show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";");
                        ps3 = connection.getConnection().prepareStatement(sql);
                        rs3 = ps3.executeQuery();
                        fields = new ArrayList<>();
                        while (rs3.next()) {
                            fields.add(new TreeItem<>(rs3.getString("field"), new ImageView(field)));
                        }
                        tables.get(j).getChildren().addAll(fields);
                        j++;
                    }
                    databases.get(i).getChildren().addAll(tables);
                    i++;
                }
            }
            manager.getChildren().addAll(databases);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        treeView.setRoot(manager);
        /*

        FXConnection connection = new FXConnectionMySQL();
        connection.setData(MySqlController.TxtUser,MySqlController.TxtPassword);
        connection.Connect();
        try {
            PreparedStatement ps;
            String sql = "select schema_name from information_schema.schemata;";
            ResultSet rs;
            ps = connection.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("schema_name").equals("mysql") || rs.getString("schema_name").equals("information_schema") || rs.getString("schema_name").equals("performance_schema")) {
                    //no se toman en cuenta
                }
                else {
                    System.out.println(rs.getString("schema_name"));
                    manager.getChildren().add(new TreeItem<>(rs.getString("schema_name")));
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        /*TreeItem<String> Database1 = new TreeItem<>("Database 1", new ImageView(database));
        TreeItem<String> Database2 = new TreeItem<>("Database 2",new ImageView(database));
        TreeItem<String> Database3 = new TreeItem<>("Database 3", new ImageView(database));

        TreeItem<String> Table1 = new TreeItem<>("Table 1", new ImageView(table));
        TreeItem<String> Table2 = new TreeItem<>("Table 2", new ImageView(table));
        TreeItem<String> Table3 = new TreeItem<>("Table 3", new ImageView(table));

        TreeItem<String> Field1 = new TreeItem<>("Field 1", new ImageView(field));
        TreeItem<String> Field2 = new TreeItem<>("Field 2",new ImageView(field));
        TreeItem<String> Field3 = new TreeItem<>("Field 3",new ImageView(field));

        Database1.getChildren().addAll(Table1,Table2,Table3);

        Database2.getChildren().addAll(Table1,Table2,Table3);

        Database3.getChildren().addAll(Table1,Table2,Table3);

        Table1.getChildren().addAll(Field1,Field2,Field3);

        Table2.getChildren().addAll(Field1,Field2,Field3);

        Table3.getChildren().addAll(Field1,Field2,Field3);

        Principal1.getChildren().addAll(Database1,Database2,Database3);*/

        //treeView.setRoot(manager);
    }

    private void ConnectMySQL(){
        toMySQL.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = (DialogPane) fxmlLoader.load(getClass().getResource("../mysql/mysql.fxml").openStream());
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("../resources/styles/Stylesheet.css").toExternalForm());
                MySQLController mySQLController = fxmlLoader.getController();
                mySQLController.getControllerPrincipal(controller);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
