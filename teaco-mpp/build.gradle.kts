import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("maven-publish")
    id("dev.icerock.mobile.multiplatform")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.Experimental")
            }
        }
    }
}

dependencies {
    val kotlinVersion: String by project
    val coroutinesVersion: String by project
    val lifecycleVersion: String by project

    mppLibrary(
        MultiPlatformLibrary(
            common = "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion",
            android = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        )
    )
    mppLibrary(
        MultiPlatformLibrary(
            common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion",
            android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion",
            ios = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion"
        )
    )

    androidLibrary(AndroidLibrary("androidx.appcompat:appcompat:1.1.0"))
    androidLibrary(AndroidLibrary("androidx.fragment:fragment-ktx:1.2.0-rc02"))
    androidLibrary(AndroidLibrary("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"))
    androidLibrary(AndroidLibrary("androidx.lifecycle:lifecycle-extensions:$lifecycleVersion"))
    androidLibrary(AndroidLibrary("androidx.lifecycle:lifecycle-viewmodel-savedstate:1.0.0-rc02"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(29)
    }

    sourceSets {
        val main by getting {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            java.srcDirs("src/androidMain/kotlin")
            res.srcDirs("src/androidMain/res")
        }
        val test by getting {
            java.srcDirs("src/androidTest/kotlin")
            res.srcDirs("src/androidTest/res")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// workaround for https://youtrack.jetbrains.com/issue/KT-27170
configurations.create("compileClasspath")

task("iosTest") {
    dependsOn("linkDebugTestIos")
    doLast {
        val testBinaryPath =
            (kotlin.targets["ios"] as KotlinNativeTarget).binaries.getTest("DEBUG")
                .outputFile.absolutePath
        exec {
            commandLine("xcrun", "simctl", "spawn", "iPhone Xʀ", testBinaryPath)
        }
    }
}

tasks["check"].dependsOn("iosTest")

val local = Properties()
val localProperties: File = rootProject.file("local.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}
val groupId: String by project
val artifactId: String by project
val versionName: String by project

val bintrayOrg: String by project
val bintrayRepo: String by project
val bintrayDescription: String by project

val websiteUrl: String by project
val issueTrackerUrl: String by project
val vcsUrl: String by project

group = groupId
version = versionName

publishing {
    repositories.maven(
        url = "https://api.bintray.com/maven/$bintrayOrg/$bintrayRepo/$artifactId/;publish=1"
    ) {
        name = "bintray"
        credentials {
            username = local.getProperty("bintray.user")
            password = local.getProperty("bintray.key")
        }
    }

    publications.all {
        this as MavenPublication
        println(name)

        this.groupId = groupId
        this.artifactId = artifactId
        this.version = versionName

        pom {
            name.set(groupId)
            description.set(bintrayDescription)
            url.set(websiteUrl)

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("egroden")
                    name.set("Denis Egorov")
                }
                developer {
                    id.set("indrih17")
                    name.set("Kirill Indrih")
                }
            }

            scm {
                url.set(websiteUrl)
            }
        }
    }
}