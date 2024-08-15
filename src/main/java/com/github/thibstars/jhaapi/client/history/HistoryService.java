package com.github.thibstars.jhaapi.client.history;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Thibault Helsmoortel
 */
public interface HistoryService {

    List<List<StateChange>> getHistory(OffsetDateTime timeStamp, Set<String> entityIds);
}
