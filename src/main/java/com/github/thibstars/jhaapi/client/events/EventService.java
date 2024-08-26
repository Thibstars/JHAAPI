package com.github.thibstars.jhaapi.client.events;

import com.github.thibstars.jhaapi.client.events.response.Event;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface EventService {

    List<Event> getEvents();
}
