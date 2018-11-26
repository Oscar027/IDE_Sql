package com.jhonyrg.dev.parser;

import scanner.TokenData;

public interface ParserListener {
    void onParserResult(TokenData result);
}
