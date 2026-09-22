import ru.vyarus.gradle.plugin.animalsniffer.AnimalSnifferExtension

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

// :ios is configured in its own ios/build.gradle.kts, not here - see the
// comment at the top of that file for why.

project(":core") {
    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")
    // Guards :core (compiled for both Android and iOS/RoboVM) against Java 8+ API usage,
    // since RoboVM's classlib only has phantom (compile-only) stubs or is entirely
    // missing a lot of it (java.time.*, java.util.stream.*, java.util.function.*,
    // several Collection default methods) - checks compiled bytecode against a JDK 7
    // signature, so it catches both Java and Kotlin sources (sourceCompatibility alone
    // doesn't - Kotlin ignores it).
    apply(plugin = "ru.vyarus.animalsniffer")

    configure<AnimalSnifferExtension> {
        // Kotlin's own data class hashCode() codegen calls Integer/Long/Float.hashCode
        // (int/long/float) unconditionally, regardless of compile target - confirmed via
        // `javap` against robovm-rt-2.3.26.jar that RoboVM's runtime actually implements
        // these three despite them being JDK 8 additions, so they're not a real risk.
        ignore("java.lang.Integer", "java.lang.Long", "java.lang.Float")
    }

    dependencies {
        "implementation"(project(":model"))
        "signature"("org.codehaus.mojo.signature:java17:1.0@signature")

        "implementation"("com.google.code.gson:gson:2.8.9")
        "implementation"("org.apache.commons:commons-lang3:3.0")
        // Used by net.artux.pda.flow's network client (iOS registration/login/story flow).
        // Plain OkHttp, not Retrofit: Retrofit builds its API implementation via
        // java.lang.reflect.Proxy at runtime, which RoboVM's AOT compiler can't handle
        // without extra reflection config - OkHttp's request/response API needs none of that.
        "implementation"("com.squareup.okhttp3:okhttp:4.12.0")

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

        // FlowApiClient has no Gdx/Android/RoboVM dependency, so it's testable on a plain JVM
        // without needing an emulator/device - see FlowApiClientTest.
        "testImplementation"("junit:junit:4.13.2")
        "testImplementation"("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    }
}
