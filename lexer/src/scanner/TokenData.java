package scanner;

public class TokenData {
    int line;
    int firstChar;
    int lastChar;
    String lexeme;

    public TokenData(int line, int firstChar, int lastChar, String lexeme) {
        this.firstChar = firstChar;
        this.lastChar = lastChar;
        this.line = line;
        this.lexeme = lexeme;
    }

    public TokenData() {
        this.firstChar = 0;
        this.lastChar = 0;
        this.line = 0;
        this.lexeme = "";
    }

    public int getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(int firstChar) {
        this.firstChar = firstChar;
    }

    public int getLastChar() {
        return lastChar;
    }

    public void setLastChar(int lastChar) {
        this.lastChar = lastChar;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}
