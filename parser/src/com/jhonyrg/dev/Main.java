package com.jhonyrg.dev;

import com.jhonyrg.dev.parser.Parser;
import java_cup.runtime.Symbol;
import scanner.Lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        try {
            /*Lexer lexer = new Lexer(new FileReader("src\\com\\jhonyrg\\dev\\test.txt"));
            Parser p = new Parser(lexer);
            p.parse();*/
            Parser p = new Parser( new File("parser\\src\\com\\jhonyrg\\dev\\test.txt") );
            p.parse();
            p.debug_parse(); // For debugging

            System.out.println("Entrada correcta");
        } catch (IOException ex) {
            System.out.println("Error: No se puede acceder al archivo" );
            System.err.println(ex.getMessage());
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
