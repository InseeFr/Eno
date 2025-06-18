pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("lunatic-model", "5.5.0")
            version("pogues-model", "1.10.0-SNAPSHOT")
            library("lunatic-model", "fr.insee.lunatic", "lunatic-model").versionRef("lunatic-model")
            library("pogues-model", "fr.insee.pogues", "pogues-model").versionRef("pogues-model")
        }
    }
}

rootProject.name = "Eno"

include("eno-core")
include("eno-treatments")
include("eno-ws")
