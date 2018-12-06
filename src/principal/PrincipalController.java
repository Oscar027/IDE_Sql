package principal;

import com.jhonyrg.dev.parser.Parser;
import com.jhonyrg.dev.parser.ParserCallback;
import com.jhonyrg.dev.parser.sym;
import com.sun.jdi.Value;
import connection.FXConnection;
import connection.FXConnectionMySQL;
import connection.FXConnectionOracle;
import connection.FXConnectionSQLServer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import mysql.MySQLController;
import oracle.OracleController;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import resources.classes.toConnection;
import scanner.LexerCallback;
import scanner.TokenData;
import sqlserver.SQLServerController;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

public class PrincipalController implements Initializable, ParserCallback, LexerCallback {
    /**Declaraciones*/
    //Instancia del Parser
    private Parser parser;

    //Listas
    private static List<String> tkKeywords;
    private static List<String> tkIdentifiers;
    private static List<String> tkSymbols;
    private static List<String> tkStrings;
    private static List<String> tkNumbers;
    private static List<String> tkErrors;

    //Encode
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset ISO = Charset.forName("ISO-8859-1");

    //Logger
    private Logger log = Logger.getLogger(getClass().getName());

    //Controles
    private int FLAG = 27;

    @FXML
    private BorderPane BorderPane_principal;

    @FXML
    private TreeView <String> treeView;

    @FXML
    private MenuItem toMySQL, toSQLServer, toOracle;

    @FXML
    private ImageView imgvRun;

    @FXML
    private MenuItem OpenFile, SaveFile;

    @FXML
    private ImageView ExecuteAll, ExecuteBlock, CleanAndBeauty;

    @FXML
    private ScrollPane SPaux;

    @FXML
    private TableView TVresult;

    @FXML
    private MenuItem disconnectDB;

    private TextFlow OutPut = new TextFlow();



    //Resaltado de Sintaxis

    //Arreglo de prueba con keywords quemadas
    private static final String[] KEYWORDS_PRUEBA = new String[] {
            "create","database","table","delete","from","drop","insert","update","select","group","foreign","if","not","exist","where","order","by","into","time",
            "values","value","set","use","primary","key","null","int","varchar","text","enum","date","datetime","auto_increment","references","engine","InnoDB","MyIsam",
            "CREATE","DATABASE","TABLE","DELETE","FROM","DROP", "INSERT","UPDATE","SELECT","GROUP","FOREIGN","IF","NOT","EXIST","WHERE","ORDER","BY",
            "INTO","VALUES","VALUE","SET","USE","PRIMARY","KEY","NULL","INT","VARCHAR","TEXT","ENUM","DATE","DATETIME","AUTO_INCREMENT","REFERENCES","ENGINE","INNODB","MYISAM","TIME"

};
    private  static final  String[] SINTAXERROR = new String[]{
      "unerror"
    };

    private static final String MODELO_KEYWORD = "\\b(" + String.join("|",KEYWORDS_PRUEBA) + ")\\b";
    private static final String MODELO_PUNTO_COMA = "\\;";
    private static final String MODELO_SYMBOL = "\\;"+ "\\(" + "\\)";
    private static final String MODELO_COMENTARIO = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static Pattern PATTERN_EDITOR;
    private static final String MODELO_COMILLAS = "(\\')([a-zA-Z0-9]*)(\\')";
    private static final String MODELO_NUMEROS = "[0-9]+";
    private static final String MODELO_ERROR = "\\b(" + String.join("|",SINTAXERROR) + ")\\b";

