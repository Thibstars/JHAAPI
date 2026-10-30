package com.github.thibstars.jhaapi.client.services;

/**
 * Service for sending notifications and managing persistent notifications in Home Assistant.
 *
 * @author Thibault Helsmoortel
 */
public interface NotificationService {

    /**
     * Sends a notification message using the default notify service ({@code notify.notify}).
     *
     * @param message the notification message (must not be blank)
     * @throws IllegalArgumentException if message is blank
     */
    void sendNotification(String message);

    /**
     * Sends a notification message and title using the default notify service ({@code notify.notify}).
     *
     * @param message the notification message (must not be blank)
     * @param title   the notification title
     * @throws IllegalArgumentException if message is blank
     */
    void sendNotification(String message, String title);

    /**
     * Sends a notification message and title using a specific notify service.
     *
     * @param serviceName the name or service of the notification target (e.g., "mobile_app_phone" or "notify")
     * @param message     the notification message (must not be blank)
     * @param title       the notification title
     * @throws IllegalArgumentException if message is blank
     */
    void sendNotification(String serviceName, String message, String title);

    /**
     * Sends a notification message, title, and additional data using a specific notify service.
     *
     * @param serviceName the name or service of the notification target (e.g., "mobile_app_phone" or "notify")
     * @param message     the notification message (must not be blank)
     * @param title       the notification title
     * @param data        optional additional platform-specific data object or map
     * @throws IllegalArgumentException if message is blank
     */
    void sendNotification(String serviceName, String message, String title, Object data);

    /**
     * Sends a notification message, title, target, and additional data using a specific notify service.
     *
     * @param serviceName the name or service of the notification target (e.g., "mobile_app_phone" or "notify")
     * @param message     the notification message (must not be blank)
     * @param title       the notification title
     * @param target      optional target (e.g., recipient username, channel, or device ID)
     * @param data        optional additional platform-specific data object or map
     * @throws IllegalArgumentException if message is blank
     */
    void sendNotification(String serviceName, String message, String title, String target, Object data);

    /**
     * Sends a notification message to a specific entity using {@code notify.send_message}.
     *
     * @param entityName the entity ID or entity name of the notify entity (must not be blank)
     * @param message    the notification message (must not be blank)
     * @throws IllegalArgumentException if entityName or message is blank
     */
    void sendMessage(String entityName, String message);

    /**
     * Sends a notification message and title to a specific entity using {@code notify.send_message}.
     *
     * @param entityName the entity ID or entity name of the notify entity (must not be blank)
     * @param message    the notification message (must not be blank)
     * @param title      the notification title
     * @throws IllegalArgumentException if entityName or message is blank
     */
    void sendMessage(String entityName, String message, String title);

    /**
     * Sends a notification message, title, and additional data to a specific entity using {@code notify.send_message}.
     *
     * @param entityName the entity ID or entity name of the notify entity (must not be blank)
     * @param message    the notification message (must not be blank)
     * @param title      the notification title
     * @param data       optional additional platform-specific data object or map
     * @throws IllegalArgumentException if entityName or message is blank
     */
    void sendMessage(String entityName, String message, String title, Object data);

    /**
     * Creates a persistent notification in the Home Assistant UI.
     *
     * @param message the notification message (must not be blank)
     * @throws IllegalArgumentException if message is blank
     */
    void createPersistentNotification(String message);

    /**
     * Creates a persistent notification with a title in the Home Assistant UI.
     *
     * @param message the notification message (must not be blank)
     * @param title   the notification title
     * @throws IllegalArgumentException if message is blank
     */
    void createPersistentNotification(String message, String title);

    /**
     * Creates a persistent notification with a title and custom notification ID in the Home Assistant UI.
     *
     * @param message        the notification message (must not be blank)
     * @param title          the notification title
     * @param notificationId the identifier for the persistent notification
     * @throws IllegalArgumentException if message is blank
     */
    void createPersistentNotification(String message, String title, String notificationId);

    /**
     * Dismisses a persistent notification by its notification ID.
     *
     * @param notificationId the identifier of the persistent notification to dismiss (must not be blank)
     * @throws IllegalArgumentException if notificationId is blank
     */
    void dismissPersistentNotification(String notificationId);

    /**
     * Dismisses all persistent notifications in Home Assistant.
     */
    void dismissAllPersistentNotifications();
}
