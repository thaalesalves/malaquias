package es.thalesalv.chatrpg.core.application.port;

import java.io.UnsupportedEncodingException;

public interface TokenizerPort {

    long[] getTokensIdsFrom(String text);

    long[] getTokensIdsFrom(String[] text);

    int getTokenCountFrom(String text);

    int getTokenCountFrom(String[] text);

    String tokenize(String text) throws UnsupportedEncodingException;
}
