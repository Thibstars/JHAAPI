package com.github.thibstars.jhaapi.client.updates;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.updates.response.Update;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class UpdateServiceImpl extends BaseService<Update> implements UpdateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private static final String UPDATE_DOMAIN = "update.";

    public UpdateServiceImpl(Configuration configuration) {
        super(configuration, "states", Update.class);
    }

    @Override
    public List<Update> getAvailableUpdates() {
        LOGGER.info("Getting available updates");

        return getObjects().stream()
                .filter(update -> update.entityId().startsWith(UPDATE_DOMAIN))
                .filter(Update::isUpdateAvailable)
                .toList();
    }
}
