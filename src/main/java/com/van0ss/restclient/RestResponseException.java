package com.van0ss.restclient;

public class RestResponseException extends RuntimeException {
    private final int statusCode;
    private final String response;

    public RestResponseException(Throwable t, int statusCode) {
        super("Server response: [" + statusCode + "]", t);
        this.statusCode = statusCode;
        this.response = null;
    }

    public RestResponseException(int statusCode, String response) {
        super("Server response [" + statusCode + "]: " + response);
        this.statusCode = statusCode;
        this.response = response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}
