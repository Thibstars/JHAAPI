package com.github.thibstars.jhaapi.client.updates.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thibault Helsmoortel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpdateAttributes(
        @JsonProperty("auto_update") Boolean autoUpdate,
        @JsonProperty("installed_version") String installedVersion,
        @JsonProperty("latest_version") String latestVersion,
        @JsonProperty("release_summary") String releaseSummary,
        @JsonProperty("release_url") String releaseUrl,
        @JsonProperty("skipped_version") String skippedVersion,
        @JsonProperty("title") String title,
        @JsonProperty("entity_picture") String entityPicture,
        @JsonProperty("friendly_name") String friendlyName,
        @JsonProperty("supported_features") Integer supportedFeatures
) {

}
