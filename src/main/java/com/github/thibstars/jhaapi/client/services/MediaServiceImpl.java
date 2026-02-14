package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.io.File;
import java.net.URLConnection;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class MediaServiceImpl extends BaseService<Void> implements MediaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaServiceImpl.class);

    public MediaServiceImpl(Configuration configuration) {
        super(configuration, "media_source", Void.class);
    }

    @Override
    public void uploadMedia(File file) {
        uploadMedia(file, "");
    }

    @Override
    public void uploadMedia(File file, String parentId) {
        LOGGER.info("Uploading media {} to parent {}", file.getName(), parentId);

        String urlSlug = parentId;
        String formMediaContentId = parentId;

        if ("local".equals(parentId) || "local_source".equals(parentId)) {
            urlSlug = "local_source";
            formMediaContentId = "media-source://media_source/local/.";
        }

        String fileName = file.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        MediaType mediaType = mimeType != null ? MediaType.parse(mimeType) : MediaType.parse("application/octet-stream");
        LOGGER.info("Guessed media type: {} for file: {}", mediaType, file.getName());

        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("media_content_id", formMediaContentId)
                .addFormDataPart("file", fileName, RequestBody.create(file, mediaType))
                .build();

        postMultipart("/" + urlSlug + "/upload", multipartBody);
    }
}
