import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.jpa") version "2.1.0"
    kotlin("plugin.allopen") version "2.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

val http4kVersion: String by project
val kotlinVersion: String by project
val ktlintVersion: String by project

application {
    mainClass = "ru.yarsu.WebApplicationKt"
}

repositories {
    mavenCentral()
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            allWarningsAsErrors.set(false)
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    shadowJar {
        archiveBaseName.set("web-application")
        archiveVersion.set("1.0")
        archiveClassifier.set("")
        manifest {
            attributes["Main-Class"] = "ru.fspn.WebApplicationKt"
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.hibernate:hibernate-core:6.1.3.Final")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    implementation("ch.qos.logback:logback-classic:1.5.4")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.slf4j:slf4j-api:1.6.1")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation(platform("org.http4k:http4k-bom:5.41.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-client-okhttp:$http4kVersion")
    implementation("org.http4k:http4k-cloudnative:$http4kVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-multipart:$http4kVersion")
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-template-pebble:$http4kVersion")
    implementation("org.http4k:http4k-cloudnative:$http4kVersion")
    implementation(platform("org.http4k:http4k-bom:$http4kVersion"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.auth0:java-jwt:4.4.0")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.http4k:http4k-testing-approval:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-kotest:$http4kVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embedabble")
}

ktlint {
    version.set(ktlintVersion)
    disabledRules.set(setOf("no-wildcard-imports"))
}
