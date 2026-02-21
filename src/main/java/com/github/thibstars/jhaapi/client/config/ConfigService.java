package com.github.thibstars.jhaapi.client.config;

import com.github.thibstars.jhaapi.client.config.response.CheckConfigResponse;
import com.github.thibstars.jhaapi.client.config.response.Config;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface ConfigService {

    Optional<Config> getConfig();

    /**
     * Checks the Home Assistant configuration.
     *
     * @return the result of the configuration check
     */
    Optional<CheckConfigResponse> checkConfig();
}
