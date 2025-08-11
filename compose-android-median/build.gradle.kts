/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import com.android.build.gradle.tasks.MergeSourceSetFolders
import java.net.URL

plugins {
    kotlin("android")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("com.android.application")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.goanderco"

    buildFeatures {
        compose = true
    }

    defaultConfig {
        applicationId = "com.goanderco.simplecharts"

        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()

        versionCode = 2
        versionName = "1.0.1"

        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }

    kotlin {
        jvmToolchain(11)
    }
}

val androidxActivityCompose = extra["androidx.activity.compose"] as String

val skikoVersion = extra["skiko.android.version"] as String
val letsPlotVersion = extra["letsPlot.android.version"] as String
val letsPlotKotlinVersion = extra["letsPlotKotlin.android.version"] as String
val letsPlotSkiaVersion = extra["letsPlotSkia.android.version"] as String

dependencies {
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.ui)
    implementation("androidx.activity:activity-compose:$androidxActivityCompose")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("com.jsoizo:kotlin-csv-jvm:1.10.0")

    implementation("org.apache.poi:poi:5.2.4")
    implementation("org.apache.poi:poi-ooxml:5.2.4")
    implementation("org.apache.poi:poi-scratchpad:5.2.4")


    implementation("org.jetbrains.skiko:skiko-android:$skikoVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-kernel:$letsPlotKotlinVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-common:$letsPlotVersion")
    implementation("org.jetbrains.lets-plot:lets-plot-compose:$letsPlotSkiaVersion")

    implementation("dev.shreyaspatil:capturable:3.0.1")

    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.3")

}

////////////////////////////////////////////////////////
// Include the following code in your Gradle build script
// to ensure that compatible Skiko binaries are
// downloaded and included in your project.
//
// Without this, you won't be able to run your app
// in the IDE on a device emulator.
// //////////////////////////////////////////////////////

val skikoJniLibsReleaseAssetName = "skiko-jni-libs.zip"
val skikoJniLibsDestDir = file("${project.projectDir}/src/main/jniLibs/")

tasks.register("downloadSkikoJniLibsReleaseAsset") {
    val repoUrl = "https://github.com/JetBrains/lets-plot-skia"
    val releaseTag = "v$letsPlotSkiaVersion"

    doLast {
        val downloadUrl = "$repoUrl/releases/download/$releaseTag/$skikoJniLibsReleaseAssetName"
        val outputFile = layout.buildDirectory.file("downloads/$skikoJniLibsReleaseAssetName").get().asFile

        if (outputFile.exists()) {
            println("File already exists: ${outputFile.absolutePath}")
            println("Skipping download.")
        } else {
            outputFile.parentFile?.mkdirs()

            println("Downloading $skikoJniLibsReleaseAssetName from $downloadUrl")
            URL(downloadUrl).openStream().use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Download completed: ${outputFile.absolutePath}")
        }
    }
}

tasks.register<Copy>("unzipSkikoJniLibsReleaseAsset") {
    dependsOn("downloadSkikoJniLibsReleaseAsset")
    from(zipTree(layout.buildDirectory.file("downloads/$skikoJniLibsReleaseAssetName")))
    into(skikoJniLibsDestDir)
    doFirst {
        delete(skikoJniLibsDestDir)
    }
}

tasks.register("cleanSkikoJniLibs") {
    doLast {
        delete(skikoJniLibsDestDir)
    }
}

tasks.named("clean") {
    dependsOn("cleanSkikoJniLibs")
}

tasks.withType<MergeSourceSetFolders>().configureEach {
    dependsOn("unzipSkikoJniLibsReleaseAsset")
}

////////////////////////////////////////////////////////
