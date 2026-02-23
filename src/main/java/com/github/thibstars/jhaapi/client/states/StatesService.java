package com.github.thibstars.jhaapi.client.states;

import com.github.thibstars.jhaapi.client.states.response.State;
import java.util.List;
import java.util.Optional;

/**
 * @author Thibault Helsmoortel
 */
public interface StatesService {

    List<State> getStates();

    List<State> getStates(String entityId);

    Optional<State> getState(String entityId);
    
    Optional<State> updateState(String entityId, String state, String friendlyName);

    /**
     * Updates the state of an entity.
     *
     * @param entityId the ID of the entity to update
     * @param state the new state
     * @param friendlyName the friendly name of the entity
     * @param readOnly whether the entity should be read-only in Home Assistant
     * @return an optional containing the updated state
     */
    Optional<State> updateState(String entityId, String state, String friendlyName, boolean readOnly);
}
