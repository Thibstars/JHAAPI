[![Java CI with Maven](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml/badge.svg)](https://github.com/Thibstars/JHAAPI/actions/workflows/ci.yml)
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

---
Apache 2.0 License