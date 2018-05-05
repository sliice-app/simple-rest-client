package com.van0ss.restclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestClientTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private Executor hc;
    @Mock
    private Response response;
    @Mock
    private HttpResponse httpResponse;
    @Mock
    private StatusLine statusLine;
    @Mock
    private HttpEntity httpEntity;

    private RestClient client;

    @Before
    public void setUp() throws Exception {
        when(hc.use(any(CookieStore.class))).thenReturn(hc);
        // Request state can't be checked as Request doesn't have equals() or any getters. Thanks Apache
        when(hc.execute(any(Request.class))).thenReturn(response);
        when(response.returnResponse()).thenReturn(httpResponse);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
    }

    @Test
    public void testGet() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(toStream("someresponse"));
        initClient();
        String result = client.get("http://url", String.class);
        Assert.assertEquals("someresponse", result);
        verifyHc();
    }

    @Test
    public void testDeleteWithoutTextResponse() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(null);
        initClient();
        client.delete("http://url");
        verifyHc();
    }

    @Test
    public void testDelete400() throws IOException {
        expectedException.expect(RestResponseException.class);
        expectedException.expectMessage(equalTo("Server response [400]: someresponse"));
        when(statusLine.getStatusCode()).thenReturn(400);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(toStream("someresponse"));
        initClient();
        client.delete("http://url");
        verifyHc();
    }

    @Test
    public void testDelete400WithoutText() throws IOException {
        expectedException.expect(RestResponseException.class);
        expectedException.expectMessage(equalTo("Server response [400]: "));
        when(statusLine.getStatusCode()).thenReturn(400);
        when(httpResponse.getEntity()).thenReturn(null);
        initClient();
        client.delete("http://url");
        verifyHc();
    }

    @Test
    public void testPatch() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(toStream("someresponse"));
        initClient();
        String result = client.patch("http://url", String.class);
        Assert.assertEquals("someresponse", result);
        verifyHc();
    }

    @Test
    public void testPatchBody() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(toStream("someresponse"));
        initClient();
        String result = client.patch("http://url", String.class, "body");
        Assert.assertEquals("someresponse", result);
        verifyHc();
    }

    @Test
    public void testPatchNoResponse() throws IOException {
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(toStream("someresponseignored"));
        initClient();
        client.patch("http://url");
        verifyHc();
    }

    private void verifyHc() throws IOException {
        verify(hc).use(any(CookieStore.class));
        verify(hc).execute(any(Request.class));
        verifyNoMoreInteractions(hc);
    }

    private void initClient() {
        client = new RestClient(new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule()),
                Collections.emptyMap(),
                () -> hc
        );
    }

    private InputStream toStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }
}