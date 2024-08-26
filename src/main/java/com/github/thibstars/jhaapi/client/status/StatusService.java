package com.github.thibstars.jhaapi.client.status;

import com.github.thibstars.jhaapi.client.status.response.Status;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface StatusService {

    Optional<Status> getStatus();
}
