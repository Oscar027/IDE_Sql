package scanner;

public class TokenData {
    private int type;
    private int token;
    private int line;
    private int firstCol;
    private int lastCol;
    private char character;
    private String lexeme;

    public TokenData(int type, int token, int line, int firstCol, int lastCol, char character, String lexeme) {
        this.type = type;
        this.token = token;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
        this.line = line;
        this.character = character;
        this.lexeme = lexeme;
    }

    public TokenData() {
        this.type = 0;
        this.token = 0;
        this.firstCol = 0;
        this.lastCol = 0;
        this.line = 0;
        this.character = ' ';
        this.lexeme = "";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getFirstCol() {
        return firstCol;
    }

    public void setFirstCol(int firstCol) {
        this.firstCol = firstCol;
    }

    public int getLastCol() {
        return lastCol;
    }

    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}
