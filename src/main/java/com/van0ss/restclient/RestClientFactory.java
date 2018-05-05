package com.van0ss.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class RestClientFactory {

    public IRestClient create() {
        return new RestClient();
    }

    public IRestClient create(Map<String, String> globalHeaders) {
        return new RestClient(globalHeaders);
    }

    public IRestClient create(ObjectMapper objectMapper, Map<String, String> globalHeaders) {
        return new RestClient(objectMapper, globalHeaders);
    }
}
