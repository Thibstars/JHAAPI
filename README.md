[![Java CI with Maven](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml/badge.svg)](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml) [![codecov](https://codecov.io/gh/Thibstars/JHAAPI/graph/badge.svg?token=ri2r2Xbq1m)](https://codecov.io/gh/Thibstars/JHAAPI)
# JHAAPI [![](https://jitpack.io/v/Thibstars/JHAAPI.svg)](https://jitpack.io/#Thibstars/JHAAPI)
Java Home Assistant API

Home Assistant exposes a [REST API](https://developers.home-assistant.io/docs/api/rest/), for which JHAAPI serves as a client.

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
Configuration configuration = new Configuration("myLongLivedAccessToken");

StatusService statusService = new StatusServiceImpl(configuration);

statusService.getStatus()
        .ifPresentOrElse(
                status -> System.out.println("Got status: " + status.message()),
                () -> System.out.println("Did not get a status... :(")
        );
````

If your instance is running on a different URL, you could also pass that to your configuration:  
`new Configuration("http://homeassistant:8123/api", "myLongLivedAccessToken");`

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

Upload media to your HomeAssistance instance:

````java
MediaService mediaService = new MediaServiceImpl(configuration);
mediaService.uploadMedia(new File("path/to/media.mp3"));
````

---
Apache 2.0 License