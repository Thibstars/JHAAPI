package com.github.thibstars.jhaapi.client.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Config(
        List<String> components,
        String configDir,
        int elevation,
        double latitude,
        String locationName,
        double longitude,
        String timeZone,
        UnitSystem unitSystem,
        String version,
        List<String> whitelistExternalDirs,
        List<String> allowlistExternalDirs,
        List<String> allowlistExternalUrls,
        String configSource,
        boolean recoveryMode,
        String state,
        String externalUrl,
        String internalUrl,
        String currency,
        String country,
        String language,
        boolean safeMode,
        boolean debug,
        String radius
        ) {

}
