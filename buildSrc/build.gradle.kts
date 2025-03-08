plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenLocal()
    mavenCentral()

    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

object PluginsVersions {
    const val KOTLIN = "2.0.0"
    const val DAGGER = "2.51.1"
}

dependencies {
    //classpath
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.android.tools.build:gradle:8.1.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")
    implementation("com.google.dagger:hilt-android-gradle-plugin:${PluginsVersions.DAGGER}")

    implementation("com.google.gms:google-services:4.4.2")
    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.9")

    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.20")
}

/*
tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokkaCustomMultiModuleOutput"))
    documentationFileName.set("README.md")
}*/
