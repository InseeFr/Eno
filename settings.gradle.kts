pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("lunatic-model", "5.2.2")
            version("pogues-model", "1.7.2")
            library("lunatic-model", "fr.insee.lunatic", "lunatic-model").versionRef("lunatic-model")
            library("pogues-model", "fr.insee.pogues", "pogues-model").versionRef("pogues-model")
        }
    }
}

rootProject.name = "Eno"

include("eno-core")
include("eno-treatments")
include("eno-ws")
