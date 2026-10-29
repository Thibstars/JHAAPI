package com.github.thibstars.jhaapi.internal.interceptors;

import java.io.IOException;
import java.net.ConnectException;
import javax.net.ssl.SSLHandshakeException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class RetryInterceptor implements Interceptor {

    private final int maximumRetries;
    private final long backoffMilliseconds;

    public RetryInterceptor(int maximumRetries, long backoffMilliseconds) {
        if (maximumRetries < 0) {
            throw new IllegalArgumentException("maximumRetries must be non-negative");
        }
        if (backoffMilliseconds < 0) {
            throw new IllegalArgumentException("backoffMilliseconds must be non-negative");
        }

        this.maximumRetries = maximumRetries;
        this.backoffMilliseconds = backoffMilliseconds;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        IOException lastException = null;
        for (int attempt = 0; attempt <= maximumRetries; attempt++) {
            try {
                return chain.proceed(chain.request());
            } catch (SSLHandshakeException | ConnectException e) {
                lastException = e;
                try {
                    Thread.sleep(backoffMilliseconds * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        throw new IOException("Unable to execute request.");
    }
}
