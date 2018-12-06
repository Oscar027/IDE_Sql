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
  private Symbol token( int tokenType, int token ) {
      //log.log(Level.INFO, "Obtain token " + tokenType + " \"" + yytext() + "\"" );
      TokenData tokenData = new TokenData(tokenType, token, yyline+1, yycolumn+1, yycolumn+yylength(),
              yycharat(yycolumn), yytext());

      //Enviando el token a travez del listener
      this.listener.onTokenFound(tokenData);

      //return new Symbol( tokenType, new TokenData(yyline+1, yycolumn+1, yycolumn+yylength(), yytext()));
      return new Symbol(token, tokenData);
  }

  /**Metodo para setear el listener*/
  public void setCallback(LexerCallback listener) {
    this.listener = listener;
  }
  /* :end user code */
%}

%%

<YYINITIAL>{
{White}                             {;}
(";")                               {return token(sym.SYMBOL, sym.SEMI);}
(",")                               {return token(sym.SYMBOL, sym.COMMA);}
("(")                               {return token(sym.SYMBOL, sym.LPAREN);}
(")")                               {return token(sym.SYMBOL, sym.RPAREN);}
(".")                               {return token(sym.SYMBOL, sym.POINT);}
("=")                               {return token(sym.SYMBOL, sym.EQUAL);}
("if not exist" | "IF NOT EXIST")   {return token(sym.KEYWORD, sym.IF_NOT_EXIST);}
("create" | "CREATE")               {return token(sym.KEYWORD, sym.CREATE);}
("database" | "DATABASE")           {return token(sym.KEYWORD, sym.DATABASE);}
("use" | "USE")                     {return token(sym.KEYWORD, sym.USE);}
("use" | "USE")                     {return token(sym.KEYWORD, sym.USE);}
("table" | "TABLE")                 {return token(sym.KEYWORD, sym.TABLE);}
("char" | "CHAR")                   {return token(sym.KEYWORD, sym.CHAR);}
("int" | "INT")                     {return token(sym.KEYWORD, sym.INTEGER);}
("varchar" | "VARCHAR")             {return token(sym.KEYWORD, sym.VARCHAR);}
("auto increment" | "AUTO INCREMENT") {return token(sym.KEYWORD, sym.AUTO_INCREMENT);}
("primary key" | "PRIMARY KEY")     {return token(sym.KEYWORD, sym.PRIMARY_KEY);}
("null" | "NULL")                   {return token(sym.KEYWORD, sym.NULL);}
("not null" | "NOT NULL")           {return token(sym.KEYWORD, sym.NOT_NULL);}
("engine" | "ENGINE")               {return token(sym.KEYWORD, sym.ENGINE);}
("true" | "TRUE")                   {return token(sym.KEYWORD, sym.TRUE);}
("false" | "FALSE")                 {return token(sym.KEYWORD, sym.FALSE);}
("insert" | "INSERT")               {return token(sym.KEYWORD, sym.INSERT);}
("values" | "VALUES")               {return token(sym.KEYWORD, sym.VALUES);}
("ins" | "INS")                     {return token(sym.KEYWORD, sym.INS);}
("as" | "AS")                       {return token(sym.KEYWORD, sym.AS);}
("into" | "INTO")                   {return token(sym.KEYWORD, sym.INTO);}
("set" | "SET")                     {return token(sym.KEYWORD, sym.SET);}
("where" | "WHERE")                 {return token(sym.KEYWORD, sym.WHERE);}
("update" | "UPDATE")               {return token(sym.KEYWORD, sym.UPDATE);}
("udp" | "UDP")                     {return token(sym.KEYWORD, sym.UDP);}
("and" | "AND")                     {return token(sym.KEYWORD, sym.AND);}
("or" | "OR")                       {return token(sym.KEYWORD, sym.OR);}
("from" | "FROM")                   {return token(sym.KEYWORD, sym.FROM);}
("delete" | "DELETE")               {return token(sym.KEYWORD, sym.DELETE);}
("decimal" | "DECIMAL")             {return token(sym.KEYWORD, sym.DECIMAL);}
("enum" | "ENUM")                   {return token(sym.KEYWORD, sym.ENUM);}
("unique" | "UNIQUE")               {return token(sym.KEYWORD, sym.UNIQUE);}
("unique key" | "unique KEY")       {return token(sym.KEYWORD, sym.UNIQUE_KEY);}
("key" | "KEY")                     {return token(sym.KEYWORD, sym.KEY);}
("references" | "REFERENCES")       {return token(sym.KEYWORD, sym.REFERENCES);}
("on update" | "ON UPDATE")         {return token(sym.KEYWORD, sym.ON_UPDATE);}
("on delete" | "ON DELETE")         {return token(sym.KEYWORD, sym.ON_DELETE);}
("no action" | "NO ACTION")         {return token(sym.KEYWORD, sym.NO_ACTION);}
("restric" | "RESTRIC")             {return token(sym.KEYWORD, sym.RESTRIC);}
("cascade" | "CASCADE")             {return token(sym.KEYWORD, sym.CASCADE);}
("set null" | "SET NULL")           {return token(sym.KEYWORD, sym.SET_NULL);}
("set default" | "SET DEFAULT")     {return token(sym.KEYWORD, sym.SET_DEFAULT);}
{Digit}+                            {return token(sym.NUMBER, sym.NUMBER_INT);}
{Digit}+"."{Digit}+                 {return token(sym.NUMBER, sym.NUMBER_DEC);}
({Letter}|["_"]|{Digit})+           {return token(sym.IDENTIFIER, sym.IDENTIFIER);}
'(\\.|''|[^'\n])*' | \"(\\.|\"\"|[^\"\n])*\" {return token(sym.STRING, sym.STRING);}
.                                   {return token(sym.error, sym.error);}
////<<EOF>>                         {return token(sym.EOF);}

}
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }

