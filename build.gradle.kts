import org.sonarqube.gradle.SonarTask

plugins {
    id("java")
    id("java-library")
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "fr.insee.eno"
    version = "3.26.1-SNAPSHOT"
}


sonar {
    properties {
        val codeCoveragePath = "$projectDir/eno-ws/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml"
        println("Aggregated code coverage report location:$codeCoveragePath")
        property("sonar.coverage.jacoco.xmlReportPaths", codeCoveragePath)
    }
}

tasks.named<SonarTask>("sonar") {
    dependsOn(tasks.named("testCodeCoverageReport"))
}

tasks.register("printVersion") {
    group = "versioning"
    description = "Prints the project version"
    doLast {
        println(project.version)
    }

}
