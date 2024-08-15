package com.github.thibstars.jhaapi.client.history;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface HistoryService {

    List<StateChange> getHistory(OffsetDateTime timeStamp, String entityId);
}
