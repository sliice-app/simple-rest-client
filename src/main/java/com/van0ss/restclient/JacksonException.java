package com.van0ss.restclient;

public class JacksonException extends RuntimeException {
    public JacksonException(String msg) {
        super(msg);
    }

    public JacksonException(Throwable t) {
        super(t);
    }

    public JacksonException(String msg, Throwable t) {
        super(msg, t);
    }
}
