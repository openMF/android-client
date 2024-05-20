pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
        jcenter()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "AndroidClient"
include(":mifosng-android")
include(":core:designsystem")
include(":core:datastore")
include(":core:common")
include(":feature:auth")
include(":core:network")
include(":core:data")
include(":feature:client")
include(":feature:checker-inbox-task")
include(":feature:collection-sheet")
