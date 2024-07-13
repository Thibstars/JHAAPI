package com.github.thibstars.jhaapi.client.config;

import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface ConfigService {

    Optional<Config> getConfig();
}
