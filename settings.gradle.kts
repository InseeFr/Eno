pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {

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

    versionCatalogs {
        create("libs") {
            version("lunatic-model", "3.13.0")
            version("pogues-model", "1.3.14")
            library("lunatic-model", "fr.insee.lunatic", "lunatic-model").versionRef("lunatic-model")
            library("pogues-model", "fr.insee.pogues", "pogues-model").versionRef("pogues-model")
        }
    }

}

rootProject.name = "Eno"

include("eno-core")
include("eno-treatments")
include("eno-ws")
