plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("application")
    id("jacoco-report-aggregation")
}

description = "Eno REST web-service application."

val javaMainClass by extra("fr.insee.eno.ws.EnoWsApplication")

application {
    mainClass.set(javaMainClass)
}

// Remove the "-plain" jar
// https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#packaging-executable.and-plain-archives
// https://docs.gradle.org/8.10/userguide/migrating_from_groovy_to_kotlin_dsl.html#configuring-tasks
tasks.named<Jar>("jar") {
    enabled = false
}

val springdocVersion = "2.8.8"

dependencies { 
    //
    implementation(project(":eno-core"))
    implementation(project(":eno-treatments"))
    // Models
    implementation(libs.lunatic.model)
    implementation(libs.pogues.model)
    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Web Client
    // since Spring MVC RestTemplate class is deprecated, the webflux dependency is added to import WebClient
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Open API
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")
    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // Spring devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
tasks.named("check") {
    dependsOn(tasks.test, tasks.named<JacocoReport>("testCodeCoverageReport"))
}

tasks.processResources {
    filesMatching("version.properties") {
        expand(project.properties)
    }
}
