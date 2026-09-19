val kotlin_version = "2.0.0"
val gdxVersion = "1.12.1"
val ashleyVersion = "1.7.4"
val dagger_version = "2.46.1"
val aiVersion = "1.8.2"
val gdxControllersVersion = "2.2.1"

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()

        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/releases/")
    }
}

project(":app") {
    apply(plugin = "com.android.application")

    configurations {
        create("natives")
    }

    dependencies {
        "implementation"(project(":core"))
        "implementation"(project(":model"))

        "api"("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
        "api"("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
        "natives"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-armeabi-v7a")
        "natives"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-arm64-v8a")
        "natives"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86")
        "natives"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-x86_64")
        "api"("com.badlogicgames.ashley:ashley:$ashleyVersion")
        "api"("com.badlogicgames.gdx-controllers:gdx-controllers-android:$gdxControllersVersion")
        "api"("com.badlogicgames.gdx:gdx-ai:$aiVersion")

        "api"("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64")

        // lua script engine
        "implementation"("org.luaj:luaj-jse:3.0.1")
    }
}

// Runs core's game/map logic on iOS via RoboVM/MobiVM, with mocked data (see
// ios/src/.../mock) standing in for what the Android app module normally fetches
// from the real backend and passes in through CoreFragment's Bundle args.
project(":ios") {
    apply(plugin = "java")
    apply(plugin = "robovm")

    val robovmVersion = "2.3.21"

    configurations {
        create("natives")
    }

    dependencies {
        "implementation"(project(":core"))
        "implementation"(project(":model"))

        "implementation"("com.mobidevelop.robovm:robovm-rt:$robovmVersion")
        "implementation"("com.mobidevelop.robovm:robovm-cocoatouch:$robovmVersion")
        "implementation"("com.badlogicgames.gdx:gdx-backend-robovm:$gdxVersion")
        "natives"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-ios")
        "natives"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-ios")
        "natives"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-ios")
        // GamepadInputSystem (core) polls Controllers.getCurrent() unconditionally every
        // frame; without this, Controllers' reflective lookup of the iOS manager class
        // throws (ClassNotFoundException -> GdxRuntimeException) as soon as the map loads.
        "implementation"("com.badlogicgames.gdx-controllers:gdx-controllers-ios:$gdxControllersVersion")

        // lua script engine - same as :app/:core, RoboVM AOT-compiles plain Java fine.
        "implementation"("org.luaj:luaj-jse:3.0.1")
    }
}

project(":core") {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")

    dependencies {
        "implementation"(project(":model"))

        "implementation"("com.google.code.gson:gson:2.8.9")
        "implementation"("org.apache.commons:commons-lang3:3.0")

        "api"("com.badlogicgames.gdx:gdx:$gdxVersion")
        "api"("com.badlogicgames.gdx:gdx-ai:$aiVersion")
        "api"("com.badlogicgames.gdx:gdx-box2d:$gdxVersion")
        "api"("com.badlogicgames.ashley:ashley:$ashleyVersion")
        "api"("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
        "api"("com.badlogicgames.gdx-controllers:gdx-controllers-core:$gdxControllersVersion")

        //dagger
        "kapt"("com.google.dagger:dagger-compiler:$dagger_version")
        "implementation"("com.google.dagger:dagger:$dagger_version")
        "annotationProcessor"("com.google.dagger:dagger-compiler:$dagger_version")

        //kotlin
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        "implementation"("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

        // lua script engine
        // https://mvnrepository.com/artifact/org.luaj/luaj-jse
        "implementation"("org.luaj:luaj-jse:3.0.1")
    }
}
