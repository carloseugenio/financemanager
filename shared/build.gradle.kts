import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // 👇 ADD THIS LINE TO ACTIVATE THE COMPILER EXTENSION
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    
    android {
       namespace = "br.inf.cepp.financemanager.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.kotlinx.datetime)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.icons.lucide.cmp)
            // Napier logging - Using PlatformUtils for the moment
            // implementation(libs.napier)

            implementation(libs.compose.material.icons)
            //implementation(libs.androidx.navigation.compose)
            implementation(libs.navigation.compose)

            // 👇 ADD THIS EXPLICIT RUNTIME TO FIX DESKTOP COMPILE SPECIFICS
            implementation(libs.kotlinx.serialization.json)

            // 👈 ADD THIS LINE: It tricks Gradle into pulling the sources artifact explicitly
            runtimeOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0:sources")

            // Dependency injection with Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Ktor
            // Core Ktor Client and Engine
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.okhttp)
            // Plugins for JSON content negotiation
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx)
            // Logging
            implementation(libs.ktor.client.logging)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)

            // 2. Adds the core Compose UI Multiplatform Testing API 👈
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
