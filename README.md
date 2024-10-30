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
>You obtain a token ("Long-Lived Access Token") by logging into the frontend using a web browser, and going to your profile.

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

A simple example that turns your light (with entity id `light.myAwesomeLight`) on:
````java
public record ServiceData(String entityId) { }

...

private void turnLightOn(ServiceService serviceService) {
    ServiceData myAwesomeLight = new ServiceData("light.myAwesomeLight");
    String serviceData = configuration.getObjectMapper().writeValueAsString(myAwesomeLight);
    serviceService.callService("light", "turn_on", serviceData);
}
````

---
Apache 2.0 License