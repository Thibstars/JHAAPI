package com.github.thibstars.jhaapi.client.sun;

import com.github.thibstars.jhaapi.client.sun.response.Sun;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface SunService {

    /**
     * Retrieves the current sun information.
     *
     * @return an optional containing the sun information, or empty if not available
     */
    Optional<Sun> getSunInfo();

}
