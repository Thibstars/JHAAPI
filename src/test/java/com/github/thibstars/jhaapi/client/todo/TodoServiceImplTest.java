package com.github.thibstars.jhaapi.client.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.thibstars.jhaapi.Configuration;
import com.github.thibstars.jhaapi.client.history.response.Attributes;
import com.github.thibstars.jhaapi.client.states.StatesService;
import com.github.thibstars.jhaapi.client.states.response.State;
import com.github.thibstars.jhaapi.client.todo.response.Todo;
import com.github.thibstars.jhaapi.client.todo.response.TodoItem;
import com.github.thibstars.jhaapi.internal.exceptions.ClientException;
import java.io.IOException;
import java.net.URI;
import okio.Buffer;
import java.time.OffsetDateTime;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Thibault Helsmoortel
 */
class TodoServiceImplTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Test
    void shouldGetTodos() {
        Configuration configuration = Mockito.mock(Configuration.class);
        StatesService statesService = Mockito.mock(StatesService.class);

        List<State> states = List.of(
                new State(new Attributes("Shopping List", null), "todo.shopping_list", OffsetDateTime.now(), "0"),
                new State(new Attributes("Chores", null), "todo.chores", OffsetDateTime.now(), "2"),
                new State(new Attributes("Living Room Light", null), "light.living_room", OffsetDateTime.now(), "on")
        );
        Mockito.when(statesService.getStates()).thenReturn(states);

        TodoService todoService = new TodoServiceImpl(configuration, statesService);
        List<Todo> todos = todoService.getTodos();

        Assertions.assertNotNull(todos);
        Assertions.assertEquals(2, todos.size());
        Assertions.assertEquals("todo.shopping_list", todos.get(0).entityId());
        Assertions.assertEquals("Shopping List", todos.get(0).name());
        Assertions.assertEquals("todo.chores", todos.get(1).entityId());
        Assertions.assertEquals("Chores", todos.get(1).name());
    }

    @Test
    void shouldReturnEmptyListWhenStatesNull() {
        Configuration configuration = Mockito.mock(Configuration.class);
        StatesService statesService = Mockito.mock(StatesService.class);
        Mockito.when(statesService.getStates()).thenReturn(null);

        TodoService todoService = new TodoServiceImpl(configuration, statesService);
        List<Todo> todos = todoService.getTodos();

        Assertions.assertNotNull(todos);
        Assertions.assertTrue(todos.isEmpty());
    }

    @Test
    void shouldGetTodoItemsWithoutFiltering() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        String json = """
                {
                  "service_response": {
                    "todo.shopping_list": {
                      "items": [
                        {
                          "summary": "Buy milk",
                          "uid": "1",
                          "status": "needs_action",
                          "description": "2% milk",
                          "due": "2023-10-01T10:00:00Z"
                        }
                      ]
                    }
                  }
                }
                """;
        Mockito.when(responseBody.string()).thenReturn(json);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        TodoService todoService = new TodoServiceImpl(configuration);
        List<TodoItem> items = todoService.getTodoItems("todo.shopping_list", null, null);

        Assertions.assertNotNull(items);
        Assertions.assertEquals(1, items.size());
        TodoItem item = items.get(0);
        Assertions.assertEquals("Buy milk", item.summary());
        Assertions.assertEquals("1", item.uid());
        Assertions.assertEquals("needs_action", item.status());
        Assertions.assertEquals("2% milk", item.description());
        Assertions.assertEquals(OffsetDateTime.parse("2023-10-01T10:00:00Z"), item.due());

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient).newCall(requestCaptor.capture());
        Request executedRequest = requestCaptor.getValue();
        Assertions.assertTrue(executedRequest.url().toString().contains("services/todo/get_items?return_response"));
    }

    @Test
    void shouldGetCompletedAndUncompletedTodoItems() throws IOException {
        var configuration = Mockito.mock(Configuration.class);
        var okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        var call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        var response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        var responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("""
                {
                  "service_response": {
                    "todo.tasks": {
                      "items": [
                        {"summary": "Done", "uid": "1", "status": "completed"},
                        {"summary": "Pending", "uid": "2", "status": "needs_action"},
                        {"summary": "Unknown", "uid": "3", "status": "other"}
                      ]
                    }
                  }
                }
                """);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        var todoService = new TodoServiceImpl(configuration);

        Assertions.assertEquals(List.of("Done"), todoService.getCompletedTodoItems("todo.tasks").stream()
                .map(TodoItem::summary).toList());
        Assertions.assertEquals(List.of("Pending"), todoService.getUncompletedTodoItems("todo.tasks").stream()
                .map(TodoItem::summary).toList());
        Mockito.verify(okHttpClient, Mockito.times(2)).newCall(Mockito.any());
    }

    @Test
    void shouldFilterTodoItemsByStartAndEndDates() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        String json = """
                {
                  "service_response": {
                    "todo.tasks": {
                      "items": [
                        {
                          "summary": "Past task",
                          "uid": "1",
                          "due": "2023-09-01T10:00:00Z"
                        },
                        {
                          "summary": "Target task",
                          "uid": "2",
                          "due": "2023-10-15T10:00:00Z"
                        },
                        {
                          "summary": "Future task",
                          "uid": "3",
                          "due": "2023-11-01T10:00:00Z"
                        },
                        {
                          "summary": "No due date",
                          "uid": "4",
                          "due": null
                        }
                      ]
                    }
                  }
                }
                """;
        Mockito.when(responseBody.string()).thenReturn(json);
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        TodoService todoService = new TodoServiceImpl(configuration);
        OffsetDateTime start = OffsetDateTime.parse("2023-10-01T00:00:00Z");
        OffsetDateTime end = OffsetDateTime.parse("2023-10-31T23:59:59Z");
        List<TodoItem> items = todoService.getTodoItems("todo.tasks", start, end);

        Assertions.assertNotNull(items);
        Assertions.assertEquals(1, items.size());
        Assertions.assertEquals("Target task", items.get(0).summary());
    }

    @Test
    void shouldReturnEmptyListWhenResponseIsEmpty() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.isSuccessful()).thenReturn(false);
        Mockito.when(call.execute()).thenReturn(response);

        TodoService todoService = new TodoServiceImpl(configuration);
        List<TodoItem> items = todoService.getTodoItems("todo.empty", null, null);

        Assertions.assertNotNull(items);
        Assertions.assertTrue(items.isEmpty());
    }

    @Test
    void shouldAddRemoveAndUpdateTodoItems() throws IOException {
        var configuration = Mockito.mock(Configuration.class);
        var okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        var call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("{}");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        var todoService = new TodoServiceImpl(configuration);
        Assertions.assertTrue(todoService.addTodoItem("todo.shopping_list", "Buy \"oat\" milk"));
        Assertions.assertTrue(todoService.removeTodoItem("todo.shopping_list", "Buy \"oat\" milk"));
        Assertions.assertTrue(todoService.setTodoItemCompleted("todo.shopping_list", "Buy \"oat\" milk", true));
        Assertions.assertTrue(todoService.setTodoItemCompleted("todo.shopping_list", "Buy \"oat\" milk", false));

        var requestCaptor = org.mockito.ArgumentCaptor.forClass(Request.class);
        Mockito.verify(okHttpClient, Mockito.times(4)).newCall(requestCaptor.capture());
        var requests = requestCaptor.getAllValues();
        Assertions.assertTrue(requests.get(0).url().toString().endsWith("services/todo/add_item"));
        Assertions.assertTrue(requests.get(1).url().toString().endsWith("services/todo/remove_item"));
        Assertions.assertTrue(requests.get(2).url().toString().endsWith("services/todo/update_item"));
        Assertions.assertTrue(requests.get(3).url().toString().endsWith("services/todo/update_item"));

        var addPayload = objectMapper.readTree(requestBody(requests.get(0)));
        Assertions.assertEquals("todo.shopping_list", addPayload.path("entity_id").asText());
        Assertions.assertEquals("Buy \"oat\" milk", addPayload.path("item").asText());

        var removePayload = objectMapper.readTree(requestBody(requests.get(1)));
        Assertions.assertEquals("todo.shopping_list", removePayload.path("entity_id").asText());
        Assertions.assertEquals("Buy \"oat\" milk", removePayload.path("item").asText());

        var completePayload = objectMapper.readTree(requestBody(requests.get(2)));
        Assertions.assertEquals("todo.shopping_list", completePayload.path("entity_id").asText());
        Assertions.assertEquals("Buy \"oat\" milk", completePayload.path("item").asText());
        Assertions.assertEquals("completed", completePayload.path("status").asText());

        var uncompletePayload = objectMapper.readTree(requestBody(requests.get(3)));
        Assertions.assertEquals("needs_action", uncompletePayload.path("status").asText());
    }

    @Test
    void shouldReturnFalseWhenUpdatingTodoItemFails() throws IOException {
        var configuration = Mockito.mock(Configuration.class);
        var okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        var call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        var response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(400);
        Mockito.when(response.isSuccessful()).thenReturn(false);
        var responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("400: Bad Request");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        var todoService = new TodoServiceImpl(configuration);

        Assertions.assertFalse(todoService.setTodoItemCompleted("todo.tasks", "Missing item", true));
    }

    private String requestBody(Request request) throws IOException {
        var buffer = new Buffer();
        request.body().writeTo(buffer);
        return buffer.readUtf8();
    }

    @Test
    void shouldThrowClientExceptionOnInvalidJson() throws IOException {
        Configuration configuration = Mockito.mock(Configuration.class);
        OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
        Mockito.when(configuration.getOkHttpClient()).thenReturn(okHttpClient);
        Mockito.when(configuration.getBaseUrl()).thenReturn(URI.create("http://localhost:8123/api").toURL());
        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);

        Call call = Mockito.mock(Call.class);
        Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(call);
        Response response = Mockito.mock(Response.class);
        Mockito.when(response.code()).thenReturn(200);
        Mockito.when(response.isSuccessful()).thenReturn(true);
        ResponseBody responseBody = Mockito.mock(ResponseBody.class);
        Mockito.when(responseBody.string()).thenReturn("invalid json");
        Mockito.when(response.body()).thenReturn(responseBody);
        Mockito.when(call.execute()).thenReturn(response);

        TodoService todoService = new TodoServiceImpl(configuration);
        Assertions.assertThrows(ClientException.class, () -> todoService.getTodoItems("todo.test", null, null));
    }
}
