package com.van0ss.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;

public class RestClient implements IRestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);

    private final Map<String, String> globalHeaders = new HashMap<>();
    private final ObjectMapper mapper;
    private final Supplier<Executor> executorSupplier;

    public RestClient() {
        this(emptyMap());
    }

    public RestClient(Map<String, String> globalHeaders) {
        this(new ObjectMapper()
                        .registerModule(new ParameterNamesModule())
                        .registerModule(new Jdk8Module())
                        .registerModule(new JavaTimeModule()),
                globalHeaders);
    }

    public RestClient(ObjectMapper objectMapper, Map<String, String> globalHeaders) {
        this(objectMapper, globalHeaders, Executor::newInstance);
    }

    /**
     * Directly using only for Tests
     */
    RestClient(ObjectMapper objectMapper, Map<String, String> globalHeaders, Supplier<Executor> executorSupplier) {
        this.mapper = objectMapper;
        this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.globalHeaders.put("Content-Type", "application/json");
        this.globalHeaders.put("Accept", "application/json");
        this.globalHeaders.putAll(globalHeaders);
        this.executorSupplier = executorSupplier;
    }

    @Override
    public <E> E post(String url, Class<E> cls) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url))), cls);
    }

    @Override
    public void post(String url) {
        log.trace("POST: " + url);
        content(addHeaders(Request.Post(url)));
    }

    @Override
    public <E> E post(String url, TypeReference<E> typeRef) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url))), typeRef);
    }

    @Override
    public <E> E post(String url, Class<E> cls, byte[] body) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url)).bodyByteArray(body)), cls);
    }

    @Override
    public <E> E post(String url, Class<E> cls, String body) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url)).bodyByteArray(body.getBytes())), cls);
    }

    @Override
    public <E> E post(String url, TypeReference<E> typeRef, byte[] body) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url)).bodyByteArray(body)), typeRef);
    }

    @Override
    public <E> E post(String url, TypeReference<E> typeRef, Object object) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url)).bodyByteArray(serialize(object))), typeRef);
    }

    @Override
    public <E> E post(String url, Class<E> cls, Object content) {
        log.trace("POST: " + url);
        return parse(content(addHeaders(Request.Post(url)).bodyByteArray(serialize(content))), cls);
    }

    @Override
    public void put(String url) {
        log.trace("PUT: " + url);
        content(addHeaders(Request.Put(url)));
    }

    @Override
    public <E> E put(String url, Class<E> cls) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url))), cls);
    }

    @Override
    public <E> E put(String url, TypeReference<E> typeRef) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url))), typeRef);
    }

    @Override
    public <E> E put(String url, Class<E> cls, byte[] body) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url)).bodyByteArray(body)), cls);
    }


    @Override
    public <E> E put(String url, Class<E> cls, String body) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url)).bodyByteArray(body.getBytes())), cls);
    }

    @Override
    public <E> E put(String url, TypeReference<E> typeRef, byte[] body) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url)).bodyByteArray(body)), typeRef);
    }

    @Override
    public <E> E put(String url, Class<E> cls, Object content) {
        log.trace("PUT: " + url);
        return parse(content(addHeaders(Request.Put(url)).bodyByteArray(serialize(content))), cls);
    }

    @Override
    public <E> E get(String url, Class<E> cls) {
        log.trace("GET: " + url);
        return parse(content(addHeaders(Request.Get(url))), cls);
    }

    @Override
    public <E> E get(String url, TypeReference<E> typeRef) {
        log.trace("GET: " + url);
        return parse(content(addHeaders(Request.Get(url))), typeRef);
    }

    @Override
    public void delete(String url) {
        log.trace("DELETE: " + url);
        content(addHeaders(Request.Delete(url)));
    }

    @Override
    public <E> E patch(String url, Class<E> cls) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url))), cls);
    }

    @Override
    public void patch(String url) {
        log.trace("PATCH: " + url);
        content(addHeaders(Request.Patch(url)));
    }

    @Override
    public <E> E patch(String url, TypeReference<E> typeRef) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url))), typeRef);
    }

    @Override
    public <E> E patch(String url, Class<E> cls, byte[] body) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url)).bodyByteArray(body)), cls);
    }

    @Override
    public <E> E patch(String url, Class<E> cls, String body) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url)).bodyByteArray(body.getBytes())), cls);
    }

    @Override
    public <E> E patch(String url, TypeReference<E> typeRef, byte[] body) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url)).bodyByteArray(body)), typeRef);
    }

    @Override
    public <E> E patch(String url, TypeReference<E> typeRef, Object object) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url)).bodyByteArray(serialize(object))), typeRef);
    }

    @Override
    public <E> E patch(String url, Class<E> cls, Object content) {
        log.trace("PATCH: " + url);
        return parse(content(addHeaders(Request.Patch(url)).bodyByteArray(serialize(content))), cls);
    }

    private InputStream content(Request req) {
        try {
            // Make sure cookie-store is fresh to make sure this client call is stateless
            CookieStore cookieStore = new BasicCookieStore();
            Executor executor = executorSupplier.get();
            HttpResponse hr = executor.use(cookieStore)
                    .execute(req)
                    .returnResponse();

            if (hr.getStatusLine() == null) {
                throw new IllegalStateException("Status Line in the response can't be null");
            }
            int statusCode = hr.getStatusLine().getStatusCode();
            log.trace("Response: Status: {}", statusCode);
            if (statusCode >= 400) {
                throw new RestResponseException(statusCode, responseToString(hr));
            }
            return hr.getEntity() != null ? hr.getEntity().getContent() : null;
        } catch (HttpResponseException e) {
            throw new RestResponseException(e, e.getStatusCode());
        } catch (IOException e) {
            throw new RestConnectException(e);
        }
    }

    private String responseToString(HttpResponse hr) {
        HttpEntity entity = hr.getEntity();
        if (entity == null) {
            return "";
        }
        try {
            return toString(hr.getEntity().getContent());
        } catch (IOException e) {
            log.warn("", e);
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private <E> E parse(InputStream is, Class<E> cls) {
        try {
            if (cls.equals(String.class)) {
                return (E) toString(is);
            } else {
                return mapper.readValue(is, cls);
            }
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    private <E> E parse(InputStream is, TypeReference<E> typeRef) {
        try {
            return mapper.readValue(is, typeRef);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    private byte[] serialize(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new JacksonException(e);
        }
    }

    private Request addHeaders(Request req) {
        if (globalHeaders != null) {
            for (String key : globalHeaders.keySet()) {
                req.addHeader(key, globalHeaders.get(key));
            }
        }
        return req;
    }

    private String toString(InputStream is) {
        if (is == null) {
            return "";
        }
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
