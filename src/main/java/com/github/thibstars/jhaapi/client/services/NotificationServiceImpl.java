package com.github.thibstars.jhaapi.client.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.thibstars.jhaapi.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link NotificationService} interface.
 *
 * @author Thibault Helsmoortel
 */
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private static final String NOTIFY_DOMAIN = "notify";
    private static final String NOTIFY_SERVICE = "notify";
    private static final String SEND_MESSAGE_SERVICE = "send_message";

    private static final String PERSISTENT_NOTIFICATION_DOMAIN = "persistent_notification";
    private static final String CREATE_SERVICE = "create";
    private static final String DISMISS_SERVICE = "dismiss";
    private static final String DISMISS_ALL_SERVICE = "dismiss_all";

    private final Configuration configuration;
    private final ServiceService serviceService;

    /**
     * Creates a new NotificationServiceImpl with configuration and custom ServiceService.
     *
     * @param configuration  the configuration to use
     * @param serviceService the service service to use
     */
    public NotificationServiceImpl(Configuration configuration, ServiceService serviceService) {
        this.configuration = configuration;
        this.serviceService = serviceService;
    }

    /**
     * Creates a new NotificationServiceImpl with configuration.
     *
     * @param configuration the configuration to use
     */
    public NotificationServiceImpl(Configuration configuration) {
        this(configuration, new ServiceServiceImpl(configuration));
    }

    @Override
    public void sendNotification(String message) {
        sendNotification(null, message, null, null, null);
    }

    @Override
    public void sendNotification(String message, String title) {
        sendNotification(null, message, title, null, null);
    }

    @Override
    public void sendNotification(String serviceName, String message, String title) {
        sendNotification(serviceName, message, title, null, null);
    }

    @Override
    public void sendNotification(String serviceName, String message, String title, Object data) {
        sendNotification(serviceName, message, title, null, data);
    }

    @Override
    public void sendNotification(String serviceName, String message, String title, String target, Object data) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message must not be blank");
        }
        String service = resolveServiceName(serviceName);
        LOGGER.info("Sending notification via {}.{} with message: '{}', title: '{}'", NOTIFY_DOMAIN, service, message, title);
        callService(NOTIFY_DOMAIN, service, new NotifyData(message, title, target, data));
    }

    @Override
    public void sendMessage(String entityName, String message) {
        sendMessage(entityName, message, null, null);
    }

    @Override
    public void sendMessage(String entityName, String message, String title) {
        sendMessage(entityName, message, title, null);
    }

    @Override
    public void sendMessage(String entityName, String message, String title, Object data) {
        if (entityName == null || entityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name must not be blank");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message must not be blank");
        }
        String entityId = resolveEntityId(entityName);
        LOGGER.info("Sending message to entity {} with message: '{}', title: '{}'", entityId, message, title);
        callService(NOTIFY_DOMAIN, SEND_MESSAGE_SERVICE, new SendMessageData(entityId, message, title, data));
    }

    @Override
    public void createPersistentNotification(String message) {
        createPersistentNotification(message, null, null);
    }

    @Override
    public void createPersistentNotification(String message, String title) {
        createPersistentNotification(message, title, null);
    }

    @Override
    public void createPersistentNotification(String message, String title, String notificationId) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message must not be blank");
        }
        LOGGER.info("Creating persistent notification with message: '{}', title: '{}', id: '{}'", message, title, notificationId);
        callService(PERSISTENT_NOTIFICATION_DOMAIN, CREATE_SERVICE, new PersistentNotificationCreateData(message, title, notificationId));
    }

    @Override
    public void dismissPersistentNotification(String notificationId) {
        if (notificationId == null || notificationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification ID must not be blank");
        }
        LOGGER.info("Dismissing persistent notification with id: '{}'", notificationId);
        callService(PERSISTENT_NOTIFICATION_DOMAIN, DISMISS_SERVICE, new PersistentNotificationDismissData(notificationId));
    }

    @Override
    public void dismissAllPersistentNotifications() {
        LOGGER.info("Dismissing all persistent notifications");
        serviceService.callService(PERSISTENT_NOTIFICATION_DOMAIN, DISMISS_ALL_SERVICE);
    }

    private void callService(String domain, String serviceName, Object serviceData) {
        try {
            String json = configuration.getObjectMapper().writeValueAsString(serviceData);
            serviceService.callService(domain, serviceName, json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize service data for service {} in domain {}", serviceName, domain, e);
            throw new RuntimeException("Failed to call " + serviceName + " on " + domain, e);
        }
    }

    private String resolveServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            return NOTIFY_SERVICE;
        }
        String trimmed = serviceName.trim();
        return trimmed.startsWith(NOTIFY_DOMAIN + ".") ? trimmed.substring((NOTIFY_DOMAIN + ".").length()) : trimmed;
    }

    private String resolveEntityId(String entityName) {
        String trimmed = entityName.trim();
        return trimmed.startsWith(NOTIFY_DOMAIN + ".") ? trimmed : NOTIFY_DOMAIN + "." + trimmed;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record NotifyData(String message, String title, String target, Object data) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record SendMessageData(
            @JsonProperty("entity_id") String entityId,
            String message,
            String title,
            Object data
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record PersistentNotificationCreateData(
            String message,
            String title,
            @JsonProperty("notification_id") String notificationId
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record PersistentNotificationDismissData(
            @JsonProperty("notification_id") String notificationId
    ) {}
}
