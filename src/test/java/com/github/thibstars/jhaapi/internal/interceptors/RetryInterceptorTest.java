package com.github.thibstars.jhaapi.internal.interceptors;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class RetryInterceptorTest {

    @Test
    void shouldProceedImmediatelyWhenSuccessful() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request)).thenReturn(response);

        RetryInterceptor interceptor = new RetryInterceptor(3, 10);
        Response result = interceptor.intercept(chain);

        Assertions.assertSame(response, result);
        Mockito.verify(chain, Mockito.times(1)).proceed(request);
    }

    @Test
    void shouldRetryAndSucceedOnConnectException() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request))
                .thenThrow(new ConnectException("Connection refused"))
                .thenReturn(response);

        RetryInterceptor interceptor = new RetryInterceptor(2, 5);
        Response result = interceptor.intercept(chain);

        Assertions.assertSame(response, result);
        Mockito.verify(chain, Mockito.times(2)).proceed(request);
    }

    @Test
    void shouldRetryAndSucceedOnSSLHandshakeException() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);
        Response response = Mockito.mock(Response.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request))
                .thenThrow(new SSLHandshakeException("Handshake failed"))
                .thenReturn(response);

        RetryInterceptor interceptor = new RetryInterceptor(2, 5);
        Response result = interceptor.intercept(chain);

        Assertions.assertSame(response, result);
        Mockito.verify(chain, Mockito.times(2)).proceed(request);
    }

    @Test
    void shouldExhaustRetriesAndThrowException() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request))
                .thenThrow(new ConnectException("Connection failed"));

        RetryInterceptor interceptor = new RetryInterceptor(2, 5);

        Assertions.assertThrows(ConnectException.class, () -> interceptor.intercept(chain));
        Mockito.verify(chain, Mockito.times(3)).proceed(request);
    }

    @Test
    void shouldNotCatchOtherIOExceptions() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request))
                .thenThrow(new SocketTimeoutException("Timeout"));

        RetryInterceptor interceptor = new RetryInterceptor(3, 5);

        Assertions.assertThrows(SocketTimeoutException.class, () -> interceptor.intercept(chain));
        Mockito.verify(chain, Mockito.times(1)).proceed(request);
    }

    @Test
    void shouldRestoreInterruptStatusWhenInterrupted() throws IOException {
        Chain chain = Mockito.mock(Chain.class);
        Request request = Mockito.mock(Request.class);

        Mockito.when(chain.request()).thenReturn(request);
        Mockito.when(chain.proceed(request))
                .thenThrow(new ConnectException("Connection failed"));

        RetryInterceptor interceptor = new RetryInterceptor(2, 1000);

        Thread.currentThread().interrupt();
        Assertions.assertThrows(ConnectException.class, () -> interceptor.intercept(chain));
        Assertions.assertTrue(Thread.interrupted()); // clears and confirms interrupt status was set
    }
}
