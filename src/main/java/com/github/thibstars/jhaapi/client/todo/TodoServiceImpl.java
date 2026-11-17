package com.github.thibstars.jhaapi.client.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.StatesServiceImpl;
import com.github.thibstars.jhaapi.client.todo.response.Todo;
import com.github.thibstars.jhaapi.client.todo.response.TodoItem;
import com.github.thibstars.jhaapi.internal.BaseService;
import com.github.thibstars.jhaapi.internal.exceptions.ClientException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thibault Helsmoortel
 */
public class TodoServiceImpl extends BaseService<Todo> implements TodoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final StatesService statesService;

    public TodoServiceImpl(Configuration configuration) {
        this(configuration, new StatesServiceImpl(configuration));
    }

    public TodoServiceImpl(Configuration configuration, StatesService statesService) {
        super(configuration, "services", Todo.class);
        this.statesService = statesService;
    }

    @Override
    public List<Todo> getTodos() {
        LOGGER.info("Getting todos");

        var states = statesService.getStates();
        if (states == null) {
            return List.of();
        }

        return states.stream()
                .filter(state -> state.entityId() != null && state.entityId().startsWith("todo"))
                .map(state -> new Todo(state.entityId(), state.attributes() != null ? state.attributes().friendlyName() : null))
                .toList();
    }

    @Override
    public List<TodoItem> getTodoItems(String entityId, OffsetDateTime start, OffsetDateTime end) {
        LOGGER.info("Getting todo items for {}", entityId);

        String data = "{\"entity_id\": \"%s\"}".formatted(entityId);
        Optional<String> response = post("todo/get_items?return_response", data);

        if (response.isEmpty()) {
            return new ArrayList<>();
        }

        String jsonResponse = response.get();
        List<TodoItem> todos = new ArrayList<>();
        ObjectMapper mapper = getConfiguration().getObjectMapper();
        try {
            JsonNode items = mapper.readTree(jsonResponse)
                    .path("service_response")
                    .path(entityId)
                    .path("items");
            if (items.isArray()) {
                todos = mapper.readerForListOf(TodoItem.class).readValue(items);
            }
        } catch (Exception e) {
            throw new ClientException("Unable to deserialize todo items.", e);
        }

        if (start != null) {
            todos = todos.stream()
                    .filter(todoItem -> todoItem.due() != null && todoItem.due().isAfter(start))
                    .toList();
        }
        if (end != null) {
            todos = todos.stream()
                    .filter(todoItem -> todoItem.due() != null && todoItem.due().isBefore(end))
                    .toList();
        }

        return todos;
    }

    @Override
    public List<TodoItem> getTodoItems(String entityId) {
        return getTodoItems(entityId, null, null);
    }

    @Override
    public List<TodoItem> getCompletedTodoItems(String entityId) {
        return getTodoItems(entityId).stream().filter(todoItem -> "completed".equals(todoItem.status())).toList();
    }

    @Override
    public List<TodoItem> getUncompletedTodoItems(String entityId) {
        return getTodoItems(entityId).stream().filter(todoItem -> "needs_action".equals(todoItem.status())).toList();
    }

    @Override
    public boolean addTodoItem(String entityId, String summary) {
        LOGGER.info("Adding todo item {} to {}", summary, entityId);

        var data = getConfiguration().getObjectMapper().createObjectNode().put("entity_id", entityId).put("item",
                summary);
        return post("todo/add_item", serializeServiceData(data)).isPresent();
    }

    @Override
    public boolean removeTodoItem(String entityId, String summary) {
        LOGGER.info("Removing todo item {} from {}", summary, entityId);

        var data = getConfiguration().getObjectMapper().createObjectNode().put("entity_id", entityId).put("item",
                summary);
        return post("todo/remove_item", serializeServiceData(data)).isPresent();
    }

    @Override
    public boolean setTodoItemCompleted(String entityId, String summary, boolean completed) {
        LOGGER.info("Setting todo item {} completion to {} for {}", summary, completed, entityId);

        var data = getConfiguration().getObjectMapper().createObjectNode().put("entity_id", entityId)
                .put("item", summary).put("status", completed ? "completed" : "needs_action");
        return post("todo/update_item", serializeServiceData(data)).isPresent();
    }

    private String serializeServiceData(ObjectNode data) {
        try {
            return getConfiguration().getObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new ClientException("Unable to serialize todo service data.", e);
        }
    }
}
