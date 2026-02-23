package com.github.thibstars.jhaapi.client.sun;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.sun.response.Sun;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class SunServiceImpl extends BaseService<Sun> implements SunService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SunServiceImpl.class);

    private static final String SUN_ENTITY_ID = "sun.sun";

    public SunServiceImpl(Configuration configuration) {
        super(configuration, "states", Sun.class);
    }

    @Override
    public Optional<Sun> getSunInfo() {
        LOGGER.info("Getting sun info");
        return getObject(SUN_ENTITY_ID);
    }
}
