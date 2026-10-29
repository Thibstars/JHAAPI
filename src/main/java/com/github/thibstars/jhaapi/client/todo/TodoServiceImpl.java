package com.github.thibstars.jhaapi.client.todo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
}
