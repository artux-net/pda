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

        // MobiVM is the maintained community fork of RoboVM (the original project's
        // commercial backing ended); this is the only practical way to run JVM/libGDX
        // code on iOS. Building anything with it - even :ios:tasks - needs a full Xcode
        // install (not just Command Line Tools) for its iOS SDK/toolchain/codesign.
        classpath("com.mobidevelop.robovm:robovm-gradle-plugin:2.3.21")
    }
}

include(
    ":app",
    "core",
    ":model",
    ":ios"
)
