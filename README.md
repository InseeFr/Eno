<img align="right" src="logo/eno-logo.png" alt="Eno logo"/>

# Eno

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## Summary

V3 "Java version" project : change of technology from XSLT to Java.

## Setup

### Requirements

JDK 17+ is required.

### Make sustainable clones!

The V3 project is developped on the `v3-develop` orphan branch. If you are not interested by the legacy xslt version of Eno, you can clone the project like this:

`git clone --no-tags --single-branch --depth=1  --branch=v3-develop https://github.com/InseeFr/Eno.git`

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
