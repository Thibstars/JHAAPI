package com.github.thibstars.jhaapi.client.updates;

import com.github.thibstars.jhaapi.client.updates.response.Update;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface UpdateService {

    /**
     * Retrieves the available updates.
     *
     * @return a list of available updates
     */
    List<Update> getAvailableUpdates();

}
