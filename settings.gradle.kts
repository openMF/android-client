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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":mifosng-android")
include(":core:common")
include(":core:network")
include(":core:data")
include(":core:designsystem")
include(":core:database")
include(":core:domain")
include(":core:datastore")
include(":core:model")
include(":core:testing")
include(":core:ui")

include(":feature:auth")
include(":feature:client")
include(":feature:checker-inbox-task")
include(":feature:collection-sheet")
include(":feature:groups")
include(":feature:settings")
include(":feature:center")
include(":feature:about")
include(":feature:report")
include(":feature:path-tracking")
include(":feature:note")
include(":feature:activate")
include(":feature:loan")
include(":feature:document")
include(":feature:savings")
include(":feature:data-table")
include(":feature:search")
