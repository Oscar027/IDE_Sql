package com.jhonyrg.dev.parser;

import scanner.TokenData;

public interface ParserCallback {
    void onParserResult(TokenData result);
}
