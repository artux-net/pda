plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
}

object PluginsVersions {
    const val ANDROID = "7.4.0"
    const val KOTLIN = "1.8.20"
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.android.tools.build:gradle:${PluginsVersions.ANDROID}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${PluginsVersions.KOTLIN}")
}