package scanner;

public interface LexerCallback {
    void onTokenFound(TokenData token);
}
