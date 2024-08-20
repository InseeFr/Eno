import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java-library")
    id("maven-publish")
    id("jacoco")
}

// https://stackoverflow.com/a/61671513/13425151
// https://docs.gradle.org/8.10/userguide/migrating_from_groovy_to_kotlin_dsl.html#configuring-tasks
tasks.named<BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

description = "eno-core"

val ddiJavaLibVersion = "1.0.0"
val modelMapperVersion = "3.2.0"

dependencies {
    // DDI
    implementation("fr.insee.ddi:ddi-lifecycle:$ddiJavaLibVersion")
    // Pogues
    implementation(libs.pogues.model)
    // Lunatic
    implementation(libs.lunatic.model)

    // Spring
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework:spring-expression")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml") // imported for DDI "CDATA" suggester
    // ModelMapper (used to copy objects, few usages)
    implementation("org.modelmapper:modelmapper:$modelMapperVersion")

    // Logging
    implementation("org.slf4j:slf4j-api")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.skyscreamer:jsonassert")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("ch.qos.logback:logback-classic")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
