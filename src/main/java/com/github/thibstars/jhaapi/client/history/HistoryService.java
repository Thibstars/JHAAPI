package com.github.thibstars.jhaapi.client.history;

import com.github.thibstars.jhaapi.client.history.response.StateChange;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Thibault Helsmoortel
 */
public interface HistoryService {

    List<List<StateChange>> getHistory(OffsetDateTime timeStamp, Set<String> entityIds);

    List<List<StateChange>> getHistory(Set<String> entityIds);
}
