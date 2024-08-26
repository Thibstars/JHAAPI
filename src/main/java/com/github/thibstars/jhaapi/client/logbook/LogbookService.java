package com.github.thibstars.jhaapi.client.logbook;

import com.github.thibstars.jhaapi.client.logbook.response.Log;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface LogbookService {

    List<Log> getLogs();

    List<Log> getLogs(String entityId);

    List<Log> getLogs(String entityId, OffsetDateTime endTimeStamp);

    List<Log> getLogs(OffsetDateTime timeStamp);

    List<Log> getLogs(OffsetDateTime timeStamp, String entityId);

    List<Log> getLogs(OffsetDateTime timeStamp, OffsetDateTime endTimeStamp);

    List<Log> getLogs(OffsetDateTime timeStamp, String entityId, OffsetDateTime endTimeStamp);

}
