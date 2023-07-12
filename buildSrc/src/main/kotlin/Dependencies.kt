object Versions {
    const val KOTLIN = "1.8.0"
    const val KTX = "1.11.0-rc6"
    const val GDX = "1.11.0"
    const val DAGGER = "2.46.1"
    const val ASHLEY = "1.7.4"
}

object Dependencies {
    const val GDX_BACKEND_ANDROID = "com.badlogicgames.gdx:gdx-backend-android:${Versions.GDX}"
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"
    const val KTX_SCENE2D = "io.github.libktx:ktx-scene2d:${Versions.KTX}"
    const val KTX_ACTORS = "io.github.libktx:ktx-actors:${Versions.KTX}"
    const val GDX_NATIVES_ARMEABI_V7A = "com.badlogicgames.gdx:gdx-platform:${Versions.GDX}:natives-armeabi-v7a"
    const val GDX_NATIVES_ARM64_V8A = "com.badlogicgames.gdx:gdx-platform:${Versions.GDX}:natives-arm64-v8a"
    const val GDX_NATIVES_X86 = "com.badlogicgames.gdx:gdx-platform:${Versions.GDX}:natives-x86"
    const val GDX_NATIVES_X86_64 = "com.badlogicgames.gdx:gdx-platform:${Versions.GDX}:natives-x86_64"
}