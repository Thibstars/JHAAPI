package com.github.thibstars.jhaapi.client.status;

import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface StatusService {

    Optional<Status> getStatus();
}
