import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java-library")
    id("maven-publish")
    id("jacoco")
}

description = "Eno XSL-FO module that contains models of XslFO."

// https://stackoverflow.com/a/61671513/13425151
// https://docs.gradle.org/8.10/userguide/migrating_from_groovy_to_kotlin_dsl.html#configuring-tasks
tasks.named<BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

val modelMapperVersion = "3.2.2"

dependencies {


    api("org.apache.xmlbeans:xmlbeans:5.2.0")
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

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
        resources {
            srcDirs("src/main/resources")
            srcDir("src/main/fo-sources")
        }
    }
}


tasks.register("generateSources", type = JavaExec::class) {

    group = "build"
    description = "Generate java sources from FOP xsd"
    classpath = sourceSets["main"].compileClasspath
    mainClass = "org.apache.xmlbeans.impl.tool.SchemaCompiler"
    args(
        "-srconly",
        "-src",
        sourceSets["main"].java.sourceDirectories.asPath,
        "-d",
        sourceSets["main"].resources.sourceDirectories.toList()[0].toPath(),
        sourceSets["main"].resources.sourceDirectories.toList()[1].toPath()
    )

    // Ajouter des logs pour le débogage
    doFirst {
        println ("Source directories: ${sourceSets["main"].java.srcDirs}")
        println ("Schema directory: ${sourceSets["main"].resources.srcDirs}")
    }
}

tasks.named("generateSources").configure {
    onlyIf {
        fileTree(sourceSets["main"].resources.sourceDirectories.toList()[0]) {
            include("**/*.xsb")
        }.isEmpty
    }
}

tasks.named("compileJava") {
    dependsOn("generateSources")
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("check") {
    dependsOn(tasks.test, tasks.jacocoTestReport)
}