    private static final Pattern MODELO = Pattern.compile(
            "(?<KEYWORD>" + MODELO_KEYWORD + ")"
                    + "|(?<PUNTOCOMA>" + MODELO_PUNTO_COMA + ")"
                    + "|(?<COMENTARIO>" + MODELO_COMENTARIO + ")"
                    + "|(?<COMILLAS>" + MODELO_COMILLAS + ")"
                    + "|(?<NUMEROS>" + MODELO_NUMEROS + ")"
                    + "|(?<ERROR>" + MODELO_ERROR + ")"

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
    //private Image close = new Image(getClass().getResourceAsStream("../resources/images/disconnect.png"));

    private PrincipalController controller;

    private List<TreeItem<String>> databases;
    private List<TreeItem<String>> tables;
    private List<TreeItem<String>> fields;

    //private MenuItem menuItem = new MenuItem("Disconnect",new ImageView(close));

    private FXConnection connectionMySQL = new FXConnectionMySQL();

    private FXConnection connectionSQLServer = new FXConnectionSQLServer();

    private FXConnection connectionOracle = new FXConnectionOracle();

    private TreeItem<String> manager;

    private ObservableList<String> row;

    private ObservableList<ObservableList> data;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controller = this;
        starPrincipal();
        editorSQL();
        this.init();
        ConnectMySQL();
        ConnectSQLServer();
        ConnectOracle();

        editorSQL();
        OpenFile();
        SaveFile();
        ExecuteAll();
        ExecuteBlockStmt();
        SPaux.setContent(OutPut);
        DisconnectInstance();
        menuContextoLog();

    }

    private void menuContextoLog(){
        ContextMenu contex = new ContextMenu();
        MenuItem item1 = new MenuItem("Limpiar");
        contex.getItems().addAll(item1);
        SPaux.setContextMenu(contex);

        item1.setOnAction(event -> {
            OutPut.getChildren().clear();
        });

    }

    private void DisconnectInstance(){
        disconnectDB.setOnAction(event -> {
            if (FLAG == 1){
                connectionMySQL.Disconnect();
                treeView.setRoot(null);
                manager = null;
                databases = null;
                tables = null;
                fields = null;
            }
            if (FLAG == 2){
                connectionSQLServer.Disconnect();
                treeView.setRoot(null);
                manager = null;
                databases = null;
                tables = null;
                fields = null;
            }
            if (FLAG == 3){
                connectionOracle.Disconnect();
                treeView.setRoot(null);
                manager = null;
                databases = null;
                tables = null;
                fields = null;
            }
        });
    }

    private void starPrincipal(){
        /*FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(100));
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setNode(BorderPane_principal);
        fade.play();*/
    }

    /**Funcion para inicializar los eventos de los controles y listener*/
    private void init(){
        //Setea el evento clic del mouse al "boton" Run
        /*this.imgvRun.setOnMouseClicked(event -> {
            if(codeArea != null){
                //System.out.println("Click");
                if(!codeArea.getText().isEmpty())
                    setSourceParser(codeArea.getText());
            }
        });*/

        //Inicializando analizador
        this.parser = new Parser(this);
        this.parser.setParserCallback(this);

        //Inicializando Listas
        tkKeywords = new ArrayList<>();
        tkIdentifiers = new ArrayList<>();
        tkSymbols = new ArrayList<>();
        tkStrings = new ArrayList<>();
        tkNumbers = new ArrayList<>();
        tkErrors = new ArrayList<>();

        //Asignando evento
        //this.codeArea.setOnKeyTyped(event -> setSourceParser(codeArea.getText()));
    }

    public void getDatabaseMySQL(String db, Image image) {
        TreeItem manager = new TreeItem<>(db, new ImageView(image));
        FXConnection connection = new FXConnectionMySQL();
        connection.setData(toConnection.getUser(), toConnection.getPassword());
        connection.Connect();
    }

    public void setConnectMySQL(){
        FLAG = 1;
        connectionMySQL.setData(toConnection.getUser(),toConnection.getPassword());
        connectionMySQL.Connect();
    }

