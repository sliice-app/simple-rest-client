package com.van0ss.restclient;

public class RestConnectException extends RuntimeException {
    public RestConnectException(String msg) {
        super(msg);
    }

    public RestConnectException(Throwable t) {
        super(t);
    }

    public RestConnectException(String msg, Throwable t) {
        super(msg, t);
    }
}
