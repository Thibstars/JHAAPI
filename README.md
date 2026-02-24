[![Java CI with Maven](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml/badge.svg)](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml) [![codecov](https://codecov.io/gh/Thibstars/JHAAPI/graph/badge.svg?token=ri2r2Xbq1m)](https://codecov.io/gh/Thibstars/JHAAPI)
# JHAAPI [![](https://jitpack.io/v/Thibstars/JHAAPI.svg)](https://jitpack.io/#Thibstars/JHAAPI)
Java Home Assistant API

Home Assistant exposes a [REST API](https://developers.home-assistant.io/docs/api/rest/), for which JHAAPI serves as a client.

Why use JHAAPI?
- **Type-safe**: JHAAPI uses Java records to represent the JSON responses from Home Assistant, which provides type safety and makes it easier to work with the data.
- **Easy to use**: JHAAPI provides a simple and intuitive API for interacting with Home Assistant, with clear documentation and examples.
- **You don't need to be an administrator on the target Home Assistant instance**: As long as you have a valid token, you can use JHAAPI to interact with your Home Assistant instance.
- **Keep CPU usage and memory on Home Assistant instance low**: While interacting with Home Assistant through JHAAPI, you can remove the need for running and storing scripts and automations on the Home Assistant side.
- **Supports WebSockets**: JHAAPI supports the Home Assistant WebSocket API.
- ...and more!

Some use cases for JHAAPI may include:
- **Augment your Java game by interacting with devices at home**: Use lights, switches and media players to set the scene.
- **Integrate with other systems**: Control your home from other systems, such as your car or your phone.
- **Automate your home**: Turn lights on and off based on weather conditions.
- **Setup virtual sensors managed by your application**: Expose aspects of your application as entities/sensors in Home Assistant. You could use it to represent a hardware device!
- **Java equivalent of an addon**: Create a Home Assistant addon that runs in your Java application.
- ...and more!

## Installation
### Maven

Include the dependency in the `dependencies` tag in your pom file (create a property with the desired version `JHAAPI.version` in the `properties` tag).

````xml
<dependency>
    <groupId>com.github.thibstars</groupId>
    <artifactId>JHAAPI</artifactId>
    <version>${JHAAPI.version}</version>
</dependency>
````

Make sure to add the repository to the `repositories` tag.
````xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
````

## Usage

**Given**: you have a token that can be used to perform authenticated requests.
According to Home Assistant's documentation:
>You obtain a token ("Long-Lived Access Token") by logging into the frontend using a web browser and going to your profile.

Here's a minimalistic example on how to retrieve your Home Assistant status using JHAAPI:
````java
Configuration configuration = Configuration.builder()
        .token("myLongLivedAccessToken")
        .build();

StatusService statusService = new StatusServiceImpl(configuration);

statusService.getStatus()
        .ifPresentOrElse(
                status -> System.out.println("Got status: " + status.message()),
                () -> System.out.println("Did not get a status... :(")
        );
````

If your instance is running on a different URL, you could also pass that to your configuration:  
````java
Configuration configuration = Configuration.builder()
        .baseUrl("http://homeassistant:8123/api")
        .token("myLongLivedAccessToken")
        .build();
````

### Calling a service within a specific domain

#### Using ServiceService directly

A simple example that turns your light (with entity id `light.myAwesomeLight`) on:
````java
public record ServiceData(String entityId) { }

...

private void turnLightOn(ServiceService serviceService) {
    ServiceData myAwesomeLight = new ServiceData("light.myAwesomeLight");
    serviceService.callService("light", "turn_on", myAwesomeLight);
}
````

#### Using ToggleableService

The `ToggleableService` (and its implementations `LightService` and `SwitchService`) provides an easy way to toggle entities:

````java
// Create the light service
LightService lightService = new LightServiceImpl(configuration);

// Toggle a light (will turn it on if it's off, or off if it's on)
lightService.toggle("myAwesomeLight");

// Create the switch service
SwitchService switchService = new SwitchServiceImpl(configuration);

// Toggle a switch
switchService.toggle("myAwesomeSwitch");
````

You can also use the generic `ToggleableServiceImpl` for other domains that support `turn_on` and `turn_off`:
````java
ToggleableService inputBooleanService = new ToggleableServiceImpl("input_boolean", configuration);
inputBooleanService.toggle("my_boolean");
````

### Upload media to your Home Assistant instance

````java
MediaService mediaService = new MediaServiceImpl(configuration);
mediaService.uploadMedia(new File("path/to/media.mp3"));
````

### Calendars API

Fetch calendars and events (optionally with a time range):

````java
CalendarService calendarService = new CalendarServiceImpl(configuration);

// Get all calendars
List<Calendar> calendars = calendarService.getCalendars();

// Get events for a specific calendar with an optional time window
OffsetDateTime start = OffsetDateTime.now().minusDays(1);
OffsetDateTime end = OffsetDateTime.now().plusDays(1);
List<CalendarEvent> events = calendarService.getCalendarEvents("calendar.personal", start, end);
````

### Templates API

Render a Home Assistant template via the REST API:

````java
TemplateService templateService = new TemplateServiceImpl(configuration);
String rendered = templateService.renderTemplate("The state of sun is {{ states('sun.sun') }}");
System.out.println(rendered);
````

### Config API — Check configuration

````java
ConfigService configService = new ConfigServiceImpl(configuration);
configService.checkConfig().ifPresentOrElse(
        result -> {
            System.out.println("Result: " + result.result());
            System.out.println("Errors: " + result.errors());
        },
        () -> System.out.println("No result returned")
);
````

### WebSocket API — Real-time Events

Active listening to Home Assistant events via WebSocket:

````java
WebSocketService webSocketService = new WebSocketServiceImpl(configuration);

// Optional: establish early; it will also auto-connect on first subscribe
webSocketService.connect();

int subscriptionId = webSocketService.subscribeToEvents("state_changed", new WebSocketService.WebSocketEventListener() {
    @Override public void onOpen() { System.out.println("Websocket opened"); }
    @Override public void onClosed(int code, String reason) { System.out.println("Websocket closed: " + reason); }
    @Override public void onFailure(Throwable throwable) { throwable.printStackTrace(); }
    @Override public void onEvent(String eventType, com.fasterxml.jackson.databind.JsonNode payload) {
        System.out.println("Event: " + eventType + " -> " + payload);
    }
});

// Later, to stop receiving these events
// webSocketService.unsubscribe(subscriptionId);
// webSocketService.close();
````

### Entity Events API

A specialized API to listen for entities being turned on or off:

````java
WebSocketService webSocketService = new WebSocketServiceImpl(configuration);
EntityEventService entityEventService = new EntityEventServiceImpl(webSocketService);

entityEventService.onTurnedOn("light.living_room", id -> System.out.println(id + " is now ON!"));
entityEventService.onTurnedOff("light.living_room", id -> System.out.println(id + " is now OFF!"));

entityEventService.start(); // Connects and subscribes to events

// Later, to stop listening
// entityEventService.stop();
````

### Managed Entity

Optionally, you can have JHAAPI manage an entity in Home Assistant for you. This is useful for representing your application itself as an entity in Home Assistant (e.g.: to see its status, or to use its state to trigger other things in Home Assistant).

To enable this, you need to configure it in your `Configuration` object:

````java
Configuration configuration = Configuration.builder()
        .baseUrl("http://homeassistant:8123/api")
        .token("myLongLivedAccessToken")
        .entityEnabled(true)
        .entityId("switch.my_awesome_app")
        .entityFriendlyName("My Awesome App")
        .entityReadOnly(false) // optional, default is true
        .entityShutdownEnabled(true) // optional, default is false
        .build();

// Initialize the entity management service
StatesService statesService = new StatesServiceImpl(configuration);
EntityManagementService entityManagementService = new EntityManagementServiceImpl(configuration, statesService);

// This will:
// 1. Create the entity if it doesn't exist
// 2. Turn the entity on
// 3. Register a JVM shutdown hook to turn the entity off upon termination
entityManagementService.initialize();
````

By default, this feature is **disabled**.

---
Apache 2.0 License