    public void getDatabaseMySQL(){
        manager = new TreeItem<>("MySQL", new ImageView(mysql));
        databases  = new ArrayList<>();
        try {
            PreparedStatement ps1,ps2,ps3;
            String sql = "select schema_name from information_schema.schemata;";
            ResultSet rs1,rs2,rs3;
            ps1 = connectionMySQL.getConnection().prepareStatement(sql);
            rs1 = ps1.executeQuery();
            int i = 0;
            while (rs1.next()) {
                if (rs1.getString("schema_name").equals("mysql") || rs1.getString("schema_name").equals("information_schema") || rs1.getString("schema_name").equals("performance_schema")) {
                    //no se toman en cuenta
                }
                else {
                    databases.add(new TreeItem<>(rs1.getString("schema_name"), new ImageView(database)));
                    sql = "show tables from " + rs1.getString("schema_name") + ";";
                    ps2 = connectionMySQL.getConnection().prepareStatement(sql);
                    rs2 = ps2.executeQuery();
                    tables = new ArrayList<>();
                    int j = 0;
                    while (rs2.next()) {
                        tables.add(new TreeItem<>(rs2.getString("tables_in_" + rs1.getString("schema_name")), new ImageView(table)));
                        sql = "show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";";
                        //System.out.println("show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";");
                        ps3 = connectionMySQL.getConnection().prepareStatement(sql);
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
        manager.setExpanded(true);
    }

    public void setConnectSQLServer(){
        FLAG = 2;
        connectionSQLServer.setData(toConnection.getUser(),toConnection.getPassword(),toConnection.getAlternative());
        connectionSQLServer.Connect();
    }

    public void getDatabaseSQLServer(){
        manager = new TreeItem<>("SQL Server", new ImageView(sqlserver));
        databases  = new ArrayList<>();
        try {
            PreparedStatement ps1,ps2,ps3;
            String sql = "select name from sys.databases;";
            ResultSet rs1,rs2,rs3;
            ps1 = connectionSQLServer.getConnection().prepareStatement(sql);
            rs1 = ps1.executeQuery();
            int i = 0;
            while (rs1.next()) {
                databases.add(new TreeItem<>(rs1.getString("name"), new ImageView(database)));
                sql = "select name from " + rs1.getString("name") + ".sys.tables;";
                ps2 = connectionSQLServer.getConnection().prepareStatement(sql);
                rs2 = ps2.executeQuery();
                tables = new ArrayList<>();
                int j = 0;
                while (rs2.next()) {
                    tables.add(new TreeItem<>(rs2.getString("name"), new ImageView(table)));
                    sql = "select COLUMN_NAME from " + rs1.getString("name") + ".INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '" + rs2.getString("name") + "';";
                    //System.out.println("show fields from " + rs1.getString("schema_name") + "." + rs2.getString("tables_in_" + rs1.getString("schema_name")) + ";");
                    ps3 = connectionSQLServer.getConnection().prepareStatement(sql);
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
        manager.setExpanded(true);
    }

    public void setConnectOracle(){
        FLAG = 3;
        connectionOracle.setData(toConnection.getUser(),toConnection.getPassword());
        connectionOracle.Connect();
    }

    public void getDatabaseOracle(String db, Image image){
        manager = new TreeItem<>(db, new ImageView(image));
        if (connectionOracle.getConnection() != null){
            System.out.println("Connected Succesfully");
        }
        else{
            System.out.println("No Connected");
        }
        treeView.setRoot(manager);
        manager.setExpanded(true);
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
                .successionEnds(java.time.Duration.ofMillis(100))
                .successionEnds(java.time.Duration.ofMillis(10))
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

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(codeArea)));
        stackEditor.getStylesheets().add(getClass().getResource("../resources/styles/keywords.css").toExternalForm());
        stackEditor.getChildren().addAll(scene.getRoot());

    }

    //Calculando el resaltado asincrono
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        //String text = codeArea.getText();
        byte []mbByteBuffer = codeArea.getText().getBytes(StandardCharsets.UTF_8);
        String text = new String(mbByteBuffer, StandardCharsets.UTF_8);
        this.setSourceParser(codeArea.getText());
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;

    }

    //Aplicando el resaltado de keywords, entre otros
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
       codeArea.setStyleSpans(0, highlighting);

    }

    /**Highlighting*/
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = getPattern().matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                                   matcher.group("SYMBOL") != null ? "simbolo" :
                                        matcher.group("COMENTARIO") != null ? "comentarios" :
                                                matcher.group("COMILLAS") != null ? "comillas":
                                                        matcher.group("NUMEROS") != null  ? "numeros":
                                                                matcher.group("ERROR") != null  ? "error":
                                                null; /* no pasa */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    /**Función para crear el patron para el Highlighting*/
    private static Pattern getPattern(){
        PATTERN_EDITOR = Pattern.compile(
                "(?<KEYWORD>" + "\\b(" + String.join("|", tkKeywords) + ")\\b" + ")"
                        + "|(?<SYMBOL>" + String.join("|", tkSymbols) + ")"
                        + "|(?<COMENTARIO>" + MODELO_COMENTARIO + ")"
                        + "|(?<COMILLAS>" + String.join("|", tkStrings) + ")"
                        + "|(?<NUMEROS>" + String.join("|", tkNumbers) + ")"
                        + "|(?<ERROR>" + String.join("|", tkErrors) + ")"
        );
        return PATTERN_EDITOR;
    }

    /**Funcion para setear el texto de entrada al parser y ejecutarlo*/
    private void setSourceParser(String sqlText){
        try {
            tkKeywords.clear();
            tkSymbols.clear();
            tkStrings.clear();
            tkNumbers.clear();
            tkErrors.clear();
            this.parser.continueRead(sqlText);
            this.parser.parse();
            log.log(Level.INFO, "re-loaded Parser");
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**Metodo sobrescrito del listener del Parser
     * Metodo para escuchar a traves del callback cuando el Parser envie un RESULT*/
    @Override
    public void onParserResult(scanner.TokenData token) {
        log.log(Level.INFO, "Escuchando al Parser");
    }

    /**Metodo sobrescrito del listener del Lexer
     * Metodo para escuchar a traves del callback cuando el Lexer envie un Token*/
    @Override
    public void onTokenFound(TokenData token) {
        log.log(Level.INFO, "Escuchando al Lexer");
        switch (token.getType()) {
            case sym.KEYWORD:
                if (!tkKeywords.contains(token.getLexeme())) {
                    tkKeywords.add(token.getLexeme());
                    log.log(Level.INFO, "Keyword added"  + token.getLexeme());
                }
                break;

            case sym.IDENTIFIER:
                if (!tkIdentifiers.contains(token.getLexeme())) {
                    tkIdentifiers.add(token.getLexeme());
                    log.log(Level.INFO, "Identifier added"  + token.getLexeme());
                }
                break;

            case sym.SYMBOL:
                if (!tkSymbols.contains(token.getLexeme())) {
                    switch (token.getLexeme()) {
                        case "(":
                            tkSymbols.add("\\(");
                            break;
                        case ")":
                            tkSymbols.add("\\)");
                            break;
                        default:
                            tkSymbols.add(token.getLexeme());
                    }
                    log.log(Level.INFO, "Symbol added" + token.getLexeme());
                }
                break;

            case sym.STRING:
                if (!tkStrings.contains(token.getLexeme())) {
                    tkStrings.add(token.getLexeme());
                    log.log(Level.INFO, "Symbol added" + token.getLexeme());
                }
                break;

            case sym.NUMBER:
                if (!tkNumbers.contains(token.getLexeme())) {
                    tkNumbers.add(token.getLexeme());
                    log.log(Level.INFO, "Symbol added" + token.getLexeme());
                }
                break;

            case sym.error:
                if (!tkErrors.contains(token.getLexeme())) {
                    tkErrors.add(token.getLexeme());
                    log.log(Level.INFO, "Symbol added" + token.getLexeme());
                }
                break;

            default:
                log.log(Level.INFO, "Token Found" + token.getLexeme());
                break;
        }
    }

    private void OpenFile(){
        OpenFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir Archivo");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("SQL", "*.sql"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            File file = fileChooser.showOpenDialog(null);

            if (file != null){
                FileReader fr = null;
                BufferedReader br = null;
                String texto = "";
                try {
                    fr = new FileReader(file);
                    br = new BufferedReader(fr);
                    String st = br.readLine();
                    while (st != null) {
                        texto = texto + st + "\n";
                        st = br.readLine();
                    }
                } catch (Exception e) {
                    codeArea.appendText(e.toString());
                } finally {
                    try {
                        if (fr != null) {
                            fr.close();
                        }
                    } catch (Exception e2) {
                        codeArea.appendText(e2.toString());
                    }
                }
                codeArea.appendText(texto);
            }
        });
    }

    private void SaveFile(){
        SaveFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Abrir Archivo");

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("SQL", "*.sql"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            File file = fileChooser.showSaveDialog(null);

            if (file != null){
                FileWriter fw = null;
                BufferedWriter bw = null;
                try {
                    fw = new FileWriter(file, false);
                    bw = new BufferedWriter(fw);
                    String texto = codeArea.getText();
                    bw.write(texto, 0, texto.length());
                } catch (Exception e) {
                    codeArea.appendText(e.toString());
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (Exception e2) {
                        codeArea.appendText(e2.toString());
                    }
                }
            }
        });
    }

    /*private boolean getAux(String text){
        String regexp = "[\\n]+";
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }*/

    private void ExecuteAll(){
        ExecuteAll.setOnMouseClicked(event -> {
            toExecute();
        });
    }
//////////////////////////////////////////////
    private void ExecuteBlockStmt(){
        ExecuteBlock.setOnMouseClicked(event -> {
            String aux = codeArea.getText();
            int position;
            String temp = "";
            String SQL = "";
            for (int i = codeArea.getCaretPosition(); i < aux.length(); i++){
                if (aux.charAt(i) == ';'){
                    position = i;
                    System.out.println(position);
                    break;
                }
            }
        });
    }

    private void toExecute(){
        row = null;
        data = null;
        TVresult.getColumns().clear();
        TVresult.setItems(null);
        String[] Parts = codeArea.getText().replaceAll("\n","").split(";");
        var ref = new Object() {
            Text text = new Text();
        };
        if (FLAG == 1) {
            new Thread(() -> {
                try {
                    PreparedStatement preparedStatement;
                    for (int i = 0; i < Parts.length; i++) {
                        try {
                            preparedStatement = connectionMySQL.getConnection().prepareStatement(Parts[i] + ";");
                            preparedStatement.execute();
                            String[] aux = Parts[i].split(" ");
                            if (aux[0].equals("insert") || aux[0].equals("INSERT")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord inserted successfully\n");
                            }
                            if (aux[0].equals("update") || aux[0].equals("UPDATE")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord updated successfully\n");
                            }
                            if (aux[0].equals("delete") || aux[0].equals("DELETE")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord deleted successfully\n");
                            }
                            if (aux[0].equals("select") || aux[0].equals("SELECT")) {
                                data = FXCollections.observableArrayList();
                                ResultSet rs = connectionMySQL.getConnection().createStatement().executeQuery(Parts[i]);
                                for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
                                    final int j = k;
                                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(k + 1));
                                    col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                                    Platform.runLater(() -> {
                                        TVresult.getColumns().addAll(col);
                                    });
                                }
                                while (rs.next()) {
                                    row = FXCollections.observableArrayList();
                                    for (int m = 1; m <= rs.getMetaData().getColumnCount(); m++) {
                                        row.add(rs.getString(m));
                                    }
                                    data.add(row);
                                }
                                TVresult.setItems(data);
                                ref.text = new Text(Parts[i] +";" + "\t\t\tExecute successfully\n");
                            }
                            if ((aux[0] + " " + aux[1]).equals("create database") || (aux[0] + " " + aux[1]).equals("CREATE DATABASE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tDatabase " + aux[2] + " created successfully\n");
                            }
                            if ((aux[0] + " " + aux[1]).equals("create table") || (aux[0] + " " + aux[1]).equals("CREATE TABLE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tTable " + aux[2] + " created successfully\n");
                            }
                            if (aux[0].equals("use") || aux[0].equals("USE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tExecute successfully\n");
                            }
                            if (aux[0].equals("drop") && aux[1].equals("database") || aux[0].equals("DROP") && aux[1].equals("DATABASE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tDatabase " + aux[2] +" deleted successfully\n");
                            }
                            if (aux[0].equals("drop") && aux[1].equals("table") || aux[0].equals("DROP") && aux[1].equals("TABLE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tTable " + aux[2] +" deleted successfully\n");
                            }
                            ref.text.setFill(Color.rgb(146, 193, 87));
                            ref.text.setFont(new Font("Arial Narrow", 17));
                            Platform.runLater(() -> {
                                OutPut.getChildren().add(ref.text);
                                OutPut.setPadding(new Insets(15));
                            });
                            Thread.sleep(1000);
                        }
                        catch (SQLException e){
                            ref.text = new Text("ERROR: " + e.getErrorCode() + " " + e.getMessage() + "\n");
                            ref.text.setFill(Color.rgb(255, 106, 80));
                            ref.text.setFont(new Font("Arial Narrow", 17));
                            Platform.runLater(() -> {
                                OutPut.getChildren().add(ref.text);
                                OutPut.setPadding(new Insets(15));
                            });
                        }
                    }
                    Platform.runLater(() -> {
                        treeView.setRoot(null);
                        manager = null;
                        databases = null;
                        tables = null;
                        fields = null;
                        getDatabaseMySQL();
                        manager.setExpanded(true);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        if (FLAG == 2) {
            new Thread(() -> {
                try {
                    PreparedStatement preparedStatement;
                    for (int i = 0; i < Parts.length; i++) {
                        try {
                            preparedStatement = connectionSQLServer.getConnection().prepareStatement(Parts[i] + ";");
                            preparedStatement.execute();
                            String[] aux = Parts[i].split(" ");
                            if (aux[0].equals("insert") || aux[0].equals("INSERT")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord inserted successfully\n");
                            }
                            if (aux[0].equals("update") || aux[0].equals("UPDATE")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord updated successfully\n");
                            }
                            if (aux[0].equals("delete") || aux[0].equals("DELETE")){
                                ref.text = new Text(Parts[i] +";" + "\t\t\trecord deleted successfully\n");
                            }
                            if (aux[0].equals("select") || aux[0].equals("SELECT")) {
                                data = FXCollections.observableArrayList();
                                ResultSet rs = connectionSQLServer.getConnection().createStatement().executeQuery(Parts[i]);
                                for (int k = 0; k < rs.getMetaData().getColumnCount(); k++) {
                                    final int j = k;
                                    TableColumn col = new TableColumn(rs.getMetaData().getColumnName(k + 1));
                                    col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                                    Platform.runLater(() -> {
                                        TVresult.getColumns().addAll(col);
                                    });
                                }
                                while (rs.next()) {
                                    row = FXCollections.observableArrayList();
                                    for (int m = 1; m <= rs.getMetaData().getColumnCount(); m++) {
                                        row.add(rs.getString(m));
                                    }
                                    data.add(row);
                                }
                                TVresult.setItems(data);
                                ref.text = new Text(Parts[i] + "\t\t\tExecute successfully\n");
                            }
                            if ((aux[0] + " " + aux[1]).equals("create database") || (aux[0] + " " + aux[1]).equals("CREATE DATABASE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tDatabase " + aux[2] + " created successfully\n");
                            }
                            if ((aux[0] + " " + aux[1]).equals("create table") || (aux[0] + " " + aux[1]).equals("CREATE TABLE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tTable " + aux[2] + " created successfully\n");
                            }
                            if (aux[0].equals("use") || aux[0].equals("USE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tExecute successfully\n");
                            }
                            if (aux[0].equals("drop") && aux[1].equals("database") || aux[0].equals("DROP") && aux[1].equals("DATABASE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tDatabase " + aux[2] +" deleted successfully\n");
                            }
                            if (aux[0].equals("drop") && aux[1].equals("table") || aux[0].equals("DROP") && aux[1].equals("TABLE")) {
                                ref.text = new Text(Parts[i] +";" + "\t\t\tTable " + aux[2] +" deleted successfully\n");
                            }
                            ref.text.setFill(Color.rgb(146, 193, 87));
                            ref.text.setFont(new Font("Arial Narrow", 17));
                            Platform.runLater(() -> {
                                OutPut.getChildren().add(ref.text);
                                OutPut.setPadding(new Insets(15));
                            });
                            Thread.sleep(1000);
                        }
                        catch (SQLException e){
                            ref.text = new Text("ERROR: " + e.getErrorCode() + " " + e.getMessage() + "\n");
                            ref.text.setFill(Color.rgb(255, 106, 80));
                            ref.text.setFont(new Font("Arial Narrow", 17));
                            Platform.runLater(() -> {
                                OutPut.getChildren().add(ref.text);
                                OutPut.setPadding(new Insets(15));
                            });
                        }
                    }
                    Platform.runLater(() -> {
                        treeView.setRoot(null);
                        manager = null;
                        databases = null;
                        tables = null;
                        fields = null;
                        getDatabaseSQLServer();
                        manager.setExpanded(true);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
