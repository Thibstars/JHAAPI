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
        Integer elevation,
        Double latitude,
        String locationName,
        Double longitude,
        String timeZone,
        UnitSystem unitSystem,
        String version,
        List<String> whitelistExternalDirs,
        List<String> allowlistExternalDirs,
        List<String> allowlistExternalUrls,
        String configSource,
        Boolean recoveryMode,
        String state,
        String externalUrl,
        String internalUrl,
        String currency,
        String country,
        String language,
        Boolean safeMode,
        Boolean debug,
        String radius
        ) {

}
