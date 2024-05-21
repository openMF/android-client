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

rootProject.name = "AndroidClient"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":mifosng-android")
include(":core:common")
include(":core:data")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:network")
include(":core:ui")
include(":core:testing")

include(":feature:auth")
include(":feature:client")
include(":feature:checker-inbox-task")
include(":feature:collection-sheet")
include(":feature:groups")
