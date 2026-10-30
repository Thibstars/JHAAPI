package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thibstars.jhaapi.Configuration;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link NotificationServiceImpl}.
 *
 * @author Thibault Helsmoortel
 */
class NotificationServiceImplTest {

    private ServiceService serviceService;
    private ObjectMapper objectMapper;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        Configuration configuration = Mockito.mock(Configuration.class);
        serviceService = Mockito.mock(ServiceService.class);
        objectMapper = Mockito.mock(ObjectMapper.class);

        Mockito.when(configuration.getObjectMapper()).thenReturn(objectMapper);
        notificationService = new NotificationServiceImpl(configuration, serviceService);
    }

    @Test
    void shouldConstructWithConfiguration() {
        Configuration configuration = Mockito.mock(Configuration.class);
        Assertions.assertDoesNotThrow(() -> new NotificationServiceImpl(configuration));
    }

    @Test
    void shouldSendNotificationWithMessageOnly() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello World\"}");

        notificationService.sendNotification("Hello World");

        Mockito.verify(serviceService).callService("notify", "notify", "{\"message\":\"Hello World\"}");
    }

    @Test
    void shouldSendNotificationWithMessageAndTitle() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello World\",\"title\":\"Alert\"}");

        notificationService.sendNotification("Hello World", "Alert");

        Mockito.verify(serviceService).callService("notify", "notify", "{\"message\":\"Hello World\",\"title\":\"Alert\"}");
    }

    @Test
    void shouldSendNotificationWithServiceMessageAndTitle() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello World\",\"title\":\"Alert\"}");

        notificationService.sendNotification("mobile_app_phone", "Hello World", "Alert");

        Mockito.verify(serviceService).callService("notify", "mobile_app_phone", "{\"message\":\"Hello World\",\"title\":\"Alert\"}");
    }

    @Test
    void shouldSendNotificationWithPrefixedServiceName() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello World\",\"title\":\"Alert\"}");

        notificationService.sendNotification("notify.mobile_app_phone", "Hello World", "Alert");

        Mockito.verify(serviceService).callService("notify", "mobile_app_phone", "{\"message\":\"Hello World\",\"title\":\"Alert\"}");
    }

    @Test
    void shouldSendNotificationWithData() throws JsonProcessingException {
        Map<String, Object> data = Map.of("image", "https://example.com/pic.jpg");
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello\",\"title\":\"Alert\",\"data\":{\"image\":\"https://example.com/pic.jpg\"}}");

        notificationService.sendNotification("mobile_app_phone", "Hello", "Alert", data);

        Mockito.verify(serviceService).callService("notify", "mobile_app_phone", "{\"message\":\"Hello\",\"title\":\"Alert\",\"data\":{\"image\":\"https://example.com/pic.jpg\"}}");
    }

    @Test
    void shouldSendNotificationWithTargetAndData() throws JsonProcessingException {
        Map<String, Object> data = Map.of("channel", "general");
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Hello\",\"title\":\"Alert\",\"target\":\"user123\",\"data\":{\"channel\":\"general\"}}");

        notificationService.sendNotification("slack", "Hello", "Alert", "user123", data);

        Mockito.verify(serviceService).callService("notify", "slack", "{\"message\":\"Hello\",\"title\":\"Alert\",\"target\":\"user123\",\"data\":{\"channel\":\"general\"}}");
    }

    @Test
    void shouldSendMessage() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\"}");

        notificationService.sendMessage("mobile_app_phone", "Test");

        Mockito.verify(serviceService).callService("notify", "send_message", "{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\"}");
    }

    @Test
    void shouldSendMessageWithTitle() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\",\"title\":\"Title\"}");

        notificationService.sendMessage("notify.mobile_app_phone", "Test", "Title");

        Mockito.verify(serviceService).callService("notify", "send_message", "{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\",\"title\":\"Title\"}");
    }

    @Test
    void shouldSendMessageWithData() throws JsonProcessingException {
        Map<String, Object> data = Map.of("priority", "high");
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\",\"title\":\"Title\",\"data\":{\"priority\":\"high\"}}");

        notificationService.sendMessage("mobile_app_phone", "Test", "Title", data);

        Mockito.verify(serviceService).callService("notify", "send_message", "{\"entity_id\":\"notify.mobile_app_phone\",\"message\":\"Test\",\"title\":\"Title\",\"data\":{\"priority\":\"high\"}}");
    }

    @Test
    void shouldCreatePersistentNotificationWithMessage() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Persistent test\"}");

        notificationService.createPersistentNotification("Persistent test");

        Mockito.verify(serviceService).callService("persistent_notification", "create", "{\"message\":\"Persistent test\"}");
    }

    @Test
    void shouldCreatePersistentNotificationWithMessageAndTitle() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Persistent test\",\"title\":\"Notice\"}");

        notificationService.createPersistentNotification("Persistent test", "Notice");

        Mockito.verify(serviceService).callService("persistent_notification", "create", "{\"message\":\"Persistent test\",\"title\":\"Notice\"}");
    }

    @Test
    void shouldCreatePersistentNotificationWithMessageTitleAndId() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"message\":\"Persistent test\",\"title\":\"Notice\",\"notification_id\":\"123\"}");

        notificationService.createPersistentNotification("Persistent test", "Notice", "123");

        Mockito.verify(serviceService).callService("persistent_notification", "create", "{\"message\":\"Persistent test\",\"title\":\"Notice\",\"notification_id\":\"123\"}");
    }

    @Test
    void shouldDismissPersistentNotification() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn("{\"notification_id\":\"123\"}");

        notificationService.dismissPersistentNotification("123");

        Mockito.verify(serviceService).callService("persistent_notification", "dismiss", "{\"notification_id\":\"123\"}");
    }

    @Test
    void shouldDismissAllPersistentNotifications() {
        notificationService.dismissAllPersistentNotifications();

        Mockito.verify(serviceService).callService("persistent_notification", "dismiss_all");
    }

    @Test
    void shouldThrowExceptionWhenMessageIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification("   "));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(null, ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification("notify", " ", "title"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendMessage("entity", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendMessage("entity", ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.createPersistentNotification(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.createPersistentNotification(""));
    }

    @Test
    void shouldThrowExceptionWhenEntityNameIsBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendMessage(null, "message"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendMessage("", "message"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.sendMessage("   ", "message"));
    }

    @Test
    void shouldThrowExceptionWhenNotificationIdIsBlankOnDismiss() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.dismissPersistentNotification(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.dismissPersistentNotification(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> notificationService.dismissPersistentNotification("   "));
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("Serialization failed") {});

        Assertions.assertThrows(RuntimeException.class, () -> notificationService.sendNotification("Hello"));
    }
}
