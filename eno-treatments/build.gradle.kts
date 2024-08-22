import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java-library")
    id("maven-publish")
    id("jacoco")
}

description = "Eno specific treatments module."

// https://stackoverflow.com/a/61671513/13425151
// https://docs.gradle.org/8.10/userguide/migrating_from_groovy_to_kotlin_dsl.html#configuring-tasks
tasks.named<BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

val jsonSchemaValidatorVersion = "1.5.0"

dependencies {
    // Eno core
    implementation(project(":eno-core"))
    // Lunatic
    implementation(libs.pogues.model)
    // Pogues
    implementation(libs.lunatic.model)

    // Spring
    implementation("org.springframework:spring-core")
    implementation("org.springframework:spring-beans")
    implementation("org.springframework:spring-expression")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")
    // Json schema validator
    implementation("com.networknt:json-schema-validator:$jsonSchemaValidatorVersion")

    // Logging
    implementation("org.slf4j:slf4j-api")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("ch.qos.logback:logback-classic")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
tasks.named("check") {
    dependsOn(tasks.test, tasks.jacocoTestReport)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
