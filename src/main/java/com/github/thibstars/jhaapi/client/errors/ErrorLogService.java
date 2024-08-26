package com.github.thibstars.jhaapi.client.errors;

import com.github.thibstars.jhaapi.client.errors.response.ErrorLog;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface ErrorLogService {

    Optional<ErrorLog> getErrorLog();
}
