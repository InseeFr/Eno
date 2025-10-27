import org.sonarqube.gradle.SonarTask

plugins {
    id("org.springframework.boot") version "3.5.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("application")
    id("jacoco-report-aggregation")
    id("org.sonarqube") version "7.0.1.6134"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "fr.insee.eno"
    version = "3.58.1"
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
    sonar {
        properties {
            property("sonar.sources", "src/main/java")
            property("sonar.tests", "src/test/java")
            property("sonar.java.binaries",
                layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
        }
    }
    tasks.named<SonarTask>("sonar") {
        // ensure Sonar runs only after classes exist
        dependsOn("build")
    }
}

sonar {
    properties {
        // The Jacoco coverage report is aggregated in the eno-ws module
        val codeCoveragePath = "$projectDir/eno-ws/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml"
        println("Aggregated code coverage report location:$codeCoveragePath")
        property("sonar.coverage.jacoco.xmlReportPaths", codeCoveragePath)
    }
}

tasks.named<SonarTask>("sonar") {
    dependsOn(tasks.named("build"), tasks.named("testCodeCoverageReport"))
}

tasks.register("printVersion") {
    group = "versioning"
    description = "Prints the project version"
    doLast {
        println(project.version)
    }
}
