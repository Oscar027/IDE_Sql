package scanner;

public class TokenData {
    private int type;
    private int line;
    private int firstCol;
    private int lastCol;
    private char fchar;
    private String lexeme;

    public TokenData(int type, int line, int firstCol, int lastCol, char fchar, String lexeme) {
        this.type = type;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
        this.line = line;
        this.fchar = fchar;
        this.lexeme = lexeme;
    }

    public TokenData() {
        this.type = 0;
        this.firstCol = 0;
        this.lastCol = 0;
        this.line = 0;
        this.fchar = ' ';
        this.lexeme = "";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public char getFchar() {
        return fchar;
    }

    public void setFchar(char fchar) {
        this.fchar = fchar;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}
