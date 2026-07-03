/**
 * Dieses File enthält die Build-Konfiguration für das Projekt.
 * Hier werden Plugins, Abhängigkeiten und Repository-Einstellungen definiert.
 */
plugins {
    kotlin("jvm") version "2.2.20"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("todo.MainKt")
}
