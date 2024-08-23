package com.github.thibstars.jhaapi.internal.interceptors;

import com.github.thibstars.jhaapi.Configuration;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Thibault Helsmoortel
 */
public class RequestTokenInterceptor implements Interceptor {

    private final Configuration configuration;

    public RequestTokenInterceptor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;

        newRequest = request.newBuilder()
                .addHeader("Authorization", "Bearer " + configuration.getLongLivedAccessToken())
                .addHeader("Content-Type", "application/json")
                .build();

        return chain.proceed(newRequest);
    }
}
