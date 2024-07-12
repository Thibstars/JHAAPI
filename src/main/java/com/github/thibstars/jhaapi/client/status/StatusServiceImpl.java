package com.github.thibstars.jhaapi.client.status;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.exceptions.ClientException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class StatusServiceImpl implements StatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusServiceImpl.class);

    private final Configuration configuration;

    public StatusServiceImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Optional<Status> getStatus() {
        LOGGER.info("Getting status");

        Request request = new Request.Builder()
                .url(configuration.getBaseUrl())
                .build();

        ResponseBody responseBody;
        Status status;
        try (Response response = configuration.getOkHttpClient().newCall(request).execute()) {
            responseBody = Objects.requireNonNull(response.body());
            status = configuration.getObjectMapper().readValue(responseBody.string(), Status.class);
        } catch (IOException e) {
            throw new ClientException("Unable to fetch status.", e);
        }

        return Optional.ofNullable(status);
    }
}
