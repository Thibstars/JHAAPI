package com.github.thibstars.jhaapi.client.config;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.BaseService;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public class ConfigServiceImpl extends BaseService<Config> implements ConfigService {

    public ConfigServiceImpl(Configuration configuration) {
        super(configuration, "config", Config.class);
    }

    @Override
    public Optional<Config> getConfig() {
        return getObject();
    }
}
