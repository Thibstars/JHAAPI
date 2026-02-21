package com.github.thibstars.jhaapi.client.templates;

/**
 * @author Thibault Helsmoortel
 */
public interface TemplateService {

    /**
     * Renders a Home Assistant template.
     *
     * @param template the template to render
     * @return the rendered template
     */
    String renderTemplate(String template);

}
