package scanner;
import static scanner.Tokens.*;
import scanner.TokenData;
import com.jhonyrg.dev.parser.sym;
import java.io.*;
import java_cup.runtime.*;

%%
%public
%class Lexer
%type Symbol
%unicode
%line
%column
%char
%cup
%full
%ignorecase

Letter = [a-zA-Z]
Digit = [0-9]
Symbol = [\( | \) | \; | \= | \* | \' | \. | \,]
Enter = \n|\r|\r\n
White = [\ \t\r\n]+ | \r|\n|\r\n
CREATE = "create" | "Create" | "CREATE"
DATABASE = "database" | "Database" | "DATABASE"
%{/* user code: */
  Logger log = Logger.getLogger(getClass().getName());

  /**Declaracion de las listas*/
  private static List<String> tkKeywords;
  private static List<String> tkIdentifiers;
  private static List<String> tkSymbols;

  /**Getters para las listas*/
  public static List<String> gettkKeywords() {
    return tkKeywords;
  }

  public static List<String> gettkIdentifiers() {
    return tkIdentifiers;
  }

  public static List<String> gettkSymbols() {
    return tkSymbols;
  }

  /**Metodo para retornar el objeto Symbol con la información del token*/
  private Symbol token( int tokenType ) {
      log.log(Level.INFO, "Obtain token " + tokenType + " \"" + yytext() + "\"" );
      TokenData tokenData = new TokenData(tokenType, yyline+1, yycolumn+1, yycolumn+yylength(),
              yycharat(yycolumn), yytext());

      //agregando el token a la lista
      this.addToken(tokenData);

      //return new Symbol( tokenType, new TokenData(yyline+1, yycolumn+1, yycolumn+yylength(), yytext()));
      return new Symbol(tokenType, tokenData);
  }

  /**Metodo para agregar los tokens encontrados a la lista correspondiente según el tipo*/
  private void addToken(TokenData data){
    switch (data.getType()){
      case sym.keyword:
        tkKeywords.add(data.getLexeme());
        log.log(Level.INFO, "Keyword added");
        break;

      case sym.identifier:
        tkIdentifiers.add(data.getLexeme());
        log.log(Level.INFO, "Identifiers added");
        break;

      case sym.symbol:
        tkSymbols.add(data.getLexeme());
        log.log(Level.INFO, "Symbol added");
        break;

      default:
        log.log(Level.INFO, "Keyword added");
        break;
    }
  }

  /**Metodo para inicializar las listas*/
  public void initList() {
    tkKeywords = new ArrayList<>();
    tkIdentifiers = new ArrayList<>();
    tkSymbols = new ArrayList<>();
  }
%}

%%

<YYINITIAL>{
{White}                                                                                                                         {}
("create" | "database" | "table" | "delete" | "from" | "drop" |  "insert" | "update" | "select" | "group" | "foreign")          {return token(sym.keyword);}
("if" | "not" | "exist" | "where" | "order" | "by" | "into" | "values" | "value" | "set" | "use" | "primary" | "key" | "null")  {return token(sym.keyword);}
("int" | "varchar" | "text" | "enum" | "date" | "datetime" | "auto_increment" | "references" | "engine" | "InnoDB" | "MyIsam")  {return token(sym.keyword);}
("CREATE" | "DATABASE" | "TABLE" | "DELETE" | "FROM" | "DROP" |  "INSERT" | "UPDATE" | "SELECT" | "GROUP" | "FOREIGN")          {return token(sym.keyword);}
("IF" | "NOT" | "EXIST" | "WHERE" | "ORDER" | "BY" | "INTO" | "VALUES" | "VALUE" | "SET" | "USE" | "PRIMARY" | "KEY" | "NULL")  {return token(sym.keyword);}
("INT" | "VARCHAR" | "TEXT" | "ENUM" | "DATE" | "DATETIME" | "AUTO_INCREMENT" | "REFERENCES")                                   {return token(sym.keyword);}
("ENGINE" | "INNODB" | "MYISAM")                                                                                                {return token(sym.keyword);}
{Symbol}                                                                                                                        {return token(sym.symbol);}
({Letter}|["_"]|{Digit})+                                                                                                       {return token(sym.identifier);}
.                                                                                                                               {return token(sym.error);}
//<<EOF>>                                                                                                                         {return token(sym.EOF);}

}
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }

