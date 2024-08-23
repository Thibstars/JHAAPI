package com.github.thibstars.jhaapi.internal.interceptors;

import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class RequestTokenInterceptorTest {

    @Test
    void shouldAddHeadersToRequest() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);
        Request.Builder requestBuilder = Mockito.mock(Request.Builder.class);
        Mockito.when(request.newBuilder()).thenReturn(requestBuilder);
        Mockito.when(requestBuilder.addHeader(Mockito.anyString(), Mockito.anyString())).thenReturn(requestBuilder);
        Request newRequest = Mockito.mock(Request.class);
        Mockito.when(requestBuilder.build()).thenReturn(newRequest);
        Mockito.when(chain.request()).thenReturn(request);

        RequestTokenInterceptor requestTokenInterceptor = new RequestTokenInterceptor(new Configuration("myToken"));
        requestTokenInterceptor.intercept(chain);

        Mockito.verify(requestBuilder).addHeader("Authorization", "Bearer myToken");
        Mockito.verify(requestBuilder).addHeader("Content-Type", "application/json");

        Mockito.verify(chain).proceed(newRequest);
    }
}