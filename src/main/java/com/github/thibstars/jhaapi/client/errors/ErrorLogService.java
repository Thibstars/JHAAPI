package com.github.thibstars.jhaapi.client.errors;

import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface ErrorLogService {

    Optional<ErrorLog> getErrorLog();
}
