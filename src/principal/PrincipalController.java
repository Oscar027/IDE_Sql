package principal;

import com.jhonyrg.dev.parser.Parser;
import connection.FXConnection;
import connection.FXConnectionMySQL;
import connection.FXConnectionOracle;
import connection.FXConnectionSQLServer;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mysql.MySQLController;
import oracle.OracleController;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import resources.classes.toConnection;
import sqlserver.SQLServerController;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrincipalController implements Initializable, Parser.ListenerParser {
    /**Declaraciones*/
    //Instancia del Parser
    private Parser parser;

    //Controles
    @FXML
    private BorderPane BorderPane_principal;

    @FXML
    private TreeView <String> treeView;

    @FXML
    private MenuItem toMySQL, toSQLServer, toOracle;

    @FXML
    private ImageView imgvRun;


    //Resaltado de Sintaxis

    //Arreglo de prueba con keywords quemadas
    private static final String[] KEYWORDS_PRUEBA = new String[] {
            "select", "from" ,"where", "delete","insert","and", "create",
            "use", "database", "table", "not","null","int","auto_increment"

    };

    private static final String MODELO_KEYWORD = "\\b(" + String.join("|",KEYWORDS_PRUEBA) + ")\\b";
    private static final String MODELO_PUNTO_COMA = "\\;";
    private static final String MODELO_COMENTARIO = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";


    private static final Pattern MODELO = Pattern.compile(
            "(?<KEYWORD>" + MODELO_KEYWORD + ")"
                    + "|(?<PUNTOCOMA>" + MODELO_PUNTO_COMA + ")"
                    + "|(?<COMENTARIO>" + MODELO_COMENTARIO + ")"
    );

    private CodeArea codeArea;
    private ExecutorService executor;

    @FXML
    private StackPane stackEditor;

    //////////////////////

    public Image mysql = new Image(getClass().getResourceAsStream("../resources/images/mysql_color1.png"));
    public Image oracle = new Image(getClass().getResourceAsStream("../resources/images/oracle_logo1.png"));
    public Image sqlserver = new Image(getClass().getResourceAsStream("../resources/images/sql_server_color1.png"));
    private Image database = new Image(getClass().getResourceAsStream("../resources/images/database_small.png"));
    private Image table = new Image(getClass().getResourceAsStream("../resources/images/table.png"));
    private Image field = new Image(getClass().getResourceAsStream("../resources/images/field.png"));
    private Image close = new Image(getClass().getResourceAsStream("../resources/images/disconnect.png"));

    private PrincipalController controller;

    private List<TreeItem<String>> databases = new ArrayList<>();
    private List<TreeItem<String>> tables;
    private List<TreeItem<String>> fields;

    private MenuItem menuItem = new MenuItem("Disconnect",new ImageView(close));


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
        starPrincipal();
        editorSQL();
        this.init();
        ConnectMySQL();
        ConnectSQLServer();
        ConnectOracle();

    }

    private void starPrincipal(){
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(100));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(BorderPane_principal);
        fade.play();
    }

    /**Funcion para inicializar los eventos de los controles y listener*/
    private void init(){
        //Setea el evento clic del mouse al "boton" Run
        this.imgvRun.setOnMouseClicked(event -> {
            if(codeArea != null){
                //System.out.println("Click");
                if(!codeArea.getText().isEmpty())
                    setSourceParser(codeArea.getText());
            }
        });

        /**Inicializando analizador*/
        this.parser = new Parser("create");
        this.parser.setListenerParser(this);
    }

    public void getDatabaseMySQL(String db, Image image){
        TreeItem manager = new TreeItem<>(db, new ImageView(image));
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
    }

    public void getDatabaseSQLServer(String db, Image image){
        TreeItem<String> manager = new TreeItem<>(db, new ImageView(image));
        FXConnection connection = new FXConnectionSQLServer();
        connection.setData(toConnection.getUser(),toConnection.getPassword(),toConnection.getAlternative());
        connection.Connect();
        try {
            PreparedStatement ps1,ps2,ps3;
            String sql = "select name from sys.databases;";
            ResultSet rs1,rs2,rs3;
            ps1 = connection.getConnection().prepareStatement(sql);
            rs1 = ps1.executeQuery();
            int i = 0;
            while (rs1.next()) {
                databases.add(new TreeItem<>(rs1.getString("name"), new ImageView(database)));
                sql = "select name from " + rs1.getString("name") + ".sys.tables;";
                ps2 = connection.getConnection().prepareStatement(sql);
                rs2 = ps2.executeQuery();
                tables = new ArrayList<>();
                int j = 0;
                while (rs2.next()) {
                    tables.add(new TreeItem<>(rs2.getString("name"), new ImageView(table)));
                    sql = "select COLUMN_NAME from " + rs1.getString("name") + ".INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '" + rs2.getString("name") + "';";
                    //System.out.println("show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";");
                    ps3 = connection.getConnection().prepareStatement(sql);
                    rs3 = ps3.executeQuery();
                    fields = new ArrayList<>();
                    while (rs3.next()) {
                        fields.add(new TreeItem<>(rs3.getString("COLUMN_NAME"), new ImageView(field)));
                    }
                    tables.get(j).getChildren().addAll(fields);
                    j++;
                }
                databases.get(i).getChildren().addAll(tables);
                i++;
            }
            manager.getChildren().addAll(databases);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        treeView.setRoot(manager);
    }

    public void getDatabaseOracle(String db, Image image){
        TreeItem<String> manager = new TreeItem<>(db, new ImageView(image));
        FXConnection connection = new FXConnectionOracle();
        connection.setData(toConnection.getUser(),toConnection.getPassword());
        connection.Connect();
        if (connection.getConnection() != null){
            System.out.println("Connected Succesfully");
        }
        else{
            System.out.println("No Connected");
        }
        treeView.setRoot(manager);
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

    private void ConnectSQLServer(){
        toSQLServer.setOnAction(event -> {
            System.out.println("SQl server presionado");
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = (DialogPane) fxmlLoader.load(getClass().getResource("../sqlserver/sqlserver.fxml").openStream());
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("../resources/styles/Stylesheet.css").toExternalForm());
                SQLServerController sqlServerController = fxmlLoader.getController();
                sqlServerController.getControllerPrincipal(controller);
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

    private void ConnectOracle(){
        toOracle.setOnAction(event -> {
            System.out.println("Oracle presionado");
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = (DialogPane) fxmlLoader.load(getClass().getResource("../oracle/oracle.fxml").openStream());
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("../resources/styles/Stylesheet.css").toExternalForm());
                OracleController oracleController = fxmlLoader.getController();
                oracleController.getControllerPrincipal(controller);
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

    ////////////////Resaltado de Sintaxis////////////////////////

    private void editorSQL(){

        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setId("codeArea");

        Subscription cleanupWhenDone = codeArea.multiPlainChanges()
                .successionEnds(java.time.Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        stackEditor.getStylesheets().add(getClass().getResource("../resources/styles/keywords.css").toExternalForm());
        stackEditor.getChildren().addAll(codeArea);

    }

    //Calculando el resaltado asincrono (NO SE SI ME EXPLICO :v )
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;

    }

    //aplicando el resaltado
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
       codeArea.setStyleSpans(0, highlighting);

    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = MODELO.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                                   matcher.group("PUNTOCOMA") != null ? "punto_coma" :
                                        matcher.group("COMENTARIO") != null ? "comentarios" :
                                                null; /* no pasa */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**Funcion para setear el texto de entrada al parser y ejecutarlo*/
    private void setSourceParser(String sqlText){
        try {
            this.parser.continueRead(sqlText);
            this.parser.parse();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    /**Configuracion del listener
     * Metodo callback que se ejecuta cada vez que el Parser devuelva un RESULT*/
    @Override
    public void onParserResult(scanner.TokenData token) {
        if(token != null){
            System.out.println("Escuchando... VALUE");
        }else{
            System.out.println("Escuchando... NULL");
        }
    }
}
