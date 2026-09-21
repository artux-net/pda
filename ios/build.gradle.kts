// Runs core's game/map logic on iOS via RoboVM/MobiVM, with mocked data (see
// ios/src/.../mock) standing in for what the Android app module normally fetches
// from the real backend and passes in through CoreFragment's Bundle args.
//
// This project has its own buildscript block (rather than sharing the root's
// via settings.gradle.kts) deliberately: robovm-gradle-plugin is a fat jar
// that shades its own unsigned copy of org.bouncycastle.*, which collides
// (SecurityException: signer information does not match) with the signed
// bcprov jar AGP uses internally the moment both land on the same buildscript
// classloader - that broke :app's debug-keystore auto-generation on every
// fresh machine/CI runner. Declaring it here instead gives :ios its own child
// classloader, so the collision never reaches :app/AGP's signing tasks.
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
        // MobiVM is the maintained community fork of RoboVM (the original project's
        // commercial backing ended); this is the only practical way to run JVM/libGDX
        // code on iOS. Building anything with it - even :ios:tasks - needs a full Xcode
        // install (not just Command Line Tools) for its iOS SDK/toolchain/codesign.
        // 2.3.22+ added iOS 17+ device launching via devicectl and Xcode 16's
        // provisioning profile location - both needed on current Xcode/iOS versions.
        classpath("com.mobidevelop.robovm:robovm-gradle-plugin:2.3.26")
    }
}

apply(plugin = "java")
apply(plugin = "robovm")

val gdxVersion = "1.12.1"
val gdxControllersVersion = "2.2.1"
val robovmVersion = "2.3.26"

dependencies {
    "implementation"(project(":core"))
    "implementation"(project(":model"))

    "implementation"("com.mobidevelop.robovm:robovm-rt:$robovmVersion")
    "implementation"("com.mobidevelop.robovm:robovm-cocoatouch:$robovmVersion")
    "implementation"("com.badlogicgames.gdx:gdx-backend-robovm:$gdxVersion")
    // Unlike Android, iOS has no separate "natives" configuration - libGDX's own
    // project template puts natives-ios jars on the regular compile classpath, since
    // RoboVM's compiler pulls their embedded xcframeworks straight off the classpath.
    // A custom "natives" config here (as Android needs) would silently exclude them,
    // leaving native methods like IOSGLES20.init() unresolved at runtime.
    "implementation"("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-ios")
    "implementation"("com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-ios")
    "implementation"("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-ios")
    "implementation"("com.badlogicgames.gdx-controllers:gdx-controllers-ios:$gdxControllersVersion")

    // lua script engine - same as :app/:core, RoboVM AOT-compiles plain Java fine.
    "implementation"("org.luaj:luaj-jse:3.0.1")
}
