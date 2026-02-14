package com.github.thibstars.jhaapi.client.services;

import java.io.File;

/**
 * @author Thibault Helsmoortel
 */
public interface MediaService {

    /**
     * Uploads media content to the local media source.
     *
     * @param file the file to upload
     */
    void uploadMedia(File file);

    /**
     * Uploads media content to the local media source.
     *
     * @param file     the file to upload
     * @param parentId the parent directory ID (e.g., "media_source/local_source/.")
     */
    void uploadMedia(File file, String parentId);

}
