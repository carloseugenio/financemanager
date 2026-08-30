// root build.gradle.kts
import org.gradle.plugins.ide.idea.model.IdeaModel // 1. Make sure to include this import

plugins {
    // Make sure the IntelliJ IDEA plugin is applied at the very top
    idea

    kotlin("plugin.serialization") version "2.4.10" // Match this version to your Kotlin version

    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.koin.compiler) apply false

//    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.room) apply false


}



idea {
    module {
        isDownloadJavadoc = true  // 👈 Forces Gradle to download the documentation bundle
        isDownloadSources = true  // 👈 Forces Gradle to download the -sources.jar file
    }
}

allprojects {
    apply(plugin = "idea")

    // 2. Pass the type directly into the configure function block
    extensions.configure<IdeaModel>("idea") {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}

