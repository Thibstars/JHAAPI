package com.github.thibstars.jhaapi.client.templates;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.internal.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class TemplateServiceImpl extends BaseService<String> implements TemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceImpl.class);

    public TemplateServiceImpl(Configuration configuration) {
        super(configuration, "template", String.class);
    }

    @Override
    public String renderTemplate(String template) {
        LOGGER.info("Rendering template: {}", template);

        String body = String.format("{\"template\": \"%s\"}", template.replace("\"", "\\\""));

        return post("", body).orElse("");
    }
}
