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
}
