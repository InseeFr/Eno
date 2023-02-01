# Eno Web Service

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Major evolution of the web service for the [Eno v3 Java library](https://github.com/InseeFr/Eno/tree/v3-develop).

The goal of this project is to progressively migrate endpoints to services using Eno v3.

During the transition, the application has two kinds of endopoints:

- Eno "XML" endpoints that call the v1 API (that uses the Eno v2 library), in a transparent way for users.
- Eno "Java" endpoints that call services of the v2 API (using Eno v3 library), with messages to notify the user that the service behind has changed.

---

Technical changes that also come with this new major version:

- [Spring Boot 3](https://spring.io/blog/2022/10/20/spring-boot-3-0-0-rc1-available-now)
- reactive implementation using [Spring Webflux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)

_[Preparing for Spring Boot 3.0 (Spring blog)](https://spring.io/blog/2022/05/24/preparing-for-spring-boot-3-0)_
