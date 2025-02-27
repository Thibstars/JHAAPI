package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.services.response.Service;
import com.github.thibstars.jhaapi.internal.BaseService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class ServiceServiceImpl extends BaseService<Service> implements ServiceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServiceImpl.class);

    public ServiceServiceImpl(Configuration configuration) {
        super(configuration, "services", Service.class);
    }

    @Override
    public List<Service> getServices() {
        LOGGER.info("Getting services");

        return getObjects();
    }

    @Override
    public void callService(String domain, String service, String serviceData) {
        LOGGER.info("Calling service {} from domain {} with data {}", service, domain, serviceData);

        post("/" + domain + "/" + service, serviceData);
    }

    @Override
    public void callService(String domain, String service) {
        LOGGER.info("Calling service {} from domain {}", service, domain);

        post("/" + domain + "/" + service);
    }
}
