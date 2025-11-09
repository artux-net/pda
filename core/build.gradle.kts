import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("kotlin")
    id("kotlin-kapt")
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")
    implementation("org.int4.dirk:dirk-di:1.0.0-beta1")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}