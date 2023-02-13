<img align="right" src="logo/eno-logo.png" alt="Eno logo"/>

# Eno

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Eno is a tool designed to transform questionnaires from their formal description in [DDI](https://ddialliance.org/) to different formats for data collection operations.

From a DDI, Eno produces:

- A [Lunatic](https://github.com/InseeFr/Lunatic) questionnaire that can be used for web web, telephone or face to face interviewing.
- A questionnaire in paper format.
- A description of the questionnaire in an editable file.

Eno is used by [Pogues](https://github.com/InseeFr/Pogues), a questionnaire design user interface. Eno converts Pogues output into a DDI.

Eno is a part of the [Bowie](https://github.com/InseeFr/Bowie) product.

## Documentation

The documentation can be found in the [docs](./docs/en) folder and [browsed online](https://inseefr.github.io/Eno).

## Eno _Java_

V3 "Eno Java" project: change of technology from XSLT to Java.

## Setup

### Requirements

JDK 17+ is required.

### Make sustainable clones!

The V3 project is developped on the `v3-develop` orphan branch. If you are not interested by the legacy xslt version of Eno, you can clone the project like this:

`git clone --no-tags --single-branch --depth=1  --branch=v3-develop https://github.com/InseeFr/Eno.git`

### Gradle

The project is build using [Gradle](https://gradle.org/). 
A Gradle installaltion is not required to work on the project. 
A Gradle wrapper is included, it will automatically download the appropriate version of Gradle to test, build, and run the project.

- Test the project: `./gradlew test` (Linux/macOS) or `gradlew.bat test` (Windows)
- Build the project: `./gradlew build` (Linux/macOS) or `gradlew.bat build` (Windows)
- Run Eno web service: `./gradlew :eno-ws:run` (Linux/macOS) or `gradlew.bat :eno-ws:run` (Windows)

### Proxy

If you are accessing the web through a proxy, you can set your proxy in a `gradle-local.properties` file at the root of project.

Example:

```
# HTTP
systemProp.http.proxyHost=www.somehost.org
systemProp.http.proxyPort=8080
systemProp.http.proxyUser=userid
systemProp.http.proxyPassword=password
systemProp.http.nonProxyHosts=*.nonproxyrepos.com|localhost
# HTTPS
systemProp.https.proxyHost=www.somehost.org
systemProp.https.proxyPort=8080
systemProp.https.proxyUser=userid
systemProp.https.proxyPassword=password
# Non proxy hosts
systemProp.http.nonProxyHosts=*.nonproxyrepos.com|localhost
```

([Gradle docs](https://docs.gradle.org/current/userguide/build_environment.html#sec:accessing_the_web_via_a_proxy))
