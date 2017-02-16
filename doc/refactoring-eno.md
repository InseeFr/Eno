# Refactoring Eno

## Objectives

In building the [REST API of Eno](https://github.com/InseeFr/Eno-WS), a simple choice was made: to replicate - in the sense of copying files - all the core transformation architecture (essentially XSLT files) and make it callable via Java classes ("services" in the layered approach).

This choice is not future proof and maintainable: we need to be able to refer to Eno-core as a standalone package with clear boundary and semantic versionning. Furthermore, we want to propose this set of core functions to other "clients", the obvious one being the command line client (Eno-Cli).

## Safe process

The Insee Coltrane project depends quite heavily on Eno, so the changes listed in the following paragraphs should be developped and tested in proper git branches, and merge according to a well defined process to be determined with the Coltrane team at least. The same consideration should be made for the SURS team integration planning.

## Work to be done

### Eno as a maven project

In order to make Eno a Java dependency of Eno-WS and Eno-cli, it must be packaged as a maven project.

Actions:

- add a proper pom.xml,
- deploy to the maven central repository

### Provide Java interfaces to the transformation process

It's what has been done in Eno-WS : two classes are responsible for exposing the transformation functions.

```java
DDIPreprocessing ddiPreprocessingService = new DDIPreprocessing();
DDI2FR ddi2frService = new DDI2FR();
```

Those classes should live in Eno and be provided throught the maven produced JAR.

Actions:

- describe interfaces,
- add the default implementation,
- (provide dependency injection mecanism)

### Add test cases using the Java API

Currently, non regression tests are made in Eno via an [ant build](https://github.com/InseeFr/Eno/blob/master/src/main/scripts/build-non-regression.xml#L210). This script is supported by a Java classes which responsibility is to compare an expected output to the actual output.

The idea is to use a Java testing framework to perform this task.

Actions:

- use the Java API via a Java test suite to perform non regression tests.

### Use the maven package in Eno-WS

In Eno-WS, use the Eno package and the provided services.
