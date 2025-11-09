


buildscript{
    repositories{
        google()
        mavenLocal()
        mavenCentral()

        maven( "https://jitpack.io" )
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
        maven("https://artifactory.appodeal.com/appodeal")
    }

    dependencies{
        val KOTLIN = "2.0.0"
        val DAGGER = "2.51.1"

        classpath("com.squareup:javapoet:1.13.0")
        classpath("com.android.tools.build:gradle:8.9.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${KOTLIN}")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${KOTLIN}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${DAGGER}")

        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

include(
    ":app",
    "core",
    ":model",
)
