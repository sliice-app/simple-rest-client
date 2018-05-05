package com.van0ss.restclient;

import com.fasterxml.jackson.core.type.TypeReference;

public interface IRestClient {
    <E> E post(String url, Class<E> cls);

    void post(String url);

    <E> E post(String url, TypeReference<E> typeRef);

    <E> E post(String url, Class<E> cls, byte[] body);

    <E> E post(String url, Class<E> cls, String body);

    <E> E post(String url, TypeReference<E> typeRef, byte[] body);

    <E> E post(String url, TypeReference<E> typeRef, Object object);

    <E> E post(String url, Class<E> cls, Object content);

    void put(String url);

    <E> E put(String url, Class<E> cls);

    <E> E put(String url, TypeReference<E> typeRef);

    <E> E put(String url, Class<E> cls, byte[] body);

    <E> E put(String url, Class<E> cls, String body);

    <E> E put(String url, TypeReference<E> typeRef, byte[] body);

    <E> E put(String url, Class<E> cls, Object content);

    <E> E get(String url, Class<E> cls);

    <E> E get(String url, TypeReference<E> typeRef);

    void delete(String url);

    <E> E patch(String url, Class<E> cls);

    void patch(String url);

    <E> E patch(String url, TypeReference<E> typeRef);

    <E> E patch(String url, Class<E> cls, byte[] body);

    <E> E patch(String url, Class<E> cls, String body);

    <E> E patch(String url, TypeReference<E> typeRef, byte[] body);

    <E> E patch(String url, TypeReference<E> typeRef, Object object);

    <E> E patch(String url, Class<E> cls, Object content);
}
