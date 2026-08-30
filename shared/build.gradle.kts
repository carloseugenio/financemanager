import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // 👇 ADD THIS LINE TO ACTIVATE THE COMPILER EXTENSION
    kotlin("plugin.serialization")
    // Koin compiler
    alias(libs.plugins.koin.compiler)
    // Room
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.room)
}

koinCompiler {
    userLogs = true
    debugLogs = false
    unsafeDslChecks = true
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

            // Android-specific Koin extensions (if needed)
            // They will automatically inherit the version from the commonMain BOM
            implementation(libs.koin.android)
            implementation("androidx.work:work-runtime-ktx:2.9.1")
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

            ////////////////////////////////////////////////////////////////////////////////
            // Koin - Dependency injection
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.koin.ktor)
            ////////////////////////////////////////////////////////////////////////////////

            ////////////////////////////////////////////////////////////////////////////////
            // Room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
//            ksp(libs.androidx.room.compiler)
            ////////////////////////////////////////////////////////////////////////////////


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

            // Koin Tests
            implementation(libs.koin.test)
            implementation(libs.koin.test.junit5)

            // 2. Adds the core Compose UI Multiplatform Testing API 👈
            // Add Compose multiplatform test artifacts (common + JUnit4 host) so test APIs resolve
            implementation("org.jetbrains.compose.ui:ui-test:${libs.versions.composeMultiplatform.get()}")
            implementation("org.jetbrains.compose.ui:ui-test-junit4:${libs.versions.composeMultiplatform.get()}")
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.apache.pdfbox:pdfbox:2.0.29")
            }
        }
    }
}

// Configure the Room Gradle Plugin
room {
    schemaDirectory("schemas/kspJvm")
}


dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
    kspAndroid(libs.androidx.room.compiler)
    // Ensure KSP runs for the JVM target too so Room generates AppDatabase_Impl for desktop/jvm
    // Use add(...) because kspJvm() accessor may not be available in this Gradle/KSP setup
    add("kspJvm", libs.androidx.room.compiler)
//    add("kspAndroid", libs.androidx.room.compiler)
//    add("kspIosX64", libs.androidx.room.compiler)
//    add("kspIosArm64", libs.androidx.room.compiler)
//    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

tasks.register("test") {
    group = "verification"
    description = "Runs the JVM test suite for the shared module."
    dependsOn("jvmTest")
}