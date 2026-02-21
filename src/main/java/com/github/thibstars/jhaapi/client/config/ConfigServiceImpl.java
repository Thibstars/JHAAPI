package com.github.thibstars.jhaapi.client.config;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.config.response.CheckConfigResponse;
import com.github.thibstars.jhaapi.client.config.response.Config;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class ConfigServiceImpl extends BaseService<Config> implements ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl(Configuration configuration) {
        super(configuration, "config", Config.class);
    }

    @Override
    public Optional<Config> getConfig() {
        LOGGER.info("Getting config");

        return getObject();
    }

    @Override
    public Optional<CheckConfigResponse> checkConfig() {
        LOGGER.info("Checking config");

        return new BaseService<>(getConfiguration(), "config/core/check_config", CheckConfigResponse.class) {
        }.post("");
    }
}
