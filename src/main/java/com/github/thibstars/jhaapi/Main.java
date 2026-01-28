package com.github.thibstars.jhaapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.thibstars.jhaapi.client.services.ServiceService;
import com.github.thibstars.jhaapi.client.services.ServiceServiceImpl;
import com.github.thibstars.jhaapi.client.services.SwitchService;
import com.github.thibstars.jhaapi.client.services.SwitchServiceImpl;
import com.github.thibstars.jhaapi.client.states.StatesServiceImpl;

/**
 * @author Thibault Helsmoortel
 */
public class Main {

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        Configuration configuration = new Configuration("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiIyMDdmODBhMTUxODM0NjNjODIyYWNlOTVjYThiYzkwYyIsImlhdCI6MTczMDMwODU4OSwiZXhwIjoyMDQ1NjY4NTg5fQ.nbdr684hmAhIeShjWw7qIXQawqfKslxFqXvRhqJw9WA");

        ServiceService serviceService = new ServiceServiceImpl(configuration);

        ServiceData outsideLights = new ServiceData("switch.buitenlamp");
        //String serviceData = configuration.getObjectMapper().writeValueAsString(outsideLights);
        //serviceService.callService("switch", "turn_off", serviceData);

        SwitchService switchService = new SwitchServiceImpl(configuration, serviceService, new StatesServiceImpl(configuration));
        switchService.toggle("buitenlamp");

        Thread.sleep(2000);

        //serviceService.callService("switch", "turn_on", serviceData);
        switchService.toggle("buitenlamp");
    }

    public record ServiceData(
            String entityId
    ) { }

}
