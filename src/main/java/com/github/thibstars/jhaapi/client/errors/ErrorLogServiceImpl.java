package com.github.thibstars.jhaapi.client.errors;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.BaseService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class ErrorLogServiceImpl extends BaseService<String> implements ErrorLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogServiceImpl.class);

    public ErrorLogServiceImpl(Configuration configuration) {
        super(configuration, "error_log", String.class);
    }

    @Override
    public Optional<ErrorLog> getErrorLog() {
        LOGGER.info("Getting error log");

        return getObject().map(ErrorLog::new);
    }
}
