<img style="float: right;" src="logo/eno-logo.png" alt="Eno logo"/>

# Eno

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=InseeFr_Eno&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=InseeFr_Eno)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=InseeFr_Eno&metric=coverage)](https://sonarcloud.io/summary/overall?id=InseeFr_Eno)

Eno is a tool designed to transform questionnaires from their formal description in [DDI](https://ddialliance.org/) to different formats for data collection operations.

From a DDI, Eno produces:

- A [Lunatic](https://github.com/InseeFr/Lunatic) questionnaire that can be used for web, telephone or face to face interviewing.
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

- JDK 21+
- Maven 3+
