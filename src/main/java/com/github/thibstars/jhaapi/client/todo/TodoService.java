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

    /**
     * Returns a list of todo events for the given todo entity.
     * 
     * @param entityId the todo entity id
     * @return a list of todo events
     */
    List<TodoItem> getTodoItems(String entityId);

    /**
     * Returns a list of todo events for the given todo entity with the state
     * "completed".
     * 
     * @param entityId the todo entity id
     * @return a list of todo events
     */
    List<TodoItem> getCompletedTodoItems(String entityId);

    /**
     * Returns a list of todo events for the given todo entity with the state
     * "needs_action".
     * 
     * @param entityId the todo entity id
     * @return a list of todo events
     */
    List<TodoItem> getUncompletedTodoItems(String entityId);

    /**
     * Adds a new todo item to the given todo entity with the specified summary.
     * 
     * @param entityId
     * @param summary
     * @return success
     */
    boolean addTodoItem(String entityId, String summary);

    /**
     * Removes a todo item from the given todo entity with the specified summary.
     * 
     * @param entityId
     * @param summary
     * @return success
     */
    boolean removeTodoItem(String entityId, String summary);

    /**
     * Sets the completion status of a todo item for the given todo entity with the
     * specified summary.
     * 
     * @param entityId
     * @param summary
     * @param completed
     * @return success
     */
    boolean setTodoItemCompleted(String entityId, String summary, boolean completed);

}
