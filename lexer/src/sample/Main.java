package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        //launch(args);
        String path = "lexer/src/scanner/scanner.flex";
       getLexer(path);


        /*try {
            Reader reader = new BufferedReader(new FileReader("lexicalParser/src/sample/fichero.txt"));
            //_lexicalParser lexicalParser = new _lexicalParser(reader);
            Scanner lexicalParser = new Scanner(reader);
            System.out.println("\n\n");
            while(true){
                TokenData token = lexicalParser.yylex();
                if(token != null){
                    System.out.println("token: " + token.getToken() + ", lexema: " + token.getLexeme() + "\t\t\t\t\t\t\tlinea: "+token.getLine() + " letra: "+token.getFirstChar() +" - "+token.getLastChar());
                }
                else {
                    System.out.println("\n\nend");
                    System.exit(0);
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private static void getLexer(String path){
        File file=new File(path);
        jflex.Main.generate(file);
    }
}
