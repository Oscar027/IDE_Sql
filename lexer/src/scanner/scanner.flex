package scanner;
import com.jhonyrg.dev.parser.sym;
import java.util.logging.Level;
import java.util.logging.Logger;
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
White = [\ \t\r\n]+ | \r|\n|\r\n

%{/*begin user code: */
  private Logger log = Logger.getLogger(getClass().getName());
  private LexerCallback listener;

  /**Metodo para retornar el objeto Symbol con la información del token*/
  private Symbol token( int tokenType ) {
      //log.log(Level.INFO, "Obtain token " + tokenType + " \"" + yytext() + "\"" );
      TokenData tokenData = new TokenData(tokenType, yyline+1, yycolumn+1, yycolumn+yylength(),
              yycharat(yycolumn), yytext());

      //Enviando el token a travez del listener
      this.listener.onTokenFound(tokenData);

      //return new Symbol( tokenType, new TokenData(yyline+1, yycolumn+1, yycolumn+yylength(), yytext()));
      return new Symbol(tokenType, tokenData);
  }

  /**Metodo para setear el listener*/
  public void setCallback(LexerCallback listener) {
    this.listener = listener;
  }
  /* :end user code */
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

