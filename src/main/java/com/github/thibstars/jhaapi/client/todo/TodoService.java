package com.github.thibstars.jhaapi.client.todo;

import com.github.thibstars.jhaapi.client.todo.response.Todo;
import com.github.thibstars.jhaapi.client.todo.response.TodoItem;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Thibault Helsmoortel
 */
public interface TodoService {

    /**
     * Returns a list of todo entities.
     *
     * @return a list of todo entities
     */
    List<Todo> getTodos();

    /**
     * Returns a list of todo events for the given todo entity and optional time range.
     *
     * @param entityId the todo entity id
     * @param start    the start of the time range (optional)
     * @param end      the end of the time range (optional)
     * @return a list of todo events
     */
    List<TodoItem> getTodoItems(String entityId, OffsetDateTime start, OffsetDateTime end);

}
