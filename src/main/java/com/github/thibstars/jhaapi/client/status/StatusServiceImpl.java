package com.github.thibstars.jhaapi.client.status;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.BaseService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class StatusServiceImpl extends BaseService<Status> implements StatusService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusServiceImpl.class);

    public StatusServiceImpl(Configuration configuration) {
        super(configuration, "", Status.class);
    }

    @Override
    public Optional<Status> getStatus() {
        LOGGER.info("Getting status");

        return getObject();
    }
}
