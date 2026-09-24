buildscript {
    repositories {
        google()
        mavenCentral()

        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:9.4.1")
        // Provides com.android.legacy-kapt (kapt under AGP 9's built-in Kotlin)
        classpath("com.android.tools.build:gradle-kotlin:9.4.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.60.1")

        classpath("com.google.gms:google-services:4.5.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.8")

        // robovm-gradle-plugin is NOT declared here on purpose - it's a fat jar
        // shading its own unsigned org.bouncycastle.* classes, which collides
        // (SecurityException: signer information does not match) with the signed
        // bcprov jar AGP uses internally once both share this classloader. It's
        // declared in ios/build.gradle.kts's own buildscript block instead, which
        // gets its own child classloader and keeps that collision away from
        // :app/AGP's signing tasks entirely.

        // Guards :core (compiled for both Android and iOS/RoboVM) against Java 8+ API
        // usage - checks compiled .class bytecode against a JDK 7 signature, so it
        // catches both Java and Kotlin sources alike (unlike sourceCompatibility, which
        // Kotlin ignores).
        classpath("ru.vyarus:gradle-animalsniffer-plugin:2.0.1")
    }
}

plugins {
    // Lets Gradle download the JDK 17 toolchain :app asks for when it isn't installed locally
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(
    ":app",
    "core",
    ":model",
    ":ios"
)
