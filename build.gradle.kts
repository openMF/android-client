import org.gradle.kotlin.dsl.libs

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        // Pin R8 to a version that understands Kotlin 2.3 metadata. The R8 bundled
        // with AGP 8.12.3 reads up to Kotlin metadata 2.1 only, so every release-mode
        // build with Kotlin 2.3.20 emits "R8: An error occurred when parsing kotlin
        // metadata" warnings for almost every class. Override it with R8 9.1.x stable.
        classpath("com.android.tools:r8:9.1.31")
    }
}

plugins {
    alias(libs.plugins.kotlinCocoapods) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.androidx.navigation) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.module.graph) apply true // Plugin for module graph generation
    //Multiplatform Plugins
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.wire) apply false
    alias(libs.plugins.ktorfit) apply false

    // Kover — root-level registration so the plugin is on the classpath for
    // KoverConventionPlugin (build-logic) to apply per-module. Per-module kover
    // application happens via `org.convention.kover.plugin` chained from base
    // convention plugins (AndroidApplication / KMPLibrary / KMPCoreBaseLibrary).
    // cmp-desktop applies it directly. Tasks: koverHtmlReport / koverXmlReport.
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.kover.convention)
}

object DynamicVersion {
    fun setDynamicVersion(file: File, version: String) {
        val cleanedVersion = version.split('+')[0]
        file.writeText(cleanedVersion)
    }
}

tasks.register("versionFile") {
    val file = File(projectDir, "version.txt")

    DynamicVersion.setDynamicVersion(file, project.version.toString())
}

// Task to print all the module paths in the project e.g. :core:data
// Used by module graph generator script
tasks.register("printModulePaths") {
    subprojects {
        if (subprojects.isEmpty()) {
            println(this.path)
        }
    }
}

// Force consistent versions across all subprojects to fix KLIB resolver duplicate warnings.
// The conflict is between org.jetbrains.androidx.* (CMP) and androidx.* (Google) transitive deps.
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.androidx.lifecycle") {
                useVersion("2.9.6")
            }
            if (requested.group == "org.jetbrains.androidx.savedstate") {
                useVersion("1.3.6")
            }
        }
    }

    // Gradle 9+ defaults Test.failOnNoDiscoveredTests to true. AGP unit-test
    // tasks (testDemoDebugUnitTest, testProdReleaseUnitTest, etc.) then fail
    // on KMP `androidUnitTest` source sets that contain expect/actual TEST
    // HELPERS but no @Test classes — those test classes legitimately live in
    // `commonTest` or `desktopTest`. Disabling per-task unblocks the kover
    // coverage gate without weakening real-test signal.
    tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
        failOnNoDiscoveredTests = false
    }
}