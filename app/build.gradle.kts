import com.android.build.api.variant.ResValue
import java.net.URI

plugins {
    id("com.android.application")
    id("com.android.legacy-kapt")
    id("dagger.hilt.android.plugin")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val apiFile = file("api.json")

val swaggerOutputDir = layout.buildDirectory.dir("generated/swagger-code")

// Runs swagger-codegen-cli directly - the org.hidetake.swagger.generator plugin that
// used to do this is abandoned and calls Project.javaexec(), which Gradle 9 removed.
val swaggerCodegen by configurations.creating

val generateSwaggerCode by tasks.registering(JavaExec::class) {
    inputs.file(apiFile)
    inputs.file("apiconfig.json")
    outputs.dir(swaggerOutputDir)

    classpath = swaggerCodegen
    mainClass.set("io.swagger.codegen.v3.cli.SwaggerCodegen")
    args(
        "generate",
        "-l", "java",
        "-i", apiFile.absolutePath,
        "-c", file("apiconfig.json").absolutePath,
        "-o", swaggerOutputDir.get().asFile.absolutePath,
    )
    doFirst { delete(swaggerOutputDir) }
}

gradle.projectsEvaluated {
    if (!apiFile.exists()) {
        generateSwaggerCode { dependsOn(downloadAPI) }
    }
    tasks.named("preBuild") {
        dependsOn(generateSwaggerCode)
    }
}

android {
    compileSdk = 36

    buildFeatures {
        buildConfig = true
    }

    namespace = "net.artux.pda"

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.setSrcDirs(listOf("src", swaggerOutputDir.get().dir("src/main/java")))
            kotlin.setSrcDirs(listOf("src"))
            aidl.setSrcDirs(listOf("src"))
            res.setSrcDirs(listOf("res"))
            assets.setSrcDirs(listOf("../assets"))
            jniLibs.setSrcDirs(listOf("libs"))
        }
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }

    defaultConfig {
        targetSdk = 36
        minSdk = 26

        applicationId = "net.artux.pda"
        versionCode = 1
        versionName = "dev-build"
        proguardFiles("proguard-rules.pro")
        testProguardFiles("test-proguard-rules.pro")
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false

            buildConfigField("String", "PROTOCOL", "\"https\"")
            buildConfigField("String", "WS_PROTOCOL", "\"wss\"")
            buildConfigField("String", "URL", "\"cdn.artux.net/static/\"")
            buildConfigField("String", "URL_API", "\"app.artux.net/pdanetwork/\"")
            buildConfigField("String", "QuestAdId", "\"ca-app-pub-4707001480680280/1079779549\"")
            buildConfigField("String", "NewsAdId", "\"ca-app-pub-4707001480680280/2723145658\"")
        }

        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true

            buildConfigField("String", "PROTOCOL", "\"https\"")
            buildConfigField("String", "URL_API", "\"dev.artux.net/pdanetwork/\"")
            buildConfigField("String", "WS_PROTOCOL", "\"wss\"")
            buildConfigField("String", "URL", "\"cdn.artux.net/static/\"")
            buildConfigField("String", "QuestAdId", "\"ca-app-pub-3940256099942544/1033173712\"")
            buildConfigField("String", "NewsAdId", "\"ca-app-pub-3940256099942544/2247696110\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        resValues = true
        aidl = true
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    jvmToolchain(17)
}

androidComponents {
    onVariants { variant ->
        // add versions to resources
        val output = variant.outputs.first()
        variant.resValues.put(
            variant.makeResValueKey("string", "versionName"),
            output.versionName.map { ResValue(it) }
        )
        variant.resValues.put(
            variant.makeResValueKey("string", "versionCode"),
            output.versionCode.map { ResValue(it.toString()) }
        )
    }
}

val oltu_version = "1.0.2"
val swagger_annotations_version = "2.0.0"
val junit_version = "4.12"
val threetenbp_version = "1.3.5"
val json_fire_version = "1.8.0"
val mapstruct_version = "1.5.2.Final"
val glide_version = "4.12.0"
val dagger_version = "2.60.1"
val retrofit_version = "2.9.0"
val androidx_version = "2.6.1"

dependencies {
    implementation(fileTree("libs") { include("*.aar") })

    // mapstruct
    implementation("org.mapstruct:mapstruct:1.5.2.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.2.Final")

    // swagger Codegen V3
    swaggerCodegen("io.swagger.codegen.v3:swagger-codegen-cli:3.0.34")
    implementation("io.swagger.core.v3:swagger-annotations:$swagger_annotations_version")
    implementation("org.apache.oltu.oauth2:org.apache.oltu.oauth2.client:$oltu_version") {
        exclude(group = "org.apache.oltu.oauth2", module = "org.apache.oltu.oauth2.common")
    }
    implementation("io.gsonfire:gson-fire:$json_fire_version")
    implementation("org.threeten:threetenbp:$threetenbp_version")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // https://mvnrepository.com/artifact/com.google.android.datatransport/transport-runtime
    implementation("com.google.android.datatransport:transport-runtime:3.3.0")

    // androidx
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$androidx_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$androidx_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$androidx_version")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("androidx.preference:preference-ktx:1.2.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-scalars:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    implementation("me.grantland:autofittextview:0.2.1")
    implementation("org.aviran.cookiebar2:cookiebar2:1.1.4")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.github.bastienpaulfr:Treessence:1.0.5")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-crashlytics")

    // ktx
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

    implementation("com.github.bumptech.glide:glide:$glide_version")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    // dagger
    implementation("com.google.dagger:dagger:$dagger_version")
    kapt("com.google.dagger:dagger-compiler:$dagger_version")

    implementation("com.google.dagger:hilt-android:$dagger_version")
    kapt("com.google.dagger:hilt-compiler:$dagger_version")
}

val downloadAPI by tasks.registering {
    outputs.file(apiFile)
    // Always refetch when run explicitly - the spec lives on the server, not in git
    outputs.upToDateWhen { false }
    doLast {
        val url = URI("https://app.artux.net/pdanetwork/v3/api-docs/pdanetwork-rest").toURL()
        apiFile.writeBytes(url.openStream().use { it.readBytes() })
    }
}

tasks.register("copyAndroidNatives") {
    doFirst {
        file("libs/armeabi/").mkdirs()
        file("libs/armeabi-v7a/").mkdirs()
        file("libs/arm64-v8a/").mkdirs()
        file("libs/x86_64/").mkdirs()
        file("libs/x86/").mkdirs()

        configurations["natives"].copy().files.forEach { jar ->
            var outputDir: File? = null
            if (jar.name.endsWith("natives-arm64-v8a.jar")) outputDir = file("libs/arm64-v8a")
            if (jar.name.endsWith("natives-armeabi-v7a.jar")) outputDir = file("libs/armeabi-v7a")
            if (jar.name.endsWith("natives-armeabi.jar")) outputDir = file("libs/armeabi")
            if (jar.name.endsWith("natives-x86_64.jar")) outputDir = file("libs/x86_64")
            if (jar.name.endsWith("natives-x86.jar")) outputDir = file("libs/x86")
            val dir = outputDir
            if (dir != null) {
                copy {
                    from(zipTree(jar))
                    into(dir)
                    include("*.so")
                }
            }
        }
    }
}

tasks.configureEach {
    if (name.contains("package")) {
        dependsOn("copyAndroidNatives")
    }
}
