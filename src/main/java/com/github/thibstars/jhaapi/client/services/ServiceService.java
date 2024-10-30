package com.github.thibstars.jhaapi.client.services;

import com.github.thibstars.jhaapi.client.services.response.Service;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface ServiceService {

    List<Service> getServices();

    void callService(String domain, String service, String serviceData);

    void callService(String domain, String service);
}
