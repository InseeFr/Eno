import org.sonarqube.gradle.SonarTask

plugins {
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    id("application")
    id("jacoco-report-aggregation")
    id("org.sonarqube") version "5.1.0.4882"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "fr.insee.eno"
    version = "3.27.1"
}

subprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
        mavenLocal()
    }
}

sonar {
    properties {
        // The Jacoco coverage report is aggreated in the eno-ws module
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
