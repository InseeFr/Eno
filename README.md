# Eno XML Web-Service

_This project contains the legacy REST API for Eno ("XML" version)._

It is based on two modules:

- [Eno-Core](./eno-core/): logic of transformations based on XSLT
- [Eno-WS](/eno-ws/): the rest api that used eno-core

---

## Summary

[Eno](https://github.com/InseeFr/Eno) is a tool that generates survey questionnaires starting from their formal description in [DDI](https://ddialliance.org/Specification/DDI-Lifecycle/3.3/).

Eno can create questionnaires in different formats from the same DDI description.

Eno generates:

- [Lunatic](https://github.com/InseeFr/Lunatic) questionnaires.
- XForms web questionnaires that can be executed on [Orbeon Forms Runner](http://www.orbeon.com/).
- XSL-FO questionnaires that can be converted to PDF files.

## Docker image

Docker images of the application are published in [Docker Hub](https://hub.docker.com/r/inseefr/eno-ws/tags).

You can pull it, run it on port `8080`, and you're all set!

## Usage example

You can find DDI example files in the test resources of the project.

## Developer requirements

- JDK 17+
- Maven 3
- Tomcat 9

## Swagger UI

The swagger-ui API documentation is mapped on the root url.

Locally, you can get it at `http://localhost:8080`

Note if you have trouble starting the application on IntelliJ with the error:

```
Field buildProperties in fr.insee.eno.ws.config.OpenApiConfiguration required a bean of type
'org.springframework.boot.info.BuildProperties' that could not be found.
```

you can configure the spring-boot:build-info plugin to "Execute After Rebuild"
(https://stackoverflow.com/a/77218232/13425151).
