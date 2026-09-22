buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()

        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
    }

    dependencies {
        classpath("com.squareup:javapoet:1.13.0")
        classpath("com.android.tools.build:gradle:8.9.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")

        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")

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

include(
    ":app",
    "core",
    ":model",
    ":ios"
)
